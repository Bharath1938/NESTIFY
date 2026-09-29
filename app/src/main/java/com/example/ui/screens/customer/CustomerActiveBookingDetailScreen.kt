package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
import com.example.data.model.CatalogData
import com.example.ui.CustomerScreen
import com.example.ui.NestifyViewModel
import com.example.ui.components.BookingTimelineTracker
import com.example.ui.components.PriceSummaryCard
import com.example.ui.components.RatingStars
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerActiveBookingDetailScreen(
  bookingId: Long,
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val allBookings by viewModel.allBookings.collectAsState()
  val booking = allBookings.firstOrNull { it.id == bookingId }

  BackHandler {
    viewModel.navigateCustomer(CustomerScreen.BookingsList)
  }

  if (booking == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Booking not found.")
    }
    return
  }

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 110.dp)
    ) {
      // Top bar
      item {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MaterialTheme.colorScheme.surface,
          shadowElevation = 2.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { viewModel.navigateCustomer(CustomerScreen.BookingsList) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Booking #${booking.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = booking.serviceName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Status & Header card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
              )
              StatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${booking.bookingDate} • ${booking.timeSlot}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = booking.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Live Tracking Timeline
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Live Service Timeline",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            BookingTimelineTracker(currentStatus = booking.status)
          }
        }
      }

      // Assigned Organizer Card
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Your Assigned Professional",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(NestifyPrimary),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "EM",
                  color = Color.White,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = booking.organizerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = NestifySuccess.copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = "VERIFIED PRO",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      color = NestifySuccess,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  RatingStars(rating = booking.organizerRating, starSize = 13)
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${booking.organizerRating} • 142 jobs done",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Text(
                  text = "Distance: ${booking.organizerDistance}",
                  style = MaterialTheme.typography.labelSmall,
                  color = NestifyPrimary,
                  fontWeight = FontWeight.Medium
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = { /* Call intent simulation */ },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Call")
              }
              Button(
                onClick = { /* Message simulation */ },
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
              ) {
                Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Message")
              }
            }
          }
        }
      }

      // Price & Payment Summary
      item {
        Spacer(modifier = Modifier.height(16.dp))
        PriceSummaryCard(
          basePrice = booking.basePrice,
          addonPrice = booking.addonPrice,
          serviceFee = booking.serviceFee,
          discount = booking.discount,
          totalPrice = booking.totalPrice,
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }
    }

    // Bottom Action based on status
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 16.dp
    ) {
      Box(modifier = Modifier.padding(16.dp)) {
        if (booking.status == "COMPLETED") {
          Button(
            onClick = {
              viewModel.navigateCustomer(CustomerScreen.BeforeAfterReview(booking.id))
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("view_before_after_cta"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFBBF24))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (booking.rating > 0) "View Transformation & Review" else "View Before / After & Rate Pro",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        } else {
          // Can fast-forward demo status if testing!
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Service in Progress",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Elena is actively organizing",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Button(
              onClick = {
                // Advance to complete to see before/after easily
                viewModel.advanceBookingStatus(booking.id, "COMPLETED")
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NestifySuccess)
            ) {
              Text("Complete Job (Demo)")
            }
          }
        }
      }
    }
  }
}
