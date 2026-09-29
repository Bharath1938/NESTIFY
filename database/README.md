# Nestify Home Organization Marketplace - Database Documentation

Welcome to the official database documentation for **NestifyDB**, the Microsoft SQL Server database powering the **Nestify Home Organization Marketplace**.

Nestify is a two-sided marketplace connecting homeowners with certified, background-checked professional home organizers, administered via an executive web portal and backed by a scalable .NET Web API.

---

## 1. Quick Start & Master Deployment

### Prerequisites
- Microsoft SQL Server 2019, 2022, 2025, Azure SQL Database, or SQL Server LocalDB.
- `sqlcmd` utility or SQL Server Management Studio (SSMS) / Azure Data Studio.

### One-Command Deployment
To execute the complete deployment (creating the database, all 22 tables, constraints, indexes, views, triggers, stored procedures, and full seed data):

```powershell
sqlcmd -S "(localdb)\mssqllocaldb" -i "c:\New folder\nestify\database\00_master_deploy.sql"
```

To run sample test queries and verify database integrity:
```powershell
sqlcmd -S "(localdb)\mssqllocaldb" -i "c:\New folder\nestify\database\09_sample_queries.sql"
```

To roll back or tear down the database objects in reverse dependency order:
```powershell
sqlcmd -S "(localdb)\mssqllocaldb" -i "c:\New folder\nestify\database\rollback.sql"
```

---

## 2. Database Architectural Principles

| Design Goal | Implementation Strategy |
| :--- | :--- |
| **High Concurrency & Non-Blocking Reads** | `READ_COMMITTED_SNAPSHOT ON` (RCSI) and `ALLOW_SNAPSHOT_ISOLATION ON` enabled at the database level. Readers never block writers and writers never block readers during peak booking hours. |
| **Relational Normalization** | Strict 3NF design with primary keys, unique constraints, foreign keys, and check constraints enforcing domain integrity. |
| **Financial & Audit Immutability** | `ON DELETE NO ACTION` on all bookings, payments, payouts, transactions, and audit histories. No accidental cascade deletions of monetary or audit records. Soft-delete via `IsActive` on catalog entities. |
| **Automated Data Integrity** | Filtered unique indexes and database triggers to guarantee single default address per customer, automatic status auditing, and real-time organizer rating aggregation. |
| **Cryptographic Security** | SHA-512 salted hashing (`dbo.fn_HashPassword`) ensures plain-text passwords are never stored in the database. |

---

## 3. Demo Credentials

The database comes pre-seeded with development and testing accounts:

| Role | Login ID | Password | Verification / Approval | Default Profile |
| :--- | :--- | :--- | :--- | :--- |
| **Customer** | `customer@nestify.demo` | `Nestify@123` | Active | Demo Customer (`+1-555-0101`), Home & Office addresses |
| **Organizer** | `organizer@nestify.demo` | `Nestify@456` | Approved & Verified | Demo Organizer (`+1-555-0202`), 5.5 yrs exp, 4.95 Rating, 38 completed jobs |
| **Admin** | `admin@nestify.demo` | `Nestify@Admin2026!` | Active | System Administrator with full marketplace monitoring |

> **Password Security Note**: Passwords in `dbo.Users` are stored as 128-character hexadecimal SHA-512 digests salted with `NestifySalt#2026$`. Plain-text credentials are never stored.

---

## 4. The 22 Marketplace Tables (Data Dictionary)

The database consists of 22 tables grouped into 4 functional modules:

```text
Dependency Execution Order:
Users
 ├── Customers
 │    └── Addresses
 └── Organizers
      ├── OrganizerSkills
      ├── OrganizerServiceAreas
      ├── OrganizerAvailability
      └── OrganizerDocuments
ServiceCategories
 └── Services
Bookings
 ├── BookingItems
 ├── BookingPhotos
 ├── BookingStatusHistory
 ├── Payments
 │    └── PaymentTransactions
 ├── Payouts
 ├── Reviews
 └── SupportTickets
PromoCodes
Notifications
```

