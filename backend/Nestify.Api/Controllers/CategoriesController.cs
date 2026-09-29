using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Nestify.Infrastructure.Data;

namespace Nestify.Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CategoriesController : ControllerBase
    {
        private readonly NestifyDbContext _context;

        public CategoriesController(NestifyDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<IActionResult> GetCategories()
        {
            var categories = await _context.ServiceCategories
                .AsNoTracking()
                .OrderBy(c => c.DisplayOrder)
                .ToListAsync();
            return Ok(categories);
        }

        [HttpGet("{slug}/services")]
        public async Task<IActionResult> GetServicesByCategory(string slug)
        {
            var category = await _context.ServiceCategories
                .Include(c => c.Services)
                    .ThenInclude(s => s.Packages)
                .AsNoTracking()
                .FirstOrDefaultAsync(c => c.Slug.ToLower() == slug.ToLower());

            if (category == null) return NotFound(new { message = "Category not found." });
            return Ok(category);
        }
    }
}
