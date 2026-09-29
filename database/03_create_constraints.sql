-- ============================================================================
-- Script: 03_create_constraints.sql
-- Description: Creates all Foreign Keys, Unique Constraints, and Check Constraints
--              for NestifyDB enforcing data integrity and business rules.
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
GO

PRINT 'Creating Unique Constraints...';
GO

-- 1. Unique Constraints
IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_Users_LoginID')
    ALTER TABLE dbo.Users ADD CONSTRAINT UQ_Users_LoginID UNIQUE NONCLUSTERED (LoginID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_Customers_UserID')
    ALTER TABLE dbo.Customers ADD CONSTRAINT UQ_Customers_UserID UNIQUE NONCLUSTERED (UserID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_Organizers_UserID')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT UQ_Organizers_UserID UNIQUE NONCLUSTERED (UserID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_OrganizerSkills_Organizer_Service')
    ALTER TABLE dbo.OrganizerSkills ADD CONSTRAINT UQ_OrganizerSkills_Organizer_Service UNIQUE NONCLUSTERED (OrganizerID, ServiceID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_OrganizerServiceAreas_Organizer_Area')
    ALTER TABLE dbo.OrganizerServiceAreas ADD CONSTRAINT UQ_OrganizerServiceAreas_Organizer_Area UNIQUE NONCLUSTERED (OrganizerID, ServiceAreaID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_Reviews_BookingID')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT UQ_Reviews_BookingID UNIQUE NONCLUSTERED (BookingID);
GO

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE [name] = N'UQ_PromoCodes_Code')
    ALTER TABLE dbo.PromoCodes ADD CONSTRAINT UQ_PromoCodes_Code UNIQUE NONCLUSTERED (Code);
GO

PRINT 'Unique constraints created successfully.';
GO

PRINT 'Creating Check Constraints...';
GO

-- 2. Check Constraints
-- Users Role check
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Users_Role')
    ALTER TABLE dbo.Users ADD CONSTRAINT CK_Users_Role 
    CHECK (Role IN (N'Customer', N'Organizer', N'Admin'));
GO

-- Customers Status check
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Customers_Status')
    ALTER TABLE dbo.Customers ADD CONSTRAINT CK_Customers_Status 
    CHECK (Status IN (N'Active', N'Inactive', N'Suspended', N'Pending'));
GO

-- Organizers Verification & Approval Status checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_ApprovalStatus')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_ApprovalStatus 
    CHECK (ApprovalStatus IN (N'Pending', N'Approved', N'Rejected', N'Suspended'));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_VerificationStatus')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_VerificationStatus 
    CHECK (VerificationStatus IN (N'Pending', N'Approved', N'Rejected'));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_Rating')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_Rating 
    CHECK (Rating >= 0.00 AND Rating <= 5.00);
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_CompletedJobs')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_CompletedJobs 
    CHECK (CompletedJobs >= 0);
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Organizers_ExperienceYears')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT CK_Organizers_ExperienceYears 
    CHECK (ExperienceYears IS NULL OR ExperienceYears >= 0);
GO

-- Services checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Services_StartingPrice')
    ALTER TABLE dbo.Services ADD CONSTRAINT CK_Services_StartingPrice 
    CHECK (StartingPrice >= 0.00);
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Services_EstimatedDuration')
    ALTER TABLE dbo.Services ADD CONSTRAINT CK_Services_EstimatedDuration 
    CHECK (EstimatedDuration IS NULL OR EstimatedDuration > 0);
GO

-- OrganizerSkills checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_OrganizerSkills_ExperienceYears')
    ALTER TABLE dbo.OrganizerSkills ADD CONSTRAINT CK_OrganizerSkills_ExperienceYears 
    CHECK (ExperienceYears IS NULL OR ExperienceYears >= 0);
GO

-- OrganizerAvailability checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_OrganizerAvailability_Times')
    ALTER TABLE dbo.OrganizerAvailability ADD CONSTRAINT CK_OrganizerAvailability_Times 
    CHECK (EndTime > StartTime);
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_OrganizerAvailability_DayOrDate')
    ALTER TABLE dbo.OrganizerAvailability ADD CONSTRAINT CK_OrganizerAvailability_DayOrDate 
    CHECK (AvailabilityDate IS NOT NULL OR DayOfWeek IS NOT NULL);
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_OrganizerAvailability_DayOfWeek')
    ALTER TABLE dbo.OrganizerAvailability ADD CONSTRAINT CK_OrganizerAvailability_DayOfWeek 
    CHECK (DayOfWeek IS NULL OR (DayOfWeek >= 0 AND DayOfWeek <= 6));
GO

-- Bookings checks (Controlled Booking Lifecycle & Payment States)
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Bookings_Status')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT CK_Bookings_Status 
    CHECK (Status IN (
        N'Requested', N'Offered', N'Accepted', N'OnTheWay', N'Arrived', 
        N'Started', N'InProgress', N'Completed', N'CustomerConfirmed', 
        N'Paid', N'Cancelled', N'Rescheduled', N'Rejected', N'Expired', N'Disputed'
    ));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Bookings_PaymentStatus')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT CK_Bookings_PaymentStatus 
    CHECK (PaymentStatus IN (
        N'Pending', N'Initiated', N'Success', N'Failed', 
        N'Refunded', N'PartiallyRefunded', N'Cancelled'
    ));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Bookings_Amounts')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT CK_Bookings_Amounts 
    CHECK (BaseAmount >= 0.00 AND ServiceFee >= 0.00 AND DiscountAmount >= 0.00 AND TotalAmount >= 0.00);
GO

-- BookingItems checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_BookingItems_Amounts')
    ALTER TABLE dbo.BookingItems ADD CONSTRAINT CK_BookingItems_Amounts 
    CHECK ((Quantity IS NULL OR Quantity >= 0) AND (UnitPrice IS NULL OR UnitPrice >= 0.00) AND (Amount IS NULL OR Amount >= 0.00));
GO

-- BookingPhotos PhotoType check
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_BookingPhotos_PhotoType')
    ALTER TABLE dbo.BookingPhotos ADD CONSTRAINT CK_BookingPhotos_PhotoType 
    CHECK (PhotoType IN (N'Request', N'Before', N'After'));
GO

-- Payments checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Payments_PaymentStatus')
    ALTER TABLE dbo.Payments ADD CONSTRAINT CK_Payments_PaymentStatus 
    CHECK (PaymentStatus IN (
        N'Pending', N'Initiated', N'Success', N'Failed', 
        N'Refunded', N'PartiallyRefunded', N'Cancelled'
    ));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Payments_Amount')
    ALTER TABLE dbo.Payments ADD CONSTRAINT CK_Payments_Amount 
    CHECK (Amount >= 0.00);
GO

-- PaymentTransactions checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_PaymentTransactions_Status')
    ALTER TABLE dbo.PaymentTransactions ADD CONSTRAINT CK_PaymentTransactions_Status 
    CHECK (Status IN (N'Pending', N'Success', N'Failed'));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_PaymentTransactions_Amount')
    ALTER TABLE dbo.PaymentTransactions ADD CONSTRAINT CK_PaymentTransactions_Amount 
    CHECK (Amount >= 0.00);
GO

-- Payouts checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Payouts_PayoutStatus')
    ALTER TABLE dbo.Payouts ADD CONSTRAINT CK_Payouts_PayoutStatus 
    CHECK (PayoutStatus IN (N'Pending', N'Processing', N'Paid', N'Failed', N'Cancelled'));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Payouts_Amounts')
    ALTER TABLE dbo.Payouts ADD CONSTRAINT CK_Payouts_Amounts 
    CHECK (GrossAmount >= 0.00 AND PlatformFee >= 0.00 AND NetAmount >= 0.00);
GO

-- Reviews check
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Reviews_Rating')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT CK_Reviews_Rating 
    CHECK (Rating >= 1 AND Rating <= 5);
GO

-- Notifications NotificationType check
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_Notifications_NotificationType')
    ALTER TABLE dbo.Notifications ADD CONSTRAINT CK_Notifications_NotificationType 
    CHECK (NotificationType IN (
        N'BookingConfirmed', N'OrganizerAssigned', N'OrganizerAccepted', 
        N'OrganizerOnTheWay', N'OrganizerArrived', N'ServiceStarted', 
        N'ServiceCompleted', N'PaymentSuccess', N'PaymentFailed', 
        N'ReviewReminder', N'BookingCancelled', N'BookingRescheduled', 
        N'AdminMessage'
    ));
GO

-- PromoCodes checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_PromoCodes_DiscountType')
    ALTER TABLE dbo.PromoCodes ADD CONSTRAINT CK_PromoCodes_DiscountType 
    CHECK (DiscountType IN (N'Percentage', N'Fixed'));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_PromoCodes_Dates')
    ALTER TABLE dbo.PromoCodes ADD CONSTRAINT CK_PromoCodes_Dates 
    CHECK (ValidTo >= ValidFrom);
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_PromoCodes_DiscountValue')
    ALTER TABLE dbo.PromoCodes ADD CONSTRAINT CK_PromoCodes_DiscountValue 
    CHECK (DiscountValue > 0.00);
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_PromoCodes_UsedCount')
    ALTER TABLE dbo.PromoCodes ADD CONSTRAINT CK_PromoCodes_UsedCount 
    CHECK (UsedCount >= 0);
GO

-- SupportTickets Status check
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_SupportTickets_Status')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT CK_SupportTickets_Status 
    CHECK (Status IN (N'Open', N'InReview', N'Waiting', N'Resolved', N'Closed'));
GO

-- OrganizerDocuments checks
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_OrganizerDocuments_DocumentType')
    ALTER TABLE dbo.OrganizerDocuments ADD CONSTRAINT CK_OrganizerDocuments_DocumentType 
    CHECK (DocumentType IN (N'Identity', N'AddressProof', N'Certification', N'ProfileDocument'));
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE [name] = N'CK_OrganizerDocuments_VerificationStatus')
    ALTER TABLE dbo.OrganizerDocuments ADD CONSTRAINT CK_OrganizerDocuments_VerificationStatus 
    CHECK (VerificationStatus IN (N'Pending', N'Approved', N'Rejected'));
GO

PRINT 'Check constraints created successfully.';
GO

PRINT 'Creating Foreign Key Constraints with NO ACTION to protect financial/audit records...';
GO

-- 3. Foreign Key Relationships
-- Customers -> Users
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Customers_Users')
    ALTER TABLE dbo.Customers ADD CONSTRAINT FK_Customers_Users 
    FOREIGN KEY (UserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;
GO

-- Organizers -> Users
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Organizers_Users')
    ALTER TABLE dbo.Organizers ADD CONSTRAINT FK_Organizers_Users 
    FOREIGN KEY (UserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;
GO

-- Services -> ServiceCategories
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Services_Categories')
    ALTER TABLE dbo.Services ADD CONSTRAINT FK_Services_Categories 
    FOREIGN KEY (CategoryID) REFERENCES dbo.ServiceCategories (CategoryID) ON DELETE NO ACTION;
GO

-- OrganizerSkills -> Organizers & Services
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerSkills_Organizers')
    ALTER TABLE dbo.OrganizerSkills ADD CONSTRAINT FK_OrganizerSkills_Organizers 
    FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerSkills_Services')
    ALTER TABLE dbo.OrganizerSkills ADD CONSTRAINT FK_OrganizerSkills_Services 
    FOREIGN KEY (ServiceID) REFERENCES dbo.Services (ServiceID) ON DELETE NO ACTION;
GO

-- OrganizerServiceAreas -> Organizers & ServiceAreas
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerServiceAreas_Organizers')
    ALTER TABLE dbo.OrganizerServiceAreas ADD CONSTRAINT FK_OrganizerServiceAreas_Organizers 
    FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerServiceAreas_ServiceAreas')
    ALTER TABLE dbo.OrganizerServiceAreas ADD CONSTRAINT FK_OrganizerServiceAreas_ServiceAreas 
    FOREIGN KEY (ServiceAreaID) REFERENCES dbo.ServiceAreas (ServiceAreaID) ON DELETE NO ACTION;
GO

-- OrganizerAvailability -> Organizers
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerAvailability_Organizers')
    ALTER TABLE dbo.OrganizerAvailability ADD CONSTRAINT FK_OrganizerAvailability_Organizers 
    FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;
GO

-- Addresses -> Customers
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Addresses_Customers')
    ALTER TABLE dbo.Addresses ADD CONSTRAINT FK_Addresses_Customers 
    FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;
GO

-- Bookings -> Customers, Organizers, Services, Addresses
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Bookings_Customers')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT FK_Bookings_Customers 
    FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Bookings_Organizers')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT FK_Bookings_Organizers 
    FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Bookings_Services')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT FK_Bookings_Services 
    FOREIGN KEY (ServiceID) REFERENCES dbo.Services (ServiceID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Bookings_Addresses')
    ALTER TABLE dbo.Bookings ADD CONSTRAINT FK_Bookings_Addresses 
    FOREIGN KEY (AddressID) REFERENCES dbo.Addresses (AddressID) ON DELETE NO ACTION;
GO

-- BookingItems -> Bookings
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingItems_Bookings')
    ALTER TABLE dbo.BookingItems ADD CONSTRAINT FK_BookingItems_Bookings 
    FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;
GO

-- BookingPhotos -> Bookings & Users
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingPhotos_Bookings')
    ALTER TABLE dbo.BookingPhotos ADD CONSTRAINT FK_BookingPhotos_Bookings 
    FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingPhotos_Users')
    ALTER TABLE dbo.BookingPhotos ADD CONSTRAINT FK_BookingPhotos_Users 
    FOREIGN KEY (UploadedByUserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;
GO

-- Payments -> Bookings & Customers
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Payments_Bookings')
    ALTER TABLE dbo.Payments ADD CONSTRAINT FK_Payments_Bookings 
    FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Payments_Customers')
    ALTER TABLE dbo.Payments ADD CONSTRAINT FK_Payments_Customers 
    FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;
GO

-- PaymentTransactions -> Payments
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_PaymentTransactions_Payments')
    ALTER TABLE dbo.PaymentTransactions ADD CONSTRAINT FK_PaymentTransactions_Payments 
    FOREIGN KEY (PaymentID) REFERENCES dbo.Payments (PaymentID) ON DELETE NO ACTION;
GO

-- Payouts -> Organizers & Bookings
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Payouts_Organizers')
    ALTER TABLE dbo.Payouts ADD CONSTRAINT FK_Payouts_Organizers 
    FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Payouts_Bookings')
    ALTER TABLE dbo.Payouts ADD CONSTRAINT FK_Payouts_Bookings 
    FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;
GO

-- Reviews -> Bookings, Customers, Organizers
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Reviews_Bookings')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT FK_Reviews_Bookings 
    FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Reviews_Customers')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT FK_Reviews_Customers 
    FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Reviews_Organizers')
    ALTER TABLE dbo.Reviews ADD CONSTRAINT FK_Reviews_Organizers 
    FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;
GO

-- Notifications -> Users
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_Notifications_Users')
    ALTER TABLE dbo.Notifications ADD CONSTRAINT FK_Notifications_Users 
    FOREIGN KEY (UserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;
GO

-- SupportTickets -> Customers, Organizers, Bookings, Users (Admin)
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_SupportTickets_Customers')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT FK_SupportTickets_Customers 
    FOREIGN KEY (CustomerID) REFERENCES dbo.Customers (CustomerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_SupportTickets_Organizers')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT FK_SupportTickets_Organizers 
    FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_SupportTickets_Bookings')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT FK_SupportTickets_Bookings 
    FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_SupportTickets_Admins')
    ALTER TABLE dbo.SupportTickets ADD CONSTRAINT FK_SupportTickets_Admins 
    FOREIGN KEY (AssignedAdminID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;
GO

-- OrganizerDocuments -> Organizers & Users (Admin Verifier)
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerDocuments_Organizers')
    ALTER TABLE dbo.OrganizerDocuments ADD CONSTRAINT FK_OrganizerDocuments_Organizers 
    FOREIGN KEY (OrganizerID) REFERENCES dbo.Organizers (OrganizerID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_OrganizerDocuments_Verifiers')
    ALTER TABLE dbo.OrganizerDocuments ADD CONSTRAINT FK_OrganizerDocuments_Verifiers 
    FOREIGN KEY (VerifiedBy) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;
GO

-- BookingStatusHistory -> Bookings & Users
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingStatusHistory_Bookings')
    ALTER TABLE dbo.BookingStatusHistory ADD CONSTRAINT FK_BookingStatusHistory_Bookings 
    FOREIGN KEY (BookingID) REFERENCES dbo.Bookings (BookingID) ON DELETE NO ACTION;
GO

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE [name] = N'FK_BookingStatusHistory_Users')
    ALTER TABLE dbo.BookingStatusHistory ADD CONSTRAINT FK_BookingStatusHistory_Users 
    FOREIGN KEY (ChangedByUserID) REFERENCES dbo.Users (UserID) ON DELETE NO ACTION;
GO

PRINT 'All constraints created successfully.';
GO
