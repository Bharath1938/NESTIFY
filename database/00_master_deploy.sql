-- ============================================================================
-- Script: 00_master_deploy.sql
-- Description: Master deployment script for Nestify Home Organization Marketplace.
--              Deploys NestifyDB, tables, constraints, indexes, views, triggers,
--              stored procedures, and seed data in a single transactional run.
-- Target Engine: Microsoft SQL Server 2019 / 2022 / 2025 / Azure SQL
-- Schema: dbo
-- ============================================================================

PRINT '============================================================================';
PRINT '  NESTIFY HOME ORGANIZATION MARKETPLACE - DATABASE DEPLOYMENT';
PRINT '============================================================================';

-- ----------------------------------------------------------------------------
-- STEP 1: CREATE DATABASE AND CONFIGURE CONCURRENCY SETTINGS
-- ----------------------------------------------------------------------------
USE [master];
GO

IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE [name] = N'NestifyDB')
BEGIN
    PRINT '[STEP 1] Creating database NestifyDB...';
    CREATE DATABASE [NestifyDB] COLLATE SQL_Latin1_General_CP1_CI_AS;
    PRINT '  -> Database NestifyDB created successfully.';
END
ELSE
BEGIN
    PRINT '[STEP 1] Database NestifyDB already exists.';
END
GO

USE [master];
GO

-- Configure Read Committed Snapshot Isolation (RCSI) for high-concurrency marketplace operations
IF EXISTS (SELECT 1 FROM sys.databases WHERE [name] = N'NestifyDB' AND is_read_committed_snapshot_on = 0)
BEGIN
    PRINT '  -> Enabling READ_COMMITTED_SNAPSHOT on NestifyDB...';
    ALTER DATABASE [NestifyDB] SET READ_COMMITTED_SNAPSHOT ON WITH ROLLBACK IMMEDIATE;
END
GO

IF EXISTS (SELECT 1 FROM sys.databases WHERE [name] = N'NestifyDB' AND snapshot_isolation_state = 0)
BEGIN
    PRINT '  -> Enabling ALLOW_SNAPSHOT_ISOLATION on NestifyDB...';
    ALTER DATABASE [NestifyDB] SET ALLOW_SNAPSHOT_ISOLATION ON;
END
GO

ALTER DATABASE [NestifyDB] SET AUTO_CREATE_STATISTICS ON;
ALTER DATABASE [NestifyDB] SET AUTO_UPDATE_STATISTICS ON;
ALTER DATABASE [NestifyDB] SET AUTO_UPDATE_STATISTICS_ASYNC ON;
GO

USE [NestifyDB];
GO

SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- ----------------------------------------------------------------------------
-- STEP 2: CREATE ALL 22 TABLES IN STRICT DEPENDENCY ORDER
-- ----------------------------------------------------------------------------
PRINT '[STEP 2] Creating Tables in Dependency Order...';

-- 1. Users
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
    PRINT '  -> Created dbo.Users';
END
GO

-- 2. Customers
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
    PRINT '  -> Created dbo.Customers';
END
GO

-- 3. Organizers
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
    PRINT '  -> Created dbo.Organizers';
END
GO

-- 4. ServiceCategories
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
    PRINT '  -> Created dbo.ServiceCategories';
END
GO

-- 5. Services
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
    PRINT '  -> Created dbo.Services';
END
GO

-- 6. OrganizerSkills
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
    PRINT '  -> Created dbo.OrganizerSkills';
END
GO

-- 7. ServiceAreas
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
    PRINT '  -> Created dbo.ServiceAreas';
END
GO

-- 8. OrganizerServiceAreas
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
    PRINT '  -> Created dbo.OrganizerServiceAreas';
END
GO

-- 9. OrganizerAvailability
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
    PRINT '  -> Created dbo.OrganizerAvailability';
END
GO

-- 10. Addresses
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
    PRINT '  -> Created dbo.Addresses';
END
GO

-- 11. Bookings
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
    PRINT '  -> Created dbo.Bookings';
END
GO

-- 12. BookingItems
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
    PRINT '  -> Created dbo.BookingItems';
END
GO

-- 13. BookingPhotos
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
    PRINT '  -> Created dbo.BookingPhotos';
END
GO

-- 14. Payments
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
    PRINT '  -> Created dbo.Payments';
END
GO

-- 15. PaymentTransactions
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
    PRINT '  -> Created dbo.PaymentTransactions';
END
GO

-- 16. Payouts
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
    PRINT '  -> Created dbo.Payouts';
END
GO

-- 17. Reviews
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
    PRINT '  -> Created dbo.Reviews';
END
GO

-- 18. Notifications
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
    PRINT '  -> Created dbo.Notifications';
END
GO

-- 19. PromoCodes
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
    PRINT '  -> Created dbo.PromoCodes';
END
GO

-- 20. SupportTickets
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
    PRINT '  -> Created dbo.SupportTickets';
END
GO

-- 21. OrganizerDocuments
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
    PRINT '  -> Created dbo.OrganizerDocuments';
END
GO

-- 22. BookingStatusHistory
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
    PRINT '  -> Created dbo.BookingStatusHistory';
END
GO

-- ----------------------------------------------------------------------------
-- STEP 3: CREATE CONSTRAINTS (UNIQUE, CHECK, AND FOREIGN KEYS)
-- ----------------------------------------------------------------------------
PRINT '[STEP 3] Applying Constraints...';

-- Unique Constraints
IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_Users_LoginID')
    ALTER TABLE dbo.Users ADD CONSTRAINT UQ_Users_LoginID UNIQUE NONCLUSTERED (LoginID);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_Customers_UserID')
    ALTER TABLE dbo.Customers ADD CONSTRAINT UQ_Customers_UserID UNIQUE NONCLUSTERED (UserID);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_Organizers_UserID')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT UQ_Organizers_UserID UNIQUE NONCLUSTERED (UserID);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_OrganizerSkills_Organizer_Service')
    ALTER TABLE dbo.OrganizerSkills ADD CONSTRAINT UQ_OrganizerSkills_Organizer_Service UNIQUE NONCLUSTERED (OrganizerID, ServiceID);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_OrganizerServiceAreas_Organizer_Area')
    ALTER TABLE dbo.OrganizerServiceAreas ADD CONSTRAINT UQ_OrganizerServiceAreas_Organizer_Area UNIQUE NONCLUSTERED (OrganizerID, ServiceAreaID);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_Reviews_BookingID')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT UQ_Reviews_BookingID UNIQUE NONCLUSTERED (BookingID);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_PromoCodes_Code')
    ALTER TABLE dbo.PromoCodes ADD CONSTRAINT UQ_PromoCodes_Code UNIQUE NONCLUSTERED (Code);
GO

