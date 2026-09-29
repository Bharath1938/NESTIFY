package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
import com.example.data.model.CatalogData
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.ui.CustomerScreen
import com.example.ui.NestifyViewModel
import com.example.ui.components.BeforeAfterTransformationCard
import com.example.ui.components.CategoryChipItem
import com.example.ui.components.CategoryIcon
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val activeBookings by viewModel.activeBookings.collectAsState()
  val addresses by viewModel.addresses.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()

  val defaultAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()
  val unreadNotificationsCount = notifications.count { !it.isRead }

  val filteredServices = CatalogData.SERVICES.filter { service ->
    val matchesCategory = selectedCategory == null || service.category == selectedCategory
    val matchesSearch = searchQuery.isBlank() ||
      service.name.contains(searchQuery, ignoreCase = true) ||
      service.description.contains(searchQuery, ignoreCase = true) ||
      service.category.displayName.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesSearch
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // 1. Top Header with Greeting, Address, and Notification Bell
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Good morning, Sarah 👋",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.clickable {
                viewModel.navigateCustomer(CustomerScreen.Profile)
              }
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = NestifyPrimary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = defaultAddress?.addressLine ?: "742 Evergreen Terrace, Apt 4B",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Notification Bell
          Box {
            IconButton(
              onClick = { viewModel.navigateCustomer(CustomerScreen.Notifications) },
              modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .testTag("notification_button")
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = NestifyPrimary
              )
            }
            if (unreadNotificationsCount > 0) {
              Box(
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .size(12.dp)
                  .background(Color(0xFFEF4444), CircleShape)
                  .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          placeholder = { Text("Search services (wardrobe, pantry, kids room...)") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NestifyPrimary)
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_services_input"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = NestifyPrimary
          ),
          singleLine = true
        )
      }
    }

    // 2. Active Booking Banner (if any)
    val ongoingBooking = activeBookings.firstOrNull()
    if (ongoingBooking != null) {
      item {
        ActiveBookingBanner(
          booking = ongoingBooking,
          onTrackClick = {
            viewModel.navigateCustomer(CustomerScreen.BookingDetail(ongoingBooking.id))
          },
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
      }
    }

    // 3. Primary Prompt: "What would you like to organize?"
    item {
      Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(
          text = "What would you like to organize?",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Select a space to discover curated systems & expert organizers",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // 4. Service Categories Horizontal List
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        item {
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable { viewModel.setCategoryFilter(null) },
            shape = RoundedCornerShape(14.dp),
            color = if (selectedCategory == null) NestifyPrimary else MaterialTheme.colorScheme.surface,
            border = if (selectedCategory == null) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = if (selectedCategory == null) Color.White else NestifyPrimary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "All Spaces",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (selectedCategory == null) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        items(ServiceCategory.values()) { category ->
          CategoryChipItem(
            category = category,
            isSelected = selectedCategory == category,
            onClick = { viewModel.setCategoryFilter(category) }
          )
        }
      }
    }

    // 5. Popular Services Carousel
    item {
      Spacer(modifier = Modifier.height(18.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (selectedCategory != null) "${selectedCategory?.displayName} Services" else "Popular Services",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "${filteredServices.size} available",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    items(filteredServices) { service ->
      ServiceCard(
        service = service,
        onServiceClick = {
          viewModel.navigateCustomer(CustomerScreen.ServiceDetail(service))
        },
        onBookNow = {
          viewModel.startBooking(service)
        },
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
      )
    }

    // 6. Before / After Visual Storytelling Showcase (Signature Nestify Experience)
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Before & After Transformations",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Swipe the slider to reveal the organization magic",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
        BeforeAfterTransformationCard(
          title = "Master Bedroom Walk-in Closet",
          serviceCategory = "Wardrobe",
          beforeSubtitle = "Chaotic stacks, tangled hangers & lost items",
          afterSubtitle = "Color-coded gradients with space-saving velvet hangers"
        )
      }
    }

    // 7. Promotional Banner
    item {
      Spacer(modifier = Modifier.height(18.dp))
      PromotionalCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        onClaimClick = {
          val fullHome = CatalogData.SERVICES.first { it.id == "full_home_transformation" }
          viewModel.startBooking(fullHome)
        }
      )
    }

    // 8. Nestify Guarantees / Trust Badges
    item {
      Spacer(modifier = Modifier.height(18.dp))
      NestifyGuaranteesCard(modifier = Modifier.padding(horizontal = 20.dp))
    }
  }
}

@Composable
fun ActiveBookingBanner(
  booking: BookingEntity,
  onTrackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onTrackClick)
      .testTag("active_booking_banner"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = NestifyPrimary),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White.copy(alpha = 0.2f)
          ) {
            Text(
              text = "LIVE BOOKING",
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          StatusBadge(status = booking.status)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = booking.serviceName,
          style = MaterialTheme.typography.titleMedium,
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Organizer: ${booking.organizerName} • ${booking.bookingDate}",
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White.copy(alpha = 0.85f)
        )
      }

      Surface(
        shape = CircleShape,
        color = Color.White,
        modifier = Modifier.size(38.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Track booking",
            tint = NestifyPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
fun ServiceCard(
  service: ServiceItem,
  onServiceClick: () -> Unit,
  onBookNow: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onServiceClick)
      .testTag("service_card_${service.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(modifier = Modifier.weight(1f)) {
          // Category Icon badge
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(service.colorSeed).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = CategoryIcon(service.category),
              contentDescription = null,
              tint = Color(service.colorSeed),
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = service.name,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = service.tagline,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1
            )
          }
        }

        if (service.popular) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFFEF3C7)
          ) {
            Text(
              text = "POPULAR",
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              color = Color(0xFF92400E),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Highlights row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${service.rating}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = " (${service.reviewsCount})",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(10.dp))
          Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "~${service.estimatedHours} hrs",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Text(
          text = "From $${service.startingPrice.toInt()}",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = NestifyPrimary
        )
      }
    }
  }
}

@Composable
fun PromotionalCard(
  onClaimClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(Color(0xFF2E1065), Color(0xFF4338CA))
          )
        )
        .padding(18.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFFBBF24)
      ) {
        Text(
          text = "LIMITED TIME OFFER",
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
          color = Color(0xFF78350F),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Spring Home Transformation",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Get $20 off and a complimentary set of 20 luxury velvet hangers with your first organization session.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.85f)
      )
      Spacer(modifier = Modifier.height(14.dp))
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier
          .clickable(onClick = onClaimClick)
          .testTag("claim_promo_button")
      ) {
        Text(
          text = "Book with Code NEST20",
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          color = NestifyPrimary,
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun NestifyGuaranteesCard(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "The Nestify Promise",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))
      GuaranteeItem(
        icon = Icons.Default.Shield,
        title = "Vetted & Background-Checked",
        description = "Only the top 5% of certified home organizers"
      )
      Spacer(modifier = Modifier.height(8.dp))
      GuaranteeItem(
        icon = Icons.Default.AutoAwesome,
        title = "Sustainable Systems",
        description = "Customized organization that stays tidy long after"
      )
      Spacer(modifier = Modifier.height(8.dp))
      GuaranteeItem(
        icon = Icons.Default.CleaningServices,
        title = "100% Happiness Guarantee",
        description = "Not in love with the result? We'll re-organize for free"
      )
    }
  }
}

@Composable
fun GuaranteeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .background(NestifyPrimary.copy(alpha = 0.15f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(16.dp))
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
      Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
