package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceCategory
import com.example.data.model.UserRole
import com.example.ui.theme.NestifyError
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess
import com.example.ui.theme.NestifyWarning

@Composable
fun StatusBadge(
  status: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, label) = when (status) {
    "CONFIRMED" -> Triple(Color(0xFFE0E7FF), Color(0xFF3730A3), "Confirmed")
    "ASSIGNED" -> Triple(Color(0xFFEDE9FE), Color(0xFF5B21B6), "Organizer Assigned")
    "ON_THE_WAY" -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "On The Way")
    "ARRIVED" -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "Arrived")
    "IN_PROGRESS" -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "In Progress")
    "COMPLETED" -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), "Completed")
    "CANCELLED" -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "Cancelled")
    else -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), status)
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = bgColor
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .background(textColor, CircleShape)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun RoleSwitcherHeader(
  currentRole: UserRole,
  onToggleRole: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = if (currentRole == UserRole.CUSTOMER) NestifyPrimary else Color(0xFF1E293B),
    tonalElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .background(if (currentRole == UserRole.CUSTOMER) NestifySuccess else Color(0xFF38BDF8), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (currentRole == UserRole.CUSTOMER) "Customer Mode" else "Organizer Partner Mode",
          color = Color.White,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold
        )
      }

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.2f),
        modifier = Modifier.clickable(onClick = onToggleRole)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Switch app mode",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (currentRole == UserRole.CUSTOMER) "Switch to Organizer" else "Switch to Customer",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun CategoryIcon(category: ServiceCategory): ImageVector {
  return when (category) {
    ServiceCategory.BEDROOM -> Icons.Default.Bed
    ServiceCategory.KITCHEN -> Icons.Default.Kitchen
    ServiceCategory.STORAGE -> Icons.Default.Inventory2
    ServiceCategory.KIDS -> Icons.Default.ChildCare
    ServiceCategory.LIVING -> Icons.Default.Weekend
    ServiceCategory.SPECIAL -> Icons.Default.HomeWork
  }
}

@Composable
fun CategoryChipItem(
  category: ServiceCategory,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag("category_chip_${category.name.lowercase()}"),
    shape = RoundedCornerShape(14.dp),
    color = if (isSelected) NestifyPrimary else MaterialTheme.colorScheme.surface,
    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    shadowElevation = if (isSelected) 3.dp else 0.dp
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = CategoryIcon(category),
        contentDescription = category.displayName,
        tint = if (isSelected) Color.White else NestifyPrimary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = category.displayName,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
fun RatingStars(
  rating: Float,
  modifier: Modifier = Modifier,
  starSize: Int = 16
) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    for (i in 1..5) {
      val isFilled = i <= rating.toInt()
      Icon(
        imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarOutline,
        contentDescription = null,
        tint = Color(0xFFF59E0B),
        modifier = Modifier.size(starSize.dp)
      )
    }
  }
}

@Composable
fun InteractiveStarRatingPicker(
  currentRating: Int,
  onRatingChanged: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (star in 1..5) {
      IconButton(
        onClick = { onRatingChanged(star) },
        modifier = Modifier.size(44.dp)
      ) {
        Icon(
          imageVector = if (star <= currentRating) Icons.Default.Star else Icons.Outlined.StarOutline,
          contentDescription = "$star Stars",
          tint = if (star <= currentRating) Color(0xFFF59E0B) else Color(0xFFD1D5DB),
          modifier = Modifier.size(36.dp)
        )
      }
    }
  }
}

