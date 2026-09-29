package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookingEntity
import com.example.ui.CustomerScreen
import com.example.ui.NestifyViewModel
import com.example.ui.components.BeforeAfterTransformationCard
import com.example.ui.components.InteractiveStarRatingPicker
import com.example.ui.components.RatingStars
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomerBeforeAfterScreen(
  bookingId: Long,
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val allBookings by viewModel.allBookings.collectAsState()
  val booking = allBookings.firstOrNull { it.id == bookingId }

  BackHandler {
    viewModel.navigateCustomer(CustomerScreen.BookingDetail(bookingId))
  }

  if (booking == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Booking not found.")
    }
    return
  }

  var rating by remember { mutableIntStateOf(if (booking.rating > 0) booking.rating else 5) }
  var writtenReview by remember { mutableStateOf(booking.reviewText.ifBlank { "Elena was amazing! The space feels like a breath of fresh air. Everything has a designated home now." }) }
  val selectedTags = remember {
    mutableStateListOf<String>().apply {
      if (booking.reviewTags.isNotBlank()) {
        addAll(booking.reviewTags.split(", ").filter { it.isNotBlank() })
      } else {
        addAll(listOf("Boutique Finish", "Super Clean", "Gentle & Fast"))
      }
    }
  }

  val availableTags = listOf(
    "Boutique Finish",
    "Super Clean",
    "Gentle & Fast",
    "Punctual Pro",
    "Space Doubled",
    "Kid Friendly",
    "10/10 Perfection"
  )

  var hasSubmittedReview by remember { mutableStateOf(booking.rating > 0) }

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 120.dp)
    ) {
      // Top Bar
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
              onClick = { viewModel.navigateCustomer(CustomerScreen.BookingDetail(bookingId)) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Home Transformation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${booking.serviceName} • Completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Transformation Hero Header
      item {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Discover → Book → Transform",
              style = MaterialTheme.typography.labelMedium,
              color = NestifyPrimary,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Your Transformed Space",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Drag the interactive slider below to inspect the before and after details captured by ${booking.organizerName}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Interactive Before/After Component
      item {
        BeforeAfterTransformationCard(
          title = booking.serviceName,
          serviceCategory = booking.category,
          beforeSubtitle = "Cluttered storage, mixed garments & lost vertical space",
          afterSubtitle = "Zoned, labeled, color-coordinated organization system",
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }

      // Transformed Areas Checklist
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Transformed Zones Checklist",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            val checklist = listOf(
              "Full Categorical Sorting & Purge Completed",
              "Vertical Space Utilization +65% Capacity",
              "Matching Non-Slip Velvet Hangers Installed",
              "Top Shelf Seasonal Bins Labeled",
              "Maintenance Guide Provided to Sarah"
            )
            checklist.forEach { item ->
              Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = NestifySuccess,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = item,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      // Rate & Review Section
      item {
        Spacer(modifier = Modifier.height(20.dp))
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = if (hasSubmittedReview) "Your Review for ${booking.organizerName}" else "Rate & Review ${booking.organizerName}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (hasSubmittedReview) "Thank you for confirming! Your review helps top organizers flourish." else "Share your experience with other homeowners.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!hasSubmittedReview) {
              InteractiveStarRatingPicker(
                currentRating = rating,
                onRatingChanged = { rating = it }
              )

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "Select Tags",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))

              FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                availableTags.forEach { tag ->
                  val isSelected = selectedTags.contains(tag)
                  Surface(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .clickable {
                        if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                      },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) NestifyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                  ) {
                    Text(
                      text = tag,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              OutlinedTextField(
                value = writtenReview,
                onValueChange = { writtenReview = it },
                label = { Text("Your Review") },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(110.dp)
                  .testTag("written_review_input"),
                shape = RoundedCornerShape(12.dp)
              )

              Spacer(modifier = Modifier.height(16.dp))

              Button(
                onClick = {
                  viewModel.submitReview(
                    bookingId = booking.id,
                    rating = rating,
                    reviewText = writtenReview,
                    tags = selectedTags.joinToString(", ")
                  )
                  hasSubmittedReview = true
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("submit_review_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
              ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Confirm Completion & Submit Review")
              }
            } else {
              // Read-only confirmed review display
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                  .padding(14.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RatingStars(rating = rating.toFloat())
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NestifySuccess.copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = "COMPLETION CONFIRMED",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      color = NestifySuccess,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = writtenReview,
                  style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Tags: ${selectedTags.joinToString(", ")}",
                  style = MaterialTheme.typography.labelSmall,
                  color = NestifyPrimary,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }
    }
  }
}