### Module A: Identity & Profiles

#### 1. `dbo.Users`
Core authentication table for all marketplace personas.
- `UserID` (BIGINT, IDENTITY, PK)
- `LoginID` (NVARCHAR(150), NOT NULL, UNIQUE) - Normalized email address or phone login.
- `PasswordHash` (NVARCHAR(500), NOT NULL) - Salted SHA-512 or ASP.NET Identity hash.
- `Role` (NVARCHAR(30), NOT NULL, CHECK: `'Customer', 'Organizer', 'Admin'`)
- `IsActive` (BIT, NOT NULL, DEFAULT 1) - Account status toggle.
- `CreatedDate` (DATETIME2(7), NOT NULL, DEFAULT `SYSUTCDATETIME()`)
- `UpdatedDate` (DATETIME2(7), NULL)
- `LastLoginDate` (DATETIME2(7), NULL) - Updated on successful authentication.

#### 2. `dbo.Customers`
Customer profile record (1:1 with `Users`).
- `CustomerID` (BIGINT, IDENTITY, PK)
- `UserID` (BIGINT, NOT NULL, FK -> `Users.UserID`, UNIQUE)
- `FullName` (NVARCHAR(150), NOT NULL)
- `Phone` (NVARCHAR(30), NOT NULL)
- `Email` (NVARCHAR(150), NULL)
- `ProfilePhotoURL` (NVARCHAR(500), NULL)
- `Status` (NVARCHAR(30), NOT NULL, DEFAULT `'Active'`, CHECK: `'Active', 'Inactive', 'Suspended', 'Pending'`)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- `UpdatedDate` (DATETIME2(7), NULL)

#### 3. `dbo.Organizers`
Professional organizer profile (1:1 with `Users`).
- `OrganizerID` (BIGINT, IDENTITY, PK)
- `UserID` (BIGINT, NOT NULL, FK -> `Users.UserID`, UNIQUE)
- `FullName` (NVARCHAR(150), NOT NULL)
- `Phone` (NVARCHAR(30), NOT NULL)
- `Email` (NVARCHAR(150), NULL)
- `ProfilePhotoURL` (NVARCHAR(500), NULL)
- `ExperienceYears` (DECIMAL(5,2), NULL, CHECK >= 0)
- `VerificationStatus` (NVARCHAR(30), NOT NULL, DEFAULT `'Pending'`, CHECK: `'Pending', 'Approved', 'Rejected'`)
- `ApprovalStatus` (NVARCHAR(30), NOT NULL, DEFAULT `'Pending'`, CHECK: `'Pending', 'Approved', 'Rejected', 'Suspended'`)
- `Rating` (DECIMAL(3,2), NOT NULL, DEFAULT 0.00, CHECK: 0.00 to 5.00) - Automatically recalculated by trigger on customer review.
- `CompletedJobs` (INT, NOT NULL, DEFAULT 0, CHECK >= 0)
- `IsActive` (BIT, NOT NULL, DEFAULT 1)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- `UpdatedDate` (DATETIME2(7), NULL)

---

### Module B: Services, Coverage & Schedule

#### 4. `dbo.ServiceCategories`
Categorization for organizing services (e.g., Bedroom, Kitchen, Storage, Kids, Living, Special).
- `CategoryID` (BIGINT, IDENTITY, PK)
- `CategoryName` (NVARCHAR(100), NOT NULL)
- `Description` (NVARCHAR(500), NULL)
- `ImageURL` (NVARCHAR(500), NULL)
- `DisplayOrder` (INT, NOT NULL, DEFAULT 0)
- `IsActive` (BIT, NOT NULL, DEFAULT 1)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- `UpdatedDate` (DATETIME2(7), NULL)

