-- ============================================================================
-- Script: 08_seed_data.sql
-- Description: Inserts production-aligned seed data including categories,
--              services, service areas, promo codes, demo customer/organizer/admin
--              accounts with SHA-512 hashed passwords, and a full end-to-end booking.
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

PRINT 'Seeding Service Categories...';
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- 1. Service Categories
MERGE INTO dbo.ServiceCategories AS target
USING (VALUES
    (1, N'Bedroom', N'Closets, dressers, master suites, and wardrobe styling.', N'https://images.unsplash.com/photo-1540518614846-7ede433c4ef7', 1, 1),
    (2, N'Kitchen', N'Pantries, cabinets, spice racks, and refrigerator zoning.', N'https://images.unsplash.com/photo-1556911220-e15b29be8c8f', 2, 1),
    (3, N'Storage', N'Attics, basements, garage shelving, and seasonal storage.', N'https://images.unsplash.com/photo-1595428774223-ef52624120d2', 3, 1),
    (4, N'Kids',    N'Nurseries, toy rooms, homework stations, and playroom cubbies.', N'https://images.unsplash.com/photo-1596461404969-9ae70f2830c1', 4, 1),
    (5, N'Living',  N'Living rooms, home offices, bookshelves, and entertainment centers.', N'https://images.unsplash.com/photo-1513694203232-719a280e022f', 5, 1),
    (6, N'Special', N'Full-home decluttering, move-in unpacking, and estate staging.', N'https://images.unsplash.com/photo-1600585154340-be6161a56a0c', 6, 1)
) AS source (CategoryID, CategoryName, Description, ImageURL, DisplayOrder, IsActive)
ON target.CategoryName = source.CategoryName
WHEN MATCHED THEN
    UPDATE SET 
        Description = source.Description,
        ImageURL = source.ImageURL,
        DisplayOrder = source.DisplayOrder,
        IsActive = source.IsActive,
        UpdatedDate = SYSUTCDATETIME()
WHEN NOT MATCHED THEN
    INSERT (CategoryName, Description, ImageURL, DisplayOrder, IsActive, CreatedDate)
    VALUES (source.CategoryName, source.Description, source.ImageURL, source.DisplayOrder, source.IsActive, SYSUTCDATETIME());
GO

PRINT 'Seeding Services...';
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- 2. Services
DECLARE @CatBedroom BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Bedroom');
DECLARE @CatKitchen BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Kitchen');
DECLARE @CatStorage BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Storage');
DECLARE @CatKids    BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Kids');
DECLARE @CatLiving  BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Living');
DECLARE @CatSpecial BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Special');

