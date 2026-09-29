using System;
using System.Collections.Generic;
using System.Data;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using Microsoft.Extensions.Configuration;

namespace Nestify.Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class MarketplaceController : ControllerBase
    {
        private readonly string _connectionString;

        public MarketplaceController(IConfiguration configuration)
        {
            _connectionString = configuration.GetConnectionString("DefaultConnection") 
                ?? "Server=(localdb)\\mssqllocaldb;Database=NestifyDB;Trusted_Connection=True;MultipleActiveResultSets=true;Encrypt=False";
        }

        public record MarketplaceLoginRequest(string LoginId, string Password, string? ExpectedRole);

        [HttpGet("stats")]
        public async Task<IActionResult> GetDashboardStats()
        {
            try
            {
                using var connection = new SqlConnection(_connectionString);
                await connection.OpenAsync();
                using var command = new SqlCommand("dbo.sp_AdminGetDashboardSummary", connection)
                {
                    CommandType = CommandType.StoredProcedure
                };

                using var reader = await command.ExecuteReaderAsync();
                if (await reader.ReadAsync())
                {
                    var result = new
                    {
                        TotalActiveCustomers = reader["TotalActiveCustomers"],
                        TotalApprovedOrganizers = reader["TotalApprovedOrganizers"],
                        PendingOrganizerApprovals = reader["PendingOrganizerApprovals"],
                        OpenBookingsPool = reader["OpenBookingsPool"],
                        InProgressBookings = reader["InProgressBookings"],
                        TotalCompletedBookings = reader["TotalCompletedBookings"],
                        GrossMarketplaceVolume = reader["GrossMarketplaceVolume"],
                        TotalPlatformFeesEarned = reader["TotalPlatformFeesEarned"],
                        OpenSupportTickets = reader["OpenSupportTickets"]
                    };
                    return Ok(result);
                }
                return Ok(new { });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = ex.Message });
            }
        }

        [HttpGet("services")]
        public async Task<IActionResult> GetActiveServices()
        {
            try
            {
                using var connection = new SqlConnection(_connectionString);
                await connection.OpenAsync();
                using var command = new SqlCommand("SELECT * FROM dbo.vw_ActiveServices ORDER BY CategoryDisplayOrder, StartingPrice;", connection);
                using var reader = await command.ExecuteReaderAsync();

                var list = new List<object>();
                while (await reader.ReadAsync())
                {
                    list.Add(new
                    {
                        ServiceID = reader["ServiceID"],
                        ServiceName = reader["ServiceName"],
                        ServiceDescription = reader["ServiceDescription"]?.ToString(),
                        StartingPrice = reader["StartingPrice"],
                        EstimatedDuration = reader["EstimatedDuration"],
                        ServiceImageURL = reader["ServiceImageURL"]?.ToString(),
                        IncludedDetails = reader["IncludedDetails"]?.ToString(),
                        ExcludedDetails = reader["ExcludedDetails"]?.ToString(),
                        CategoryID = reader["CategoryID"],
                        CategoryName = reader["CategoryName"]?.ToString(),
                        CategoryDescription = reader["CategoryDescription"]?.ToString()
                    });
                }
                return Ok(list);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = ex.Message });
            }
        }

        [HttpGet("bookings")]
        public async Task<IActionResult> GetBookings()
        {
            try
            {
                using var connection = new SqlConnection(_connectionString);
                await connection.OpenAsync();
                using var command = new SqlCommand("SELECT * FROM dbo.vw_BookingDetails ORDER BY BookingID DESC;", connection);
                using var reader = await command.ExecuteReaderAsync();

                var list = new List<object>();
                while (await reader.ReadAsync())
                {
                    list.Add(new
                    {
                        BookingID = reader["BookingID"],
                        BookingDate = Convert.ToDateTime(reader["BookingDate"]).ToString("yyyy-MM-dd"),
                        StartTime = reader["StartTime"]?.ToString(),
                        EstimatedDuration = reader["EstimatedDuration"],
                        BookingStatus = reader["BookingStatus"]?.ToString(),
                        PaymentStatus = reader["PaymentStatus"]?.ToString(),
                        BaseAmount = reader["BaseAmount"],
                        ServiceFee = reader["ServiceFee"],
                        DiscountAmount = reader["DiscountAmount"],
                        TotalAmount = reader["TotalAmount"],
                        CustomerNotes = reader["CustomerNotes"]?.ToString(),
                        CustomerName = reader["CustomerName"]?.ToString(),
                        CustomerPhone = reader["CustomerPhone"]?.ToString(),
                        OrganizerName = reader["OrganizerName"] != DBNull.Value ? reader["OrganizerName"]?.ToString() : "Unassigned",
                        OrganizerRating = reader["OrganizerRating"] != DBNull.Value ? reader["OrganizerRating"] : null,
                        ServiceName = reader["ServiceName"]?.ToString(),
                        CategoryName = reader["CategoryName"]?.ToString(),
                        AddressLine = reader["AddressLine1"]?.ToString(),
                        City = reader["City"]?.ToString(),
                        ItemCount = reader["ItemCount"],
                        PhotoCount = reader["PhotoCount"],
                        HasReview = reader["HasReview"]
                    });
                }
                return Ok(list);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = ex.Message });
            }
        }

        [HttpGet("organizers")]
        public async Task<IActionResult> GetApprovedOrganizers()
        {
            try
            {
                using var connection = new SqlConnection(_connectionString);
                await connection.OpenAsync();
                using var command = new SqlCommand("SELECT * FROM dbo.vw_ApprovedOrganizers ORDER BY Rating DESC, CompletedJobs DESC;", connection);
                using var reader = await command.ExecuteReaderAsync();

                var list = new List<object>();
                while (await reader.ReadAsync())
                {
                    list.Add(new
                    {
                        OrganizerID = reader["OrganizerID"],
                        FullName = reader["FullName"]?.ToString(),
                        Email = reader["Email"]?.ToString(),
                        Phone = reader["Phone"]?.ToString(),
                        ExperienceYears = reader["ExperienceYears"],
                        Rating = reader["Rating"],
                        CompletedJobs = reader["CompletedJobs"],
                        ActiveSkillsCount = reader["ActiveSkillsCount"],
                        ActiveServiceAreasCount = reader["ActiveServiceAreasCount"],
                        ProfilePhotoURL = reader["ProfilePhotoURL"]?.ToString()
                    });
                }
                return Ok(list);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = ex.Message });
            }
        }

        [HttpPost("login")]
        public async Task<IActionResult> Authenticate([FromBody] MarketplaceLoginRequest request)
        {
            try
            {
                using var connection = new SqlConnection(_connectionString);
                await connection.OpenAsync();
                using var command = new SqlCommand("dbo.sp_AuthenticateUser", connection)
                {
                    CommandType = CommandType.StoredProcedure
                };

                command.Parameters.AddWithValue("@LoginID", request.LoginId);
                command.Parameters.AddWithValue("@Password", request.Password);
                command.Parameters.AddWithValue("@ExpectedRole", (object?)request.ExpectedRole ?? DBNull.Value);

                using var reader = await command.ExecuteReaderAsync();
                if (await reader.ReadAsync())
                {
                    bool isAuth = Convert.ToInt32(reader["IsAuthenticated"]) == 1;
                    if (!isAuth)
                    {
                        return Unauthorized(new { message = reader["ErrorMessage"]?.ToString() });
                    }

                    return Ok(new
                    {
                        success = true,
                        userId = reader["UserID"],
                        loginId = reader["LoginID"]?.ToString(),
                        role = reader["Role"]?.ToString(),
                        lastLoginDate = reader["LastLoginDate"],
                        customerFullName = reader["CustomerFullName"] != DBNull.Value ? reader["CustomerFullName"]?.ToString() : null,
                        organizerFullName = reader["OrganizerFullName"] != DBNull.Value ? reader["OrganizerFullName"]?.ToString() : null
                    });
                }
                return Unauthorized(new { message = "Authentication failed." });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { message = ex.Message });
            }
        }
    }
}
