-- ============================================================================
-- Script: 01_create_database.sql
-- Description: Creates the NestifyDB database with production-grade configuration
--              including Read Committed Snapshot Isolation (RCSI) for high-concurrency
--              marketplace transactions.
-- Target: Microsoft SQL Server 2019 / 2022 / 2025 / Azure SQL
-- Schema: dbo
-- ============================================================================

USE [master];
GO

SET NOCOUNT ON;
GO

-- 1. Create NestifyDB if it does not already exist
IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE [name] = N'NestifyDB')
BEGIN
    PRINT 'Creating database NestifyDB...';
    CREATE DATABASE [NestifyDB]
    COLLATE SQL_Latin1_General_CP1_CI_AS;
    PRINT 'Database NestifyDB created successfully.';
END
ELSE
BEGIN
    PRINT 'Database NestifyDB already exists.';
END
GO

-- 2. Configure Database Options for Production Marketplace Concurrency
USE [master];
GO

-- Enable Read Committed Snapshot Isolation (RCSI) so readers don't block writers
-- and writers don't block readers in high-traffic marketplace bookings.
IF EXISTS (SELECT 1 FROM sys.databases WHERE [name] = N'NestifyDB' AND is_read_committed_snapshot_on = 0)
BEGIN
    PRINT 'Enabling READ_COMMITTED_SNAPSHOT on NestifyDB...';
    ALTER DATABASE [NestifyDB] SET READ_COMMITTED_SNAPSHOT ON WITH ROLLBACK IMMEDIATE;
    PRINT 'READ_COMMITTED_SNAPSHOT enabled.';
END
GO

IF EXISTS (SELECT 1 FROM sys.databases WHERE [name] = N'NestifyDB' AND snapshot_isolation_state = 0)
BEGIN
    PRINT 'Enabling ALLOW_SNAPSHOT_ISOLATION on NestifyDB...';
    ALTER DATABASE [NestifyDB] SET ALLOW_SNAPSHOT_ISOLATION ON;
    PRINT 'ALLOW_SNAPSHOT_ISOLATION enabled.';
END
GO

-- Set standard ANSI options and Auto-Update Statistics
ALTER DATABASE [NestifyDB] SET ANSI_NULL_DEFAULT ON;
ALTER DATABASE [NestifyDB] SET ANSI_NULLS ON;
ALTER DATABASE [NestifyDB] SET ANSI_PADDING ON;
ALTER DATABASE [NestifyDB] SET ANSI_WARNINGS ON;
ALTER DATABASE [NestifyDB] SET ARITHABORT ON;
ALTER DATABASE [NestifyDB] SET CONCAT_NULL_YIELDS_NULL ON;
ALTER DATABASE [NestifyDB] SET QUOTED_IDENTIFIER ON;
ALTER DATABASE [NestifyDB] SET AUTO_CREATE_STATISTICS ON;
ALTER DATABASE [NestifyDB] SET AUTO_UPDATE_STATISTICS ON;
ALTER DATABASE [NestifyDB] SET AUTO_UPDATE_STATISTICS_ASYNC ON;
GO

PRINT 'Database configuration completed for NestifyDB.';
GO
