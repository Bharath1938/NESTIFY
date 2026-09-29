-- ============================================================================
-- Script: 02_create_tables.sql
-- Description: Creates all 22 tables for Nestify Home Organization Marketplace
--              in strict dependency order under the dbo schema.
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
GO

PRINT 'Starting table creation in dependency order...';
GO

-- 1. Users Table (Core Authentication)
IF OBJECT_ID(N'dbo.Users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Users
    (
        UserID          BIGINT IDENTITY(1,1) NOT NULL,
        LoginID         NVARCHAR(150)        NOT NULL,
        PasswordHash    NVARCHAR(500)        NOT NULL,
        Role            NVARCHAR(30)         NOT NULL,
        IsActive        BIT                  NOT NULL CONSTRAINT DF_Users_IsActive DEFAULT (1),
        CreatedDate     DATETIME2(7)         NOT NULL CONSTRAINT DF_Users_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate     DATETIME2(7)         NULL,
        LastLoginDate   DATETIME2(7)         NULL,
        CONSTRAINT PK_Users PRIMARY KEY CLUSTERED (UserID)
    );
    PRINT 'Created table: dbo.Users';
END
GO

-- 2. Customers Table
IF OBJECT_ID(N'dbo.Customers', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Customers
    (
        CustomerID      BIGINT IDENTITY(1,1) NOT NULL,
        UserID          BIGINT               NOT NULL,
        FullName        NVARCHAR(150)        NOT NULL,
        Phone           NVARCHAR(30)         NOT NULL,
        Email           NVARCHAR(150)        NULL,
        ProfilePhotoURL NVARCHAR(500)        NULL,
        Status          NVARCHAR(30)         NOT NULL CONSTRAINT DF_Customers_Status DEFAULT (N'Active'),
        CreatedDate     DATETIME2(7)         NOT NULL CONSTRAINT DF_Customers_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate     DATETIME2(7)         NULL,
        CONSTRAINT PK_Customers PRIMARY KEY CLUSTERED (CustomerID)
    );
    PRINT 'Created table: dbo.Customers';
END
GO

-- 3. Organizers Table
IF OBJECT_ID(N'dbo.Organizers', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Organizers
    (
        OrganizerID        BIGINT IDENTITY(1,1) NOT NULL,
        UserID             BIGINT               NOT NULL,
        FullName           NVARCHAR(150)        NOT NULL,
        Phone              NVARCHAR(30)         NOT NULL,
        Email              NVARCHAR(150)        NULL,
        ProfilePhotoURL    NVARCHAR(500)        NULL,
        ExperienceYears    DECIMAL(5,2)         NULL,
        VerificationStatus NVARCHAR(30)         NOT NULL CONSTRAINT DF_Organizers_VerificationStatus DEFAULT (N'Pending'),
        ApprovalStatus     NVARCHAR(30)         NOT NULL CONSTRAINT DF_Organizers_ApprovalStatus DEFAULT (N'Pending'),
        Rating             DECIMAL(3,2)         NOT NULL CONSTRAINT DF_Organizers_Rating DEFAULT (0.00),
        CompletedJobs      INT                  NOT NULL CONSTRAINT DF_Organizers_CompletedJobs DEFAULT (0),
        IsActive           BIT                  NOT NULL CONSTRAINT DF_Organizers_IsActive DEFAULT (1),
        CreatedDate        DATETIME2(7)         NOT NULL CONSTRAINT DF_Organizers_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate        DATETIME2(7)         NULL,
        CONSTRAINT PK_Organizers PRIMARY KEY CLUSTERED (OrganizerID)
    );
    PRINT 'Created table: dbo.Organizers';
END
GO

-- 4. ServiceCategories Table
IF OBJECT_ID(N'dbo.ServiceCategories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ServiceCategories
    (
        CategoryID      BIGINT IDENTITY(1,1) NOT NULL,
        CategoryName    NVARCHAR(100)        NOT NULL,
        Description     NVARCHAR(500)        NULL,
        ImageURL        NVARCHAR(500)        NULL,
        DisplayOrder    INT                  NOT NULL CONSTRAINT DF_ServiceCategories_DisplayOrder DEFAULT (0),
        IsActive        BIT                  NOT NULL CONSTRAINT DF_ServiceCategories_IsActive DEFAULT (1),
        CreatedDate     DATETIME2(7)         NOT NULL CONSTRAINT DF_ServiceCategories_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate     DATETIME2(7)         NULL,
        CONSTRAINT PK_ServiceCategories PRIMARY KEY CLUSTERED (CategoryID)
    );
    PRINT 'Created table: dbo.ServiceCategories';
END
GO

-- 5. Services Table
IF OBJECT_ID(N'dbo.Services', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Services
    (
        ServiceID           BIGINT IDENTITY(1,1) NOT NULL,
        CategoryID          BIGINT               NOT NULL,
        ServiceName         NVARCHAR(150)        NOT NULL,
        Description         NVARCHAR(MAX)        NULL,
        IncludedDetails     NVARCHAR(MAX)        NULL,
        ExcludedDetails     NVARCHAR(MAX)        NULL,
        StartingPrice       DECIMAL(18,2)        NOT NULL,
        EstimatedDuration   DECIMAL(10,2)        NULL,
        ImageURL            NVARCHAR(500)        NULL,
        IsActive            BIT                  NOT NULL CONSTRAINT DF_Services_IsActive DEFAULT (1),
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_Services_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate         DATETIME2(7)         NULL,
        CONSTRAINT PK_Services PRIMARY KEY CLUSTERED (ServiceID)
    );
    PRINT 'Created table: dbo.Services';
END
GO

-- 6. OrganizerSkills Table
IF OBJECT_ID(N'dbo.OrganizerSkills', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.OrganizerSkills
    (
        OrganizerSkillID    BIGINT IDENTITY(1,1) NOT NULL,
        OrganizerID         BIGINT               NOT NULL,
        ServiceID           BIGINT               NOT NULL,
        ExperienceYears     DECIMAL(5,2)         NULL,
        IsActive            BIT                  NOT NULL CONSTRAINT DF_OrganizerSkills_IsActive DEFAULT (1),
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_OrganizerSkills_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_OrganizerSkills PRIMARY KEY CLUSTERED (OrganizerSkillID)
    );
    PRINT 'Created table: dbo.OrganizerSkills';
END
GO

-- 7. ServiceAreas Table
IF OBJECT_ID(N'dbo.ServiceAreas', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ServiceAreas
    (
        ServiceAreaID       BIGINT IDENTITY(1,1) NOT NULL,
        AreaName            NVARCHAR(150)        NOT NULL,
        City                NVARCHAR(100)        NOT NULL,
        State               NVARCHAR(100)        NULL,
        PostalCode          NVARCHAR(20)         NULL,
        Latitude            DECIMAL(10,7)        NULL,
        Longitude           DECIMAL(10,7)        NULL,
        IsActive            BIT                  NOT NULL CONSTRAINT DF_ServiceAreas_IsActive DEFAULT (1),
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_ServiceAreas_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_ServiceAreas PRIMARY KEY CLUSTERED (ServiceAreaID)
    );
    PRINT 'Created table: dbo.ServiceAreas';
END
GO

-- 8. OrganizerServiceAreas Table
IF OBJECT_ID(N'dbo.OrganizerServiceAreas', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.OrganizerServiceAreas
    (
        OrganizerServiceAreaID BIGINT IDENTITY(1,1) NOT NULL,
        OrganizerID            BIGINT               NOT NULL,
        ServiceAreaID          BIGINT               NOT NULL,
        IsActive               BIT                  NOT NULL CONSTRAINT DF_OrganizerServiceAreas_IsActive DEFAULT (1),
        CreatedDate            DATETIME2(7)         NOT NULL CONSTRAINT DF_OrganizerServiceAreas_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_OrganizerServiceAreas PRIMARY KEY CLUSTERED (OrganizerServiceAreaID)
    );
    PRINT 'Created table: dbo.OrganizerServiceAreas';
END
GO

-- 9. OrganizerAvailability Table
IF OBJECT_ID(N'dbo.OrganizerAvailability', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.OrganizerAvailability
    (
        AvailabilityID      BIGINT IDENTITY(1,1) NOT NULL,
        OrganizerID         BIGINT               NOT NULL,
        AvailabilityDate    DATE                 NULL,
        DayOfWeek           TINYINT              NULL,
        StartTime           TIME(0)              NOT NULL,
        EndTime             TIME(0)              NOT NULL,
        IsAvailable         BIT                  NOT NULL CONSTRAINT DF_OrganizerAvailability_IsAvailable DEFAULT (1),
        IsBlocked           BIT                  NOT NULL CONSTRAINT DF_OrganizerAvailability_IsBlocked DEFAULT (0),
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_OrganizerAvailability_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate         DATETIME2(7)         NULL,
        CONSTRAINT PK_OrganizerAvailability PRIMARY KEY CLUSTERED (AvailabilityID)
    );
    PRINT 'Created table: dbo.OrganizerAvailability';
END
GO

-- 10. Addresses Table
IF OBJECT_ID(N'dbo.Addresses', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Addresses
    (
        AddressID           BIGINT IDENTITY(1,1) NOT NULL,
        CustomerID          BIGINT               NOT NULL,
        AddressLabel        NVARCHAR(50)         NOT NULL,
        AddressLine1        NVARCHAR(250)        NOT NULL,
        AddressLine2        NVARCHAR(250)        NULL,
        City                NVARCHAR(100)        NOT NULL,
        State               NVARCHAR(100)        NULL,
        PostalCode          NVARCHAR(20)         NULL,
        Landmark            NVARCHAR(250)        NULL,
        Latitude            DECIMAL(10,7)        NULL,
        Longitude           DECIMAL(10,7)        NULL,
        IsDefault           BIT                  NOT NULL CONSTRAINT DF_Addresses_IsDefault DEFAULT (0),
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_Addresses_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate         DATETIME2(7)         NULL,
        CONSTRAINT PK_Addresses PRIMARY KEY CLUSTERED (AddressID)
    );
    PRINT 'Created table: dbo.Addresses';
END
GO

-- 11. Bookings Table (Core Marketplace Transaction)
IF OBJECT_ID(N'dbo.Bookings', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Bookings
    (
        BookingID           BIGINT IDENTITY(1,1) NOT NULL,
        CustomerID          BIGINT               NOT NULL,
        OrganizerID         BIGINT               NULL,
        ServiceID           BIGINT               NOT NULL,
        AddressID           BIGINT               NOT NULL,
        BookingDate         DATE                 NOT NULL,
        StartTime           TIME(0)              NOT NULL,
        EstimatedDuration   DECIMAL(10,2)        NULL,
        Status              NVARCHAR(40)         NOT NULL,
        PaymentStatus       NVARCHAR(40)         NOT NULL,
        BaseAmount          DECIMAL(18,2)        NOT NULL CONSTRAINT DF_Bookings_BaseAmount DEFAULT (0.00),
        ServiceFee          DECIMAL(18,2)        NOT NULL CONSTRAINT DF_Bookings_ServiceFee DEFAULT (0.00),
        DiscountAmount      DECIMAL(18,2)        NOT NULL CONSTRAINT DF_Bookings_DiscountAmount DEFAULT (0.00),
        TotalAmount         DECIMAL(18,2)        NOT NULL CONSTRAINT DF_Bookings_TotalAmount DEFAULT (0.00),
        CustomerNotes       NVARCHAR(MAX)        NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_Bookings_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate         DATETIME2(7)         NULL,
        CONSTRAINT PK_Bookings PRIMARY KEY CLUSTERED (BookingID)
    );
    PRINT 'Created table: dbo.Bookings';
END
GO

-- 12. BookingItems Table
IF OBJECT_ID(N'dbo.BookingItems', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BookingItems
    (
        BookingItemID       BIGINT IDENTITY(1,1) NOT NULL,
        BookingID           BIGINT               NOT NULL,
        ItemType            NVARCHAR(100)        NULL,
        ItemName            NVARCHAR(150)        NOT NULL,
        Quantity            DECIMAL(10,2)        NULL,
        UnitPrice           DECIMAL(18,2)        NULL,
        Amount              DECIMAL(18,2)        NULL,
        Notes               NVARCHAR(500)        NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_BookingItems_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_BookingItems PRIMARY KEY CLUSTERED (BookingItemID)
    );
    PRINT 'Created table: dbo.BookingItems';
END
GO

-- 13. BookingPhotos Table
IF OBJECT_ID(N'dbo.BookingPhotos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BookingPhotos
    (
        BookingPhotoID      BIGINT IDENTITY(1,1) NOT NULL,
        BookingID           BIGINT               NOT NULL,
        PhotoType           NVARCHAR(30)         NOT NULL,
        FileName            NVARCHAR(250)        NULL,
        FileURL             NVARCHAR(1000)       NOT NULL,
        StorageKey          NVARCHAR(500)        NULL,
        UploadedByUserID    BIGINT               NOT NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_BookingPhotos_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_BookingPhotos PRIMARY KEY CLUSTERED (BookingPhotoID)
    );
    PRINT 'Created table: dbo.BookingPhotos';
END
GO

-- 14. Payments Table
IF OBJECT_ID(N'dbo.Payments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Payments
    (
        PaymentID           BIGINT IDENTITY(1,1) NOT NULL,
        BookingID           BIGINT               NOT NULL,
        CustomerID          BIGINT               NOT NULL,
        Amount              DECIMAL(18,2)        NOT NULL,
        CurrencyCode        CHAR(3)              NOT NULL,
        PaymentMethod       NVARCHAR(50)         NULL,
        GatewayName         NVARCHAR(100)        NULL,
        GatewayReference    NVARCHAR(250)        NULL,
        PaymentStatus       NVARCHAR(40)         NOT NULL,
        PaidDate            DATETIME2(7)         NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_Payments_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate         DATETIME2(7)         NULL,
        CONSTRAINT PK_Payments PRIMARY KEY CLUSTERED (PaymentID)
    );
    PRINT 'Created table: dbo.Payments';
END
GO

-- 15. PaymentTransactions Table (Audit and gateway interactions)
IF OBJECT_ID(N'dbo.PaymentTransactions', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PaymentTransactions
    (
        TransactionID       BIGINT IDENTITY(1,1) NOT NULL,
        PaymentID           BIGINT               NOT NULL,
        GatewayReference    NVARCHAR(250)        NULL,
        TransactionType     NVARCHAR(50)         NOT NULL,
        Amount              DECIMAL(18,2)        NOT NULL,
        Status              NVARCHAR(40)         NOT NULL,
        ResponsePayload     NVARCHAR(MAX)        NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_PaymentTransactions_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_PaymentTransactions PRIMARY KEY CLUSTERED (TransactionID)
    );
    PRINT 'Created table: dbo.PaymentTransactions';
END
GO

-- 16. Payouts Table (Organizer Marketplace Earnings)
IF OBJECT_ID(N'dbo.Payouts', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Payouts
    (
        PayoutID            BIGINT IDENTITY(1,1) NOT NULL,
        OrganizerID         BIGINT               NOT NULL,
        BookingID           BIGINT               NOT NULL,
        GrossAmount         DECIMAL(18,2)        NOT NULL,
        PlatformFee         DECIMAL(18,2)        NOT NULL,
        NetAmount           DECIMAL(18,2)        NOT NULL,
        PayoutStatus        NVARCHAR(40)         NOT NULL,
        PayoutReference     NVARCHAR(250)        NULL,
        PayoutDate          DATETIME2(7)         NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_Payouts_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_Payouts PRIMARY KEY CLUSTERED (PayoutID)
    );
    PRINT 'Created table: dbo.Payouts';
END
GO

-- 17. Reviews Table
IF OBJECT_ID(N'dbo.Reviews', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Reviews
    (
        ReviewID            BIGINT IDENTITY(1,1) NOT NULL,
        BookingID           BIGINT               NOT NULL,
        CustomerID          BIGINT               NOT NULL,
        OrganizerID         BIGINT               NOT NULL,
        Rating              TINYINT              NOT NULL,
        ReviewText          NVARCHAR(2000)       NULL,
        IsPublished         BIT                  NOT NULL CONSTRAINT DF_Reviews_IsPublished DEFAULT (1),
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_Reviews_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate         DATETIME2(7)         NULL,
        CONSTRAINT PK_Reviews PRIMARY KEY CLUSTERED (ReviewID)
    );
    PRINT 'Created table: dbo.Reviews';
END
GO

-- 18. Notifications Table
IF OBJECT_ID(N'dbo.Notifications', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Notifications
    (
        NotificationID      BIGINT IDENTITY(1,1) NOT NULL,
        UserID              BIGINT               NOT NULL,
        Title               NVARCHAR(200)        NOT NULL,
        Message             NVARCHAR(1000)       NOT NULL,
        NotificationType    NVARCHAR(50)         NOT NULL,
        ReferenceID         BIGINT               NULL,
        IsRead              BIT                  NOT NULL CONSTRAINT DF_Notifications_IsRead DEFAULT (0),
        SentDate            DATETIME2(7)         NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_Notifications_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_Notifications PRIMARY KEY CLUSTERED (NotificationID)
    );
    PRINT 'Created table: dbo.Notifications';
END
GO

-- 19. PromoCodes Table
IF OBJECT_ID(N'dbo.PromoCodes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PromoCodes
    (
        PromoCodeID         BIGINT IDENTITY(1,1) NOT NULL,
        Code                NVARCHAR(50)         NOT NULL,
        Description         NVARCHAR(250)        NULL,
        DiscountType        NVARCHAR(20)         NOT NULL,
        DiscountValue       DECIMAL(18,2)        NOT NULL,
        MinimumAmount       DECIMAL(18,2)        NULL,
        MaximumDiscount     DECIMAL(18,2)        NULL,
        ValidFrom           DATETIME2(7)         NOT NULL,
        ValidTo             DATETIME2(7)         NOT NULL,
        UsageLimit          INT                  NULL,
        UsedCount           INT                  NOT NULL CONSTRAINT DF_PromoCodes_UsedCount DEFAULT (0),
        IsActive            BIT                  NOT NULL CONSTRAINT DF_PromoCodes_IsActive DEFAULT (1),
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_PromoCodes_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate         DATETIME2(7)         NULL,
        CONSTRAINT PK_PromoCodes PRIMARY KEY CLUSTERED (PromoCodeID)
    );
    PRINT 'Created table: dbo.PromoCodes';
END
GO

-- 20. SupportTickets Table
IF OBJECT_ID(N'dbo.SupportTickets', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.SupportTickets
    (
        SupportTicketID     BIGINT IDENTITY(1,1) NOT NULL,
        CustomerID          BIGINT               NULL,
        OrganizerID         BIGINT               NULL,
        BookingID           BIGINT               NULL,
        Category            NVARCHAR(100)        NOT NULL,
        Description         NVARCHAR(MAX)        NOT NULL,
        Status              NVARCHAR(30)         NOT NULL CONSTRAINT DF_SupportTickets_Status DEFAULT (N'Open'),
        AssignedAdminID     BIGINT               NULL,
        ResolutionNotes     NVARCHAR(MAX)        NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_SupportTickets_CreatedDate DEFAULT (SYSUTCDATETIME()),
        UpdatedDate         DATETIME2(7)         NULL,
        ClosedDate          DATETIME2(7)         NULL,
        CONSTRAINT PK_SupportTickets PRIMARY KEY CLUSTERED (SupportTicketID)
    );
    PRINT 'Created table: dbo.SupportTickets';
END
GO

-- 21. OrganizerDocuments Table (Verification docs)
IF OBJECT_ID(N'dbo.OrganizerDocuments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.OrganizerDocuments
    (
        DocumentID          BIGINT IDENTITY(1,1) NOT NULL,
        OrganizerID         BIGINT               NOT NULL,
        DocumentType        NVARCHAR(50)         NOT NULL,
        FileURL             NVARCHAR(1000)       NOT NULL,
        VerificationStatus  NVARCHAR(30)         NOT NULL CONSTRAINT DF_OrganizerDocuments_VerificationStatus DEFAULT (N'Pending'),
        VerifiedBy          BIGINT               NULL,
        VerifiedDate        DATETIME2(7)         NULL,
        CreatedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_OrganizerDocuments_CreatedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_OrganizerDocuments PRIMARY KEY CLUSTERED (DocumentID)
    );
    PRINT 'Created table: dbo.OrganizerDocuments';
END
GO

-- 22. BookingStatusHistory Table (Audit trail)
IF OBJECT_ID(N'dbo.BookingStatusHistory', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BookingStatusHistory
    (
        HistoryID           BIGINT IDENTITY(1,1) NOT NULL,
        BookingID           BIGINT               NOT NULL,
        OldStatus           NVARCHAR(40)         NULL,
        NewStatus           NVARCHAR(40)         NOT NULL,
        ChangedByUserID     BIGINT               NOT NULL,
        Remarks             NVARCHAR(500)        NULL,
        ChangedDate         DATETIME2(7)         NOT NULL CONSTRAINT DF_BookingStatusHistory_ChangedDate DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT PK_BookingStatusHistory PRIMARY KEY CLUSTERED (HistoryID)
    );
    PRINT 'Created table: dbo.BookingStatusHistory';
END
GO

PRINT 'Table creation script completed successfully.';
GO
