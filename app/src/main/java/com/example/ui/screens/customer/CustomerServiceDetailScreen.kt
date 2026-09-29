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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogData
import com.example.data.model.CustomerReview
import com.example.data.model.ServiceItem
import com.example.ui.CustomerScreen
import com.example.ui.NestifyViewModel
import com.example.ui.components.BeforeAfterTransformationCard
import com.example.ui.components.CategoryIcon
import com.example.ui.components.RatingStars
import com.example.ui.theme.NestifyError
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess

@Composable
fun CustomerServiceDetailScreen(
  service: ServiceItem,
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.navigateCustomer(CustomerScreen.Home)
  }

  Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 110.dp)
    ) {
      // 1. Hero Header Banner
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(service.colorSeed),
                  Color(service.colorSeed).copy(alpha = 0.85f)
                )
              )
            )
        ) {
          // Top Navigation row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { viewModel.navigateCustomer(CustomerScreen.Home) },
              modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                .testTag("back_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.Black.copy(alpha = 0.3f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = Color(0xFFF59E0B),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${service.rating} (${service.reviewsCount} reviews)",
                  color = Color.White,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          // Hero Icon and Category
          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(20.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.25f)
            ) {
              Text(
                text = service.category.displayName.uppercase(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = service.name,
              style = MaterialTheme.typography.headlineMedium,
              color = Color.White,
              fontWeight = FontWeight.ExtraBold
            )
            Text(
              text = service.tagline,
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.9f)
            )
          }
        }
      }

      // 2. Quick Overview Stats
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            QuickStat(label = "Duration", value = "~${service.estimatedHours} hrs")
            QuickStat(label = "Organizers", value = if (service.startingPrice > 300) "2-3 Pros" else "1 Pro")
            QuickStat(label = "Starting at", value = "$${service.startingPrice.toInt()}")
          }
        }
      }

      // 3. Description
      item {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
          Text(
            text = "About this Service",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = service.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          )
        }
      }

      // 4. What's Included vs What's Excluded
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "What's Included",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            service.included.forEach { inc ->
              Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
              ) {
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .background(NestifySuccess.copy(alpha = 0.15f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = NestifySuccess,
                    modifier = Modifier.size(12.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = inc,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }

            if (service.excluded.isNotEmpty()) {
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "What's Not Included",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(6.dp))
              service.excluded.forEach { exc ->
                Row(
                  modifier = Modifier.padding(vertical = 3.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Box(
                    modifier = Modifier
                      .size(18.dp)
                      .background(NestifyError.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = null,
                      tint = NestifyError,
                      modifier = Modifier.size(12.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = exc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }

      // 5. Signature Before & After Transformation
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
          Text(
            text = "Transformation Guarantee",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          BeforeAfterTransformationCard(
            title = service.name,
            serviceCategory = service.category.displayName,
            beforeSubtitle = service.beforeSummary,
            afterSubtitle = service.afterSummary
          )
        }
      }

      // 6. Benefits
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Key Benefits for You",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            service.benefits.forEach { benefit ->
              Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "✨", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = benefit,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }

      // 7. Customer Reviews Section
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Verified Customer Reviews",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${service.rating}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      items(CatalogData.SAMPLE_REVIEWS) { review ->
        ReviewCard(
          review = review,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }
    }

    // Sticky Bottom Book Now CTA Bar
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth(),
      shadowElevation = 16.dp,
      color = MaterialTheme.colorScheme.surface
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Total Estimate",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "$${service.startingPrice.toInt()}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = NestifyPrimary
          )
        }

        Button(
          onClick = {
            viewModel.startBooking(service)
          },
          modifier = Modifier
            .width(200.dp)
            .height(52.dp)
            .testTag("book_now_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
        ) {
          Text(
            text = "Book Now",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun QuickStat(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = NestifyPrimary
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun ReviewCard(
  review: CustomerReview,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = review.reviewerName,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = review.date,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      RatingStars(rating = review.rating, starSize = 14)

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = review.comment,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (review.tags.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          review.tags.forEach { tag ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = NestifyPrimary.copy(alpha = 0.08f)
            ) {
              Text(
                text = "#$tag",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 11.sp,
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