MERGE INTO dbo.Services AS target
USING (VALUES
    (@CatBedroom, N'Wardrobe Organization', 
     N'Transform your closet into a boutique experience with color coding, slim hangers, and folded shelf curation.',
     N'Emptying closet, sorting keep/donate/discard, folding/hanging layout, labelling bins',
     N'Offsite donation delivery, dry cleaning',
     120.00, 3.00, N'https://images.unsplash.com/photo-1558997519-83ea9252def8', 1),

    (@CatKitchen, N'Kitchen Organization', 
     N'Maximize counter space, optimize cooking zones, and organize drawers and cabinets for seamless meal prep.',
     N'Categorizing cookware, utensils, spice rack arrangement, drawer dividers setup',
     N'Major appliance scrubbing, plumbing work',
     150.00, 3.50, N'https://images.unsplash.com/photo-1556911220-e15b29be8c8f', 1),

    (@CatKitchen, N'Pantry Organization', 
     N'Decant dry goods into airtight jars, install lazy susans, and create easy-access snack stations.',
     N'Expiration date audit, decanting grains/snacks, bin containment, customized labels',
     N'Grocery replenishment costs',
     95.00, 2.50, N'https://images.unsplash.com/photo-1600585154526-990dced4db0d', 1),

    (@CatStorage, N'Storage Room Organization', 
     N'Heavy-duty shelf organization, weather-resistant totes, and labeled inventory for basements or attics.',
     N'Sorting seasonal decor, gear zoning, heavy tote stacking, digital inventory checklist',
     N'Hauling hazardous waste, structural carpentry',
     180.00, 4.00, N'https://images.unsplash.com/photo-1595428774223-ef52624120d2', 1),

    (@CatKids, N'Kids Room Organization', 
     N'Child-accessible toy rotation systems, color-coded cubbies, and streamlined clothing drawers.',
     N'Toy bin sorting, low-height labeling, book display wall, outgrown clothing purge',
     N'Deep toy sanitization/repair',
     110.00, 2.50, N'https://images.unsplash.com/photo-1596461404969-9ae70f2830c1', 1),

    (@CatLiving, N'Bookshelf Organization', 
     N'Aesthetic rainbow or genre-based bookshelf styling, memorabilia curation, and decluttering.',
     N'Book spine cleaning, genre/color grouping, accent decor placement, bookmark archiving',
     N'Rare book appraisal/restoration',
     85.00, 2.00, N'https://images.unsplash.com/photo-1513694203232-719a280e022f', 1),

    (@CatStorage, N'Shoe Rack Organization', 
     N'Clean and pair all footwear, install stackable shoe drop-boxes, and seasonal boot shaping.',
     N'Pair matching, clean wipe down, clear drop-front container sorting, seasonal rotation',
     N'Cobbler shoe repair',
     75.00, 1.50, N'https://images.unsplash.com/photo-1549298916-b41d501d3772', 1),

    (@CatSpecial, N'Full Home Organization', 
     N'Multi-room deep organization package tailored to whole-house resets with lead and assistant organizers.',
     N'Bedroom, kitchen, living, and storage areas complete overhaul with custom labeling system',
     N'Moving truck rental',
     450.00, 8.00, N'https://images.unsplash.com/photo-1600585154340-be6161a56a0c', 1),

    (@CatSpecial, N'Move-in Organization', 
     N'Unpack boxes and place every item in its optimal permanent home so you feel settled on day one.',
     N'Unpacking boxes, immediate kitchen/bedroom setup, packing paper disposal, pantry stocking',
     N'Heavy furniture lifting/assembly',
     320.00, 6.00, N'https://images.unsplash.com/photo-1600565193348-f74bd3c7ccdf', 1),

    (@CatSpecial, N'Move-out Organization', 
     N'Systematic packing, room-by-room categorization, color-coded box labeling for movers.',
     N'Fragile item wrapping, box itemization by room destination, keep vs purge triage',
     N'Long distance truck driving',
     280.00, 5.00, N'https://images.unsplash.com/photo-1600585152220-90363fe7e115', 1)
) AS source (CategoryID, ServiceName, Description, IncludedDetails, ExcludedDetails, StartingPrice, EstimatedDuration, ImageURL, IsActive)
ON target.ServiceName = source.ServiceName
WHEN MATCHED THEN
    UPDATE SET 
        CategoryID = source.CategoryID,
        Description = source.Description,
        IncludedDetails = source.IncludedDetails,
        ExcludedDetails = source.ExcludedDetails,
        StartingPrice = source.StartingPrice,
        EstimatedDuration = source.EstimatedDuration,
        ImageURL = source.ImageURL,
        IsActive = source.IsActive,
        UpdatedDate = SYSUTCDATETIME()
WHEN NOT MATCHED THEN
    INSERT (CategoryID, ServiceName, Description, IncludedDetails, ExcludedDetails, StartingPrice, EstimatedDuration, ImageURL, IsActive, CreatedDate)
    VALUES (source.CategoryID, source.ServiceName, source.Description, source.IncludedDetails, source.ExcludedDetails, source.StartingPrice, source.EstimatedDuration, source.ImageURL, source.IsActive, SYSUTCDATETIME());
GO

PRINT 'Seeding Service Areas...';
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- 3. Service Areas (Metro coverage)
MERGE INTO dbo.ServiceAreas AS target
USING (VALUES
    (N'Downtown Metro & Financial District', N'San Francisco', N'CA', N'94105', 37.7891720, -122.3992480, 1),
    (N'Mission & Pacific Heights',           N'San Francisco', N'CA', N'94115', 37.7925000, -122.4382000, 1),
    (N'Silicon Valley North (Palo Alto)',    N'Palo Alto',     N'CA', N'94301', 37.4418834, -122.1430195, 1),
    (N'East Bay (Oakland / Berkeley)',       N'Oakland',       N'CA', N'94612', 37.8043637, -122.2711137, 1),
    (N'South Bay (San Jose Downtown)',       N'San Jose',      N'CA', N'95113', 37.3382082, -121.8863286, 1)
) AS source (AreaName, City, State, PostalCode, Latitude, Longitude, IsActive)
ON target.AreaName = source.AreaName
WHEN NOT MATCHED THEN
    INSERT (AreaName, City, State, PostalCode, Latitude, Longitude, IsActive, CreatedDate)
    VALUES (source.AreaName, source.City, source.State, source.PostalCode, source.Latitude, source.Longitude, source.IsActive, SYSUTCDATETIME());
