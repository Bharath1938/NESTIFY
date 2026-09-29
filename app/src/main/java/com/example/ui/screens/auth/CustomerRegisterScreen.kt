package com.example.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun CustomerRegisterScreen(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  var fullName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var loginId by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var localError by remember { mutableStateOf<String?>(null) }

  val authError by viewModel.authErrorMessage.collectAsState()
  val isAuthenticating by viewModel.isAuthenticating.collectAsState()

  BackHandler {
    viewModel.navigateAuth(AuthScreen.CustomerLogin)
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
        onClick = { viewModel.navigateAuth(AuthScreen.CustomerLogin) },
        modifier = Modifier.testTag("customer_register_back_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
      }
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "Customer Registration",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = NestifyPrimary
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
    ) {
      Text(
        text = "Create Customer Account",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Sign up to book professional organizers and transform your living spaces.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Locked Role Indicator
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = NestifyPrimary.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Account Role:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Customer (Fixed)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = NestifyPrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = fullName,
        onValueChange = {
          fullName = it
          localError = null
        },
        label = { Text("Full Name") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NestifyPrimary) },
        modifier = Modifier.fillMaxWidth().testTag("customer_reg_name_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = {
          phone = it
          localError = null
        },
        label = { Text("Mobile Number") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NestifyPrimary) },
        modifier = Modifier.fillMaxWidth().testTag("customer_reg_phone_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = email,
        onValueChange = {
          email = it
          localError = null
        },
        label = { Text("Email Address") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NestifyPrimary) },
        modifier = Modifier.fillMaxWidth().testTag("customer_reg_email_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = loginId,
        onValueChange = {
          loginId = it
          localError = null
        },
        label = { Text("Login ID / Username") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NestifyPrimary) },
        modifier = Modifier.fillMaxWidth().testTag("customer_reg_login_id_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = password,
        onValueChange = {
          password = it
          localError = null
        },
        label = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NestifyPrimary) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth().testTag("customer_reg_password_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = confirmPassword,
        onValueChange = {
          confirmPassword = it
          localError = null
        },
        label = { Text("Confirm Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NestifyPrimary) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth().testTag("customer_reg_confirm_password_input"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
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
            localError = "Please fill in all registration fields."
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

          viewModel.registerCustomer(fullName, phone, email, loginId, password)
        },
        enabled = !isAuthenticating,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("submit_customer_registration_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
      ) {
        if (isAuthenticating) {
          CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
          Spacer(modifier = Modifier.width(10.dp))
          Text("Creating Customer Account...")
        } else {
          Text(
            text = "Create Customer Account",
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
          text = "Already have an account?",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = { viewModel.navigateAuth(AuthScreen.CustomerLogin) }) {
          Text(
            text = "Login as Customer",
            color = NestifyPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