#### 5. `dbo.Services`
Service offerings with pricing baselines and work scope.
- `ServiceID` (BIGINT, IDENTITY, PK)
- `CategoryID` (BIGINT, NOT NULL, FK -> `ServiceCategories.CategoryID`)
- `ServiceName` (NVARCHAR(150), NOT NULL)
- `Description` (NVARCHAR(MAX), NULL)
- `IncludedDetails` (NVARCHAR(MAX), NULL) - What is covered in the standard package.
- `ExcludedDetails` (NVARCHAR(MAX), NULL) - What is explicitly excluded (e.g. hazardous hauling).
- `StartingPrice` (DECIMAL(18,2), NOT NULL, CHECK >= 0)
- `EstimatedDuration` (DECIMAL(10,2), NULL, CHECK > 0) - Estimated hours.
- `ImageURL` (NVARCHAR(500), NULL)
- `IsActive` (BIT, NOT NULL, DEFAULT 1)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- `UpdatedDate` (DATETIME2(7), NULL)

#### 6. `dbo.OrganizerSkills`
Many-to-Many mapping linking Organizers to Services they are certified/qualified to perform.
- `OrganizerSkillID` (BIGINT, IDENTITY, PK)
- `OrganizerID` (BIGINT, NOT NULL, FK -> `Organizers.OrganizerID`)
- `ServiceID` (BIGINT, NOT NULL, FK -> `Services.ServiceID`)
- `ExperienceYears` (DECIMAL(5,2), NULL)
- `IsActive` (BIT, NOT NULL, DEFAULT 1)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- **Constraint**: `UQ_OrganizerSkills_Organizer_Service` on `(OrganizerID, ServiceID)`

#### 7. `dbo.ServiceAreas`
Geographical operating zones (cities, zip codes, coordinates).
- `ServiceAreaID` (BIGINT, IDENTITY, PK)
- `AreaName` (NVARCHAR(150), NOT NULL)
- `City` (NVARCHAR(100), NOT NULL)
- `State` (NVARCHAR(100), NULL)
- `PostalCode` (NVARCHAR(20), NULL)
- `Latitude` / `Longitude` (DECIMAL(10,7), NULL)
- `IsActive` (BIT, NOT NULL, DEFAULT 1)
- `CreatedDate` (DATETIME2(7), NOT NULL)

#### 8. `dbo.OrganizerServiceAreas`
Many-to-Many mapping of geographical areas covered by each organizer.
- `OrganizerServiceAreaID` (BIGINT, IDENTITY, PK)
- `OrganizerID` (BIGINT, NOT NULL, FK -> `Organizers.OrganizerID`)
- `ServiceAreaID` (BIGINT, NOT NULL, FK -> `ServiceAreas.ServiceAreaID`)
- `IsActive` (BIT, NOT NULL, DEFAULT 1)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- **Constraint**: `UQ_OrganizerServiceAreas_Organizer_Area` on `(OrganizerID, ServiceAreaID)`

#### 9. `dbo.OrganizerAvailability`
Supports recurring weekly working windows and date-specific exceptions/time-off blocks.
- `AvailabilityID` (BIGINT, IDENTITY, PK)
- `OrganizerID` (BIGINT, NOT NULL, FK -> `Organizers.OrganizerID`)
- `AvailabilityDate` (DATE, NULL) - Specific calendar date override.
- `DayOfWeek` (TINYINT, NULL, CHECK: 0 to 6) - 0=Sunday, 1=Monday... for recurring schedule.
- `StartTime` / `EndTime` (TIME(0), NOT NULL, CHECK: `EndTime > StartTime`)
- `IsAvailable` (BIT, NOT NULL, DEFAULT 1)
- `IsBlocked` (BIT, NOT NULL, DEFAULT 0) - Flag for vacation or blocked dates.
- `CreatedDate` (DATETIME2(7), NOT NULL)
- `UpdatedDate` (DATETIME2(7), NULL)
- **Constraint**: `(AvailabilityDate IS NOT NULL) OR (DayOfWeek IS NOT NULL)`