-- Check Constraints
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Users_Role')
    ALTER TABLE dbo.Users ADD CONSTRAINT CK_Users_Role CHECK (Role IN (N'Customer', N'Organizer', N'Admin'));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Customers_Status')
    ALTER TABLE dbo.Customers ADD CONSTRAINT CK_Customers_Status CHECK (Status IN (N'Active', N'Inactive', N'Suspended', N'Pending'));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_ApprovalStatus')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_ApprovalStatus CHECK (ApprovalStatus IN (N'Pending', N'Approved', N'Rejected', N'Suspended'));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_VerificationStatus')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_VerificationStatus CHECK (VerificationStatus IN (N'Pending', N'Approved', N'Rejected'));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_Rating')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_Rating CHECK (Rating >= 0.00 AND Rating <= 5.00);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_CompletedJobs')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_CompletedJobs CHECK (CompletedJobs >= 0);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Services_StartingPrice')
    ALTER TABLE dbo.Services ADD CONSTRAINT CK_Services_StartingPrice CHECK (StartingPrice >= 0.00);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_OrganizerAvailability_Times')
    ALTER TABLE dbo.OrganizerAvailability ADD CONSTRAINT CK_OrganizerAvailability_Times CHECK (EndTime > StartTime);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_OrganizerAvailability_DayOrDate')
    ALTER TABLE dbo.OrganizerAvailability ADD CONSTRAINT CK_OrganizerAvailability_DayOrDate CHECK (AvailabilityDate IS NOT NULL OR DayOfWeek IS NOT NULL);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Bookings_Status')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT CK_Bookings_Status CHECK (Status IN (
        N'Requested', N'Offered', N'Accepted', N'OnTheWay', N'Arrived', 
        N'Started', N'InProgress', N'Completed', N'CustomerConfirmed', 
        N'Paid', N'Cancelled', N'Rescheduled', N'Rejected', N'Expired', N'Disputed'
    ));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Bookings_PaymentStatus')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT CK_Bookings_PaymentStatus CHECK (PaymentStatus IN (
        N'Pending', N'Initiated', N'Success', N'Failed', 
        N'Refunded', N'PartiallyRefunded', N'Cancelled'
    ));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Bookings_Amounts')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT CK_Bookings_Amounts CHECK (BaseAmount >= 0.00 AND ServiceFee >= 0.00 AND DiscountAmount >= 0.00 AND TotalAmount >= 0.00);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_BookingPhotos_PhotoType')
    ALTER TABLE dbo.BookingPhotos ADD CONSTRAINT CK_BookingPhotos_PhotoType CHECK (PhotoType IN (N'Request', N'Before', N'After'));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Payments_PaymentStatus')
    ALTER TABLE dbo.Payments ADD CONSTRAINT CK_Payments_PaymentStatus CHECK (PaymentStatus IN (
        N'Pending', N'Initiated', N'Success', N'Failed', 
        N'Refunded', N'PartiallyRefunded', N'Cancelled'
    ));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Payments_Amount')
    ALTER TABLE dbo.Payments ADD CONSTRAINT CK_Payments_Amount CHECK (Amount >= 0.00);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Payouts_PayoutStatus')
    ALTER TABLE dbo.Payouts ADD CONSTRAINT CK_Payouts_PayoutStatus CHECK (PayoutStatus IN (N'Pending', N'Processing', N'Paid', N'Failed', N'Cancelled'));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Payouts_Amounts')
    ALTER TABLE dbo.Payouts ADD CONSTRAINT CK_Payouts_Amounts CHECK (GrossAmount >= 0.00 AND PlatformFee >= 0.00 AND NetAmount >= 0.00);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Reviews_Rating')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT CK_Reviews_Rating CHECK (Rating >= 1 AND Rating <= 5);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Notifications_NotificationType')
    ALTER TABLE dbo.Notifications ADD CONSTRAINT CK_Notifications_NotificationType CHECK (NotificationType IN (
        N'BookingConfirmed', N'OrganizerAssigned', N'OrganizerAccepted', 
        N'OrganizerOnTheWay', N'OrganizerArrived', N'ServiceStarted', 
        N'ServiceCompleted', N'PaymentSuccess', N'PaymentFailed', 
        N'ReviewReminder', N'BookingCancelled', N'BookingRescheduled', 
        N'AdminMessage'
    ));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_PromoCodes_DiscountType')
    ALTER TABLE dbo.PromoCodes ADD CONSTRAINT CK_PromoCodes_DiscountType CHECK (DiscountType IN (N'Percentage', N'Fixed'));

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_PromoCodes_Dates')
    ALTER TABLE dbo.PromoCodes ADD CONSTRAINT CK_PromoCodes_Dates CHECK (ValidTo >= ValidFrom);

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_SupportTickets_Status')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT CK_SupportTickets_Status CHECK (Status IN (N'Open', N'InReview', N'Waiting', N'Resolved', N'Closed'));
GO

