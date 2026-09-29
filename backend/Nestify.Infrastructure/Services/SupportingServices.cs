using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Nestify.Core.Entities;
using Nestify.Core.Interfaces;

namespace Nestify.Infrastructure.Services
{
    public class CloudStorageService : IStorageService
    {
        public Task<string> UploadFileAsync(byte[] fileBytes, string fileName, string contentType)
        {
            // Abstraction for Azure Blob Storage / AWS S3
            var mockCloudUrl = $"https://storage.nestify.com/uploads/{Guid.NewGuid()}_{fileName}";
            return Task.FromResult(mockCloudUrl);
        }
    }

    public class FcmNotificationService : INotificationService
    {
        public Task SendPushNotificationAsync(string deviceToken, string title, string body, IDictionary<string, string>? data = null)
        {
            // Firebase Cloud Messaging (FCM) push notification delivery
            Console.WriteLine($"[FCM] Sent to token {deviceToken}: {title} - {body}");
            return Task.CompletedTask;
        }
    }

    public class StripePaymentGateway : IPaymentGateway
    {
        public Task<PaymentResult> ProcessPaymentAsync(decimal amount, string currency, string paymentMethodToken)
        {
            // Payment gateway integration abstraction (Stripe/Paypal)
            var transactionId = $"txn_{Guid.NewGuid().ToString().Replace("-", "")}";
            return Task.FromResult(new PaymentResult(true, transactionId, null));
        }

        public Task<PaymentResult> RefundPaymentAsync(string transactionId, decimal amount)
        {
            return Task.FromResult(new PaymentResult(true, $"ref_{transactionId}", null));
        }
    }

    public class GoogleMapsLocationService : ILocationService
    {
        public Task<LocationCoordinates?> GeocodeAddressAsync(string address)
        {
            // Google Maps Geocoding API abstraction
            return Task.FromResult<LocationCoordinates?>(new LocationCoordinates(37.7749, -122.4194, address));
        }

        public Task<double> CalculateDistanceInKmAsync(double originLat, double originLng, double destLat, double destLng)
        {
            // Haversine / Google Distance Matrix calculation
            var dLat = (destLat - originLat) * (Math.PI / 180.0);
            var dLng = (destLng - originLng) * (Math.PI / 180.0);
            var a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                    Math.Cos(originLat * (Math.PI / 180.0)) * Math.Cos(destLat * (Math.PI / 180.0)) *
                    Math.Sin(dLng / 2) * Math.Sin(dLng / 2);
            var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            var earthRadiusKm = 6371.0;
            return Task.FromResult(earthRadiusKm * c);
        }
    }
}