#### 10. `dbo.Addresses`
Customer delivery addresses.
- `AddressID` (BIGINT, IDENTITY, PK)
- `CustomerID` (BIGINT, NOT NULL, FK -> `Customers.CustomerID`)
- `AddressLabel` (NVARCHAR(50), NOT NULL) - e.g. "Home", "Office"
- `AddressLine1` (NVARCHAR(250), NOT NULL)
- `AddressLine2` (NVARCHAR(250), NULL)
- `City` (NVARCHAR(100), NOT NULL)
- `State` / `PostalCode` / `Landmark` (NVARCHAR)
- `Latitude` / `Longitude` (DECIMAL(10,7), NULL)
- `IsDefault` (BIT, NOT NULL, DEFAULT 0)
- **Filtered Unique Index**: `UQ_Addresses_CustomerID_IsDefault` on `(CustomerID) WHERE IsDefault = 1`.
- **Integrity Trigger**: `TR_Addresses_SingleDefault` automatically toggles off previous defaults.

---

### Module C: Bookings & Operations

#### 11. `dbo.Bookings`
Primary transaction table coordinating the two-sided marketplace.
- `BookingID` (BIGINT, IDENTITY, PK)
- `CustomerID` (BIGINT, NOT NULL, FK -> `Customers.CustomerID`)
- `OrganizerID` (BIGINT, NULL, FK -> `Organizers.OrganizerID`) - **Nullable** prior to organizer assignment/acceptance.
- `ServiceID` (BIGINT, NOT NULL, FK -> `Services.ServiceID`)
- `AddressID` (BIGINT, NOT NULL, FK -> `Addresses.AddressID`)
- `BookingDate` (DATE, NOT NULL)
- `StartTime` (TIME(0), NOT NULL)
- `EstimatedDuration` (DECIMAL(10,2), NULL)
- `Status` (NVARCHAR(40), NOT NULL, CHECK controlled lifecycle states)
- `PaymentStatus` (NVARCHAR(40), NOT NULL, CHECK: `'Pending', 'Initiated', 'Success', 'Failed', 'Refunded', 'PartiallyRefunded', 'Cancelled'`)
- `BaseAmount` (DECIMAL(18,2), NOT NULL, DEFAULT 0.00)
- `ServiceFee` (DECIMAL(18,2), NOT NULL, DEFAULT 0.00)
- `DiscountAmount` (DECIMAL(18,2), NOT NULL, DEFAULT 0.00)
- `TotalAmount` (DECIMAL(18,2), NOT NULL, DEFAULT 0.00)
- `CustomerNotes` (NVARCHAR(MAX), NULL)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- `UpdatedDate` (DATETIME2(7), NULL)

#### 12. `dbo.BookingItems`
Itemized scope elements or add-ons (e.g. Wardrobe count, storage bins, additional hours).
- `BookingItemID` (BIGINT, IDENTITY, PK)
- `BookingID` (BIGINT, NOT NULL, FK -> `Bookings.BookingID`)
- `ItemType` (NVARCHAR(100), NULL)
- `ItemName` (NVARCHAR(150), NOT NULL)
- `Quantity` (DECIMAL(10,2), NULL)
- `UnitPrice` (DECIMAL(18,2), NULL)
- `Amount` (DECIMAL(18,2), NULL)
- `Notes` (NVARCHAR(500), NULL)
- `CreatedDate` (DATETIME2(7), NOT NULL)

#### 13. `dbo.BookingPhotos`
Photo evidence across the job lifecycle (`Request`, `Before`, `After`).
- `BookingPhotoID` (BIGINT, IDENTITY, PK)
- `BookingID` (BIGINT, NOT NULL, FK -> `Bookings.BookingID`)
- `PhotoType` (NVARCHAR(30), NOT NULL, CHECK: `'Request', 'Before', 'After'`)
- `FileName` (NVARCHAR(250), NULL)
- `FileURL` (NVARCHAR(1000), NOT NULL)
- `StorageKey` (NVARCHAR(500), NULL)
- `UploadedByUserID` (BIGINT, NOT NULL, FK -> `Users.UserID`)
- `CreatedDate` (DATETIME2(7), NOT NULL)

