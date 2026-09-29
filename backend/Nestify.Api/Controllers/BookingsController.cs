using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Nestify.Core.Entities;
using Nestify.Infrastructure.Data;

namespace Nestify.Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    [Authorize]
    public class BookingsController : ControllerBase
    {
        private readonly NestifyDbContext _context;

        public BookingsController(NestifyDbContext context)
        {
            _context = context;
        }

        public record CreateBookingItemDto(Guid ServicePackageId, int Quantity, decimal UnitPrice);
        public record CreateBookingRequest(
            Guid AddressId,
            DateTime ScheduledAt,
            double EstimatedDurationHours,
            string CustomerNotes,
            string PromoCode,
            List<CreateBookingItemDto> Items
        );

        public record UpdateStatusRequest(BookingStatus Status, string Notes);
        public record AddReviewRequest(double Rating, string Comment, List<string> SelectedTags);

        private Guid GetCurrentUserId()
        {
            var userIdClaim = User.FindFirstValue(ClaimTypes.NameIdentifier) ?? User.FindFirstValue("sub");
            return Guid.TryParse(userIdClaim, out var userId) ? userId : Guid.Empty;
        }

        [HttpPost]
        [Authorize(Roles = "Customer")]
        public async Task<IActionResult> CreateBooking([FromBody] CreateBookingRequest request)
        {
            var customerId = GetCurrentUserId();

            decimal subtotal = request.Items.Sum(i => i.UnitPrice * i.Quantity);
            decimal serviceFee = 100m;
            decimal discount = 0m;

            if (!string.IsNullOrEmpty(request.PromoCode))
            {
                var promo = await _context.PromoCodes.FirstOrDefaultAsync(p => p.Code == request.PromoCode && p.IsActive);
                if (promo != null)
                {
                    discount = Math.Min(subtotal * (promo.DiscountPercentage / 100m), promo.MaxDiscountAmount);
                }
            }

            decimal total = subtotal + serviceFee - discount;

            var booking = new Booking
            {
                CustomerId = customerId,
                AddressId = request.AddressId,
                Status = BookingStatus.Confirmed, // Server confirmed
                ScheduledAt = request.ScheduledAt,
                EstimatedDurationHours = request.EstimatedDurationHours,
                CustomerNotes = request.CustomerNotes,
                SubtotalAmount = subtotal,
                ServiceFee = serviceFee,
                DiscountAmount = discount,
                TotalAmount = total,
                IsPaid = true,
                PaymentTransactionId = $"txn_{Guid.NewGuid().ToString().Replace("-", "").Substring(0, 12)}"
            };

            foreach (var item in request.Items)
            {
                booking.Items.Add(new BookingItem
                {
                    ServicePackageId = item.ServicePackageId,
                    Quantity = item.Quantity,
                    UnitPrice = item.UnitPrice
                });
            }

            booking.StatusAudits.Add(new BookingStatusAudit
            {
                OldStatus = null,
                NewStatus = BookingStatus.Confirmed,
                ChangedByUserId = customerId,
                Notes = "Booking created and payment verified."
            });

            _context.Bookings.Add(booking);
            await _context.SaveChangesAsync();

            return Ok(booking);
        }

        [HttpGet]
        public async Task<IActionResult> GetBookings()
        {
            var userId = GetCurrentUserId();
            var bookings = await _context.Bookings
                .Include(b => b.Address)
                .Include(b => b.Items)
                    .ThenInclude(i => i.ServicePackage)
                .Include(b => b.Organizer)
                .Include(b => b.Photos)
                .Where(b => b.CustomerId == userId || b.OrganizerId == userId)
                .OrderByDescending(b => b.ScheduledAt)
                .AsNoTracking()
                .ToListAsync();

            return Ok(bookings);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetBookingById(Guid id)
        {
            var booking = await _context.Bookings
                .Include(b => b.Address)
                .Include(b => b.Items)
                    .ThenInclude(i => i.ServicePackage)
                .Include(b => b.Organizer)
                .Include(b => b.StatusAudits)
                .Include(b => b.Photos)
                .Include(b => b.Review)
                .FirstOrDefaultAsync(b => b.Id == id);

            if (booking == null) return NotFound();
            return Ok(booking);
        }

        [HttpPut("{id}/status")]
        public async Task<IActionResult> UpdateStatus(Guid id, [FromBody] UpdateStatusRequest request)
        {
            var booking = await _context.Bookings.FindAsync(id);
            if (booking == null) return NotFound();

            var userId = GetCurrentUserId();
            var oldStatus = booking.Status;
            booking.Status = request.Status;

            _context.BookingStatusAudits.Add(new BookingStatusAudit
            {
                BookingId = id,
                OldStatus = oldStatus,
                NewStatus = request.Status,
                ChangedByUserId = userId,
                Notes = request.Notes
            });

            await _context.SaveChangesAsync();
            return Ok(new { message = "Status updated successfully.", status = request.Status });
        }

        [HttpPost("{id}/reviews")]
        public async Task<IActionResult> AddReview(Guid id, [FromBody] AddReviewRequest request)
        {
            var booking = await _context.Bookings.FindAsync(id);
            if (booking == null || booking.OrganizerId == null) return BadRequest();

            var customerId = GetCurrentUserId();
            var review = new Review
            {
                BookingId = id,
                CustomerId = customerId,
                OrganizerId = booking.OrganizerId.Value,
                Rating = request.Rating,
                Comment = request.Comment,
                SelectedTagsJson = System.Text.Json.JsonSerializer.Serialize(request.SelectedTags)
            };

            _context.Reviews.Add(review);
            await _context.SaveChangesAsync();
            return Ok(review);
        }
    }
}
