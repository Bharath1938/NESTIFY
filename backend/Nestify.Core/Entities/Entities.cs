using System;
using System.Collections.Generic;

namespace Nestify.Core.Entities
{
    public enum UserRole
    {
        Customer,
        Organizer,
        Admin
    }

    public enum BookingStatus
    {
        Pending,
        Confirmed,
        Assigned,
        OnTheWay,
        Arrived,
        InProgress,
        Completed,
        CustomerConfirmed,
        Cancelled
    }

    public class User
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public string FullName { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string PasswordHash { get; set; } = string.Empty;
        public string Phone { get; set; } = string.Empty;
        public UserRole Role { get; set; }
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Navigation properties
        public CustomerProfile? CustomerProfile { get; set; }
        public OrganizerProfile? OrganizerProfile { get; set; }
        public ICollection<RefreshToken> RefreshTokens { get; set; } = new List<RefreshToken>();
        public ICollection<Address> Addresses { get; set; } = new List<Address>();
    }

    public class RefreshToken
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid UserId { get; set; }
        public string Token { get; set; } = string.Empty;
        public DateTime ExpiresAt { get; set; }
        public DateTime? RevokedAt { get; set; }
        public bool IsActive => RevokedAt == null && DateTime.UtcNow < ExpiresAt;

        public User User { get; set; } = null!;
    }

    public class CustomerProfile
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid UserId { get; set; }
        public string SelectedCity { get; set; } = "Chennai";
        public User User { get; set; } = null!;
    }

    public class OrganizerProfile
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid UserId { get; set; }
        public string Bio { get; set; } = string.Empty;
        public double Rating { get; set; } = 4.8;
        public int TotalJobsCompleted { get; set; } = 12;
        public bool IsAvailable { get; set; } = true;
        public string ServiceArea { get; set; } = "Chennai Central";

        public User User { get; set; } = null!;
    }

    public class Address
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid UserId { get; set; }
        public string Label { get; set; } = "Home"; // Home, Work, Other
        public string HouseNumberAndStreet { get; set; } = string.Empty;
        public string Landmark { get; set; } = string.Empty;
        public string City { get; set; } = "Chennai";
        public string ZipCode { get; set; } = "600001";
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public bool IsDefault { get; set; } = false;

        public User User { get; set; } = null!;
    }

    public class ServiceCategory
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public string Name { get; set; } = string.Empty;
        public string Slug { get; set; } = string.Empty; // e.g. "wardrobe", "kitchen", "storage"
        public string Description { get; set; } = string.Empty;
        public string ImageUrl { get; set; } = string.Empty;
        public int DisplayOrder { get; set; } = 0;

        public ICollection<Service> Services { get; set; } = new List<Service>();
    }

    public class Service
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid CategoryId { get; set; }
        public string Title { get; set; } = string.Empty;
        public string ShortDescription { get; set; } = string.Empty;
        public string FullDescription { get; set; } = string.Empty;
        public string ImageUrl { get; set; } = string.Empty;
        public double Rating { get; set; } = 4.8;
        public int ReviewCount { get; set; } = 120;
        public bool IsFeatured { get; set; } = false;
        public bool IsPopular { get; set; } = false;

        public ServiceCategory Category { get; set; } = null!;
        public ICollection<ServicePackage> Packages { get; set; } = new List<ServicePackage>();
    }

    public class ServicePackage
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid ServiceId { get; set; }
        public string PackageName { get; set; } = string.Empty; // e.g. "Basic", "Standard", "Premium"
        public string Description { get; set; } = string.Empty;
        public decimal StartingPrice { get; set; }
        public double EstimatedDurationHours { get; set; }
        public string IncludedDetailsJson { get; set; } = "[]"; // Array of strings
        public string ExcludedDetailsJson { get; set; } = "[]"; // Array of strings
        public string ImageUrl { get; set; } = string.Empty;
        public int DisplayOrder { get; set; } = 0;
        public bool IsActive { get; set; } = true;

        public Service Service { get; set; } = null!;
    }

    public class PromoCode
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public string Code { get; set; } = string.Empty;
        public decimal DiscountPercentage { get; set; } = 10;
        public decimal MaxDiscountAmount { get; set; } = 200;
        public DateTime ValidUntil { get; set; } = DateTime.UtcNow.AddMonths(1);
        public bool IsActive { get; set; } = true;
    }

    public class Booking
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid CustomerId { get; set; }
        public Guid? OrganizerId { get; set; }
        public Guid AddressId { get; set; }
        public BookingStatus Status { get; set; } = BookingStatus.Pending;
        public DateTime ScheduledAt { get; set; }
        public double EstimatedDurationHours { get; set; }
        public string CustomerNotes { get; set; } = string.Empty;
        public decimal SubtotalAmount { get; set; }
        public decimal ServiceFee { get; set; } = 100;
        public decimal DiscountAmount { get; set; } = 0;
        public decimal TotalAmount { get; set; }
        public bool IsPaid { get; set; } = false;
        public string PaymentTransactionId { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Navigation properties
        public User Customer { get; set; } = null!;
        public User? Organizer { get; set; }
        public Address Address { get; set; } = null!;
        public ICollection<BookingItem> Items { get; set; } = new List<BookingItem>();
        public ICollection<BookingStatusAudit> StatusAudits { get; set; } = new List<BookingStatusAudit>();
        public ICollection<BeforeAfterPhoto> Photos { get; set; } = new List<BeforeAfterPhoto>();
        public Review? Review { get; set; }
    }

    public class BookingItem
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid BookingId { get; set; }
        public Guid ServicePackageId { get; set; }
        public int Quantity { get; set; } = 1;
        public decimal UnitPrice { get; set; }

        public Booking Booking { get; set; } = null!;
        public ServicePackage ServicePackage { get; set; } = null!;
    }

    public class BookingStatusAudit
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid BookingId { get; set; }
        public BookingStatus? OldStatus { get; set; }
        public BookingStatus NewStatus { get; set; }
        public Guid ChangedByUserId { get; set; }
        public DateTime ChangedAt { get; set; } = DateTime.UtcNow;
        public string Notes { get; set; } = string.Empty;

        public Booking Booking { get; set; } = null!;
        public User ChangedByUser { get; set; } = null!;
    }

    public class BeforeAfterPhoto
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid BookingId { get; set; }
        public string BeforePhotoUrl { get; set; } = string.Empty;
        public string AfterPhotoUrl { get; set; } = string.Empty;
        public DateTime UploadedAt { get; set; } = DateTime.UtcNow;

        public Booking Booking { get; set; } = null!;
    }

    public class Review
    {
        public Guid Id { get; set; } = Guid.NewGuid();
        public Guid BookingId { get; set; }
        public Guid CustomerId { get; set; }
        public Guid OrganizerId { get; set; }
        public double Rating { get; set; }
        public string Comment { get; set; } = string.Empty;
        public string SelectedTagsJson { get; set; } = "[]"; // e.g. ["Professional", "On Time", "Great Organization"]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        public Booking Booking { get; set; } = null!;
        public User Customer { get; set; } = null!;
        public User Organizer { get; set; } = null!;
    }
}
