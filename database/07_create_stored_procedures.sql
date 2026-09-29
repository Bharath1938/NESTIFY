-- ============================================================================
-- Script: 07_create_stored_procedures.sql
-- Description: Stored procedures and scalar functions supporting the .NET Web API,
--              Customer Mobile App, Organizer Mobile App, and Admin Web Panel.
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

PRINT 'Creating Security and Password Functions...';
GO

-- 1. Password Hashing Helper Function (SHA-512 with per-user/system salt)
CREATE OR ALTER FUNCTION dbo.fn_HashPassword
(
    @Password NVARCHAR(200),
    @Salt     NVARCHAR(100)
)
RETURNS NVARCHAR(500)
AS
BEGIN
    -- Generates a 128-character hexadecimal SHA2_512 hash
    RETURN CONVERT(NVARCHAR(500), HASHBYTES('SHA2_512', CONCAT(@Salt, @Password)), 2);
END;
GO

-- 2. Password Verification Helper Function
CREATE OR ALTER FUNCTION dbo.fn_VerifyPassword
(
    @Password   NVARCHAR(200),
    @Salt       NVARCHAR(100),
    @StoredHash NVARCHAR(500)
)
RETURNS BIT
AS
BEGIN
    DECLARE @CalculatedHash NVARCHAR(500) = dbo.fn_HashPassword(@Password, @Salt);
    IF @CalculatedHash = @StoredHash
        RETURN 1;
    RETURN 0;
END;
GO

PRINT 'Creating Authentication Stored Procedures...';
GO

