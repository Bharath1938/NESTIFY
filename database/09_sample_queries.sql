-- ============================================================================
-- Script: 09_sample_queries.sql
-- Description: Complete sample query library for testing, reporting,
--              and backend API implementation for Nestify Home Organization Marketplace.
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
GO

PRINT '=============================================================';
PRINT '1. AUTHENTICATION & LOGIN VERIFICATION';
PRINT '=============================================================';

-- Test Customer Login
EXEC dbo.sp_AuthenticateUser 
    @LoginID = N'customer@nestify.demo', 
    @Password = N'Nestify@123', 
    @ExpectedRole = N'Customer';

-- Test Organizer Login
EXEC dbo.sp_AuthenticateUser 
    @LoginID = N'organizer@nestify.demo', 
    @Password = N'Nestify@456', 
    @ExpectedRole = N'Organizer';

-- Test Admin Login
EXEC dbo.sp_AuthenticateUser 
    @LoginID = N'admin@nestify.demo', 
    @Password = N'Nestify@Admin2026!', 
    @ExpectedRole = N'Admin';
GO

PRINT '=============================================================';
PRINT '2. CUSTOMER MOBILE APP QUERIES';
PRINT '=============================================================';

-- A. Browse Active Services Catalog grouped by Category
SELECT 
    CategoryName,
    ServiceName,
    StartingPrice,
    EstimatedDuration,
    ServiceDescription,
    IncludedDetails
FROM dbo.vw_ActiveServices
ORDER BY CategoryDisplayOrder, StartingPrice;

-- B. Customer View Profile and Addresses
DECLARE @DemoCustID BIGINT = (SELECT TOP 1 CustomerID FROM dbo.Customers WHERE Email = N'customer@nestify.demo');
EXEC dbo.sp_GetCustomerProfile @CustomerID = @DemoCustID;

-- C. Customer View Bookings (Active and Past)
EXEC dbo.sp_GetCustomerBookings @CustomerID = @DemoCustID, @StatusFilter = NULL;

-- D. Customer View 360-Degree Booking Detail
DECLARE @SampleBookingID BIGINT = (SELECT TOP 1 BookingID FROM dbo.Bookings WHERE CustomerID = @DemoCustID ORDER BY BookingID ASC);
EXEC dbo.sp_GetBookingDetail @BookingID = @SampleBookingID;
GO

PRINT '=============================================================';
PRINT '3. ORGANIZER MOBILE APP QUERIES';
PRINT '=============================================================';

-- A. Organizer View Available Jobs Matching Skills and Service Area
DECLARE @DemoOrgID BIGINT = (SELECT TOP 1 OrganizerID FROM dbo.Organizers WHERE Email = N'organizer@nestify.demo');
EXEC dbo.sp_GetOrganizerAvailableJobs @OrganizerID = @DemoOrgID;

-- B. Organizer Active & Assigned Schedule
SELECT 
    b.BookingID,
    b.BookingDate,
    b.StartTime,
    b.EstimatedDuration,
    b.Status,
    s.ServiceName,
    c.FullName AS CustomerName,
    c.Phone AS CustomerPhone,
    a.AddressLine1,
    a.City,
    a.Landmark
FROM dbo.Bookings b
INNER JOIN dbo.Services s ON b.ServiceID = s.ServiceID
INNER JOIN dbo.Customers c ON b.CustomerID = c.CustomerID
INNER JOIN dbo.Addresses a ON b.AddressID = a.AddressID
WHERE b.OrganizerID = @DemoOrgID
  AND b.Status IN (N'Accepted', N'OnTheWay', N'Arrived', N'Started', N'InProgress')
ORDER BY b.BookingDate ASC, b.StartTime ASC;

-- C. Organizer Financial Earnings Summary
SELECT * FROM dbo.vw_OrganizerEarningsSummary WHERE OrganizerID = @DemoOrgID;

-- D. Organizer Individual Payout Breakdown
SELECT 
    p.PayoutID,
    p.BookingID,
    b.BookingDate,
    s.ServiceName,
    p.GrossAmount,
    p.PlatformFee,
    p.NetAmount,
    p.PayoutStatus,
    p.PayoutReference,
    p.PayoutDate
FROM dbo.Payouts p
INNER JOIN dbo.Bookings b ON p.BookingID = b.BookingID
INNER JOIN dbo.Services s ON b.ServiceID = s.ServiceID
WHERE p.OrganizerID = @DemoOrgID
ORDER BY p.CreatedDate DESC;
GO

PRINT '=============================================================';
PRINT '4. ADMIN WEB PANEL QUERIES';
PRINT '=============================================================';

-- A. Admin KPI Dashboard Summary
EXEC dbo.sp_AdminGetDashboardSummary;

-- B. Daily Financial and Operational Performance Trend
SELECT * FROM dbo.vw_MarketplaceDailyPerformance ORDER BY SummaryDate DESC;

-- C. Pending Organizer Verification Applications Queue
SELECT * FROM dbo.vw_PendingOrganizerVerifications;

-- D. Full Booking Status Audit Trail for Compliance / Dispute Investigation
SELECT 
    b.BookingID,
    b.Status AS CurrentStatus,
    h.OldStatus,
    h.NewStatus,
    u.LoginID AS TransitionBy,
    h.Remarks,
    h.ChangedDate
FROM dbo.BookingStatusHistory h
INNER JOIN dbo.Bookings b ON h.BookingID = b.BookingID
INNER JOIN dbo.Users u ON h.ChangedByUserID = u.UserID
ORDER BY h.BookingID ASC, h.ChangedDate ASC;

-- E. Reviews & Organizer Quality Monitoring
SELECT 
    r.ReviewID,
    o.FullName AS OrganizerName,
    c.FullName AS CustomerName,
    r.Rating,
    r.ReviewText,
    r.CreatedDate
FROM dbo.Reviews r
INNER JOIN dbo.Organizers o ON r.OrganizerID = o.OrganizerID
INNER JOIN dbo.Customers c ON r.CustomerID = c.CustomerID
ORDER BY r.CreatedDate DESC;
GO
