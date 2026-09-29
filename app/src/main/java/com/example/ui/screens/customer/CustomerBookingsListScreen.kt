package com.example.ui.screens.customer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
import com.example.ui.CustomerScreen
import com.example.ui.NestifyViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySuccess

@Composable
fun CustomerBookingsListScreen(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val allBookings by viewModel.allBookings.collectAsState()
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabTitles = listOf("Active (Now)", "Upcoming", "Completed", "All")

  val filteredBookings = when (selectedTab) {
    0 -> allBookings.filter { it.status in listOf("IN_PROGRESS", "ARRIVED", "ON_THE_WAY") }
    1 -> allBookings.filter { it.status in listOf("CONFIRMED", "ASSIGNED") }
    2 -> allBookings.filter { it.status == "COMPLETED" }
    else -> allBookings
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 2.dp
    ) {
      Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(
          text = "My Bookings",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Track your home organization appointments & view past transformations",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = NestifyPrimary,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = NestifyPrimary
            )
          }
        ) {
          tabTitles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = title,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                )
              }
            )
          }
        }
      }
    }

    // List of Bookings
    if (filteredBookings.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "📦", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No bookings found in this category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Ready to transform a cluttered space in your home?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { viewModel.navigateCustomer(CustomerScreen.Home) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
          ) {
            Text("Discover Services")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(filteredBookings) { booking ->
          CustomerBookingCard(
            booking = booking,
            onCardClick = {
              viewModel.navigateCustomer(CustomerScreen.BookingDetail(booking.id))
            },
            onViewBeforeAfter = {
              viewModel.navigateCustomer(CustomerScreen.BeforeAfterReview(booking.id))
            }
          )
        }
      }
    }
  }
}

@Composable
fun CustomerBookingCard(
  booking: BookingEntity,
  onCardClick: () -> Unit,
  onViewBeforeAfter: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onCardClick)
      .testTag("booking_card_${booking.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = booking.serviceName,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        StatusBadge(status = booking.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${booking.bookingDate} • ${booking.timeSlot}",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = booking.address,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Organizer: ${booking.organizerName}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "Total: $${booking.totalPrice.toInt()}",
            style = MaterialTheme.typography.bodySmall,
            color = NestifyPrimary,
            fontWeight = FontWeight.Bold
          )
        }

        if (booking.status == "COMPLETED") {
          Button(
            onClick = onViewBeforeAfter,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (booking.rating > 0) "Review" else "Before / After")
          }
        } else {
          OutlinedButton(
            onClick = onCardClick,
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Track Service")
          }
        }
      }
    }
  }
}