-- 3. Register Customer
CREATE OR ALTER PROCEDURE dbo.sp_RegisterCustomer
    @LoginID         NVARCHAR(150),
    @Password        NVARCHAR(200),
    @FullName        NVARCHAR(150),
    @Phone           NVARCHAR(30),
    @Email           NVARCHAR(150) = NULL,
    @ProfilePhotoURL NVARCHAR(500) = NULL,
    @NewCustomerID   BIGINT OUTPUT,
    @NewUserID       BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        IF EXISTS (SELECT 1 FROM dbo.Users WHERE LoginID = @LoginID)
        BEGIN
            RAISERROR(N'LoginID is already registered.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        DECLARE @Salt NVARCHAR(100) = N'NestifySalt#2026$';
        DECLARE @PasswordHash NVARCHAR(500) = dbo.fn_HashPassword(@Password, @Salt);

        -- Insert User Record
        INSERT INTO dbo.Users (LoginID, PasswordHash, Role, IsActive, CreatedDate)
        VALUES (@LoginID, @PasswordHash, N'Customer', 1, SYSUTCDATETIME());

        SET @NewUserID = SCOPE_IDENTITY();

        -- Insert Customer Record
        INSERT INTO dbo.Customers (UserID, FullName, Phone, Email, ProfilePhotoURL, Status, CreatedDate)
        VALUES (@NewUserID, @FullName, @Phone, COALESCE(@Email, @LoginID), @ProfilePhotoURL, N'Active', SYSUTCDATETIME());

        SET @NewCustomerID = SCOPE_IDENTITY();

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- 4. Register Organizer
CREATE OR ALTER PROCEDURE dbo.sp_RegisterOrganizer
    @LoginID          NVARCHAR(150),
    @Password         NVARCHAR(200),
    @FullName         NVARCHAR(150),
    @Phone            NVARCHAR(30),
    @Email            NVARCHAR(150) = NULL,
    @ExperienceYears  DECIMAL(5,2) = 0.00,
    @ProfilePhotoURL  NVARCHAR(500) = NULL,
    @NewOrganizerID   BIGINT OUTPUT,
    @NewUserID        BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        IF EXISTS (SELECT 1 FROM dbo.Users WHERE LoginID = @LoginID)
        BEGIN
            RAISERROR(N'LoginID is already registered.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        DECLARE @Salt NVARCHAR(100) = N'NestifySalt#2026$';
        DECLARE @PasswordHash NVARCHAR(500) = dbo.fn_HashPassword(@Password, @Salt);

        -- Insert User Record
        INSERT INTO dbo.Users (LoginID, PasswordHash, Role, IsActive, CreatedDate)
        VALUES (@LoginID, @PasswordHash, N'Organizer', 1, SYSUTCDATETIME());

        SET @NewUserID = SCOPE_IDENTITY();

        -- Insert Organizer Record (Default: Pending Approval)
        INSERT INTO dbo.Organizers (
            UserID, FullName, Phone, Email, ProfilePhotoURL, 
            ExperienceYears, VerificationStatus, ApprovalStatus, Rating, CompletedJobs, IsActive, CreatedDate
        )
        VALUES (
            @NewUserID, @FullName, @Phone, COALESCE(@Email, @LoginID), @ProfilePhotoURL,
            @ExperienceYears, N'Pending', N'Pending', 0.00, 0, 1, SYSUTCDATETIME()
        );

        SET @NewOrganizerID = SCOPE_IDENTITY();

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- 5. Authenticate User (Customer, Organizer, Admin)
CREATE OR ALTER PROCEDURE dbo.sp_AuthenticateUser
    @LoginID     NVARCHAR(150),
    @Password    NVARCHAR(200),
    @ExpectedRole NVARCHAR(30) = NULL -- If specified, verifies role segregation
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @Salt NVARCHAR(100) = N'NestifySalt#2026$';
    DECLARE @PasswordHash NVARCHAR(500) = dbo.fn_HashPassword(@Password, @Salt);
    DECLARE @UserID BIGINT, @Role NVARCHAR(30), @IsActive BIT;

    SELECT 
        @UserID = UserID,
        @Role = Role,
        @IsActive = IsActive
    FROM dbo.Users
    WHERE LoginID = @LoginID AND PasswordHash = @PasswordHash;

    IF @UserID IS NULL
    BEGIN
        SELECT 0 AS IsAuthenticated, N'Invalid LoginID or password.' AS ErrorMessage;
        RETURN;
    END

    IF @IsActive = 0
    BEGIN
        SELECT 0 AS IsAuthenticated, N'Your account has been deactivated. Please contact support.' AS ErrorMessage;
        RETURN;
    END

    IF @ExpectedRole IS NOT NULL AND @Role <> @ExpectedRole
    BEGIN
        SELECT 0 AS IsAuthenticated, CONCAT(N'Access denied for role: ', @Role) AS ErrorMessage;
        RETURN;
    END

    -- Update Last Login Date
    UPDATE dbo.Users SET LastLoginDate = SYSUTCDATETIME() WHERE UserID = @UserID;

    -- Return full user profile payload
    SELECT 
        1 AS IsAuthenticated,
        NULL AS ErrorMessage,
        u.UserID,
        u.LoginID,
        u.Role,
        u.LastLoginDate,
        c.CustomerID,
        c.FullName AS CustomerFullName,
        o.OrganizerID,
        o.FullName AS OrganizerFullName,
        o.ApprovalStatus AS OrganizerApprovalStatus,
        o.VerificationStatus AS OrganizerVerificationStatus
    FROM dbo.Users u
    LEFT JOIN dbo.Customers c ON u.UserID = c.UserID
    LEFT JOIN dbo.Organizers o ON u.UserID = o.UserID
    WHERE u.UserID = @UserID;
END;
GO

PRINT 'Creating Customer API Stored Procedures...';
GO

-- 6. Get Customer Profile
CREATE OR ALTER PROCEDURE dbo.sp_GetCustomerProfile
    @CustomerID BIGINT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        c.CustomerID,
        c.UserID,
        u.LoginID,
        c.FullName,
        c.Phone,
        c.Email,
        c.ProfilePhotoURL,
        c.Status,
        c.CreatedDate
    FROM dbo.Customers c
    INNER JOIN dbo.Users u ON c.UserID = u.UserID
    WHERE c.CustomerID = @CustomerID;

    -- Return Customer Addresses
    SELECT 
        AddressID,
        AddressLabel,
        AddressLine1,
        AddressLine2,
        City,
        State,
        PostalCode,
        Landmark,
        Latitude,
        Longitude,
        IsDefault
    FROM dbo.Addresses
    WHERE CustomerID = @CustomerID
    ORDER BY IsDefault DESC, AddressID ASC;
END;
GO

-- 7. Add or Update Customer Address
CREATE OR ALTER PROCEDURE dbo.sp_SaveCustomerAddress
    @CustomerID   BIGINT,
    @AddressID    BIGINT = NULL, -- NULL to insert, ID to update
    @AddressLabel NVARCHAR(50),
    @AddressLine1 NVARCHAR(250),
    @AddressLine2 NVARCHAR(250) = NULL,
    @City         NVARCHAR(100),
    @State        NVARCHAR(100) = NULL,
    @PostalCode   NVARCHAR(20) = NULL,
    @Landmark     NVARCHAR(250) = NULL,
    @Latitude     DECIMAL(10,7) = NULL,
    @Longitude    DECIMAL(10,7) = NULL,
    @IsDefault    BIT = 0,
    @ResultAddressID BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- If this is the customer's first address, make it default automatically
        IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE CustomerID = @CustomerID)
            SET @IsDefault = 1;

        -- If setting to default, clear previous default
        IF @IsDefault = 1
        BEGIN
            UPDATE dbo.Addresses
            SET IsDefault = 0, UpdatedDate = SYSUTCDATETIME()
            WHERE CustomerID = @CustomerID;
        END

        IF @AddressID IS NULL OR @AddressID = 0
        BEGIN
            INSERT INTO dbo.Addresses (
                CustomerID, AddressLabel, AddressLine1, AddressLine2,
                City, State, PostalCode, Landmark, Latitude, Longitude, IsDefault, CreatedDate
            )
            VALUES (
                @CustomerID, @AddressLabel, @AddressLine1, @AddressLine2,
                @City, @State, @PostalCode, @Landmark, @Latitude, @Longitude, @IsDefault, SYSUTCDATETIME()
            );
            SET @ResultAddressID = SCOPE_IDENTITY();
        END
        ELSE
        BEGIN
            UPDATE dbo.Addresses
            SET 
                AddressLabel = @AddressLabel,
                AddressLine1 = @AddressLine1,
                AddressLine2 = @AddressLine2,
                City = @City,
                State = @State,
                PostalCode = @PostalCode,
                Landmark = @Landmark,
                Latitude = @Latitude,
                Longitude = @Longitude,
                IsDefault = @IsDefault,
                UpdatedDate = SYSUTCDATETIME()
            WHERE AddressID = @AddressID AND CustomerID = @CustomerID;

            SET @ResultAddressID = @AddressID;
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- 8. Create Marketplace Booking
CREATE OR ALTER PROCEDURE dbo.sp_CreateBooking
    @CustomerID        BIGINT,
    @ServiceID         BIGINT,
    @AddressID         BIGINT,
    @BookingDate       DATE,
    @StartTime         TIME(0),
    @EstimatedDuration DECIMAL(10,2) = NULL,
    @CustomerNotes     NVARCHAR(MAX) = NULL,
    @PromoCode         NVARCHAR(50) = NULL,
    @NewBookingID      BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- 1. Validate Customer
        IF NOT EXISTS (SELECT 1 FROM dbo.Customers WHERE CustomerID = @CustomerID AND Status = N'Active')
        BEGIN
            RAISERROR(N'Customer does not exist or is inactive.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- 2. Validate Service
        DECLARE @BasePrice DECIMAL(18,2), @ServiceDuration DECIMAL(10,2);
        SELECT 
            @BasePrice = StartingPrice,
            @ServiceDuration = COALESCE(@EstimatedDuration, EstimatedDuration, 2.00)
        FROM dbo.Services
        WHERE ServiceID = @ServiceID AND IsActive = 1;

        IF @BasePrice IS NULL
        BEGIN
            RAISERROR(N'Service does not exist or is inactive.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- 3. Validate Address belongs to Customer
        IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE AddressID = @AddressID AND CustomerID = @CustomerID)
        BEGIN
            RAISERROR(N'Selected address does not belong to this customer.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- 4. Calculate Financials (Platform Service Fee = 15%)
        DECLARE @ServiceFee DECIMAL(18,2) = ROUND(@BasePrice * 0.15, 2);
        DECLARE @DiscountAmount DECIMAL(18,2) = 0.00;

        -- 5. Apply Promo Code if valid
        IF @PromoCode IS NOT NULL AND LEN(TRIM(@PromoCode)) > 0
        BEGIN
            DECLARE @PromoID BIGINT, @DiscType NVARCHAR(20), @DiscVal DECIMAL(18,2), @MinAmt DECIMAL(18,2), @MaxDisc DECIMAL(18,2);
            SELECT 
                @PromoID = PromoCodeID,
                @DiscType = DiscountType,
                @DiscVal = DiscountValue,
                @MinAmt = MinimumAmount,
                @MaxDisc = MaximumDiscount
            FROM dbo.PromoCodes
            WHERE Code = @PromoCode 
              AND IsActive = 1 
              AND SYSUTCDATETIME() BETWEEN ValidFrom AND ValidTo
              AND (UsageLimit IS NULL OR UsedCount < UsageLimit);

            IF @PromoID IS NOT NULL AND (@MinAmt IS NULL OR @BasePrice >= @MinAmt)
            BEGIN
                IF @DiscType = N'Percentage'
                    SET @DiscountAmount = ROUND(@BasePrice * (@DiscVal / 100.0), 2);
                ELSE
                    SET @DiscountAmount = @DiscVal;

                IF @MaxDisc IS NOT NULL AND @DiscountAmount > @MaxDisc
                    SET @DiscountAmount = @MaxDisc;

                -- Increment Promo Code usage
                UPDATE dbo.PromoCodes SET UsedCount = UsedCount + 1, UpdatedDate = SYSUTCDATETIME() WHERE PromoCodeID = @PromoID;
            END
        END

        DECLARE @TotalAmount DECIMAL(18,2) = (@BasePrice + @ServiceFee) - @DiscountAmount;
        IF @TotalAmount < 0.00 SET @TotalAmount = 0.00;

        -- 6. Insert Booking record (Status = 'Requested')
        INSERT INTO dbo.Bookings (
            CustomerID, OrganizerID, ServiceID, AddressID,
            BookingDate, StartTime, EstimatedDuration,
            Status, PaymentStatus,
            BaseAmount, ServiceFee, DiscountAmount, TotalAmount,
            CustomerNotes, CreatedDate
        )
        VALUES (
            @CustomerID, NULL, @ServiceID, @AddressID,
            @BookingDate, @StartTime, @ServiceDuration,
            N'Requested', N'Pending',
            @BasePrice, @ServiceFee, @DiscountAmount, @TotalAmount,
            @CustomerNotes, SYSUTCDATETIME()
        );

        SET @NewBookingID = SCOPE_IDENTITY();

        -- 7. Insert Initial Status Audit History
        DECLARE @CustomerUserID BIGINT = (SELECT UserID FROM dbo.Customers WHERE CustomerID = @CustomerID);
        INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
        VALUES (@NewBookingID, NULL, N'Requested', @CustomerUserID, N'Booking requested by customer', SYSUTCDATETIME());

        -- 8. Add Customer Notification
        INSERT INTO dbo.Notifications (UserID, Title, Message, NotificationType, ReferenceID, IsRead, SentDate, CreatedDate)
        VALUES (@CustomerUserID, N'Booking Requested', N'Your booking request has been submitted and is waiting for organizer assignment.', N'BookingConfirmed', @NewBookingID, 0, SYSUTCDATETIME(), SYSUTCDATETIME());

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- 9. Get Customer Bookings
CREATE OR ALTER PROCEDURE dbo.sp_GetCustomerBookings
    @CustomerID BIGINT,
    @StatusFilter NVARCHAR(40) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        b.BookingID,
        b.BookingDate,
        b.StartTime,
        b.EstimatedDuration,
        b.Status,
        b.PaymentStatus,
        b.TotalAmount,
        s.ServiceName,
        s.ImageURL AS ServiceImageURL,
        o.OrganizerID,
        o.FullName AS OrganizerName,
        o.Phone AS OrganizerPhone,
        o.Rating AS OrganizerRating,
        a.City,
        a.AddressLine1
    FROM dbo.Bookings b
    INNER JOIN dbo.Services s ON b.ServiceID = s.ServiceID
    INNER JOIN dbo.Addresses a ON b.AddressID = a.AddressID
    LEFT JOIN dbo.Organizers o ON b.OrganizerID = o.OrganizerID
    WHERE b.CustomerID = @CustomerID
      AND (@StatusFilter IS NULL OR b.Status = @StatusFilter)
    ORDER BY b.BookingDate DESC, b.StartTime DESC;
END;
GO

-- 10. Get Booking Detail (Complete 360-degree view)
CREATE OR ALTER PROCEDURE dbo.sp_GetBookingDetail
    @BookingID BIGINT
AS
BEGIN
    SET NOCOUNT ON;

    -- Main Booking Details
    SELECT * FROM dbo.vw_BookingDetails WHERE BookingID = @BookingID;

    -- Booking Items
    SELECT 
        BookingItemID,
        ItemType,
        ItemName,
        Quantity,
        UnitPrice,
        Amount,
        Notes
    FROM dbo.BookingItems
    WHERE BookingID = @BookingID;

    -- Booking Photos
    SELECT 
        BookingPhotoID,
        PhotoType,
        FileName,
        FileURL,
        UploadedByUserID,
        CreatedDate
    FROM dbo.BookingPhotos
    WHERE BookingID = @BookingID
    ORDER BY PhotoType, CreatedDate;

    -- Payments & Transactions
    SELECT 
        p.PaymentID,
        p.Amount,
        p.CurrencyCode,
        p.PaymentMethod,
        p.GatewayName,
        p.GatewayReference,
        p.PaymentStatus,
        p.PaidDate,
        p.CreatedDate
    FROM dbo.Payments p
    WHERE p.BookingID = @BookingID;

    -- Review (if any)
    SELECT 
        ReviewID,
        Rating,
        ReviewText,
        IsPublished,
        CreatedDate
    FROM dbo.Reviews
    WHERE BookingID = @BookingID;

    -- Status Audit History
    SELECT 
        h.HistoryID,
        h.OldStatus,
        h.NewStatus,
        u.LoginID AS ChangedByLogin,
        h.Remarks,
        h.ChangedDate
    FROM dbo.BookingStatusHistory h
    INNER JOIN dbo.Users u ON h.ChangedByUserID = u.UserID
    WHERE h.BookingID = @BookingID
    ORDER BY h.ChangedDate ASC;
END;
GO

PRINT 'Creating Organizer API Stored Procedures...';
GO

-- 11. Get Available Jobs Pool for an Organizer
-- Checks: Organizer is approved/active, matches service skills, matches service area
CREATE OR ALTER PROCEDURE dbo.sp_GetOrganizerAvailableJobs
    @OrganizerID BIGINT
AS
BEGIN
    SET NOCOUNT ON;

    -- Verify Organizer is approved
    IF NOT EXISTS (
        SELECT 1 FROM dbo.Organizers 
        WHERE OrganizerID = @OrganizerID 
          AND ApprovalStatus = N'Approved' 
          AND VerificationStatus = N'Approved' 
          AND IsActive = 1
    )
    BEGIN
        SELECT N'Organizer is not approved or is inactive.' AS Message;
        RETURN;
    END

    SELECT 
        b.BookingID,
        b.BookingDate,
        b.StartTime,
        b.EstimatedDuration,
        b.Status,
        b.TotalAmount,
        b.CustomerNotes,
        s.ServiceID,
        s.ServiceName,
        cat.CategoryName,
        a.City,
        a.PostalCode,
        a.AddressLine1,
        c.FullName AS CustomerName
    FROM dbo.Bookings b
    INNER JOIN dbo.Services s ON b.ServiceID = s.ServiceID
    INNER JOIN dbo.ServiceCategories cat ON s.CategoryID = cat.CategoryID
    INNER JOIN dbo.Addresses a ON b.AddressID = a.AddressID
    INNER JOIN dbo.Customers c ON b.CustomerID = c.CustomerID
    -- Must have matching skill
    INNER JOIN dbo.OrganizerSkills os ON os.OrganizerID = @OrganizerID AND os.ServiceID = s.ServiceID AND os.IsActive = 1
    -- Must serve the customer's area (matching City or ServiceArea)
    INNER JOIN dbo.OrganizerServiceAreas osa ON osa.OrganizerID = @OrganizerID AND osa.IsActive = 1
    INNER JOIN dbo.ServiceAreas sa ON osa.ServiceAreaID = sa.ServiceAreaID AND (sa.City = a.City OR sa.PostalCode = a.PostalCode)
    WHERE b.Status IN (N'Requested', N'Offered')
      AND (b.OrganizerID IS NULL OR b.OrganizerID = @OrganizerID)
      AND b.BookingDate >= CAST(SYSUTCDATETIME() AS DATE)
    ORDER BY b.BookingDate ASC, b.StartTime ASC;
END;
GO

-- 12. Organizer Accept Job
CREATE OR ALTER PROCEDURE dbo.sp_OrganizerAcceptJob
    @OrganizerID BIGINT,
    @BookingID   BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- 1. Validate Organizer is approved
        IF NOT EXISTS (
            SELECT 1 FROM dbo.Organizers 
            WHERE OrganizerID = @OrganizerID 
              AND ApprovalStatus = N'Approved' 
              AND IsActive = 1
        )
        BEGIN
            RAISERROR(N'Only approved and active organizers can accept jobs.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- 2. Validate Booking State (Must be Requested or Offered)
        DECLARE @CurrentStatus NVARCHAR(40), @CurrentOrganizer BIGINT, @CustomerID BIGINT;
        SELECT 
            @CurrentStatus = Status,
            @CurrentOrganizer = OrganizerID,
            @CustomerID = CustomerID
        FROM dbo.Bookings WITH (UPDLOCK, ROWLOCK)
        WHERE BookingID = @BookingID;

        IF @CurrentStatus IS NULL
        BEGIN
            RAISERROR(N'Booking not found.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        IF @CurrentStatus NOT IN (N'Requested', N'Offered')
        BEGIN
            RAISERROR(N'Job is no longer available for acceptance.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        IF @CurrentOrganizer IS NOT NULL AND @CurrentOrganizer <> @OrganizerID
        BEGIN
            RAISERROR(N'Job has already been assigned to another organizer.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- 3. Update Booking
        UPDATE dbo.Bookings
        SET 
            OrganizerID = @OrganizerID,
            Status = N'Accepted',
            UpdatedDate = SYSUTCDATETIME()
        WHERE BookingID = @BookingID;

        -- 4. Audit Log
        DECLARE @OrganizerUserID BIGINT = (SELECT UserID FROM dbo.Organizers WHERE OrganizerID = @OrganizerID);
        INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
        VALUES (@BookingID, @CurrentStatus, N'Accepted', @OrganizerUserID, N'Job accepted by organizer', SYSUTCDATETIME());

        -- 5. Notify Customer
        DECLARE @CustomerUserID BIGINT = (SELECT UserID FROM dbo.Customers WHERE CustomerID = @CustomerID);
        DECLARE @OrganizerName NVARCHAR(150) = (SELECT FullName FROM dbo.Organizers WHERE OrganizerID = @OrganizerID);
        INSERT INTO dbo.Notifications (UserID, Title, Message, NotificationType, ReferenceID, IsRead, SentDate, CreatedDate)
        VALUES (
            @CustomerUserID, 
            N'Organizer Assigned', 
            CONCAT(@OrganizerName, N' has accepted your booking!'), 
            N'OrganizerAccepted', 
            @BookingID, 0, SYSUTCDATETIME(), SYSUTCDATETIME()
        );

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- 13. Update Booking Lifecycle Status (Controlled state transitions)
CREATE OR ALTER PROCEDURE dbo.sp_UpdateBookingStatus
    @BookingID      BIGINT,
    @NewStatus      NVARCHAR(40),
    @ActionByUserID BIGINT,
    @Remarks        NVARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        DECLARE @OldStatus NVARCHAR(40), @CustomerID BIGINT, @OrganizerID BIGINT;
        SELECT 
            @OldStatus = Status,
            @CustomerID = CustomerID,
            @OrganizerID = OrganizerID
        FROM dbo.Bookings WITH (UPDLOCK, ROWLOCK)
        WHERE BookingID = @BookingID;

        IF @OldStatus IS NULL
        BEGIN
            RAISERROR(N'Booking does not exist.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Validate lifecycle sequence:
        -- Requested -> Offered -> Accepted -> OnTheWay -> Arrived -> Started -> InProgress -> Completed -> CustomerConfirmed -> Paid
        -- Plus terminal states: Cancelled, Rescheduled, Rejected, Disputed
        DECLARE @IsValidTransition BIT = 0;

        IF @OldStatus = N'Requested' AND @NewStatus IN (N'Offered', N'Accepted', N'Cancelled', N'Expired') SET @IsValidTransition = 1;
        ELSE IF @OldStatus = N'Offered' AND @NewStatus IN (N'Accepted', N'Rejected', N'Cancelled', N'Expired') SET @IsValidTransition = 1;
        ELSE IF @OldStatus = N'Accepted' AND @NewStatus IN (N'OnTheWay', N'Rescheduled', N'Cancelled') SET @IsValidTransition = 1;
        ELSE IF @OldStatus = N'OnTheWay' AND @NewStatus IN (N'Arrived', N'Cancelled') SET @IsValidTransition = 1;
        ELSE IF @OldStatus = N'Arrived' AND @NewStatus IN (N'Started', N'Cancelled') SET @IsValidTransition = 1;
        ELSE IF @OldStatus = N'Started' AND @NewStatus IN (N'InProgress', N'Completed', N'Disputed') SET @IsValidTransition = 1;
        ELSE IF @OldStatus = N'InProgress' AND @NewStatus IN (N'Completed', N'Disputed') SET @IsValidTransition = 1;
        ELSE IF @OldStatus = N'Completed' AND @NewStatus IN (N'CustomerConfirmed', N'Paid', N'Disputed') SET @IsValidTransition = 1;
        ELSE IF @OldStatus = N'CustomerConfirmed' AND @NewStatus IN (N'Paid', N'Disputed') SET @IsValidTransition = 1;
        ELSE IF @NewStatus IN (N'Cancelled', N'Disputed') SET @IsValidTransition = 1; -- Administrative overrides

        IF @IsValidTransition = 0
        BEGIN
            RAISERROR(N'Invalid booking state transition from %s to %s.', 16, 1, @OldStatus, @NewStatus);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Perform Status Update
        UPDATE dbo.Bookings
        SET 
            Status = @NewStatus,
            UpdatedDate = SYSUTCDATETIME()
        WHERE BookingID = @BookingID;

        -- Audit Log
        INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
        VALUES (@BookingID, @OldStatus, @NewStatus, @ActionByUserID, COALESCE(@Remarks, CONCAT(N'Transition to ', @NewStatus)), SYSUTCDATETIME());

        -- Notify customer or organizer based on state
        DECLARE @CustomerUserID BIGINT = (SELECT UserID FROM dbo.Customers WHERE CustomerID = @CustomerID);
        DECLARE @NotifTitle NVARCHAR(200) = CONCAT(N'Booking ', @NewStatus);
        DECLARE @NotifMessage NVARCHAR(1000) = CONCAT(N'Your booking #', CAST(@BookingID AS NVARCHAR(20)), N' is now ', @NewStatus);

        INSERT INTO dbo.Notifications (UserID, Title, Message, NotificationType, ReferenceID, IsRead, SentDate, CreatedDate)
        VALUES (@CustomerUserID, @NotifTitle, @NotifMessage, N'ServiceStarted', @BookingID, 0, SYSUTCDATETIME(), SYSUTCDATETIME());

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

-- 14. Process Payment and Automatic Payout Calculation
CREATE OR ALTER PROCEDURE dbo.sp_ProcessPaymentSuccess
    @BookingID        BIGINT,
    @CustomerID       BIGINT,
    @Amount           DECIMAL(18,2),
    @CurrencyCode     CHAR(3),
    @PaymentMethod    NVARCHAR(50),
    @GatewayName      NVARCHAR(100),
    @GatewayReference NVARCHAR(250),
    @NewPaymentID     BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- Insert Payment Record
        INSERT INTO dbo.Payments (
            BookingID, CustomerID, Amount, CurrencyCode,
            PaymentMethod, GatewayName, GatewayReference,
            PaymentStatus, PaidDate, CreatedDate
        )
        VALUES (
            @BookingID, @CustomerID, @Amount, @CurrencyCode,
            @PaymentMethod, @GatewayName, @GatewayReference,
            N'Success', SYSUTCDATETIME(), SYSUTCDATETIME()
        );

        SET @NewPaymentID = SCOPE_IDENTITY();

        -- Insert Audit Transaction
        INSERT INTO dbo.PaymentTransactions (PaymentID, GatewayReference, TransactionType, Amount, Status, CreatedDate)
        VALUES (@NewPaymentID, @GatewayReference, N'Capture', @Amount, N'Success', SYSUTCDATETIME());

        -- Update Booking Payment Status
        UPDATE dbo.Bookings
        SET 
            PaymentStatus = N'Success',
            Status = CASE WHEN Status IN (N'Completed', N'CustomerConfirmed') THEN N'Paid' ELSE Status END,
            UpdatedDate = SYSUTCDATETIME()
        WHERE BookingID = @BookingID;

        -- Automatically calculate Organizer Payout if Organizer is assigned
        DECLARE @OrganizerID BIGINT;
        SELECT @OrganizerID = OrganizerID FROM dbo.Bookings WHERE BookingID = @BookingID;

        IF @OrganizerID IS NOT NULL
        BEGIN
            -- Marketplace standard commission: 15% Platform fee, 85% Net Payout
            DECLARE @PlatformFee DECIMAL(18,2) = ROUND(@Amount * 0.15, 2);
            DECLARE @NetAmount DECIMAL(18,2) = @Amount - @PlatformFee;

            IF NOT EXISTS (SELECT 1 FROM dbo.Payouts WHERE BookingID = @BookingID)
            BEGIN
                INSERT INTO dbo.Payouts (
                    OrganizerID, BookingID, GrossAmount, PlatformFee, NetAmount,
                    PayoutStatus, PayoutReference, CreatedDate
                )
                VALUES (
                    @OrganizerID, @BookingID, @Amount, @PlatformFee, @NetAmount,
                    N'Pending', CONCAT(N'PAYOUT-BKG-', CAST(@BookingID AS NVARCHAR(20))), SYSUTCDATETIME()
                );
            END
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

PRINT 'Creating Admin Dashboard & Management Stored Procedures...';
GO

-- 15. Admin Dashboard Summary Metrics
CREATE OR ALTER PROCEDURE dbo.sp_AdminGetDashboardSummary
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        (SELECT COUNT(1) FROM dbo.Customers WHERE Status = N'Active') AS TotalActiveCustomers,
        (SELECT COUNT(1) FROM dbo.Organizers WHERE ApprovalStatus = N'Approved' AND IsActive = 1) AS TotalApprovedOrganizers,
        (SELECT COUNT(1) FROM dbo.Organizers WHERE ApprovalStatus = N'Pending') AS PendingOrganizerApprovals,
        (SELECT COUNT(1) FROM dbo.Bookings WHERE Status IN (N'Requested', N'Offered')) AS OpenBookingsPool,
        (SELECT COUNT(1) FROM dbo.Bookings WHERE Status IN (N'Accepted', N'OnTheWay', N'Arrived', N'Started', N'InProgress')) AS InProgressBookings,
        (SELECT COUNT(1) FROM dbo.Bookings WHERE Status IN (N'Completed', N'CustomerConfirmed', N'Paid')) AS TotalCompletedBookings,
        (SELECT COALESCE(SUM(TotalAmount), 0.00) FROM dbo.Bookings WHERE PaymentStatus = N'Success') AS GrossMarketplaceVolume,
        (SELECT COALESCE(SUM(ServiceFee), 0.00) FROM dbo.Bookings WHERE PaymentStatus = N'Success') AS TotalPlatformFeesEarned,
        (SELECT COUNT(1) FROM dbo.SupportTickets WHERE Status IN (N'Open', N'InReview')) AS OpenSupportTickets;
END;
GO

-- 16. Admin Approve/Reject Organizer
CREATE OR ALTER PROCEDURE dbo.sp_AdminReviewOrganizer
    @OrganizerID      BIGINT,
    @NewApprovalStatus NVARCHAR(30), -- 'Approved', 'Rejected', 'Suspended'
    @AdminUserID      BIGINT
AS
BEGIN
    SET NOCOUNT ON;

    IF @NewApprovalStatus NOT IN (N'Approved', N'Rejected', N'Suspended')
    BEGIN
        RAISERROR(N'Invalid approval status.', 16, 1);
        RETURN;
    END

    UPDATE dbo.Organizers
    SET 
        ApprovalStatus = @NewApprovalStatus,
        VerificationStatus = CASE WHEN @NewApprovalStatus = N'Approved' THEN N'Approved' ELSE VerificationStatus END,
        UpdatedDate = SYSUTCDATETIME()
    WHERE OrganizerID = @OrganizerID;

    -- Notify Organizer
    DECLARE @OrganizerUserID BIGINT = (SELECT UserID FROM dbo.Organizers WHERE OrganizerID = @OrganizerID);
    INSERT INTO dbo.Notifications (UserID, Title, Message, NotificationType, ReferenceID, IsRead, SentDate, CreatedDate)
    VALUES (
        @OrganizerUserID,
        CONCAT(N'Account ', @NewApprovalStatus),
        CONCAT(N'Your organizer profile application has been ', @NewApprovalStatus, N'.'),
        N'AdminMessage',
        @OrganizerID, 0, SYSUTCDATETIME(), SYSUTCDATETIME()
    );
END;
GO

PRINT 'Stored procedures and functions created successfully.';
GO
