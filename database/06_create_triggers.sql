-- ============================================================================
-- Script: 06_create_triggers.sql
-- Description: Creates data integrity, auditing, and business automation triggers
--              for NestifyDB (address defaults, booking audits, rating recalcs).
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

PRINT 'Creating Database Triggers...';
GO

-- 1. Trigger: Enforce Single Default Address per Customer
-- When an address is set as default, set IsDefault = 0 on all other addresses of that customer.
CREATE OR ALTER TRIGGER dbo.TR_Addresses_SingleDefault
ON dbo.Addresses
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Only proceed if IsDefault was set to 1 in the inserted/updated rows
    IF EXISTS (SELECT 1 FROM inserted WHERE IsDefault = 1)
    BEGIN
        UPDATE a
        SET a.IsDefault = 0,
            a.UpdatedDate = SYSUTCDATETIME()
        FROM dbo.Addresses a
        INNER JOIN inserted i ON a.CustomerID = i.CustomerID
        WHERE a.AddressID <> i.AddressID
          AND a.IsDefault = 1
          AND i.IsDefault = 1;
    END
END;
GO

-- 2. Trigger: Automatic Booking Status Audit Logging
-- When a booking status changes, automatically create a record in BookingStatusHistory.
CREATE OR ALTER TRIGGER dbo.TR_Bookings_AuditStatusHistory
ON dbo.Bookings
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Only record if Status actually changed
    IF UPDATE(Status)
    BEGIN
        INSERT INTO dbo.BookingStatusHistory (
            BookingID,
            OldStatus,
            NewStatus,
            ChangedByUserID,
            Remarks,
            ChangedDate
        )
        SELECT 
            i.BookingID,
            d.Status AS OldStatus,
            i.Status AS NewStatus,
            -- If OrganizerID is assigned, attribute to Organizer's UserID or Customer's UserID or System
            COALESCE(
                (SELECT c.UserID FROM dbo.Customers c WHERE c.CustomerID = i.CustomerID),
                1 -- System default
            ) AS ChangedByUserID,
            CONCAT(N'Automated transition from ', d.Status, N' to ', i.Status),
            SYSUTCDATETIME()
        FROM inserted i
        INNER JOIN deleted d ON i.BookingID = d.BookingID
        WHERE i.Status <> d.Status;
    END
END;
GO

-- 3. Trigger: Recalculate Organizer Rating and Completed Jobs on Review
-- When a review is inserted, updated, or removed, recalculate the organizer's overall rating.
CREATE OR ALTER TRIGGER dbo.TR_Reviews_UpdateOrganizerRating
ON dbo.Reviews
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    -- Collect affected Organizer IDs
    DECLARE @AffectedOrganizers TABLE (OrganizerID BIGINT PRIMARY KEY);

    INSERT INTO @AffectedOrganizers (OrganizerID)
    SELECT DISTINCT OrganizerID FROM inserted WHERE OrganizerID IS NOT NULL
    UNION
    SELECT DISTINCT OrganizerID FROM deleted WHERE OrganizerID IS NOT NULL;

    -- Recalculate average rating for each affected organizer
    UPDATE o
    SET 
        o.Rating = COALESCE(stats.AvgRating, 0.00),
        o.CompletedJobs = COALESCE(jobStats.TotalCompleted, o.CompletedJobs),
        o.UpdatedDate = SYSUTCDATETIME()
    FROM dbo.Organizers o
    INNER JOIN @AffectedOrganizers ao ON o.OrganizerID = ao.OrganizerID
    OUTER APPLY (
        SELECT 
            ROUND(AVG(CAST(r.Rating AS DECIMAL(3,2))), 2) AS AvgRating
        FROM dbo.Reviews r
        WHERE r.OrganizerID = o.OrganizerID
          AND r.IsPublished = 1
    ) stats
    OUTER APPLY (
        SELECT 
            COUNT(1) AS TotalCompleted
        FROM dbo.Bookings b
        WHERE b.OrganizerID = o.OrganizerID
          AND b.Status IN (N'Completed', N'CustomerConfirmed', N'Paid')
    ) jobStats;
END;
GO

-- 4. Trigger: Enforce Review Eligibility Rule
-- A customer can only review a booking that has reached 'Completed', 'CustomerConfirmed', or 'Paid' status.
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

-- 5. Trigger: Auto-Update Booking Payment Status upon Successful Payment
CREATE OR ALTER TRIGGER dbo.TR_Payments_SyncBookingPaymentStatus
ON dbo.Payments
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF UPDATE(PaymentStatus)
    BEGIN
        UPDATE b
        SET 
            b.PaymentStatus = i.PaymentStatus,
            b.UpdatedDate = SYSUTCDATETIME()
        FROM dbo.Bookings b
        INNER JOIN inserted i ON b.BookingID = i.BookingID
        WHERE b.PaymentStatus <> i.PaymentStatus;
    END
END;
GO

PRINT 'Triggers created successfully.';
GO
