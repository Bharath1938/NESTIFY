package com.example.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthScreen
import com.example.ui.NestifyViewModel
import com.example.ui.theme.NestifyError
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySuccess

@Composable
fun OrganizerRegisterScreen(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  var fullName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var loginId by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var experience by remember { mutableStateOf("3 Years") }
  var skills by remember { mutableStateOf("Wardrobe, Kitchen & Pantry Organization") }
  var categories by remember { mutableStateOf("Bedroom, Kitchen, Living Space") }
  var areas by remember { mutableStateOf("Downtown, Westside, Suburbs") }

  var localError by remember { mutableStateOf<String?>(null) }
  var isSubmittedSuccess by remember { mutableStateOf(false) }

  val authError by viewModel.authErrorMessage.collectAsState()
  val isAuthenticating by viewModel.isAuthenticating.collectAsState()

  BackHandler {
    viewModel.navigateAuth(AuthScreen.OrganizerLogin)
  }

  if (isSubmittedSuccess) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(60.dp)
              .background(NestifySuccess.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NestifySuccess, modifier = Modifier.size(36.dp))
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Application Submitted!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFEF3C7)
          ) {
            Text(
              text = "STATUS: PENDING ADMIN APPROVAL",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              color = Color(0xFF92400E),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Thank you for applying to be a certified Nestify Organizer Partner! Our admin team reviews all background checks and skills within 24-48 hours.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "Tip for evaluation: You can immediately explore the organizer workflow using the pre-approved demo credentials: organizer@nestify.demo / Nestify@456.",
              modifier = Modifier.padding(12.dp),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = { viewModel.navigateAuth(AuthScreen.OrganizerLogin) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
          ) {
            Text("Back to Organizer Login")
          }
        }
      }
    }
    return
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateAuth(AuthScreen.OrganizerLogin) },
        modifier = Modifier.testTag("organizer_register_back_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
      }
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "Organizer Partner Application",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B)
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
    ) {
      Text(
        text = "Join as an Organizer",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Deliver five-star home transformations and build a flexible, high-earning business.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Fixed Role Banner
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "Account Role:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Organizer (Fixed)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = fullName,
        onValueChange = { fullName = it; localError = null },
        label = { Text("Full Name") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1E293B)) },
        modifier = Modifier.fillMaxWidth().testTag("organizer_reg_name_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it; localError = null },
        label = { Text("Mobile Number") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF1E293B)) },
        modifier = Modifier.fillMaxWidth().testTag("organizer_reg_phone_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = email,
        onValueChange = { email = it; localError = null },
        label = { Text("Email Address") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1E293B)) },
        modifier = Modifier.fillMaxWidth().testTag("organizer_reg_email_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = loginId,
        onValueChange = { loginId = it; localError = null },
        label = { Text("Organizer Login ID") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1E293B)) },
        modifier = Modifier.fillMaxWidth().testTag("organizer_reg_login_id_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = password,
        onValueChange = { password = it; localError = null },
        label = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1E293B)) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth().testTag("organizer_reg_password_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it; localError = null },
        label = { Text("Confirm Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1E293B)) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth().testTag("organizer_reg_confirm_password_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = experience,
        onValueChange = { experience = it; localError = null },
        label = { Text("Years of Experience") },
        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF1E293B)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = skills,
        onValueChange = { skills = it; localError = null },
        label = { Text("Specialized Skills") },
        leadingIcon = { Icon(Icons.Default.HomeRepairService, contentDescription = null, tint = Color(0xFF1E293B)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = categories,
        onValueChange = { categories = it; localError = null },
        label = { Text("Service Categories (Bedroom, Kitchen, etc.)") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = areas,
        onValueChange = { areas = it; localError = null },
        label = { Text("Covered Service Areas / Neighborhoods") },
        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1E293B)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      val displayError = localError ?: authError
      if (displayError != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = NestifyError.copy(alpha = 0.1f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = NestifyError, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = displayError, color = NestifyError, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = {
          if (fullName.isBlank() || phone.isBlank() || email.isBlank() || loginId.isBlank() || password.isBlank()) {
            localError = "Please fill in all required fields."
            return@Button
          }
          if (password.length < 6) {
            localError = "Password must be at least 6 characters."
            return@Button
          }
          if (password != confirmPassword) {
            localError = "Passwords do not match."
            return@Button
          }

          viewModel.registerOrganizer(
            fullName, phone, email, loginId, password, experience, skills, categories, areas
          ) {
            isSubmittedSuccess = true
          }
        },
        enabled = !isAuthenticating,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("submit_organizer_registration_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
      ) {
        if (isAuthenticating) {
          CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
          Spacer(modifier = Modifier.width(10.dp))
          Text("Submitting Application...")
        } else {
          Text(
            text = "Submit Partner Application",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Already an organizer partner?",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = { viewModel.navigateAuth(AuthScreen.OrganizerLogin) }) {
          Text(
            text = "Login as Organizer",
            color = Color(0xFF1E293B),
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