GO

PRINT 'Seeding Promo Codes...';
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- 4. Promo Codes
MERGE INTO dbo.PromoCodes AS target
USING (VALUES
    (N'NESTIFYWELCOME', N'Welcome 15% discount for first-time customers', N'Percentage', 15.00, 80.00, 50.00, SYSUTCDATETIME(), DATEADD(YEAR, 2, SYSUTCDATETIME()), 1000, 0, 1),
    (N'SPRINGCLEAN25',  N'Fixed $25 discount on any home organizing service', N'Fixed', 25.00, 100.00, 25.00, SYSUTCDATETIME(), DATEADD(YEAR, 1, SYSUTCDATETIME()), 500, 0, 1)
) AS source (Code, Description, DiscountType, DiscountValue, MinimumAmount, MaximumDiscount, ValidFrom, ValidTo, UsageLimit, UsedCount, IsActive)
ON target.Code = source.Code
WHEN NOT MATCHED THEN
    INSERT (Code, Description, DiscountType, DiscountValue, MinimumAmount, MaximumDiscount, ValidFrom, ValidTo, UsageLimit, UsedCount, IsActive, CreatedDate)
    VALUES (source.Code, source.Description, source.DiscountType, source.DiscountValue, source.MinimumAmount, source.MaximumDiscount, source.ValidFrom, source.ValidTo, source.UsageLimit, source.UsedCount, source.IsActive, SYSUTCDATETIME());
GO

PRINT 'Seeding Demo Accounts (Customer, Organizer, Admin)...';
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- 5. Demo Accounts with Cryptographically Salted Hashes
-- Common Salt: 'NestifySalt#2026$'
DECLARE @Salt NVARCHAR(100) = N'NestifySalt#2026$';

-- Passwords:
-- Customer:  'Nestify@123'
-- Organizer: 'Nestify@456'
-- Admin:     'Nestify@Admin2026!'

DECLARE @CustomerHash  NVARCHAR(500) = dbo.fn_HashPassword(N'Nestify@123', @Salt);
DECLARE @OrganizerHash NVARCHAR(500) = dbo.fn_HashPassword(N'Nestify@456', @Salt);
DECLARE @AdminHash     NVARCHAR(500) = dbo.fn_HashPassword(N'Nestify@Admin2026!', @Salt);

-- 5A. Insert/Update Demo Customer
DECLARE @CustomerUserID BIGINT;
SELECT @CustomerUserID = UserID FROM dbo.Users WHERE LoginID = N'customer@nestify.demo';

IF @CustomerUserID IS NULL
BEGIN
    INSERT INTO dbo.Users (LoginID, PasswordHash, Role, IsActive, CreatedDate)
    VALUES (N'customer@nestify.demo', @CustomerHash, N'Customer', 1, SYSUTCDATETIME());
    SET @CustomerUserID = SCOPE_IDENTITY();
END
ELSE
BEGIN
    UPDATE dbo.Users SET PasswordHash = @CustomerHash, Role = N'Customer', IsActive = 1 WHERE UserID = @CustomerUserID;
END

DECLARE @DemoCustomerID BIGINT;
SELECT @DemoCustomerID = CustomerID FROM dbo.Customers WHERE UserID = @CustomerUserID;

IF @DemoCustomerID IS NULL
BEGIN
    INSERT INTO dbo.Customers (UserID, FullName, Phone, Email, ProfilePhotoURL, Status, CreatedDate)
    VALUES (@CustomerUserID, N'Demo Customer', N'+1-555-0101', N'customer@nestify.demo', N'https://images.unsplash.com/photo-1494790108377-be9c29b29330', N'Active', SYSUTCDATETIME());
    SET @DemoCustomerID = SCOPE_IDENTITY();
END

