using Microsoft.EntityFrameworkCore;
using Nestify.Core.Entities;

namespace Nestify.Infrastructure.Data
{
    public class NestifyDbContext : DbContext
    {
        public NestifyDbContext(DbContextOptions<NestifyDbContext> options) : base(options) { }

        public DbSet<User> Users => Set<User>();
        public DbSet<RefreshToken> RefreshTokens => Set<RefreshToken>();
        public DbSet<CustomerProfile> CustomerProfiles => Set<CustomerProfile>();
        public DbSet<OrganizerProfile> OrganizerProfiles => Set<OrganizerProfile>();
        public DbSet<Address> Addresses => Set<Address>();
        public DbSet<ServiceCategory> ServiceCategories => Set<ServiceCategory>();
        public DbSet<Service> Services => Set<Service>();
        public DbSet<ServicePackage> ServicePackages => Set<ServicePackage>();
        public DbSet<PromoCode> PromoCodes => Set<PromoCode>();
        public DbSet<Booking> Bookings => Set<Booking>();
        public DbSet<BookingItem> BookingItems => Set<BookingItem>();
        public DbSet<BookingStatusAudit> BookingStatusAudits => Set<BookingStatusAudit>();
        public DbSet<BeforeAfterPhoto> BeforeAfterPhotos => Set<BeforeAfterPhoto>();
        public DbSet<Review> Reviews => Set<Review>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Indexes
            modelBuilder.Entity<User>().HasIndex(u => u.Email).IsUnique();
            modelBuilder.Entity<RefreshToken>().HasIndex(rt => rt.Token).IsUnique();
            modelBuilder.Entity<ServiceCategory>().HasIndex(sc => sc.Slug).IsUnique();
            modelBuilder.Entity<PromoCode>().HasIndex(pc => pc.Code).IsUnique();

            modelBuilder.Entity<Booking>()
                .HasIndex(b => b.CustomerId)
                .HasDatabaseName("IX_Bookings_CustomerId");

            modelBuilder.Entity<Booking>()
                .HasIndex(b => b.OrganizerId)
                .HasDatabaseName("IX_Bookings_OrganizerId");

            modelBuilder.Entity<Booking>()
                .HasIndex(b => b.Status)
                .HasDatabaseName("IX_Bookings_Status");

            modelBuilder.Entity<Booking>()
                .HasIndex(b => b.ScheduledAt)
                .HasDatabaseName("IX_Bookings_ScheduledAt");

            // Relationships
            modelBuilder.Entity<Address>()
                .HasOne(a => a.User)
                .WithMany(u => u.Addresses)
                .HasForeignKey(a => a.UserId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Service>()
                .HasOne(s => s.Category)
                .WithMany(c => c.Services)
                .HasForeignKey(s => s.CategoryId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<ServicePackage>()
                .HasOne(sp => sp.Service)
                .WithMany(s => s.Packages)
                .HasForeignKey(sp => sp.ServiceId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Booking>()
                .HasOne(b => b.Customer)
                .WithMany()
                .HasForeignKey(b => b.CustomerId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Booking>()
                .HasOne(b => b.Organizer)
                .WithMany()
                .HasForeignKey(b => b.OrganizerId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Booking>()
                .HasOne(b => b.Address)
                .WithMany()
                .HasForeignKey(b => b.AddressId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<BookingItem>()
                .HasOne(bi => bi.Booking)
                .WithMany(b => b.Items)
                .HasForeignKey(bi => bi.BookingId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<BookingItem>()
                .HasOne(bi => bi.ServicePackage)
                .WithMany()
                .HasForeignKey(bi => bi.ServicePackageId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Review>()
                .HasOne(r => r.Booking)
                .WithOne(b => b.Review)
                .HasForeignKey<Review>(r => r.BookingId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}