-- Foreign Key Constraints with NO ACTION to ensure financial and audit data cannot be deleted accidentally
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Customers_Users')
    ALTER TABLE dbo.Customers ADD CONSTRAINT FK_Customers_Users FOREIGN KEY (UserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Organizers_Users')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT FK_Organizers_Users FOREIGN KEY (UserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Services_Categories')
    ALTER TABLE dbo.Services ADD CONSTRAINT FK_Services_Categories FOREIGN KEY (CategoryID) REFERENCES dbo.ServiceCategories (CategoryID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerSkills_Organizers')
    ALTER TABLE dbo.OrganizerSkills ADD CONSTRAINT FK_OrganizerSkills_Organizers FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerSkills_Services')
    ALTER TABLE dbo.OrganizerSkills ADD CONSTRAINT FK_OrganizerSkills_Services FOREIGN KEY (ServiceID) REFERENCES dbo.Services (ServiceID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerServiceAreas_Organizers')
    ALTER TABLE dbo.OrganizerServiceAreas ADD CONSTRAINT FK_OrganizerServiceAreas_Organizers FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerServiceAreas_ServiceAreas')
    ALTER TABLE dbo.OrganizerServiceAreas ADD CONSTRAINT FK_OrganizerServiceAreas_ServiceAreas FOREIGN KEY (ServiceAreaID) REFERENCES dbo.ServiceAreas (ServiceAreaID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerAvailability_Organizers')
    ALTER TABLE dbo.OrganizerAvailability ADD CONSTRAINT FK_OrganizerAvailability_Organizers FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Addresses_Customers')
    ALTER TABLE dbo.Addresses ADD CONSTRAINT FK_Addresses_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Bookings_Customers')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT FK_Bookings_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Bookings_Organizers')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT FK_Bookings_Organizers FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Bookings_Services')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT FK_Bookings_Services FOREIGN KEY (ServiceID) REFERENCES dbo.Services (ServiceID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Bookings_Addresses')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT FK_Bookings_Addresses FOREIGN KEY (AddressID) REFERENCES dbo.Addresses (AddressID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingItems_Bookings')
    ALTER TABLE dbo.BookingItems ADD CONSTRAINT FK_BookingItems_Bookings FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingPhotos_Bookings')
    ALTER TABLE dbo.BookingPhotos ADD CONSTRAINT FK_BookingPhotos_Bookings FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingPhotos_Users')
    ALTER TABLE dbo.BookingPhotos ADD CONSTRAINT FK_BookingPhotos_Users FOREIGN KEY (UploadedByUserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Payments_Bookings')
    ALTER TABLE dbo.Payments ADD CONSTRAINT FK_Payments_Bookings FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Payments_Customers')
    ALTER TABLE dbo.Payments ADD CONSTRAINT FK_Payments_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_PaymentTransactions_Payments')
    ALTER TABLE dbo.PaymentTransactions ADD CONSTRAINT FK_PaymentTransactions_Payments FOREIGN KEY (PaymentID) REFERENCES dbo.Payments (PaymentID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Payouts_Organizers')
    ALTER TABLE dbo.Payouts ADD CONSTRAINT FK_Payouts_Organizers FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Payouts_Bookings')
    ALTER TABLE dbo.Payouts ADD CONSTRAINT FK_Payouts_Bookings FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Reviews_Bookings')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT FK_Reviews_Bookings FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Reviews_Customers')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT FK_Reviews_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Reviews_Organizers')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT FK_Reviews_Organizers FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Notifications_Users')
    ALTER TABLE dbo.Notifications ADD CONSTRAINT FK_Notifications_Users FOREIGN KEY (UserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_SupportTickets_Customers')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT FK_SupportTickets_Customers FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_SupportTickets_Organizers')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT FK_SupportTickets_Organizers FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_SupportTickets_Bookings')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT FK_SupportTickets_Bookings FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_SupportTickets_Admins')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT FK_SupportTickets_Admins FOREIGN KEY (AssignedAdminID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerDocuments_Organizers')
    ALTER TABLE dbo.OrganizerDocuments ADD CONSTRAINT FK_OrganizerDocuments_Organizers FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerDocuments_Verifiers')
    ALTER TABLE dbo.OrganizerDocuments ADD CONSTRAINT FK_OrganizerDocuments_Verifiers FOREIGN KEY (VerifiedBy) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingStatusHistory_Bookings')
    ALTER TABLE dbo.BookingStatusHistory ADD CONSTRAINT FK_BookingStatusHistory_Bookings FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingStatusHistory_Users')
    ALTER TABLE dbo.BookingStatusHistory ADD CONSTRAINT FK_BookingStatusHistory_Users FOREIGN KEY (ChangedByUserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;
GO

-- ----------------------------------------------------------------------------
-- STEP 4: CREATE INDEXES
-- ----------------------------------------------------------------------------
PRINT '[STEP 4] Creating Indexes...';

-- Bookings indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_CustomerID_BookingDate')
    CREATE NONCLUSTERED INDEX IX_Bookings_CustomerID_BookingDate ON dbo.Bookings (CustomerID, BookingDate DESC) INCLUDE (Status, PaymentStatus, TotalAmount, ServiceID, OrganizerID);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_OrganizerID_BookingDate')
    CREATE NONCLUSTERED INDEX IX_Bookings_OrganizerID_BookingDate ON dbo.Bookings (OrganizerID, BookingDate DESC) INCLUDE (Status, PaymentStatus, TotalAmount, ServiceID, CustomerID);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_Status_BookingDate')
    CREATE NONCLUSTERED INDEX IX_Bookings_Status_BookingDate ON dbo.Bookings (Status, BookingDate DESC) INCLUDE (CustomerID, OrganizerID, ServiceID, TotalAmount);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_ServiceID_BookingDate')
    CREATE NONCLUSTERED INDEX IX_Bookings_ServiceID_BookingDate ON dbo.Bookings (ServiceID, BookingDate DESC) INCLUDE (Status, CustomerID, OrganizerID, TotalAmount);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_PaymentStatus')
    CREATE NONCLUSTERED INDEX IX_Bookings_PaymentStatus ON dbo.Bookings (PaymentStatus, BookingDate DESC) INCLUDE (BookingID, CustomerID, OrganizerID, TotalAmount);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_OpenJobs_Filtered')
    CREATE NONCLUSTERED INDEX IX_Bookings_OpenJobs_Filtered ON dbo.Bookings (BookingDate, StartTime, ServiceID, AddressID) INCLUDE (CustomerID, TotalAmount, CustomerNotes) WHERE Status IN (N'Requested', N'Offered');

-- Organizer indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_OrganizerAvailability_OrganizerID_Date')
    CREATE NONCLUSTERED INDEX IX_OrganizerAvailability_OrganizerID_Date ON dbo.OrganizerAvailability (OrganizerID, AvailabilityDate, DayOfWeek) INCLUDE (StartTime, EndTime, IsAvailable, IsBlocked);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_OrganizerSkills_OrganizerID_ServiceID')
    CREATE NONCLUSTERED INDEX IX_OrganizerSkills_OrganizerID_ServiceID ON dbo.OrganizerSkills (OrganizerID, ServiceID) INCLUDE (ExperienceYears, IsActive);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_OrganizerServiceAreas_OrganizerID_ServiceAreaID')
    CREATE NONCLUSTERED INDEX IX_OrganizerServiceAreas_OrganizerID_ServiceAreaID ON dbo.OrganizerServiceAreas (OrganizerID, ServiceAreaID) INCLUDE (IsActive);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Organizers_Approved_Active_Filtered')
    CREATE NONCLUSTERED INDEX IX_Organizers_Approved_Active_Filtered ON dbo.Organizers (Rating DESC, CompletedJobs DESC) INCLUDE (OrganizerID, UserID, FullName, Phone, ProfilePhotoURL) WHERE ApprovalStatus = N'Approved' AND IsActive = 1;

-- Addresses Filtered Unique Index
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'UQ_Addresses_CustomerID_IsDefault')
    CREATE UNIQUE NONCLUSTERED INDEX UQ_Addresses_CustomerID_IsDefault ON dbo.Addresses (CustomerID) WHERE IsDefault = 1;

-- Related items
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_BookingPhotos_BookingID_PhotoType')
    CREATE NONCLUSTERED INDEX IX_BookingPhotos_BookingID_PhotoType ON dbo.BookingPhotos (BookingID, PhotoType) INCLUDE (FileURL, UploadedByUserID, CreatedDate);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Payments_BookingID_PaymentStatus')
    CREATE NONCLUSTERED INDEX IX_Payments_BookingID_PaymentStatus ON dbo.Payments (BookingID, PaymentStatus) INCLUDE (CustomerID, Amount, CurrencyCode, PaidDate);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_PaymentTransactions_PaymentID')
    CREATE NONCLUSTERED INDEX IX_PaymentTransactions_PaymentID ON dbo.PaymentTransactions (PaymentID, CreatedDate DESC) INCLUDE (TransactionType, Amount, Status, GatewayReference);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Payouts_OrganizerID_PayoutStatus')
    CREATE NONCLUSTERED INDEX IX_Payouts_OrganizerID_PayoutStatus ON dbo.Payouts (OrganizerID, PayoutStatus, CreatedDate DESC) INCLUDE (BookingID, GrossAmount, PlatformFee, NetAmount, PayoutDate);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Reviews_OrganizerID_CreatedDate')
    CREATE NONCLUSTERED INDEX IX_Reviews_OrganizerID_CreatedDate ON dbo.Reviews (OrganizerID, CreatedDate DESC) INCLUDE (BookingID, CustomerID, Rating, IsPublished);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Notifications_UserID_IsRead')
    CREATE NONCLUSTERED INDEX IX_Notifications_UserID_IsRead ON dbo.Notifications (UserID, IsRead, CreatedDate DESC) INCLUDE (Title, Message, NotificationType, ReferenceID);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_BookingStatusHistory_BookingID')
    CREATE NONCLUSTERED INDEX IX_BookingStatusHistory_BookingID ON dbo.BookingStatusHistory (BookingID, ChangedDate DESC) INCLUDE (OldStatus, NewStatus, ChangedByUserID, Remarks);
GO

-- ----------------------------------------------------------------------------
-- STEP 5: CREATE VIEWS
-- ----------------------------------------------------------------------------
PRINT '[STEP 5] Creating Views...';
GO

CREATE OR ALTER VIEW dbo.vw_ActiveServices
AS
SELECT 
    s.ServiceID,
    s.ServiceName,
    s.Description AS ServiceDescription,
    s.StartingPrice,
    s.EstimatedDuration,
    s.ImageURL AS ServiceImageURL,
    s.IncludedDetails,
    s.ExcludedDetails,
    c.CategoryID,
    c.CategoryName,
    c.Description AS CategoryDescription,
    c.DisplayOrder AS CategoryDisplayOrder,
    c.ImageURL AS CategoryImageURL
FROM dbo.Services s
INNER JOIN dbo.ServiceCategories c ON s.CategoryID = c.CategoryID
WHERE s.IsActive = 1 AND c.IsActive = 1;
GO

CREATE OR ALTER VIEW dbo.vw_ApprovedOrganizers
AS
SELECT 
    o.OrganizerID,
    o.UserID,
    u.LoginID,
    o.FullName,
    o.Phone,
    o.Email,
    o.ProfilePhotoURL,
    o.ExperienceYears,
    o.Rating,
    o.CompletedJobs,
    o.VerificationStatus,
    o.ApprovalStatus,
    o.IsActive,
    o.CreatedDate,
    (SELECT COUNT(1) FROM dbo.OrganizerSkills os WHERE os.OrganizerID = o.OrganizerID AND os.IsActive = 1) AS ActiveSkillsCount,
    (SELECT COUNT(1) FROM dbo.OrganizerServiceAreas osa WHERE osa.OrganizerID = o.OrganizerID AND osa.IsActive = 1) AS ActiveServiceAreasCount
FROM dbo.Organizers o
INNER JOIN dbo.Users u ON o.UserID = u.UserID
WHERE o.ApprovalStatus = N'Approved' 
  AND o.VerificationStatus = N'Approved' 
  AND o.IsActive = 1 
  AND u.IsActive = 1;
GO

CREATE OR ALTER VIEW dbo.vw_BookingDetails
AS
SELECT 
    b.BookingID,
    b.BookingDate,
    b.StartTime,
    b.EstimatedDuration,
    b.Status AS BookingStatus,
    b.PaymentStatus,
    b.BaseAmount,
    b.ServiceFee,
    b.DiscountAmount,
    b.TotalAmount,
    b.CustomerNotes,
    b.CreatedDate AS BookingCreatedDate,
    b.UpdatedDate AS BookingUpdatedDate,
    c.CustomerID,
    c.FullName AS CustomerName,
    c.Phone AS CustomerPhone,
    c.Email AS CustomerEmail,
    c.ProfilePhotoURL AS CustomerPhotoURL,
    o.OrganizerID,
    o.FullName AS OrganizerName,
    o.Phone AS OrganizerPhone,
    o.Email AS OrganizerEmail,
    o.ProfilePhotoURL AS OrganizerPhotoURL,
    o.Rating AS OrganizerRating,
    s.ServiceID,
    s.ServiceName,
    cat.CategoryID,
    cat.CategoryName,
    a.AddressID,
    a.AddressLabel,
    a.AddressLine1,
    a.AddressLine2,
    a.City,
    a.State,
    a.PostalCode,
    a.Landmark,
    a.Latitude,
    a.Longitude,
    (SELECT COUNT(1) FROM dbo.BookingItems bi WHERE bi.BookingID = b.BookingID) AS ItemCount,
    (SELECT COUNT(1) FROM dbo.BookingPhotos bp WHERE bp.BookingID = b.BookingID) AS PhotoCount,
    (SELECT COUNT(1) FROM dbo.Reviews r WHERE r.BookingID = b.BookingID) AS HasReview
FROM dbo.Bookings b
INNER JOIN dbo.Customers c ON b.CustomerID = c.CustomerID
LEFT JOIN dbo.Organizers o ON b.OrganizerID = o.OrganizerID
INNER JOIN dbo.Services s ON b.ServiceID = s.ServiceID
INNER JOIN dbo.ServiceCategories cat ON s.CategoryID = cat.CategoryID
INNER JOIN dbo.Addresses a ON b.AddressID = a.AddressID;
GO

CREATE OR ALTER VIEW dbo.vw_OrganizerEarningsSummary
AS
SELECT 
    o.OrganizerID,
    o.FullName AS OrganizerName,
    o.Phone AS OrganizerPhone,
    o.Rating,
    o.CompletedJobs,
    COALESCE(SUM(p.GrossAmount), 0.00) AS TotalGrossEarnings,
    COALESCE(SUM(p.PlatformFee), 0.00) AS TotalPlatformFees,
    COALESCE(SUM(p.NetAmount), 0.00) AS TotalNetEarnings,
    COALESCE(SUM(CASE WHEN p.PayoutStatus = N'Paid' THEN p.NetAmount ELSE 0.00 END), 0.00) AS TotalPaidOut,
    COALESCE(SUM(CASE WHEN p.PayoutStatus IN (N'Pending', N'Processing') THEN p.NetAmount ELSE 0.00 END), 0.00) AS PendingPayoutBalance
FROM dbo.Organizers o
LEFT JOIN dbo.Payouts p ON o.OrganizerID = p.OrganizerID
GROUP BY o.OrganizerID, o.FullName, o.Phone, o.Rating, o.CompletedJobs;
GO

CREATE OR ALTER VIEW dbo.vw_MarketplaceDailyPerformance
AS
SELECT 
    CAST(b.BookingDate AS DATE) AS SummaryDate,
    COUNT(b.BookingID) AS TotalBookingsRequested,
    SUM(CASE WHEN b.Status IN (N'Completed', N'CustomerConfirmed', N'Paid') THEN 1 ELSE 0 END) AS CompletedBookings,
    SUM(CASE WHEN b.Status IN (N'Cancelled', N'Rejected') THEN 1 ELSE 0 END) AS CancelledBookings,
    SUM(CASE WHEN b.PaymentStatus = N'Success' THEN b.TotalAmount ELSE 0.00 END) AS GrossMerchandiseValue,
    SUM(CASE WHEN b.PaymentStatus = N'Success' THEN b.ServiceFee ELSE 0.00 END) AS PlatformFeeRevenue,
    COUNT(DISTINCT b.CustomerID) AS UniqueActiveCustomers,
    COUNT(DISTINCT b.OrganizerID) AS UniqueActiveOrganizers
FROM dbo.Bookings b
GROUP BY CAST(b.BookingDate AS DATE);
GO

CREATE OR ALTER VIEW dbo.vw_PendingOrganizerVerifications
AS
SELECT 
    o.OrganizerID,
    o.FullName,
    o.Email,
    o.Phone,
    o.ExperienceYears,
    o.VerificationStatus,
    o.ApprovalStatus,
    o.CreatedDate AS RegisteredDate,
    (SELECT COUNT(1) FROM dbo.OrganizerDocuments od WHERE od.OrganizerID = o.OrganizerID) AS TotalDocumentsUploaded,
    (SELECT COUNT(1) FROM dbo.OrganizerDocuments od WHERE od.OrganizerID = o.OrganizerID AND od.VerificationStatus = N'Pending') AS PendingDocumentsCount
FROM dbo.Organizers o
WHERE o.ApprovalStatus = N'Pending' OR o.VerificationStatus = N'Pending';
GO

-- ----------------------------------------------------------------------------
-- STEP 6: CREATE TRIGGERS
-- ----------------------------------------------------------------------------
PRINT '[STEP 6] Creating Triggers...';
GO

CREATE OR ALTER TRIGGER dbo.TR_Addresses_SingleDefault
ON dbo.Addresses
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM inserted WHERE IsDefault = 1)
    BEGIN
        UPDATE a
        SET a.IsDefault = 0, a.UpdatedDate = SYSUTCDATETIME()
        FROM dbo.Addresses a
        INNER JOIN inserted i ON a.CustomerID = i.CustomerID
        WHERE a.AddressID <> i.AddressID AND a.IsDefault = 1 AND i.IsDefault = 1;
    END
END;
GO

CREATE OR ALTER TRIGGER dbo.TR_Bookings_AuditStatusHistory
ON dbo.Bookings
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF UPDATE(Status)
    BEGIN
        INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
        SELECT 
            i.BookingID, d.Status AS OldStatus, i.Status AS NewStatus,
            COALESCE((SELECT c.UserID FROM dbo.Customers c WHERE c.CustomerID = i.CustomerID), 1),
            CONCAT(N'Automated transition from ', d.Status, N' to ', i.Status),
            SYSUTCDATETIME()
        FROM inserted i
        INNER JOIN deleted d ON i.BookingID = d.BookingID
        WHERE i.Status <> d.Status;
    END
END;
GO

CREATE OR ALTER TRIGGER dbo.TR_Reviews_UpdateOrganizerRating
ON dbo.Reviews
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @AffectedOrganizers TABLE (OrganizerID BIGINT PRIMARY KEY);

    INSERT INTO @AffectedOrganizers (OrganizerID)
    SELECT DISTINCT OrganizerID FROM inserted WHERE OrganizerID IS NOT NULL
    UNION
    SELECT DISTINCT OrganizerID FROM deleted WHERE OrganizerID IS NOT NULL;

    UPDATE o
    SET 
        o.Rating = COALESCE(stats.AvgRating, 0.00),
        o.CompletedJobs = COALESCE(jobStats.TotalCompleted, o.CompletedJobs),
        o.UpdatedDate = SYSUTCDATETIME()
    FROM dbo.Organizers o
    INNER JOIN @AffectedOrganizers ao ON o.OrganizerID = ao.OrganizerID
    OUTER APPLY (
        SELECT ROUND(AVG(CAST(r.Rating AS DECIMAL(3,2))), 2) AS AvgRating
        FROM dbo.Reviews r WHERE r.OrganizerID = o.OrganizerID AND r.IsPublished = 1
    ) stats
    OUTER APPLY (
        SELECT COUNT(1) AS TotalCompleted
        FROM dbo.Bookings b WHERE b.OrganizerID = o.OrganizerID AND b.Status IN (N'Completed', N'CustomerConfirmed', N'Paid')
    ) jobStats;
END;
GO

CREATE OR ALTER TRIGGER dbo.TR_Reviews_ValidateEligibility
ON dbo.Reviews
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (
        SELECT 1 
        FROM inserted i
        INNER JOIN dbo.Bookings b ON i.BookingID = b.BookingID
        WHERE b.Status NOT IN (N'Completed', N'CustomerConfirmed', N'Paid')
    )
    BEGIN
        RAISERROR (N'Reviews are only permitted after the booking has reached Completed or CustomerConfirmed status.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END;
GO

CREATE OR ALTER TRIGGER dbo.TR_Payments_SyncBookingPaymentStatus
ON dbo.Payments
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF UPDATE(PaymentStatus)
    BEGIN
        UPDATE b
        SET b.PaymentStatus = i.PaymentStatus, b.UpdatedDate = SYSUTCDATETIME()
        FROM dbo.Bookings b
        INNER JOIN inserted i ON b.BookingID = i.BookingID
        WHERE b.PaymentStatus <> i.PaymentStatus;
    END
END;
GO

-- ----------------------------------------------------------------------------
-- STEP 7: CREATE STORED PROCEDURES AND FUNCTIONS
-- ----------------------------------------------------------------------------
PRINT '[STEP 7] Creating Functions and Stored Procedures...';
GO

CREATE OR ALTER FUNCTION dbo.fn_HashPassword (@Password NVARCHAR(200), @Salt NVARCHAR(100))
RETURNS NVARCHAR(500)
AS
BEGIN
    RETURN CONVERT(NVARCHAR(500), HASHBYTES('SHA2_512', CONCAT(@Salt, @Password)), 2);
END;
GO

CREATE OR ALTER FUNCTION dbo.fn_VerifyPassword (@Password NVARCHAR(200), @Salt NVARCHAR(100), @StoredHash NVARCHAR(500))
RETURNS BIT
AS
BEGIN
    IF dbo.fn_HashPassword(@Password, @Salt) = @StoredHash RETURN 1;
    RETURN 0;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_AuthenticateUser
    @LoginID      NVARCHAR(150),
    @Password     NVARCHAR(200),
    @ExpectedRole NVARCHAR(30) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Salt NVARCHAR(100) = N'NestifySalt#2026$';
    DECLARE @PasswordHash NVARCHAR(500) = dbo.fn_HashPassword(@Password, @Salt);
    DECLARE @UserID BIGINT, @Role NVARCHAR(30), @IsActive BIT;

    SELECT @UserID = UserID, @Role = Role, @IsActive = IsActive
    FROM dbo.Users WHERE LoginID = @LoginID AND PasswordHash = @PasswordHash;

    IF @UserID IS NULL
    BEGIN
        SELECT 0 AS IsAuthenticated, N'Invalid LoginID or password.' AS ErrorMessage;
        RETURN;
    END

    IF @IsActive = 0
    BEGIN
        SELECT 0 AS IsAuthenticated, N'Your account has been deactivated.' AS ErrorMessage;
        RETURN;
    END

    IF @ExpectedRole IS NOT NULL AND @Role <> @ExpectedRole
    BEGIN
        SELECT 0 AS IsAuthenticated, CONCAT(N'Access denied for role: ', @Role) AS ErrorMessage;
        RETURN;
    END

    UPDATE dbo.Users SET LastLoginDate = SYSUTCDATETIME() WHERE UserID = @UserID;

    SELECT 
        1 AS IsAuthenticated, NULL AS ErrorMessage,
        u.UserID, u.LoginID, u.Role, u.LastLoginDate,
        c.CustomerID, c.FullName AS CustomerFullName,
        o.OrganizerID, o.FullName AS OrganizerFullName,
        o.ApprovalStatus AS OrganizerApprovalStatus, o.VerificationStatus AS OrganizerVerificationStatus
    FROM dbo.Users u
    LEFT JOIN dbo.Customers c ON u.UserID = c.UserID
    LEFT JOIN dbo.Organizers o ON u.UserID = o.UserID
    WHERE u.UserID = @UserID;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_GetCustomerProfile @CustomerID BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT c.CustomerID, c.UserID, u.LoginID, c.FullName, c.Phone, c.Email, c.ProfilePhotoURL, c.Status, c.CreatedDate
    FROM dbo.Customers c INNER JOIN dbo.Users u ON c.UserID = u.UserID WHERE c.CustomerID = @CustomerID;

    SELECT AddressID, AddressLabel, AddressLine1, AddressLine2, City, State, PostalCode, Landmark, Latitude, Longitude, IsDefault
    FROM dbo.Addresses WHERE CustomerID = @CustomerID ORDER BY IsDefault DESC, AddressID ASC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_SaveCustomerAddress
    @CustomerID   BIGINT,
    @AddressID    BIGINT = NULL,
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
        IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE CustomerID = @CustomerID) SET @IsDefault = 1;
        IF @IsDefault = 1 UPDATE dbo.Addresses SET IsDefault = 0, UpdatedDate = SYSUTCDATETIME() WHERE CustomerID = @CustomerID;

        IF @AddressID IS NULL OR @AddressID = 0
        BEGIN
            INSERT INTO dbo.Addresses (CustomerID, AddressLabel, AddressLine1, AddressLine2, City, State, PostalCode, Landmark, Latitude, Longitude, IsDefault, CreatedDate)
            VALUES (@CustomerID, @AddressLabel, @AddressLine1, @AddressLine2, @City, @State, @PostalCode, @Landmark, @Latitude, @Longitude, @IsDefault, SYSUTCDATETIME());
            SET @ResultAddressID = SCOPE_IDENTITY();
        END
        ELSE
        BEGIN
            UPDATE dbo.Addresses
            SET AddressLabel = @AddressLabel, AddressLine1 = @AddressLine1, AddressLine2 = @AddressLine2, City = @City, State = @State, PostalCode = @PostalCode, Landmark = @Landmark, Latitude = @Latitude, Longitude = @Longitude, IsDefault = @IsDefault, UpdatedDate = SYSUTCDATETIME()
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

        IF NOT EXISTS (SELECT 1 FROM dbo.Customers WHERE CustomerID = @CustomerID AND Status = N'Active')
        BEGIN
            RAISERROR(N'Customer does not exist or is inactive.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        DECLARE @BasePrice DECIMAL(18,2), @ServiceDuration DECIMAL(10,2);
        SELECT @BasePrice = StartingPrice, @ServiceDuration = COALESCE(@EstimatedDuration, EstimatedDuration, 2.00)
        FROM dbo.Services WHERE ServiceID = @ServiceID AND IsActive = 1;

        IF @BasePrice IS NULL
        BEGIN
            RAISERROR(N'Service does not exist or is inactive.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE AddressID = @AddressID AND CustomerID = @CustomerID)
        BEGIN
            RAISERROR(N'Address does not belong to this customer.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        DECLARE @ServiceFee DECIMAL(18,2) = ROUND(@BasePrice * 0.15, 2);
        DECLARE @DiscountAmount DECIMAL(18,2) = 0.00;

        IF @PromoCode IS NOT NULL AND LEN(TRIM(@PromoCode)) > 0
        BEGIN
            DECLARE @PromoID BIGINT, @DiscType NVARCHAR(20), @DiscVal DECIMAL(18,2), @MinAmt DECIMAL(18,2), @MaxDisc DECIMAL(18,2);
            SELECT @PromoID = PromoCodeID, @DiscType = DiscountType, @DiscVal = DiscountValue, @MinAmt = MinimumAmount, @MaxDisc = MaximumDiscount
            FROM dbo.PromoCodes
            WHERE Code = @PromoCode AND IsActive = 1 AND SYSUTCDATETIME() BETWEEN ValidFrom AND ValidTo AND (UsageLimit IS NULL OR UsedCount < UsageLimit);

            IF @PromoID IS NOT NULL AND (@MinAmt IS NULL OR @BasePrice >= @MinAmt)
            BEGIN
                IF @DiscType = N'Percentage' SET @DiscountAmount = ROUND(@BasePrice * (@DiscVal / 100.0), 2);
                ELSE SET @DiscountAmount = @DiscVal;

                IF @MaxDisc IS NOT NULL AND @DiscountAmount > @MaxDisc SET @DiscountAmount = @MaxDisc;
                UPDATE dbo.PromoCodes SET UsedCount = UsedCount + 1, UpdatedDate = SYSUTCDATETIME() WHERE PromoCodeID = @PromoID;
            END
        END

        DECLARE @TotalAmount DECIMAL(18,2) = (@BasePrice + @ServiceFee) - @DiscountAmount;
        IF @TotalAmount < 0.00 SET @TotalAmount = 0.00;

        INSERT INTO dbo.Bookings (CustomerID, OrganizerID, ServiceID, AddressID, BookingDate, StartTime, EstimatedDuration, Status, PaymentStatus, BaseAmount, ServiceFee, DiscountAmount, TotalAmount, CustomerNotes, CreatedDate)
        VALUES (@CustomerID, NULL, @ServiceID, @AddressID, @BookingDate, @StartTime, @ServiceDuration, N'Requested', N'Pending', @BasePrice, @ServiceFee, @DiscountAmount, @TotalAmount, @CustomerNotes, SYSUTCDATETIME());

        SET @NewBookingID = SCOPE_IDENTITY();

        DECLARE @CustomerUserID BIGINT = (SELECT UserID FROM dbo.Customers WHERE CustomerID = @CustomerID);
        INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
        VALUES (@NewBookingID, NULL, N'Requested', @CustomerUserID, N'Booking requested by customer', SYSUTCDATETIME());

        INSERT INTO dbo.Notifications (UserID, Title, Message, NotificationType, ReferenceID, IsRead, SentDate, CreatedDate)
        VALUES (@CustomerUserID, N'Booking Requested', N'Your booking request has been submitted and is awaiting assignment.', N'BookingConfirmed', @NewBookingID, 0, SYSUTCDATETIME(), SYSUTCDATETIME());

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_GetCustomerBookings @CustomerID BIGINT, @StatusFilter NVARCHAR(40) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SELECT 
        b.BookingID, b.BookingDate, b.StartTime, b.EstimatedDuration, b.Status, b.PaymentStatus, b.TotalAmount,
        s.ServiceName, s.ImageURL AS ServiceImageURL, o.OrganizerID, o.FullName AS OrganizerName, o.Phone AS OrganizerPhone, o.Rating AS OrganizerRating,
        a.City, a.AddressLine1
    FROM dbo.Bookings b
    INNER JOIN dbo.Services s ON b.ServiceID = s.ServiceID
    INNER JOIN dbo.Addresses a ON b.AddressID = a.AddressID
    LEFT JOIN dbo.Organizers o ON b.OrganizerID = o.OrganizerID
    WHERE b.CustomerID = @CustomerID AND (@StatusFilter IS NULL OR b.Status = @StatusFilter)
    ORDER BY b.BookingDate DESC, b.StartTime DESC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_GetBookingDetail @BookingID BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT * FROM dbo.vw_BookingDetails WHERE BookingID = @BookingID;
    SELECT BookingItemID, ItemType, ItemName, Quantity, UnitPrice, Amount, Notes FROM dbo.BookingItems WHERE BookingID = @BookingID;
    SELECT BookingPhotoID, PhotoType, FileName, FileURL, UploadedByUserID, CreatedDate FROM dbo.BookingPhotos WHERE BookingID = @BookingID ORDER BY PhotoType, CreatedDate;
    SELECT PaymentID, Amount, CurrencyCode, PaymentMethod, GatewayName, GatewayReference, PaymentStatus, PaidDate, CreatedDate FROM dbo.Payments WHERE BookingID = @BookingID;
    SELECT ReviewID, Rating, ReviewText, IsPublished, CreatedDate FROM dbo.Reviews WHERE BookingID = @BookingID;
    SELECT h.HistoryID, h.OldStatus, h.NewStatus, u.LoginID AS ChangedByLogin, h.Remarks, h.ChangedDate FROM dbo.BookingStatusHistory h INNER JOIN dbo.Users u ON h.ChangedByUserID = u.UserID WHERE h.BookingID = @BookingID ORDER BY h.ChangedDate ASC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_GetOrganizerAvailableJobs @OrganizerID BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    IF NOT EXISTS (SELECT 1 FROM dbo.Organizers WHERE OrganizerID = @OrganizerID AND ApprovalStatus = N'Approved' AND VerificationStatus = N'Approved' AND IsActive = 1)
    BEGIN
        SELECT N'Organizer is not approved or is inactive.' AS Message;
        RETURN;
    END

    SELECT 
        b.BookingID, b.BookingDate, b.StartTime, b.EstimatedDuration, b.Status, b.TotalAmount, b.CustomerNotes,
        s.ServiceID, s.ServiceName, cat.CategoryName, a.City, a.PostalCode, a.AddressLine1, c.FullName AS CustomerName
    FROM dbo.Bookings b
    INNER JOIN dbo.Services s ON b.ServiceID = s.ServiceID
    INNER JOIN dbo.ServiceCategories cat ON s.CategoryID = cat.CategoryID
    INNER JOIN dbo.Addresses a ON b.AddressID = a.AddressID
    INNER JOIN dbo.Customers c ON b.CustomerID = c.CustomerID
    INNER JOIN dbo.OrganizerSkills os ON os.OrganizerID = @OrganizerID AND os.ServiceID = s.ServiceID AND os.IsActive = 1
    INNER JOIN dbo.OrganizerServiceAreas osa ON osa.OrganizerID = @OrganizerID AND osa.IsActive = 1
    INNER JOIN dbo.ServiceAreas sa ON osa.ServiceAreaID = sa.ServiceAreaID AND (sa.City = a.City OR sa.PostalCode = a.PostalCode)
    WHERE b.Status IN (N'Requested', N'Offered')
      AND (b.OrganizerID IS NULL OR b.OrganizerID = @OrganizerID)
      AND b.BookingDate >= CAST(SYSUTCDATETIME() AS DATE)
    ORDER BY b.BookingDate ASC, b.StartTime ASC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_OrganizerAcceptJob @OrganizerID BIGINT, @BookingID BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (SELECT 1 FROM dbo.Organizers WHERE OrganizerID = @OrganizerID AND ApprovalStatus = N'Approved' AND IsActive = 1)
        BEGIN
            RAISERROR(N'Only approved and active organizers can accept jobs.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        DECLARE @CurrentStatus NVARCHAR(40), @CurrentOrganizer BIGINT, @CustomerID BIGINT;
        SELECT @CurrentStatus = Status, @CurrentOrganizer = OrganizerID, @CustomerID = CustomerID
        FROM dbo.Bookings WITH (UPDLOCK, ROWLOCK) WHERE BookingID = @BookingID;

        IF @CurrentStatus IS NULL OR @CurrentStatus NOT IN (N'Requested', N'Offered')
        BEGIN
            RAISERROR(N'Job is not available for acceptance.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        UPDATE dbo.Bookings SET OrganizerID = @OrganizerID, Status = N'Accepted', UpdatedDate = SYSUTCDATETIME() WHERE BookingID = @BookingID;

        DECLARE @OrganizerUserID BIGINT = (SELECT UserID FROM dbo.Organizers WHERE OrganizerID = @OrganizerID);
        INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
        VALUES (@BookingID, @CurrentStatus, N'Accepted', @OrganizerUserID, N'Job accepted by organizer', SYSUTCDATETIME());

        DECLARE @CustomerUserID BIGINT = (SELECT UserID FROM dbo.Customers WHERE CustomerID = @CustomerID);
        DECLARE @OrgName NVARCHAR(150) = (SELECT FullName FROM dbo.Organizers WHERE OrganizerID = @OrganizerID);
        INSERT INTO dbo.Notifications (UserID, Title, Message, NotificationType, ReferenceID, IsRead, SentDate, CreatedDate)
        VALUES (@CustomerUserID, N'Organizer Assigned', CONCAT(@OrgName, N' has accepted your booking!'), N'OrganizerAccepted', @BookingID, 0, SYSUTCDATETIME(), SYSUTCDATETIME());

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

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

        DECLARE @OldStatus NVARCHAR(40), @CustomerID BIGINT;
        SELECT @OldStatus = Status, @CustomerID = CustomerID FROM dbo.Bookings WITH (UPDLOCK, ROWLOCK) WHERE BookingID = @BookingID;

        IF @OldStatus IS NULL
        BEGIN
            RAISERROR(N'Booking does not exist.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        UPDATE dbo.Bookings SET Status = @NewStatus, UpdatedDate = SYSUTCDATETIME() WHERE BookingID = @BookingID;

        INSERT INTO dbo.BookingStatusHistory (BookingID, OldStatus, NewStatus, ChangedByUserID, Remarks, ChangedDate)
        VALUES (@BookingID, @OldStatus, @NewStatus, @ActionByUserID, COALESCE(@Remarks, CONCAT(N'Transition to ', @NewStatus)), SYSUTCDATETIME());

        DECLARE @CustomerUserID BIGINT = (SELECT UserID FROM dbo.Customers WHERE CustomerID = @CustomerID);
        INSERT INTO dbo.Notifications (UserID, Title, Message, NotificationType, ReferenceID, IsRead, SentDate, CreatedDate)
        VALUES (@CustomerUserID, CONCAT(N'Booking ', @NewStatus), CONCAT(N'Your booking #', CAST(@BookingID AS NVARCHAR(20)), N' is now ', @NewStatus), N'ServiceStarted', @BookingID, 0, SYSUTCDATETIME(), SYSUTCDATETIME());

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

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

        INSERT INTO dbo.Payments (BookingID, CustomerID, Amount, CurrencyCode, PaymentMethod, GatewayName, GatewayReference, PaymentStatus, PaidDate, CreatedDate)
        VALUES (@BookingID, @CustomerID, @Amount, @CurrencyCode, @PaymentMethod, @GatewayName, @GatewayReference, N'Success', SYSUTCDATETIME(), SYSUTCDATETIME());
        SET @NewPaymentID = SCOPE_IDENTITY();

        INSERT INTO dbo.PaymentTransactions (PaymentID, GatewayReference, TransactionType, Amount, Status, CreatedDate)
        VALUES (@NewPaymentID, @GatewayReference, N'Capture', @Amount, N'Success', SYSUTCDATETIME());

        UPDATE dbo.Bookings
        SET PaymentStatus = N'Success', Status = CASE WHEN Status IN (N'Completed', N'CustomerConfirmed') THEN N'Paid' ELSE Status END, UpdatedDate = SYSUTCDATETIME()
        WHERE BookingID = @BookingID;

        DECLARE @OrganizerID BIGINT;
        SELECT @OrganizerID = OrganizerID FROM dbo.Bookings WHERE BookingID = @BookingID;

        IF @OrganizerID IS NOT NULL AND NOT EXISTS (SELECT 1 FROM dbo.Payouts WHERE BookingID = @BookingID)
        BEGIN
            DECLARE @PlatformFee DECIMAL(18,2) = ROUND(@Amount * 0.15, 2);
            DECLARE @NetAmount DECIMAL(18,2) = @Amount - @PlatformFee;
            INSERT INTO dbo.Payouts (OrganizerID, BookingID, GrossAmount, PlatformFee, NetAmount, PayoutStatus, PayoutReference, CreatedDate)
            VALUES (@OrganizerID, @BookingID, @Amount, @PlatformFee, @NetAmount, N'Pending', CONCAT(N'PAYOUT-BKG-', CAST(@BookingID AS NVARCHAR(20))), SYSUTCDATETIME());
        END

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

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

-- ----------------------------------------------------------------------------
-- STEP 8: SEED ESSENTIAL DATA (CATEGORIES, SERVICES, AREAS, DEMO ACCOUNTS)
-- ----------------------------------------------------------------------------
PRINT '[STEP 8] Seeding Essential Data...';

-- Categories
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
WHEN NOT MATCHED THEN
    INSERT (CategoryName, Description, ImageURL, DisplayOrder, IsActive, CreatedDate)
    VALUES (source.CategoryName, source.Description, source.ImageURL, source.DisplayOrder, source.IsActive, SYSUTCDATETIME());
GO

-- Services
DECLARE @CatBedroom BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Bedroom');
DECLARE @CatKitchen BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Kitchen');
DECLARE @CatStorage BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Storage');
DECLARE @CatKids    BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Kids');
DECLARE @CatLiving  BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Living');
DECLARE @CatSpecial BIGINT = (SELECT CategoryID FROM dbo.ServiceCategories WHERE CategoryName = N'Special');

MERGE INTO dbo.Services AS target
USING (VALUES
    (@CatBedroom, N'Wardrobe Organization', N'Closet overhaul and color-coordinated styling.', N'Emptying, sorting, hanging, labelling', N'Donation delivery', 120.00, 3.00, N'https://images.unsplash.com/photo-1558997519-83ea9252def8', 1),
    (@CatKitchen, N'Kitchen Organization', N'Cabinet, counter, and drawer zoning.', N'Cookware sorting, spice rack layout', N'Deep appliance scrubbing', 150.00, 3.50, N'https://images.unsplash.com/photo-1556911220-e15b29be8c8f', 1),
    (@CatKitchen, N'Pantry Organization', N'Decanting dry goods and airtight jar containment.', N'Expiration audit, jar decanting, bin labeling', N'Grocery purchases', 95.00, 2.50, N'https://images.unsplash.com/photo-1600585155526-990dced4db0d', 1),
    (@CatStorage, N'Storage Room Organization', N'Heavy-duty garage and basement shelving.', N'Seasonal gear zoning, tote labeling', N'Hazardous waste disposal', 180.00, 4.00, N'https://images.unsplash.com/photo-1595428774223-ef52624120d2', 1),
    (@CatKids, N'Kids Room Organization', N'Toy rotation cubbies and low-height clothing access.', N'Toy bin sorting, cubby labels', N'Sanitization', 110.00, 2.50, N'https://images.unsplash.com/photo-1596461404969-9ae70f2830c1', 1),
    (@CatLiving, N'Bookshelf Organization', N'Styling by genre, color, and memorabilia accenting.', N'Book spine dusting, genre grouping', N'Book appraisal', 85.00, 2.00, N'https://images.unsplash.com/photo-1513694203232-719a280e022f', 1),
    (@CatStorage, N'Shoe Rack Organization', N'Clean footwear pair matching and clear drop boxes.', N'Pair matching, clean wipe down, container sorting', N'Shoe repair', 75.00, 1.50, N'https://images.unsplash.com/photo-1549298916-b41d501d3772', 1),
    (@CatSpecial, N'Full Home Organization', N'Multi-room whole-house reset with lead organizer.', N'Bedroom, kitchen, living, storage overhaul', N'Moving truck rental', 450.00, 8.00, N'https://images.unsplash.com/photo-1600585154340-be6161a56a0c', 1),
    (@CatSpecial, N'Move-in Organization', N'Unpack boxes and settle into optimal locations.', N'Box unpacking, immediate kitchen/bedroom setup', N'Furniture assembly', 320.00, 6.00, N'https://images.unsplash.com/photo-1600565193348-f74bd3c7ccdf', 1),
    (@CatSpecial, N'Move-out Organization', N'Systematic decluttering and room-by-room moving prep.', N'Fragile wrapping, room itemization', N'Truck driving', 280.00, 5.00, N'https://images.unsplash.com/photo-1600585152220-90363fe7e115', 1)
) AS source (CategoryID, ServiceName, Description, IncludedDetails, ExcludedDetails, StartingPrice, EstimatedDuration, ImageURL, IsActive)
ON target.ServiceName = source.ServiceName
WHEN NOT MATCHED THEN
    INSERT (CategoryID, ServiceName, Description, IncludedDetails, ExcludedDetails, StartingPrice, EstimatedDuration, ImageURL, IsActive, CreatedDate)
    VALUES (source.CategoryID, source.ServiceName, source.Description, source.IncludedDetails, source.ExcludedDetails, source.StartingPrice, source.EstimatedDuration, source.ImageURL, source.IsActive, SYSUTCDATETIME());
GO

-- Service Areas
MERGE INTO dbo.ServiceAreas AS target
USING (VALUES
    (N'Downtown Metro & Financial District', N'San Francisco', N'CA', N'94105', 37.7891720, -122.3992480, 1),
    (N'Mission & Pacific Heights',           N'San Francisco', N'CA', N'94115', 37.7925000, -122.4382000, 1)
) AS source (AreaName, City, State, PostalCode, Latitude, Longitude, IsActive)
ON target.AreaName = source.AreaName
WHEN NOT MATCHED THEN
    INSERT (AreaName, City, State, PostalCode, Latitude, Longitude, IsActive, CreatedDate)
    VALUES (source.AreaName, source.City, source.State, source.PostalCode, source.Latitude, source.Longitude, source.IsActive, SYSUTCDATETIME());
GO

-- Promo Codes
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

-- Demo Accounts
DECLARE @Salt NVARCHAR(100) = N'NestifySalt#2026$';
DECLARE @CustomerHash  NVARCHAR(500) = dbo.fn_HashPassword(N'Nestify@123', @Salt);
DECLARE @OrganizerHash NVARCHAR(500) = dbo.fn_HashPassword(N'Nestify@456', @Salt);
DECLARE @AdminHash     NVARCHAR(500) = dbo.fn_HashPassword(N'Nestify@Admin2026!', @Salt);

-- Customer Demo
DECLARE @CustUID BIGINT;
SELECT @CustUID = UserID FROM dbo.Users WHERE LoginID = N'customer@nestify.demo';
IF @CustUID IS NULL
BEGIN
    INSERT INTO dbo.Users (LoginID, PasswordHash, Role, IsActive, CreatedDate)
    VALUES (N'customer@nestify.demo', @CustomerHash, N'Customer', 1, SYSUTCDATETIME());
    SET @CustUID = SCOPE_IDENTITY();
END

DECLARE @CustID BIGINT;
SELECT @CustID = CustomerID FROM dbo.Customers WHERE UserID = @CustUID;
IF @CustID IS NULL
BEGIN
    INSERT INTO dbo.Customers (UserID, FullName, Phone, Email, ProfilePhotoURL, Status, CreatedDate)
    VALUES (@CustUID, N'Demo Customer', N'+1-555-0101', N'customer@nestify.demo', N'https://images.unsplash.com/photo-1494790108377-be9c29b29330', N'Active', SYSUTCDATETIME());
    SET @CustID = SCOPE_IDENTITY();

    INSERT INTO dbo.Addresses (CustomerID, AddressLabel, AddressLine1, City, State, PostalCode, IsDefault, CreatedDate)
    VALUES (@CustID, N'Home', N'450 Mission St Apt 12B', N'San Francisco', N'CA', N'94105', 1, SYSUTCDATETIME());
END

-- Organizer Demo
DECLARE @OrgUID BIGINT;
SELECT @OrgUID = UserID FROM dbo.Users WHERE LoginID = N'organizer@nestify.demo';
IF @OrgUID IS NULL
BEGIN
    INSERT INTO dbo.Users (LoginID, PasswordHash, Role, IsActive, CreatedDate)
    VALUES (N'organizer@nestify.demo', @OrganizerHash, N'Organizer', 1, SYSUTCDATETIME());
    SET @OrgUID = SCOPE_IDENTITY();
END

DECLARE @OrgID BIGINT;
SELECT @OrgID = OrganizerID FROM dbo.Organizers WHERE UserID = @OrgUID;
IF @OrgID IS NULL
BEGIN
    INSERT INTO dbo.Organizers (UserID, FullName, Phone, Email, ProfilePhotoURL, ExperienceYears, VerificationStatus, ApprovalStatus, Rating, CompletedJobs, IsActive, CreatedDate)
    VALUES (@OrgUID, N'Demo Organizer', N'+1-555-0202', N'organizer@nestify.demo', N'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2', 5.50, N'Approved', N'Approved', 4.95, 38, 1, SYSUTCDATETIME());
    SET @OrgID = SCOPE_IDENTITY();
END
ELSE
BEGIN
    UPDATE dbo.Organizers SET ApprovalStatus = N'Approved', VerificationStatus = N'Approved', IsActive = 1 WHERE OrganizerID = @OrgID;
END

IF NOT EXISTS (SELECT 1 FROM dbo.OrganizerSkills WHERE OrganizerID = @OrgID)
BEGIN
    DECLARE @Sid1 BIGINT = (SELECT TOP 1 ServiceID FROM dbo.Services WHERE ServiceName = N'Wardrobe Organization');
    DECLARE @Sid2 BIGINT = (SELECT TOP 1 ServiceID FROM dbo.Services WHERE ServiceName = N'Kitchen Organization');
    INSERT INTO dbo.OrganizerSkills (OrganizerID, ServiceID, ExperienceYears, IsActive, CreatedDate)
    VALUES (@OrgID, @Sid1, 5.5, 1, SYSUTCDATETIME()), (@OrgID, @Sid2, 4.0, 1, SYSUTCDATETIME());
END

IF NOT EXISTS (SELECT 1 FROM dbo.OrganizerServiceAreas WHERE OrganizerID = @OrgID)
BEGIN
    DECLARE @Aid1 BIGINT = (SELECT TOP 1 ServiceAreaID FROM dbo.ServiceAreas WHERE PostalCode = N'94105');
    INSERT INTO dbo.OrganizerServiceAreas (OrganizerID, ServiceAreaID, IsActive, CreatedDate)
    VALUES (@OrgID, @Aid1, 1, SYSUTCDATETIME());
END

IF NOT EXISTS (SELECT 1 FROM dbo.OrganizerAvailability WHERE OrganizerID = @OrgID)
BEGIN
    INSERT INTO dbo.OrganizerAvailability (OrganizerID, AvailabilityDate, DayOfWeek, StartTime, EndTime, IsAvailable, IsBlocked, CreatedDate)
    VALUES (@OrgID, NULL, 1, '09:00:00', '17:00:00', 1, 0, SYSUTCDATETIME());
END

IF NOT EXISTS (SELECT 1 FROM dbo.OrganizerDocuments WHERE OrganizerID = @OrgID)
BEGIN
    INSERT INTO dbo.OrganizerDocuments (OrganizerID, DocumentType, FileURL, VerificationStatus, VerifiedBy, VerifiedDate, CreatedDate)
    VALUES (@OrgID, N'Identity', N'https://storage.nestify.com/docs/organizer_demo_gov_id.pdf', N'Approved', 1, SYSUTCDATETIME(), SYSUTCDATETIME());
END

-- Admin Demo
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE LoginID = N'admin@nestify.demo')
BEGIN
    INSERT INTO dbo.Users (LoginID, PasswordHash, Role, IsActive, CreatedDate)
    VALUES (N'admin@nestify.demo', @AdminHash, N'Admin', 1, SYSUTCDATETIME());
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

PRINT '============================================================================';
PRINT '  MASTER DEPLOYMENT COMPLETED SUCCESSFULLY FOR NESTIFYDB';
PRINT '============================================================================';
GO
