using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Nestify.Core.Entities;
using Nestify.Core.Interfaces;

namespace Nestify.Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        public record RegisterRequest(string FullName, string Email, string Password, string Phone, UserRole Role);
        public record LoginRequest(string Email, string Password);
        public record RefreshRequest(string RefreshToken);
        public record SendOtpRequest(string PhoneNumber);
        public record VerifyOtpRequest(string PhoneNumber, string Code);

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterRequest request)
        {
            var result = await _authService.RegisterAsync(request.FullName, request.Email, request.Password, request.Phone, request.Role);
            if (!result.Success)
            {
                return BadRequest(new { message = result.ErrorMessage });
            }
            return Ok(result);
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            var result = await _authService.LoginAsync(request.Email, request.Password);
            if (!result.Success)
            {
                return Unauthorized(new { message = result.ErrorMessage });
            }
            return Ok(result);
        }

        [HttpPost("refresh")]
        public async Task<IActionResult> Refresh([FromBody] RefreshRequest request)
        {
            var result = await _authService.RefreshTokenAsync(request.RefreshToken);
            if (!result.Success)
            {
                return Unauthorized(new { message = result.ErrorMessage });
            }
            return Ok(result);
        }

        [HttpPost("send-otp")]
        public async Task<IActionResult> SendOtp([FromBody] SendOtpRequest request)
        {
            var result = await _authService.SendOtpAsync(request.PhoneNumber);
            return Ok(result);
        }

        [HttpPost("verify-otp")]
        public async Task<IActionResult> VerifyOtp([FromBody] VerifyOtpRequest request)
        {
            var result = await _authService.VerifyOtpAsync(request.PhoneNumber, request.Code);
            if (!result.Success)
            {
                return BadRequest(new { message = result.ErrorMessage });
            }
            return Ok(result);
        }
    }
}