#### 14. `dbo.BookingStatusHistory`
Immutable audit log recording every booking state transition with the executing user and notes.
- `HistoryID` (BIGINT, IDENTITY, PK)
- `BookingID` (BIGINT, NOT NULL, FK -> `Bookings.BookingID`)
- `OldStatus` (NVARCHAR(40), NULL)
- `NewStatus` (NVARCHAR(40), NOT NULL)
- `ChangedByUserID` (BIGINT, NOT NULL, FK -> `Users.UserID`)
- `Remarks` (NVARCHAR(500), NULL)
- `ChangedDate` (DATETIME2(7), NOT NULL)

---

### Module D: Financials, Reviews & Support

#### 15. `dbo.Payments`
Master customer payment records.
- `PaymentID` (BIGINT, IDENTITY, PK)
- `BookingID` (BIGINT, NOT NULL, FK -> `Bookings.BookingID`)
- `CustomerID` (BIGINT, NOT NULL, FK -> `Customers.CustomerID`)
- `Amount` (DECIMAL(18,2), NOT NULL, CHECK >= 0)
- `CurrencyCode` (CHAR(3), NOT NULL) - e.g. `'USD'`
- `PaymentMethod` (NVARCHAR(50), NULL)
- `GatewayName` (NVARCHAR(100), NULL) - e.g. `'Stripe'`, `'PayPal'`
- `GatewayReference` (NVARCHAR(250), NULL) - Charge ID or PaymentIntent ID.
- `PaymentStatus` (NVARCHAR(40), NOT NULL, CHECK constraint)
- `PaidDate` (DATETIME2(7), NULL)
- `CreatedDate` (DATETIME2(7), NOT NULL)

#### 16. `dbo.PaymentTransactions`
Detailed gateway callback and transaction attempt history (authorizations, captures, refunds).
- `TransactionID` (BIGINT, IDENTITY, PK)
- `PaymentID` (BIGINT, NOT NULL, FK -> `Payments.PaymentID`)
- `GatewayReference` (NVARCHAR(250), NULL)
- `TransactionType` (NVARCHAR(50), NOT NULL) - `'Authorize', 'Capture', 'Refund'`
- `Amount` (DECIMAL(18,2), NOT NULL)
- `Status` (NVARCHAR(40), NOT NULL, CHECK: `'Pending', 'Success', 'Failed'`)
- `ResponsePayload` (NVARCHAR(MAX), NULL) - Full webhook/response JSON.
- `CreatedDate` (DATETIME2(7), NOT NULL)

#### 17. `dbo.Payouts`
Marketplace earnings transfers to Organizers.
- `PayoutID` (BIGINT, IDENTITY, PK)
- `OrganizerID` (BIGINT, NOT NULL, FK -> `Organizers.OrganizerID`)
- `BookingID` (BIGINT, NOT NULL, FK -> `Bookings.BookingID`)
- `GrossAmount` (DECIMAL(18,2), NOT NULL)
- `PlatformFee` (DECIMAL(18,2), NOT NULL) - Platform commission (default 15%).
- `NetAmount` (DECIMAL(18,2), NOT NULL) - `GrossAmount - PlatformFee`
- `PayoutStatus` (NVARCHAR(40), NOT NULL, CHECK: `'Pending', 'Processing', 'Paid', 'Failed', 'Cancelled'`)
- `PayoutReference` (NVARCHAR(250), NULL) - Bank / ACH transfer reference.
- `PayoutDate` (DATETIME2(7), NULL)
- `CreatedDate` (DATETIME2(7), NOT NULL)

