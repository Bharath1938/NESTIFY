-- ============================================================================
-- Script: 04_create_indexes.sql
-- Description: Creates performance, covering, and filtered indexes for NestifyDB
--              optimized for customer booking, organizer matching, and admin analytics.
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

PRINT 'Creating Indexes for Marketplace Performance...';
GO

-- 1. Bookings Indexes (High-throughput marketplace query paths)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_CustomerID_BookingDate')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Bookings_CustomerID_BookingDate
    ON dbo.Bookings (CustomerID, BookingDate DESC)
    INCLUDE (Status, PaymentStatus, TotalAmount, ServiceID, OrganizerID);
    PRINT 'Created index: IX_Bookings_CustomerID_BookingDate';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_OrganizerID_BookingDate')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Bookings_OrganizerID_BookingDate
    ON dbo.Bookings (OrganizerID, BookingDate DESC)
    INCLUDE (Status, PaymentStatus, TotalAmount, ServiceID, CustomerID);
    PRINT 'Created index: IX_Bookings_OrganizerID_BookingDate';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_Status_BookingDate')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Bookings_Status_BookingDate
    ON dbo.Bookings (Status, BookingDate DESC)
    INCLUDE (CustomerID, OrganizerID, ServiceID, TotalAmount);
    PRINT 'Created index: IX_Bookings_Status_BookingDate';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_ServiceID_BookingDate')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Bookings_ServiceID_BookingDate
    ON dbo.Bookings (ServiceID, BookingDate DESC)
    INCLUDE (Status, CustomerID, OrganizerID, TotalAmount);
    PRINT 'Created index: IX_Bookings_ServiceID_BookingDate';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_PaymentStatus')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Bookings_PaymentStatus
    ON dbo.Bookings (PaymentStatus, BookingDate DESC)
    INCLUDE (BookingID, CustomerID, OrganizerID, TotalAmount);
    PRINT 'Created index: IX_Bookings_PaymentStatus';
END
GO

-- Filtered Index: Open unassigned jobs available for organizers to accept
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Bookings_OpenJobs_Filtered')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Bookings_OpenJobs_Filtered
    ON dbo.Bookings (BookingDate, StartTime, ServiceID, AddressID)
    INCLUDE (CustomerID, TotalAmount, CustomerNotes)
    WHERE Status IN (N'Requested', N'Offered');
    PRINT 'Created filtered index: IX_Bookings_OpenJobs_Filtered';
END
GO

-- 2. Organizer Availability Indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_OrganizerAvailability_OrganizerID_Date')
BEGIN
    CREATE NONCLUSTERED INDEX IX_OrganizerAvailability_OrganizerID_Date
    ON dbo.OrganizerAvailability (OrganizerID, AvailabilityDate, DayOfWeek)
    INCLUDE (StartTime, EndTime, IsAvailable, IsBlocked);
    PRINT 'Created index: IX_OrganizerAvailability_OrganizerID_Date';
END
GO

-- 3. Organizer Skills & Service Areas Indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_OrganizerSkills_OrganizerID_ServiceID')
BEGIN
    CREATE NONCLUSTERED INDEX IX_OrganizerSkills_OrganizerID_ServiceID
    ON dbo.OrganizerSkills (OrganizerID, ServiceID)
    INCLUDE (ExperienceYears, IsActive);
    PRINT 'Created index: IX_OrganizerSkills_OrganizerID_ServiceID';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_OrganizerSkills_ServiceID_OrganizerID')
BEGIN
    CREATE NONCLUSTERED INDEX IX_OrganizerSkills_ServiceID_OrganizerID
    ON dbo.OrganizerSkills (ServiceID, IsActive)
    INCLUDE (OrganizerID, ExperienceYears);
    PRINT 'Created index: IX_OrganizerSkills_ServiceID_OrganizerID';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_OrganizerServiceAreas_OrganizerID_ServiceAreaID')
BEGIN
    CREATE NONCLUSTERED INDEX IX_OrganizerServiceAreas_OrganizerID_ServiceAreaID
    ON dbo.OrganizerServiceAreas (OrganizerID, ServiceAreaID)
    INCLUDE (IsActive);
    PRINT 'Created index: IX_OrganizerServiceAreas_OrganizerID_ServiceAreaID';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_OrganizerServiceAreas_ServiceAreaID_OrganizerID')
BEGIN
    CREATE NONCLUSTERED INDEX IX_OrganizerServiceAreas_ServiceAreaID_OrganizerID
    ON dbo.OrganizerServiceAreas (ServiceAreaID, IsActive)
    INCLUDE (OrganizerID);
    PRINT 'Created index: IX_OrganizerServiceAreas_ServiceAreaID_OrganizerID';
END
GO