-- Customer Demo Addresses (Home is default)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE CustomerID = @DemoCustomerID)
BEGIN
    INSERT INTO dbo.Addresses (CustomerID, AddressLabel, AddressLine1, AddressLine2, City, State, PostalCode, Landmark, Latitude, Longitude, IsDefault, CreatedDate)
    VALUES 
    (@DemoCustomerID, N'Home', N'450 Mission St', N'Apt 12B', N'San Francisco', N'CA', N'94105', N'Near Salesforce Tower', 37.7900140, -122.3998500, 1, SYSUTCDATETIME()),
    (@DemoCustomerID, N'Office Studio', N'220 Montgomery St', N'Suite 400', N'San Francisco', N'CA', N'94104', N'Mills Building', 37.7911000, -122.4025000, 0, SYSUTCDATETIME());
END

-- 5B. Insert/Update Demo Organizer
DECLARE @OrganizerUserID BIGINT;
SELECT @OrganizerUserID = UserID FROM dbo.Users WHERE LoginID = N'organizer@nestify.demo';

IF @OrganizerUserID IS NULL
BEGIN
    INSERT INTO dbo.Users (LoginID, PasswordHash, Role, IsActive, CreatedDate)
    VALUES (N'organizer@nestify.demo', @OrganizerHash, N'Organizer', 1, SYSUTCDATETIME());
    SET @OrganizerUserID = SCOPE_IDENTITY();
END
ELSE
BEGIN
    UPDATE dbo.Users SET PasswordHash = @OrganizerHash, Role = N'Organizer', IsActive = 1 WHERE UserID = @OrganizerUserID;
END

DECLARE @DemoOrganizerID BIGINT;
SELECT @DemoOrganizerID = OrganizerID FROM dbo.Organizers WHERE UserID = @OrganizerUserID;

IF @DemoOrganizerID IS NULL
BEGIN
    INSERT INTO dbo.Organizers (
        UserID, FullName, Phone, Email, ProfilePhotoURL, 
        ExperienceYears, VerificationStatus, ApprovalStatus, Rating, CompletedJobs, IsActive, CreatedDate
    )
    VALUES (
        @OrganizerUserID, N'Demo Organizer', N'+1-555-0202', N'organizer@nestify.demo', N'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2',
        5.50, N'Approved', N'Approved', 4.95, 38, 1, SYSUTCDATETIME()
    );
    SET @DemoOrganizerID = SCOPE_IDENTITY();
END
ELSE
BEGIN
    UPDATE dbo.Organizers 
    SET ApprovalStatus = N'Approved', VerificationStatus = N'Approved', IsActive = 1 
    WHERE OrganizerID = @DemoOrganizerID;
END

-- Demo Organizer Skills
DECLARE @SkillWardrobe BIGINT = (SELECT ServiceID FROM dbo.Services WHERE ServiceName = N'Wardrobe Organization');
DECLARE @SkillKitchen  BIGINT = (SELECT ServiceID FROM dbo.Services WHERE ServiceName = N'Kitchen Organization');
DECLARE @SkillPantry   BIGINT = (SELECT ServiceID FROM dbo.Services WHERE ServiceName = N'Pantry Organization');
DECLARE @SkillStorage  BIGINT = (SELECT ServiceID FROM dbo.Services WHERE ServiceName = N'Storage Room Organization');

IF NOT EXISTS (SELECT 1 FROM dbo.OrganizerSkills WHERE OrganizerID = @DemoOrganizerID)
BEGIN
    INSERT INTO dbo.OrganizerSkills (OrganizerID, ServiceID, ExperienceYears, IsActive, CreatedDate)
    VALUES 
    (@DemoOrganizerID, @SkillWardrobe, 5.50, 1, SYSUTCDATETIME()),
    (@DemoOrganizerID, @SkillKitchen, 4.00, 1, SYSUTCDATETIME()),
    (@DemoOrganizerID, @SkillPantry, 3.50, 1, SYSUTCDATETIME()),
    (@DemoOrganizerID, @SkillStorage, 3.00, 1, SYSUTCDATETIME());
END

-- Demo Organizer Service Areas
DECLARE @AreaDowntown BIGINT = (SELECT ServiceAreaID FROM dbo.ServiceAreas WHERE PostalCode = N'94105');
DECLARE @AreaMission  BIGINT = (SELECT ServiceAreaID FROM dbo.ServiceAreas WHERE PostalCode = N'94115');

