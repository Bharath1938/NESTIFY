package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthScreen
import com.example.ui.NestifyViewModel
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess

@Composable
fun WelcomeRoleSelectionScreen(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 24.dp, vertical = 28.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Nestify Logo Header
    Box(
      modifier = Modifier
        .size(68.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(
          Brush.linearGradient(listOf(NestifyPrimary, NestifySecondary))
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Home,
        contentDescription = "Nestify Logo",
        tint = Color.White,
        modifier = Modifier.size(38.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "NESTIFY",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.ExtraBold,
      color = NestifyPrimary,
      letterSpacing = 2.sp
    )

    Text(
      text = "We organize your home, so you don't have to.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(32.dp))

    // Heading
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = "Welcome to Nestify",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Choose how you want to continue.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Card 1: Customer Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(22.dp))
        .border(1.5.dp, NestifyPrimary.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
        .clickable {
          viewModel.navigateAuth(AuthScreen.CustomerLogin)
        }
        .testTag("welcome_customer_card"),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(modifier = Modifier.padding(22.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(NestifyPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = null,
              tint = NestifyPrimary,
              modifier = Modifier.size(30.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = NestifyPrimary.copy(alpha = 0.12f)
          ) {
            Text(
              text = "HOMEOWNER",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              color = NestifyPrimary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Customer",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Book home organization services",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = { viewModel.navigateAuth(AuthScreen.CustomerLogin) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("continue_as_customer_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
        ) {
          Text(
            text = "Continue as Customer",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Card 2: Organizer Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(22.dp))
        .border(1.5.dp, Color(0xFF1E293B).copy(alpha = 0.18f), RoundedCornerShape(22.dp))
        .clickable {
          viewModel.navigateAuth(AuthScreen.OrganizerLogin)
        }
        .testTag("welcome_organizer_card"),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(modifier = Modifier.padding(22.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF1E293B).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.BusinessCenter,
              contentDescription = null,
              tint = Color(0xFF1E293B),
              modifier = Modifier.size(28.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.1f)
          ) {
            Text(
              text = "PRO PARTNER",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              color = Color(0xFF1E293B),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Organizer",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Accept jobs and organize homes",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = { viewModel.navigateAuth(AuthScreen.OrganizerLogin) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("continue_as_organizer_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
        ) {
          Text(
            text = "Continue as Organizer",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Brand pillars footer
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Discover",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = NestifyPrimary
      )
      Text(
        text = " → ",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "Book",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = NestifySecondary
      )
      Text(
        text = " → ",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "Transform",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = NestifySuccess
      )
    }
  }
}
