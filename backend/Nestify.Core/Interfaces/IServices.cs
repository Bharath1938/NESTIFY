using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Nestify.Core.Entities;

namespace Nestify.Core.Interfaces
{
    public record AuthResult(bool Success, string AccessToken, string RefreshToken, string? ErrorMessage, User? User);
    public record OtpResult(bool Success, string? ErrorMessage);
    public record PaymentResult(bool Success, string TransactionId, string? ErrorMessage);
    public record LocationCoordinates(double Latitude, double Longitude, string FormattedAddress);

    public interface IAuthService
    {
        Task<AuthResult> RegisterAsync(string fullName, string email, string password, string phone, UserRole role);
        Task<AuthResult> LoginAsync(string email, string password);
        Task<AuthResult> RefreshTokenAsync(string refreshToken);
        Task<OtpResult> SendOtpAsync(string phoneNumber);
        Task<OtpResult> VerifyOtpAsync(string phoneNumber, string code);
    }

    public interface IBookingService
    {
        Task<Booking> CreateBookingAsync(Guid customerId, Guid serviceId, DateTime scheduledAt, string address, double lat, double lng);
        Task<IEnumerable<Booking>> GetUserBookingsAsync(Guid userId, UserRole role);
        Task<Booking?> GetBookingByIdAsync(Guid bookingId);
        Task<bool> UpdateBookingStatusAsync(Guid bookingId, BookingStatus newStatus, Guid changedByUserId, string notes = "");
        Task<BeforeAfterPhoto> AddBeforeAfterPhotoAsync(Guid bookingId, string beforePhotoUrl, string afterPhotoUrl);
    }

    public interface IStorageService
    {
        Task<string> UploadFileAsync(byte[] fileBytes, string fileName, string contentType);
    }

    public interface INotificationService
    {
        Task SendPushNotificationAsync(string deviceToken, string title, string body, IDictionary<string, string>? data = null);
    }

    public interface IPaymentGateway
    {
        Task<PaymentResult> ProcessPaymentAsync(decimal amount, string currency, string paymentMethodToken);
        Task<PaymentResult> RefundPaymentAsync(string transactionId, decimal amount);
    }

    public interface ILocationService
    {
        Task<LocationCoordinates?> GeocodeAddressAsync(string address);
        Task<double> CalculateDistanceInKmAsync(double originLat, double originLng, double destLat, double destLng);
    }
}