IF NOT EXISTS (SELECT 1 FROM dbo.OrganizerServiceAreas WHERE OrganizerID = @DemoOrganizerID)
BEGIN
    INSERT INTO dbo.OrganizerServiceAreas (OrganizerID, ServiceAreaID, IsActive, CreatedDate)
    VALUES 
    (@DemoOrganizerID, @AreaDowntown, 1, SYSUTCDATETIME()),
    (@DemoOrganizerID, @AreaMission, 1, SYSUTCDATETIME());
END

-- Demo Organizer Availability (Mon-Fri 09:00 - 17:00 recurring, Friday blocked)
IF NOT EXISTS (SELECT 1 FROM dbo.OrganizerAvailability WHERE OrganizerID = @DemoOrganizerID)
BEGIN
    INSERT INTO dbo.OrganizerAvailability (OrganizerID, AvailabilityDate, DayOfWeek, StartTime, EndTime, IsAvailable, IsBlocked, CreatedDate)
    VALUES 
    (@DemoOrganizerID, NULL, 1, '09:00:00', '17:00:00', 1, 0, SYSUTCDATETIME()), -- Monday
    (@DemoOrganizerID, NULL, 2, '09:00:00', '17:00:00', 1, 0, SYSUTCDATETIME()), -- Tuesday
    (@DemoOrganizerID, NULL, 3, '09:00:00', '17:00:00', 1, 0, SYSUTCDATETIME()), -- Wednesday
    (@DemoOrganizerID, NULL, 4, '09:00:00', '17:00:00', 1, 0, SYSUTCDATETIME()), -- Thursday
    (@DemoOrganizerID, NULL, 5, '09:00:00', '17:00:00', 0, 1, SYSUTCDATETIME());  -- Friday Blocked
END

-- Demo Organizer Verification Documents
IF NOT EXISTS (SELECT 1 FROM dbo.OrganizerDocuments WHERE OrganizerID = @DemoOrganizerID)
BEGIN
    INSERT INTO dbo.OrganizerDocuments (OrganizerID, DocumentType, FileURL, VerificationStatus, VerifiedBy, VerifiedDate, CreatedDate)
    VALUES 
    (@DemoOrganizerID, N'Identity', N'https://storage.nestify.com/docs/organizer_demo_gov_id.pdf', N'Approved', 1, SYSUTCDATETIME(), SYSUTCDATETIME()),
    (@DemoOrganizerID, N'Certification', N'https://storage.nestify.com/docs/napo_cert_organizer.pdf', N'Approved', 1, SYSUTCDATETIME(), SYSUTCDATETIME());
END

-- 5C. Insert/Update Demo Admin
DECLARE @AdminUserID BIGINT;
SELECT @AdminUserID = UserID FROM dbo.Users WHERE LoginID = N'admin@nestify.demo';

IF @AdminUserID IS NULL
BEGIN
    INSERT INTO dbo.Users (LoginID, PasswordHash, Role, IsActive, CreatedDate)
    VALUES (N'admin@nestify.demo', @AdminHash, N'Admin', 1, SYSUTCDATETIME());
    SET @AdminUserID = SCOPE_IDENTITY();
END
ELSE
BEGIN
    UPDATE dbo.Users SET PasswordHash = @AdminHash, Role = N'Admin', IsActive = 1 WHERE UserID = @AdminUserID;
END
GO

PRINT 'Seeding Complete End-to-End Marketplace Booking Lifecycle...';
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- 6. End-to-End Walkthrough Booking Data
-- Scenario 1: Completed Booking demonstrating full lifecycle
-- Requested -> Offered -> Accepted -> OnTheWay -> Arrived -> Started -> InProgress -> Completed -> CustomerConfirmed -> Paid
DECLARE @CustomerUserID BIGINT = (SELECT UserID FROM dbo.Users WHERE LoginID = N'customer@nestify.demo');
DECLARE @DemoCustomerID BIGINT = (SELECT CustomerID FROM dbo.Customers WHERE UserID = @CustomerUserID);
DECLARE @OrganizerUserID BIGINT = (SELECT UserID FROM dbo.Users WHERE LoginID = N'organizer@nestify.demo');
DECLARE @DemoOrganizerID BIGINT = (SELECT OrganizerID FROM dbo.Organizers WHERE UserID = @OrganizerUserID);
DECLARE @AdminUserID BIGINT = (SELECT UserID FROM dbo.Users WHERE LoginID = N'admin@nestify.demo');
DECLARE @SkillWardrobe BIGINT = (SELECT ServiceID FROM dbo.Services WHERE ServiceName = N'Wardrobe Organization');
DECLARE @DemoAddressID BIGINT = (SELECT TOP 1 AddressID FROM dbo.Addresses WHERE CustomerID = @DemoCustomerID);

