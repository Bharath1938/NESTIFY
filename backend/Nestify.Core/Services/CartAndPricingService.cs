using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Nestify.Core.Services
{
    public record CartItemInput(Guid PackageId, int Quantity, decimal UnitPrice);
    public record PricingCalculationResult(decimal Subtotal, decimal ServiceFee, decimal DiscountAmount, decimal TotalAmount);

    public class CartAndPricingService
    {
        public const decimal DefaultServiceFee = 100.00m; // ₹100 Flat Service Fee

        public PricingCalculationResult CalculateTotal(IEnumerable<CartItemInput> items, decimal discountPercentage = 0, decimal maxDiscount = 0)
        {
            decimal subtotal = 0;
            foreach (var item in items)
            {
                subtotal += item.UnitPrice * item.Quantity;
            }

            decimal discount = 0;
            if (discountPercentage > 0)
            {
                discount = subtotal * (discountPercentage / 100m);
                if (maxDiscount > 0 && discount > maxDiscount)
                {
                    discount = maxDiscount;
                }
            }

            decimal total = subtotal + DefaultServiceFee - discount;
            if (total < 0) total = 0;

            return new PricingCalculationResult(subtotal, DefaultServiceFee, discount, total);
        }
    }
}