-- Filtered Index: Approved and active organizers for job dispatch and public search
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Organizers_Approved_Active_Filtered')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Organizers_Approved_Active_Filtered
    ON dbo.Organizers (Rating DESC, CompletedJobs DESC)
    INCLUDE (OrganizerID, UserID, FullName, Phone, ProfilePhotoURL)
    WHERE ApprovalStatus = N'Approved' AND IsActive = 1;
    PRINT 'Created filtered index: IX_Organizers_Approved_Active_Filtered';
END
GO

-- 4. Customer Addresses Filtered Unique Index: Enforces exactly one default address per customer
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'UQ_Addresses_CustomerID_IsDefault')
BEGIN
    CREATE UNIQUE NONCLUSTERED INDEX UQ_Addresses_CustomerID_IsDefault
    ON dbo.Addresses (CustomerID)
    WHERE IsDefault = 1;
    PRINT 'Created unique filtered index: UQ_Addresses_CustomerID_IsDefault (Single Default Address)';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Addresses_CustomerID')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Addresses_CustomerID
    ON dbo.Addresses (CustomerID)
    INCLUDE (AddressLabel, City, State, PostalCode, IsDefault);
    PRINT 'Created index: IX_Addresses_CustomerID';
END
GO

-- 5. Booking Photos Indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_BookingPhotos_BookingID_PhotoType')
BEGIN
    CREATE NONCLUSTERED INDEX IX_BookingPhotos_BookingID_PhotoType
    ON dbo.BookingPhotos (BookingID, PhotoType)
    INCLUDE (FileURL, UploadedByUserID, CreatedDate);
    PRINT 'Created index: IX_BookingPhotos_BookingID_PhotoType';
END
GO

-- 6. Payments & Payment Transactions Indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Payments_BookingID_PaymentStatus')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Payments_BookingID_PaymentStatus
    ON dbo.Payments (BookingID, PaymentStatus)
    INCLUDE (CustomerID, Amount, CurrencyCode, PaidDate);
    PRINT 'Created index: IX_Payments_BookingID_PaymentStatus';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Payments_CustomerID')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Payments_CustomerID
    ON dbo.Payments (CustomerID, CreatedDate DESC)
    INCLUDE (BookingID, Amount, PaymentStatus);
    PRINT 'Created index: IX_Payments_CustomerID';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_PaymentTransactions_PaymentID')
BEGIN
    CREATE NONCLUSTERED INDEX IX_PaymentTransactions_PaymentID
    ON dbo.PaymentTransactions (PaymentID, CreatedDate DESC)
    INCLUDE (TransactionType, Amount, Status, GatewayReference);
    PRINT 'Created index: IX_PaymentTransactions_PaymentID';
END
GO

-- 7. Payouts Indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Payouts_OrganizerID_PayoutStatus')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Payouts_OrganizerID_PayoutStatus
    ON dbo.Payouts (OrganizerID, PayoutStatus, CreatedDate DESC)
    INCLUDE (BookingID, GrossAmount, PlatformFee, NetAmount, PayoutDate);
    PRINT 'Created index: IX_Payouts_OrganizerID_PayoutStatus';
END
GO

-- 8. Reviews Indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Reviews_OrganizerID_CreatedDate')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Reviews_OrganizerID_CreatedDate
    ON dbo.Reviews (OrganizerID, CreatedDate DESC)
    INCLUDE (BookingID, CustomerID, Rating, IsPublished);
    PRINT 'Created index: IX_Reviews_OrganizerID_CreatedDate';
END
GO

-- 9. Notifications Indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Notifications_UserID_IsRead')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Notifications_UserID_IsRead
    ON dbo.Notifications (UserID, IsRead, CreatedDate DESC)
    INCLUDE (Title, Message, NotificationType, ReferenceID);
    PRINT 'Created index: IX_Notifications_UserID_IsRead';
END
GO

-- 10. Audit, Support & Admin Indexes
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_BookingStatusHistory_BookingID')
BEGIN
    CREATE NONCLUSTERED INDEX IX_BookingStatusHistory_BookingID
    ON dbo.BookingStatusHistory (BookingID, ChangedDate DESC)
    INCLUDE (OldStatus, NewStatus, ChangedByUserID, Remarks);
    PRINT 'Created index: IX_BookingStatusHistory_BookingID';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_SupportTickets_Status_Assigned')
BEGIN
    CREATE NONCLUSTERED INDEX IX_SupportTickets_Status_Assigned
    ON dbo.SupportTickets (Status, AssignedAdminID, CreatedDate DESC)
    INCLUDE (CustomerID, OrganizerID, BookingID, Category);
    PRINT 'Created index: IX_SupportTickets_Status_Assigned';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE [name] = N'IX_Services_CategoryID_IsActive')
BEGIN
    CREATE NONCLUSTERED INDEX IX_Services_CategoryID_IsActive
    ON dbo.Services (CategoryID, IsActive)
    INCLUDE (ServiceName, StartingPrice, EstimatedDuration, ImageURL);
    PRINT 'Created index: IX_Services_CategoryID_IsActive';
END
GO

PRINT 'All indexes created successfully.';
GO
