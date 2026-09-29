-- ============================================================================
-- Script: rollback.sql
-- Description: Rollback script for NestifyDB. Drops all triggers, views,
--              stored procedures, constraints, and tables in reverse dependency order.
--              Optionally drops the database.
-- Database: NestifyDB
-- ============================================================================

USE [NestifyDB];
GO

SET NOCOUNT ON;
GO

PRINT 'Starting Rollback for NestifyDB...';
GO

-- 1. Drop Triggers
PRINT 'Dropping Triggers...';
DROP TRIGGER IF EXISTS dbo.TR_Payments_SyncBookingPaymentStatus;
DROP TRIGGER IF EXISTS dbo.TR_Reviews_ValidateEligibility;
DROP TRIGGER IF EXISTS dbo.TR_Reviews_UpdateOrganizerRating;
DROP TRIGGER IF EXISTS dbo.TR_Bookings_AuditStatusHistory;
DROP TRIGGER IF EXISTS dbo.TR_Addresses_SingleDefault;
GO

-- 2. Drop Stored Procedures and Functions
PRINT 'Dropping Stored Procedures and Functions...';
DROP PROCEDURE IF EXISTS dbo.sp_AdminReviewOrganizer;
DROP PROCEDURE IF EXISTS dbo.sp_AdminGetDashboardSummary;
DROP PROCEDURE IF EXISTS dbo.sp_ProcessPaymentSuccess;
DROP PROCEDURE IF EXISTS dbo.sp_UpdateBookingStatus;
DROP PROCEDURE IF EXISTS dbo.sp_OrganizerAcceptJob;
DROP PROCEDURE IF EXISTS dbo.sp_GetOrganizerAvailableJobs;
DROP PROCEDURE IF EXISTS dbo.sp_GetBookingDetail;
DROP PROCEDURE IF EXISTS dbo.sp_GetCustomerBookings;
DROP PROCEDURE IF EXISTS dbo.sp_CreateBooking;
DROP PROCEDURE IF EXISTS dbo.sp_SaveCustomerAddress;
DROP PROCEDURE IF EXISTS dbo.sp_GetCustomerProfile;
DROP PROCEDURE IF EXISTS dbo.sp_AuthenticateUser;
DROP PROCEDURE IF EXISTS dbo.sp_RegisterOrganizer;
DROP PROCEDURE IF EXISTS dbo.sp_RegisterCustomer;
DROP FUNCTION IF EXISTS dbo.fn_VerifyPassword;
DROP FUNCTION IF EXISTS dbo.fn_HashPassword;
GO

-- 3. Drop Views
PRINT 'Dropping Views...';
DROP VIEW IF EXISTS dbo.vw_PendingOrganizerVerifications;
DROP VIEW IF EXISTS dbo.vw_MarketplaceDailyPerformance;
DROP VIEW IF EXISTS dbo.vw_OrganizerEarningsSummary;
DROP VIEW IF EXISTS dbo.vw_BookingDetails;
DROP VIEW IF EXISTS dbo.vw_ApprovedOrganizers;
DROP VIEW IF EXISTS dbo.vw_ActiveServices;
GO

-- 4. Drop Tables in Reverse Dependency Order
PRINT 'Dropping Tables in Reverse Dependency Order...';
DROP TABLE IF EXISTS dbo.BookingStatusHistory;
DROP TABLE IF EXISTS dbo.OrganizerDocuments;
DROP TABLE IF EXISTS dbo.SupportTickets;
DROP TABLE IF EXISTS dbo.PromoCodes;
DROP TABLE IF EXISTS dbo.Notifications;
DROP TABLE IF EXISTS dbo.Reviews;
DROP TABLE IF EXISTS dbo.Payouts;
DROP TABLE IF EXISTS dbo.PaymentTransactions;
DROP TABLE IF EXISTS dbo.Payments;
DROP TABLE IF EXISTS dbo.BookingPhotos;
DROP TABLE IF EXISTS dbo.BookingItems;
DROP TABLE IF EXISTS dbo.Bookings;
DROP TABLE IF EXISTS dbo.Addresses;
DROP TABLE IF EXISTS dbo.OrganizerAvailability;
DROP TABLE IF EXISTS dbo.OrganizerServiceAreas;
DROP TABLE IF EXISTS dbo.ServiceAreas;
DROP TABLE IF EXISTS dbo.OrganizerSkills;
DROP TABLE IF EXISTS dbo.Services;
DROP TABLE IF EXISTS dbo.ServiceCategories;
DROP TABLE IF EXISTS dbo.Organizers;
DROP TABLE IF EXISTS dbo.Customers;
DROP TABLE IF EXISTS dbo.Users;
GO

PRINT 'All objects dropped successfully from NestifyDB.';
GO

-- Optional: To completely drop the database itself, uncomment the following block:
/*
USE [master];
GO
IF EXISTS (SELECT 1 FROM sys.databases WHERE [name] = N'NestifyDB')
BEGIN
    ALTER DATABASE [NestifyDB] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [NestifyDB];
    PRINT 'Database NestifyDB dropped.';
END
GO
*/