IF NOT EXISTS (SELECT 1 FROM dbo.Bookings WHERE CustomerID = @DemoCustomerID AND Status = N'Paid')
BEGIN
    DECLARE @CompletedBookingID BIGINT;

    INSERT INTO dbo.Bookings (
        CustomerID, OrganizerID, ServiceID, AddressID,
        BookingDate, StartTime, EstimatedDuration,
        Status, PaymentStatus,
        BaseAmount, ServiceFee, DiscountAmount, TotalAmount,
        CustomerNotes, CreatedDate
    )
    VALUES (
        @DemoCustomerID, @DemoOrganizerID, @SkillWardrobe, @DemoAddressID,
        DATEADD(DAY, -2, CAST(SYSUTCDATETIME() AS DATE)), '10:00:00', 3.00,
        N'Paid', N'Success',
        120.00, 18.00, 0.00, 138.00,
        N'Please help sort out winter jackets and organize shoes by color.', DATEADD(DAY, -3, SYSUTCDATETIME())
    );
    SET @CompletedBookingID = SCOPE_IDENTITY();

    -- Booking Items
    INSERT INTO dbo.BookingItems (BookingID, ItemType, ItemName, Quantity, UnitPrice, Amount, Notes, CreatedDate)
    VALUES 
    (@CompletedBookingID, N'Wardrobe', N'Master Bedroom Closet', 2.00, 50.00, 100.00, N'Two full closets', DATEADD(DAY, -3, SYSUTCDATETIME())),
    (@CompletedBookingID, N'Storage Totes', N'Clear Velvet Hanger Bundles', 3.00, 12.67, 38.00, N'Hanger replacements', DATEADD(DAY, -3, SYSUTCDATETIME()));

    -- Booking Photos (Request, Before, After)
    INSERT INTO dbo.BookingPhotos (BookingID, PhotoType, FileName, FileURL, StorageKey, UploadedByUserID, CreatedDate)
    VALUES 
    (@CompletedBookingID, N'Request', N'closet_initial_clutter.jpg', N'https://images.unsplash.com/photo-1540518614846-7ede433c4ef7', N'photos/bkg_1/req.jpg', @CustomerUserID, DATEADD(DAY, -3, SYSUTCDATETIME())),
    (@CompletedBookingID, N'Before',  N'closet_before_sorting.jpg', N'https://images.unsplash.com/photo-1558997519-83ea9252def8', N'photos/bkg_1/before.jpg', @OrganizerUserID, DATEADD(DAY, -2, SYSUTCDATETIME())),
    (@CompletedBookingID, N'After',   N'closet_after_organized.jpg', N'https://images.unsplash.com/photo-1584622650111-993a426fbf0a', N'photos/bkg_1/after.jpg', @OrganizerUserID, DATEADD(DAY, -2, SYSUTCDATETIME()));

    -- Payment & PaymentTransaction
    DECLARE @SamplePaymentID BIGINT;
    INSERT INTO dbo.Payments (
        BookingID, CustomerID, Amount, CurrencyCode,
        PaymentMethod, GatewayName, GatewayReference,
        PaymentStatus, PaidDate, CreatedDate
    )
    VALUES (
        @CompletedBookingID, @DemoCustomerID, 138.00, 'USD',
        N'CreditCard_Stripe', N'Stripe', N'ch_3N4yAb2eZvKYlo2C01234567',
        N'Success', DATEADD(DAY, -2, SYSUTCDATETIME()), DATEADD(DAY, -2, SYSUTCDATETIME())
    );
    SET @SamplePaymentID = SCOPE_IDENTITY();

    INSERT INTO dbo.PaymentTransactions (PaymentID, GatewayReference, TransactionType, Amount, Status, ResponsePayload, CreatedDate)
    VALUES (@SamplePaymentID, N'ch_3N4yAb2eZvKYlo2C01234567', N'Capture', 138.00, N'Success', N'{"status":"succeeded","card_brand":"Visa","last4":"4242"}', DATEADD(DAY, -2, SYSUTCDATETIME()));

    -- Organizer Payout
    INSERT INTO dbo.Payouts (OrganizerID, BookingID, GrossAmount, PlatformFee, NetAmount, PayoutStatus, PayoutReference, PayoutDate, CreatedDate)
    VALUES (@DemoOrganizerID, @CompletedBookingID, 138.00, 20.70, 117.30, N'Paid', N'ACH_PAYOUT_REF_987654', DATEADD(DAY, -1, SYSUTCDATETIME()), DATEADD(DAY, -2, SYSUTCDATETIME()));

    -- Customer Review
    INSERT INTO dbo.Reviews (BookingID, CustomerID, OrganizerID, Rating, ReviewText, IsPublished, CreatedDate)
    VALUES (@CompletedBookingID, @DemoCustomerID, @DemoOrganizerID, 5, N'Absolutely phenomenal experience! My closet looks like a luxury boutique now. Fast, respectful, and highly efficient.', 1, DATEADD(DAY, -1, SYSUTCDATETIME()));

    -- Complete Booking Status History Audit Trail
    INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
    VALUES 
    (@CompletedBookingID, NULL,               N'Requested',         @CustomerUserID,  N'Customer placed booking',                   DATEADD(HOUR, -50, SYSUTCDATETIME())),
    (@CompletedBookingID, N'Requested',        N'Offered',           @AdminUserID,     N'Dispatched to top qualified organizers',    DATEADD(HOUR, -48, SYSUTCDATETIME())),
    (@CompletedBookingID, N'Offered',          N'Accepted',          @OrganizerUserID, N'Organizer accepted the job',                DATEADD(HOUR, -47, SYSUTCDATETIME())),
    (@CompletedBookingID, N'Accepted',         N'OnTheWay',          @OrganizerUserID, N'Organizer traveling to customer address',  DATEADD(HOUR, -45, SYSUTCDATETIME())),
    (@CompletedBookingID, N'OnTheWay',         N'Arrived',           @OrganizerUserID, N'Organizer arrived at destination',         DATEADD(HOUR, -44, SYSUTCDATETIME())),
    (@CompletedBookingID, N'Arrived',          N'Started',           @OrganizerUserID, N'Initial assessment and setup commenced',    DATEADD(HOUR, -43, SYSUTCDATETIME())),
    (@CompletedBookingID, N'Started',          N'InProgress',        @OrganizerUserID, N'Sorting and categorization underway',       DATEADD(HOUR, -42, SYSUTCDATETIME())),
    (@CompletedBookingID, N'InProgress',       N'Completed',         @OrganizerUserID, N'Organizing complete, after photos uploaded',DATEADD(HOUR, -40, SYSUTCDATETIME())),
    (@CompletedBookingID, N'Completed',        N'CustomerConfirmed', @CustomerUserID,  N'Customer confirmed job completion',        DATEADD(HOUR, -39, SYSUTCDATETIME())),
    (@CompletedBookingID, N'CustomerConfirmed',N'Paid',              @CustomerUserID,  N'Payment finalized via Stripe',              DATEADD(HOUR, -38, SYSUTCDATETIME()));

    -- Notifications
    INSERT INTO dbo.Notifications (UserID, Title, Message, NotificationType, ReferenceID, IsRead, SentDate, CreatedDate)
    VALUES 
    (@CustomerUserID, N'Booking Completed', N'Your Wardrobe Organization has been completed.', N'ServiceCompleted', @CompletedBookingID, 1, DATEADD(HOUR, -40, SYSUTCDATETIME()), DATEADD(HOUR, -40, SYSUTCDATETIME())),
    (@CustomerUserID, N'Payment Successful', N'Payment of $138.00 was successfully processed.', N'PaymentSuccess', @CompletedBookingID, 1, DATEADD(HOUR, -38, SYSUTCDATETIME()), DATEADD(HOUR, -38, SYSUTCDATETIME())),
    (@CustomerUserID, N'Leave a Review', N'How was your experience with Demo Organizer?', N'ReviewReminder', @CompletedBookingID, 1, DATEADD(HOUR, -37, SYSUTCDATETIME()), DATEADD(HOUR, -37, SYSUTCDATETIME()));
