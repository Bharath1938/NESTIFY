-- ============================================================================
-- Script: 05_create_views.sql
-- Description: Creates database views for Customer App, Organizer App,
--              Admin Web Panel, and .NET Web API reporting.
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
GO

PRINT 'Creating Views...';
GO

-- 1. Active Services Catalog View (Used by Customer mobile app and web frontend)
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

-- 2. Approved Organizers Directory View (Used for matching and public profiles)
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

-- 3. Comprehensive Booking Details View (Used by .NET API for rich DTO mapping)
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
    
    -- Customer Information
    c.CustomerID,
    c.FullName AS CustomerName,
    c.Phone AS CustomerPhone,
    c.Email AS CustomerEmail,
    c.ProfilePhotoURL AS CustomerPhotoURL,
    
    -- Organizer Information (Nullable)
    o.OrganizerID,
    o.FullName AS OrganizerName,
    o.Phone AS OrganizerPhone,
    o.Email AS OrganizerEmail,
    o.ProfilePhotoURL AS OrganizerPhotoURL,
    o.Rating AS OrganizerRating,
    
    -- Service Information
    s.ServiceID,
    s.ServiceName,
    cat.CategoryID,
    cat.CategoryName,
    
    -- Address Information
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

    -- Counts of related items
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

-- 4. Organizer Earnings Summary View (Used by Organizer App & Payouts processing)
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
GROUP BY 
    o.OrganizerID,
    o.FullName,
    o.Phone,
    o.Rating,
    o.CompletedJobs;
GO

-- 5. Admin Marketplace Daily Performance View (Executive KPI Dashboard)
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

-- 6. Pending Organizer Verifications View (Admin compliance and onboarding queue)
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

PRINT 'Views created successfully.';
GO