#### 18. `dbo.Reviews`
Verified customer ratings and testimonials.
- `ReviewID` (BIGINT, IDENTITY, PK)
- `BookingID` (BIGINT, NOT NULL, FK -> `Bookings.BookingID`, UNIQUE) - **1 Review per Booking**.
- `CustomerID` (BIGINT, NOT NULL, FK -> `Customers.CustomerID`)
- `OrganizerID` (BIGINT, NOT NULL, FK -> `Organizers.OrganizerID`)
- `Rating` (TINYINT, NOT NULL, CHECK: 1 to 5)
- `ReviewText` (NVARCHAR(2000), NULL)
- `IsPublished` (BIT, NOT NULL, DEFAULT 1)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- **Business Rule**: Enforced by trigger `TR_Reviews_ValidateEligibility` to allow reviews only after the booking reaches `Completed`, `CustomerConfirmed`, or `Paid`.

#### 19. `dbo.Notifications`
Multi-channel notification inbox for users.
- `NotificationID` (BIGINT, IDENTITY, PK)
- `UserID` (BIGINT, NOT NULL, FK -> `Users.UserID`)
- `Title` (NVARCHAR(200), NOT NULL)
- `Message` (NVARCHAR(1000), NOT NULL)
- `NotificationType` (NVARCHAR(50), NOT NULL, CHECK constraint)
- `ReferenceID` (BIGINT, NULL) - Links to BookingID or PayoutID.
- `IsRead` (BIT, NOT NULL, DEFAULT 0)
- `SentDate` (DATETIME2(7), NULL)
- `CreatedDate` (DATETIME2(7), NOT NULL)

#### 20. `dbo.PromoCodes`
Marketing discounts and coupon validation.
- `PromoCodeID` (BIGINT, IDENTITY, PK)
- `Code` (NVARCHAR(50), NOT NULL, UNIQUE)
- `Description` (NVARCHAR(250), NULL)
- `DiscountType` (NVARCHAR(20), NOT NULL, CHECK: `'Percentage', 'Fixed'`)
- `DiscountValue` (DECIMAL(18,2), NOT NULL)
- `MinimumAmount` (DECIMAL(18,2), NULL)
- `MaximumDiscount` (DECIMAL(18,2), NULL)
- `ValidFrom` / `ValidTo` (DATETIME2(7), NOT NULL)
- `UsageLimit` (INT, NULL)
- `UsedCount` (INT, NOT NULL, DEFAULT 0)
- `IsActive` (BIT, NOT NULL, DEFAULT 1)

#### 21. `dbo.SupportTickets`
Dispute and marketplace customer service ticketing.
- `SupportTicketID` (BIGINT, IDENTITY, PK)
- `CustomerID` / `OrganizerID` / `BookingID` (BIGINT, NULL, FKs)
- `Category` (NVARCHAR(100), NOT NULL)
- `Description` (NVARCHAR(MAX), NOT NULL)
- `Status` (NVARCHAR(30), NOT NULL, DEFAULT `'Open'`, CHECK: `'Open', 'InReview', 'Waiting', 'Resolved', 'Closed'`)
- `AssignedAdminID` (BIGINT, NULL, FK -> `Users.UserID`)
- `ResolutionNotes` (NVARCHAR(MAX), NULL)
- `CreatedDate` (DATETIME2(7), NOT NULL)
- `ClosedDate` (DATETIME2(7), NULL)

#### 22. `dbo.OrganizerDocuments`
Organizer background check, government ID, and professional certifications.
- `DocumentID` (BIGINT, IDENTITY, PK)
- `OrganizerID` (BIGINT, NOT NULL, FK -> `Organizers.OrganizerID`)
- `DocumentType` (NVARCHAR(50), NOT NULL, CHECK: `'Identity', 'AddressProof', 'Certification', 'ProfileDocument'`)
- `FileURL` (NVARCHAR(1000), NOT NULL)
- `VerificationStatus` (NVARCHAR(30), NOT NULL, DEFAULT `'Pending'`, CHECK: `'Pending', 'Approved', 'Rejected'`)
- `VerifiedBy` (BIGINT, NULL, FK -> `Users.UserID`)
- `VerifiedDate` (DATETIME2(7), NULL)
- `CreatedDate` (DATETIME2(7), NOT NULL)