END
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- Scenario 2: Active / InProgress Booking
DECLARE @CustomerUserID2 BIGINT = (SELECT UserID FROM dbo.Users WHERE LoginID = N'customer@nestify.demo');
DECLARE @OrganizerUserID2 BIGINT = (SELECT UserID FROM dbo.Users WHERE LoginID = N'organizer@nestify.demo');
DECLARE @DemoCustomerID2 BIGINT = (SELECT CustomerID FROM dbo.Customers WHERE UserID = @CustomerUserID2);
DECLARE @DemoOrganizerID2 BIGINT = (SELECT OrganizerID FROM dbo.Organizers WHERE UserID = @OrganizerUserID2);
DECLARE @DemoAddressID2 BIGINT = (SELECT TOP 1 AddressID FROM dbo.Addresses WHERE CustomerID = @DemoCustomerID2);
DECLARE @SkillKitchen2 BIGINT = (SELECT ServiceID FROM dbo.Services WHERE ServiceName = N'Kitchen Organization');

IF NOT EXISTS (SELECT 1 FROM dbo.Bookings WHERE CustomerID = @DemoCustomerID2 AND Status = N'InProgress')
BEGIN
    DECLARE @InProgressBookingID BIGINT;

    INSERT INTO dbo.Bookings (
        CustomerID, OrganizerID, ServiceID, AddressID,
        BookingDate, StartTime, EstimatedDuration,
        Status, PaymentStatus,
        BaseAmount, ServiceFee, DiscountAmount, TotalAmount,
        CustomerNotes, CreatedDate
    )
    VALUES (
        @DemoCustomerID2, @DemoOrganizerID2, @SkillKitchen2, @DemoAddressID2,
        CAST(SYSUTCDATETIME() AS DATE), '13:00:00', 3.50,
        N'InProgress', N'Pending',
        150.00, 22.50, 0.00, 172.50,
        N'Full reorganization of kitchen pantry and under-sink storage.', SYSUTCDATETIME()
    );
    SET @InProgressBookingID = SCOPE_IDENTITY();

    INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
    VALUES 
    (@InProgressBookingID, NULL,       N'Requested',  @CustomerUserID2,  N'Booking requested', SYSUTCDATETIME()),
    (@InProgressBookingID, N'Requested',N'Accepted',   @OrganizerUserID2, N'Organizer accepted job', SYSUTCDATETIME()),
    (@InProgressBookingID, N'Accepted', N'OnTheWay',   @OrganizerUserID2, N'Organizer en route', SYSUTCDATETIME()),
    (@InProgressBookingID, N'OnTheWay', N'Arrived',    @OrganizerUserID2, N'Organizer on site', SYSUTCDATETIME()),
    (@InProgressBookingID, N'Arrived',  N'InProgress', @OrganizerUserID2, N'Reorganizing spices and cabinets', SYSUTCDATETIME());
