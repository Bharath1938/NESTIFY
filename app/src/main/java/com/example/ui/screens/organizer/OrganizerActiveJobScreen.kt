package com.example.ui.screens.organizer

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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.NestifyViewModel
import com.example.ui.OrganizerScreen
import com.example.ui.components.PhotoUploadSlots
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NestifyError
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess
import com.example.util.CameraPermissionRationaleDialog
import com.example.util.rememberCameraPermissionHandler

@Composable
fun OrganizerActiveJobScreen(
  bookingId: Long,
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val cameraPermission = rememberCameraPermissionHandler()
  val allBookings by viewModel.allBookings.collectAsState()
  val booking = allBookings.firstOrNull { it.id == bookingId }

  BackHandler {
    viewModel.navigateOrganizer(OrganizerScreen.Dashboard)
  }

  if (booking == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Job not found.")
    }
    return
  }

  var beforePhotos by remember { mutableIntStateOf(if (booking.beforePhotosCount > 0) booking.beforePhotosCount else 0) }
  var afterPhotos by remember { mutableIntStateOf(if (booking.afterPhotosCount > 0) booking.afterPhotosCount else 0) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 120.dp)
    ) {
      // 1. Top App Bar
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
              onClick = { viewModel.navigateOrganizer(OrganizerScreen.Dashboard) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Job #${booking.id} • ${booking.serviceName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Payout: $${(booking.totalPrice * 0.85).toInt()} (Net)",
                style = MaterialTheme.typography.bodySmall,
                color = NestifySuccess,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // 2. Customer & Address Information Card
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
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NestifyPrimary),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "SJ",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(text = booking.customerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                  Text(text = booking.customerPhone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }

              StatusBadge(status = booking.status)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = booking.address, style = MaterialTheme.typography.bodyMedium)
            }

            if (booking.landmark.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Gate / Landmark: ${booking.landmark}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            if (booking.customerNotes.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text(text = "Customer Instructions:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                  Text(text = "\"${booking.customerNotes}\"", style = MaterialTheme.typography.bodySmall)
                }
              }
            }
          }
        }
      }

      // 3. Workflow Step: Mandatory BEFORE Photos
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
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "1. Before Photos (Required)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              if (beforePhotos >= 3) {
                Surface(shape = RoundedCornerShape(6.dp), color = NestifySuccess.copy(alpha = 0.15f)) {
                  Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = NestifySuccess, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "VERIFIED", color = NestifySuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Take 3-5 photos of the cluttered space before moving any items.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhotoUploadSlots(
              photoCount = beforePhotos,
              maxPhotos = 4,
              onAddPhoto = {
                if (cameraPermission.isGranted) {
                  if (beforePhotos < 4) {
                    beforePhotos++
                    viewModel.updateBeforePhotos(booking.id, beforePhotos)
                  }
                } else {
                  cameraPermission.requestPermission()
                }
              },
              onRemovePhoto = {
                if (beforePhotos > 0) {
                  beforePhotos--
                  viewModel.updateBeforePhotos(booking.id, beforePhotos)
                }
              }
            )
          }
        }
      }

      // 4. Workflow Step: Organization in Progress Timer
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
              text = "2. Service Progress",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = NestifyPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(text = "Organization Active", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                  Text(text = "Target: 3.5 hours", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (booking.status == "IN_PROGRESS") NestifySuccess else Color.Gray
              ) {
                Text(
                  text = if (booking.status == "IN_PROGRESS") "RUNNING" else "READY",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      // 5. Workflow Step: Mandatory AFTER Photos
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
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "3. After Photos (Transformation)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              if (afterPhotos >= 3) {
                Surface(shape = RoundedCornerShape(6.dp), color = NestifySuccess.copy(alpha = 0.15f)) {
                  Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = NestifySuccess, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "COMPLETE", color = NestifySuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Take 3-5 photos of the finished boutique space. The customer will review these.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhotoUploadSlots(
              photoCount = afterPhotos,
              maxPhotos = 4,
              onAddPhoto = {
                if (cameraPermission.isGranted) {
                  if (afterPhotos < 4) {
                    afterPhotos++
                    viewModel.updateAfterPhotos(booking.id, afterPhotos)
                  }
                } else {
                  cameraPermission.requestPermission()
                }
              },
              onRemovePhoto = {
                if (afterPhotos > 0) {
                  afterPhotos--
                  viewModel.updateAfterPhotos(booking.id, afterPhotos)
                }
              }
            )
          }
        }
      }
    }

    // Bottom Workflow Action Bar
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 16.dp
    ) {
      Box(modifier = Modifier.padding(16.dp)) {
        when (booking.status) {
          "ASSIGNED" -> {
            Button(
              onClick = { viewModel.advanceBookingStatus(booking.id, "ON_THE_WAY") },
              modifier = Modifier.fillMaxWidth().height(52.dp),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
            ) {
              Icon(Icons.Default.Navigation, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Start Navigation to Customer")
            }
          }
          "ON_THE_WAY" -> {
            Button(
              onClick = { viewModel.advanceBookingStatus(booking.id, "ARRIVED") },
              modifier = Modifier.fillMaxWidth().height(52.dp),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
              Icon(Icons.Default.LocationOn, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("I Have Arrived at Location")
            }
          }
          "ARRIVED" -> {
            Button(
              onClick = {
                if (beforePhotos == 0) {
                  beforePhotos = 3
                  viewModel.updateBeforePhotos(booking.id, 3)
                }
                viewModel.advanceBookingStatus(booking.id, "IN_PROGRESS")
              },
              modifier = Modifier.fillMaxWidth().height(52.dp),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NestifySuccess)
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Save Before Photos & Start Service")
            }
          }
          "IN_PROGRESS" -> {
            Button(
              onClick = {
                if (afterPhotos == 0) {
                  afterPhotos = 3
                  viewModel.updateAfterPhotos(booking.id, 3)
                }
                viewModel.advanceBookingStatus(booking.id, "COMPLETED")
                viewModel.navigateOrganizer(OrganizerScreen.Dashboard)
              },
              modifier = Modifier.fillMaxWidth().height(52.dp).testTag("complete_job_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = NestifySuccess)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Upload After Photos & Complete Job")
            }
          }
          else -> {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = NestifySuccess.copy(alpha = 0.15f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "✓ Job Completed & Submitted to Customer for Review",
                modifier = Modifier.padding(14.dp),
                color = NestifySuccess,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
              )
            }
          }
        }
      }
    }

    CameraPermissionRationaleDialog(state = cameraPermission)
  }
}