---

## 5. Booking Lifecycle State Machine

The marketplace operates under a strict, controlled state progression:

```text
[Requested]
    │ (Dispatched to qualified organizers in area)
    ▼
 [Offered]
    │ (Organizer accepts job via sp_OrganizerAcceptJob)
    ▼
[Accepted]
    │ (Organizer en route)
    ▼
[OnTheWay]
    │ (Organizer arrives at location)
    ▼
 [Arrived]
    │ (Organizer uploads Before Photos and commences setup)
    ▼
 [Started]
    │ (Active organizing in progress)
    ▼
[InProgress]
    │ (Organizer finishes work and uploads After Photos)
    ▼
[Completed]
    │ (Customer inspects space and confirms satisfaction)
    ▼
[CustomerConfirmed]
    │ (Server captures payment from Stripe/Payment Gateway)
    ▼
  [Paid] ────► [Payouts Triggered] & [Customer Review Enabled]
```

### Exception & Terminal States
- `Cancelled`: Allowed prior to `Started` (with refund rules).
- `Rescheduled`: Allowed between `Accepted` and `OnTheWay`.
- `Rejected`: When an organizer declines an offer.
- `Expired`: When a booking request times out without acceptance.
- `Disputed`: Flagged for admin resolution via `SupportTickets`.

---

## 6. High-Performance Indexing Strategy

In addition to standard primary and foreign key indexes, NestifyDB features targeted filtered indexes designed specifically for two-sided marketplace traffic:

1. **Open Jobs Pool Filtered Index**:
   ```sql
   CREATE NONCLUSTERED INDEX IX_Bookings_OpenJobs_Filtered
   ON dbo.Bookings (BookingDate, StartTime, ServiceID, AddressID)
   INCLUDE (CustomerID, TotalAmount, CustomerNotes)
   WHERE Status IN (N'Requested', N'Offered');
   ```
   *Benefit*: Organizers querying available jobs scan only a tiny fraction of active unassigned bookings rather than millions of historical rows.

2. **Single Default Address Unique Filtered Index**:
   ```sql
   CREATE UNIQUE NONCLUSTERED INDEX UQ_Addresses_CustomerID_IsDefault
   ON dbo.Addresses (CustomerID)
   WHERE IsDefault = 1;
   ```
   *Benefit*: Enforces at the database engine level that no customer can ever have more than one default address.

3. **Active & Approved Organizers Directory Index**:
   ```sql
   CREATE NONCLUSTERED INDEX IX_Organizers_Approved_Active_Filtered
   ON dbo.Organizers (Rating DESC, CompletedJobs DESC)
   INCLUDE (OrganizerID, UserID, FullName, Phone, ProfilePhotoURL)
   WHERE ApprovalStatus = N'Approved' AND IsActive = 1;
   ```
   *Benefit*: Instant, high-speed sorting and public search results for certified organizers.

---

## 7. Stored Procedures & API Integration

NestifyDB includes built-in stored procedures that encapsulate core marketplace workflows:

| Procedure Name | Persona | Purpose |
| :--- | :--- | :--- |
| `dbo.sp_AuthenticateUser` | All | Verifies LoginID & salted hash; updates `LastLoginDate`; returns role profile. |
| `dbo.sp_RegisterCustomer` | Customer | Atomically registers User and Customer profile. |
| `dbo.sp_RegisterOrganizer` | Organizer | Atomically registers User and Organizer profile (Status = Pending). |
| `dbo.sp_SaveCustomerAddress` | Customer | Adds or updates address with automatic default handling. |
| `dbo.sp_CreateBooking` | Customer | Validates customer, service, and address; calculates commission & promo discount; creates booking and initial audit history. |
| `dbo.sp_GetCustomerBookings` | Customer | Retrieves historical and active bookings with pagination/filter. |
| `dbo.sp_GetBookingDetail` | Customer/Org | Returns 360-degree booking view with items, photos, payments, review, and status audit trail. |
| `dbo.sp_GetOrganizerAvailableJobs` | Organizer | Returns available jobs matching the organizer's active skills and covered service areas. |
| `dbo.sp_OrganizerAcceptJob` | Organizer | Atomically locks and assigns booking to organizer; sends notification to customer. |
| `dbo.sp_UpdateBookingStatus` | Organizer/Admin | Validates state transition; updates status; inserts audit record in `BookingStatusHistory`. |
| `dbo.sp_ProcessPaymentSuccess` | Server/API | Records payment; creates transaction log; updates booking payment status; automatically creates organizer payout record. |
| `dbo.sp_AdminGetDashboardSummary` | Admin | Real-time executive metrics: active users, open jobs, completed jobs, GMV, and platform revenue. |
| `dbo.sp_AdminReviewOrganizer` | Admin | Approves, rejects, or suspends organizer applications with automated notifications. |