END
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- Scenario 3: Newly Requested Booking waiting in Open Jobs Pool
DECLARE @CustomerUserID3 BIGINT = (SELECT UserID FROM dbo.Users WHERE LoginID = N'customer@nestify.demo');
DECLARE @DemoCustomerID3 BIGINT = (SELECT CustomerID FROM dbo.Customers WHERE UserID = @CustomerUserID3);
DECLARE @DemoAddressID3 BIGINT = (SELECT TOP 1 AddressID FROM dbo.Addresses WHERE CustomerID = @DemoCustomerID3);
DECLARE @SkillPantry3 BIGINT = (SELECT ServiceID FROM dbo.Services WHERE ServiceName = N'Pantry Organization');

IF NOT EXISTS (SELECT 1 FROM dbo.Bookings WHERE CustomerID = @DemoCustomerID3 AND Status = N'Requested')
BEGIN
    INSERT INTO dbo.Bookings (
        CustomerID, OrganizerID, ServiceID, AddressID,
        BookingDate, StartTime, EstimatedDuration,
        Status, PaymentStatus,
        BaseAmount, ServiceFee, DiscountAmount, TotalAmount,
        CustomerNotes, CreatedDate
    )
    VALUES (
        @DemoCustomerID3, NULL, @SkillPantry3, @DemoAddressID3,
        DATEADD(DAY, 1, CAST(SYSUTCDATETIME() AS DATE)), '11:00:00', 2.50,
        N'Requested', N'Pending',
        95.00, 14.25, 14.25, 95.00,
        N'Pantry decanting project with NESTIFYWELCOME coupon applied.', SYSUTCDATETIME()
    );
END
GO

PRINT 'Seed data successfully loaded.';
GO