@Composable
fun PhotoUploadSlots(
  photoCount: Int,
  maxPhotos: Int = 5,
  onAddPhoto: () -> Unit,
  onRemovePhoto: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Space Photos ($photoCount/$maxPhotos)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Helps organizers plan supplies",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      for (i in 0 until maxPhotos) {
        if (i < photoCount) {
          // Uploaded Photo Slot with thumbnail
          Box(
            modifier = Modifier
              .weight(1f)
              .height(72.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(NestifySecondary.copy(alpha = 0.15f))
              .border(1.5.dp, NestifySecondary, RoundedCornerShape(12.dp))
          ) {
            Column(
              modifier = Modifier.align(Alignment.Center),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = NestifySuccess,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Photo #${i + 1}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = NestifySecondary
              )
            }

            Box(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(NestifyError)
                .clickable { onRemovePhoto(i) },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove photo",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
              )
            }
          }
        } else if (i == photoCount) {
          // Next Add Photo button
          Box(
            modifier = Modifier
              .weight(1f)
              .height(72.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .border(1.5.dp, NestifyPrimary, RoundedCornerShape(12.dp))
              .clickable(onClick = onAddPhoto)
              .testTag("add_photo_button"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Take or upload photo",
                tint = NestifyPrimary,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Add",
                style = MaterialTheme.typography.labelSmall,
                color = NestifyPrimary,
                fontWeight = FontWeight.Bold
              )
            }
          }
        } else {
          // Empty inactive placeholder
          Box(
            modifier = Modifier
              .weight(1f)
              .height(72.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "+",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
          }
        }
      }
    }
  }
}

@Composable
fun PriceSummaryCard(
  basePrice: Double,
  addonPrice: Double,
  serviceFee: Double,
  discount: Double,
  totalPrice: Double,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Price Breakdown",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(12.dp))

      PriceRow(label = "Base Service Fee", amount = basePrice)
      if (addonPrice > 0) {
        PriceRow(label = "Extra Units / Volume Add-on", amount = addonPrice)
      }
      PriceRow(label = "Platform & Supplies Fee", amount = serviceFee)
      if (discount > 0) {
        PriceRow(label = "Promo Discount (NEST20)", amount = -discount, isDiscount = true)
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        color = MaterialTheme.colorScheme.outlineVariant
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Total Amount",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Includes 100% Nestify Guarantee",
            style = MaterialTheme.typography.labelSmall,
            color = NestifySuccess
          )
        }
        Text(
          text = "$${String.format("%.2f", totalPrice)}",
          style = MaterialTheme.typography.headlineSmall,
          color = NestifyPrimary,
          fontWeight = FontWeight.ExtraBold
        )
      }
    }
  }
}

@Composable
fun PriceRow(label: String, amount: Double, isDiscount: Boolean = false) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = if (isDiscount) "-$${String.format("%.2f", -amount)}" else "$${String.format("%.2f", amount)}",
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold,
      color = if (isDiscount) NestifySuccess else MaterialTheme.colorScheme.onSurface
    )
  }
}

@Composable
fun BookingTimelineTracker(
  currentStatus: String,
  modifier: Modifier = Modifier
) {
  val steps = listOf(
    "Booking Confirmed",
    "Organizer Assigned",
    "On The Way",
    "Arrived",
    "In Progress",
    "Completed"
  )

  val currentIndex = when (currentStatus) {
    "CONFIRMED" -> 0
    "ASSIGNED" -> 1
    "ON_THE_WAY" -> 2
    "ARRIVED" -> 3
    "IN_PROGRESS" -> 4
    "COMPLETED" -> 5
    else -> 0
  }

  Column(modifier = modifier.fillMaxWidth()) {
    steps.forEachIndexed { index, stepLabel ->
      val isCompleted = index < currentIndex
      val isCurrent = index == currentIndex
      val isUpcoming = index > currentIndex

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        // Dot + connecting line
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.width(32.dp)
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(
                when {
                  isCompleted -> NestifySuccess
                  isCurrent -> NestifyPrimary
                  else -> MaterialTheme.colorScheme.outlineVariant
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            if (isCompleted) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
            } else if (isCurrent) {
              Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
            } else {
              Text(
                text = "${index + 1}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (index < steps.size - 1) {
            Box(
              modifier = Modifier
                .width(2.dp)
                .height(28.dp)
                .background(
                  if (isCompleted) NestifySuccess else MaterialTheme.colorScheme.outlineVariant
                )
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)) {
          Text(
            text = stepLabel,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isCurrent) FontWeight.Bold else if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isCurrent) NestifyPrimary else if (isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (isCurrent) {
            Text(
              text = "Current Status",
              style = MaterialTheme.typography.labelSmall,
              color = NestifyPrimary,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}