---

## 8. .NET Web API Integration Example

### Connection String Configuration (`appsettings.json`)
```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Server=(localdb)\\mssqllocaldb;Database=NestifyDB;Trusted_Connection=True;MultipleActiveResultSets=true;Encrypt=False"
  }
}
```

### Calling Stored Procedures with Dapper in .NET Core
```csharp
using Dapper;
using Microsoft.Data.SqlClient;
using System.Data;

public class BookingRepository
{
    private readonly string _connectionString;

    public BookingRepository(IConfiguration configuration)
    {
        _connectionString = configuration.GetConnectionString("DefaultConnection")!;
    }

    public async Task<long> CreateBookingAsync(long customerId, long serviceId, long addressId, 
        DateTime bookingDate, TimeSpan startTime, string promoCode)
    {
        using var connection = new SqlConnection(_connectionString);
        var parameters = new DynamicParameters();
        parameters.Add("@CustomerID", customerId);
        parameters.Add("@ServiceID", serviceId);
        parameters.Add("@AddressID", addressId);
        parameters.Add("@BookingDate", bookingDate.Date);
        parameters.Add("@StartTime", startTime);
        parameters.Add("@PromoCode", promoCode);
        parameters.Add("@NewBookingID", dbType: DbType.Int64, direction: ParameterDirection.Output);

        await connection.ExecuteAsync("dbo.sp_CreateBooking", parameters, commandType: CommandType.StoredProcedure);
        return parameters.Get<long>("@NewBookingID");
    }
}
```

---

## 9. File Manifest

All SQL source files are organized under `c:\New folder\nestify\database\`:

| Filename | Purpose |
| :--- | :--- |
| `00_master_deploy.sql` | **Single all-in-one idempotent master script** deploying the entire database from scratch. |
| `01_create_database.sql` | Database creation, collation, ANSI settings, and RCSI concurrency configuration. |
| `02_create_tables.sql` | 22 table definitions with data types, nullability, defaults, and primary keys. |
| `03_create_constraints.sql` | Foreign keys with NO ACTION, unique constraints, and domain check constraints. |
| `04_create_indexes.sql` | Clustered, non-clustered, covering, and filtered performance indexes. |
| `05_create_views.sql` | Rich reporting views (`vw_ActiveServices`, `vw_BookingDetails`, `vw_OrganizerEarningsSummary`, `vw_MarketplaceDailyPerformance`). |
| `06_create_triggers.sql` | Data integrity triggers (default address handling, audit status logging, rating recalcs). |
| `07_create_stored_procedures.sql` | Production stored procedures and security functions for API operations. |
| `08_seed_data.sql` | Seed catalog (6 categories, 10 services, areas, promo codes, demo accounts, and complete end-to-end booking). |
| `09_sample_queries.sql` | Verification and reporting queries for Customer, Organizer, and Admin workflows. |
| `rollback.sql` | Clean teardown script dropping all database objects in reverse dependency order. |
| `ERD.md` | Full visual Mermaid Entity-Relationship diagrams and domain mappings. |
| `README.md` | Comprehensive documentation and operational guide. |
