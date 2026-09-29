package com.example.ui.screens.organizer

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.CatalogData
import com.example.ui.NestifyViewModel
import com.example.ui.OrganizerScreen
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NestifyError
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess

@Composable
fun OrganizerDashboardScreen(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val allBookings by viewModel.allBookings.collectAsState()
  var isOnline by remember { mutableStateOf(true) }

  val activeJob = allBookings.firstOrNull { it.status in listOf("ASSIGNED", "ON_THE_WAY", "ARRIVED", "IN_PROGRESS") }
  val requestedJobs = allBookings.filter { it.status == "CONFIRMED" }
  val completedJobs = allBookings.filter { it.status == "COMPLETED" }

  val todayEarnings = completedJobs.sumOf { it.totalPrice * 0.85 } + (activeJob?.let { it.totalPrice * 0.85 } ?: 0.0)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 100.dp)
  ) {
    // 1. Organizer Top Profile Banner
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E293B),
        shadowElevation = 4.dp
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(52.dp)
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

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Elena Martinez",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "4.95",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                  )
                }
                Text(
                  text = "Certified Senior Organizer • 142 Jobs",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.75f)
                )
              }
            }

            // Online Toggle
            Column(horizontalAlignment = Alignment.End) {
              Switch(
                checked = isOnline,
                onCheckedChange = { isOnline = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = NestifySuccess
                )
              )
              Text(
                text = if (isOnline) "Accepting Jobs" else "Offline",
                color = if (isOnline) NestifySuccess else Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // 2. Earnings Snapshot Cards
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OrganizerStatCard(
          title = "Today's Net",
          value = "$${todayEarnings.toInt()}",
          sub = "After 15% platform fee",
          modifier = Modifier.weight(1f)
        )
        OrganizerStatCard(
          title = "This Week",
          value = "$820",
          sub = "5 jobs completed",
          modifier = Modifier.weight(1f)
        )
        OrganizerStatCard(
          title = "Rating",
          value = "4.95 ★",
          sub = "Top 1% in Austin",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Active Job in Progress (Actionable)
    if (activeJob != null) {
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
          Text(
            text = "Active Job Workflow",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                viewModel.navigateOrganizer(OrganizerScreen.ActiveJobWorkflow(activeJob.id))
              }
              .testTag("organizer_active_job_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = activeJob.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "Customer: ${activeJob.customerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                StatusBadge(status = activeJob.status)
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = activeJob.address,
                  style = MaterialTheme.typography.bodyMedium,
                  maxLines = 1
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = {
                  viewModel.navigateOrganizer(OrganizerScreen.ActiveJobWorkflow(activeJob.id))
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
              ) {
                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Workflow & Capture Photos")
              }
            }
          }
        }
      }
    }

    // 4. Pending Job Requests (Accept / Reject)
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "New Job Requests",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${requestedJobs.size} pending",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    if (requestedJobs.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No pending job requests right now. New requests in your service areas will notify you instantly.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(requestedJobs) { reqJob ->
        OrganizerJobRequestCard(
          job = reqJob,
          onAccept = {
            viewModel.advanceBookingStatus(reqJob.id, "ASSIGNED")
          },
          onReject = {
            viewModel.advanceBookingStatus(reqJob.id, "CANCELLED")
          },
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }
    }

    // 5. Completed Jobs Summary
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = "Completed Transformations (${completedJobs.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
      }
    }

    items(completedJobs) { compJob ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = compJob.serviceName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${compJob.bookingDate} • Customer: ${compJob.customerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "+$${(compJob.totalPrice * 0.85).toInt()}",
              fontWeight = FontWeight.Bold,
              color = NestifySuccess,
              style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Paid out", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }
    }
  }
}

@Composable
fun OrganizerStatCard(
  title: String,
  value: String,
  sub: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = NestifyPrimary)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = sub, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
fun OrganizerJobRequestCard(
  job: BookingEntity,
  onAccept: () -> Unit,
  onReject: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
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
        Column(modifier = Modifier.weight(1f)) {
          Text(text = job.serviceName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text(text = "${job.customerName} • ${job.address}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
          text = "+$${(job.totalPrice * 0.85).toInt()}",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = NestifySuccess
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.AccessTime, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "${job.bookingDate} • ${job.timeSlot}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
      }

      if (job.customerNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Note: \"${job.customerNotes}\"",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onReject,
          modifier = Modifier.weight(1f).height(44.dp),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Decline")
        }
        Button(
          onClick = onAccept,
          modifier = Modifier.weight(1.3f).height(44.dp),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Accept Job")
        }
      }
    }
  }
}
