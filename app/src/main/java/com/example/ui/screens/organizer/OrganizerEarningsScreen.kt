package com.example.ui.screens.organizer

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NestifyViewModel
import com.example.ui.theme.NestifyError
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySuccess

@Composable
fun OrganizerEarningsScreen(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val allBookings by viewModel.allBookings.collectAsState()
  var selectedTab by remember { mutableIntStateOf(1) } // Default This Week
  val tabs = listOf("Today", "This Week", "This Month")

  val completedBookings = allBookings.filter { it.status == "COMPLETED" }

  val multiplier = when (selectedTab) {
    0 -> 1.0
    1 -> 3.5
    else -> 12.0
  }

  val baseSampleGross = if (completedBookings.isNotEmpty()) {
    completedBookings.sumOf { it.totalPrice }
  } else {
    219.0
  }

  val grossEarnings = baseSampleGross * multiplier
  val platformFee = grossEarnings * 0.15
  val netEarnings = grossEarnings - platformFee

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 110.dp)
  ) {
    // Header
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
      ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
          Text(
            text = "Earnings & Payouts",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Track gross income, Nestify platform fees, and direct bank deposits",
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
            tabs.forEachIndexed { index, title ->
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
    }

    // Hero Total Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Net Take-Home Pay (${tabs[selectedTab]})",
              style = MaterialTheme.typography.labelMedium,
              color = Color.White.copy(alpha = 0.8f)
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = NestifySuccess.copy(alpha = 0.2f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = NestifySuccess, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "+18% vs last period", color = NestifySuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "$${String.format("%.2f", netEarnings)}",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "Gross Billings", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
              Text(text = "$${String.format("%.2f", grossEarnings)}", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column {
              Text(text = "Platform Fee (15%)", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
              Text(text = "-$${String.format("%.2f", platformFee)}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFFFB4AB), fontWeight = FontWeight.Bold)
            }
            Column {
              Text(text = "Payout Schedule", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
              Text(text = "Weekly (Friday)", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Direct Bank Account Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(NestifySuccess.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = NestifySuccess)
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "Chase Premier Checking •••• 8812", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = "Next automatic payout: Tomorrow, $${String.format("%.2f", netEarnings)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = NestifySuccess.copy(alpha = 0.15f)
          ) {
            Text(
              text = "ACTIVE",
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              color = NestifySuccess,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Breakdown per service
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
          text = "Completed Transformations History",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(completedBookings) { booking ->
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
            Text(text = booking.serviceName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${booking.bookingDate} • Customer: ${booking.customerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "Gross: $${booking.totalPrice.toInt()} | Fee: -$${(booking.totalPrice * 0.15).toInt()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "+$${(booking.totalPrice * 0.85).toInt()}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = NestifySuccess
            )
            Surface(shape = RoundedCornerShape(4.dp), color = NestifySuccess.copy(alpha = 0.15f)) {
              Text(text = "DIRECT DEPOSIT", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = NestifySuccess, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
