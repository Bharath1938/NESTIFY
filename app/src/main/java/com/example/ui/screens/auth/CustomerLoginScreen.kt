package com.example.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthScreen
import com.example.ui.NestifyViewModel
import com.example.ui.theme.NestifyError
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess

@Composable
fun CustomerLoginScreen(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  var loginId by remember { mutableStateOf("customer@nestify.demo") }
  var password by remember { mutableStateOf("Nestify@123") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var rememberMe by remember { mutableStateOf(true) }
  var showForgotPassword by remember { mutableStateOf(false) }

  val authError by viewModel.authErrorMessage.collectAsState()
  val isAuthenticating by viewModel.isAuthenticating.collectAsState()

  BackHandler {
    viewModel.navigateAuth(AuthScreen.Welcome)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
  ) {
    // Top Bar with Back Button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateAuth(AuthScreen.Welcome) },
        modifier = Modifier.testTag("customer_login_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back to role selection"
        )
      }
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "Customer Portal",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = NestifyPrimary
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(8.dp))

      // Home & Nestify Branding Visual
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(Brush.linearGradient(listOf(NestifyPrimary, NestifySecondary))),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Home,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Welcome Home",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Book a professional organizer and transform your space.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Demo Credentials Quick-Fill Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            loginId = "customer@nestify.demo"
            password = "Nestify@123"
            viewModel.clearAuthError()
          }
          .testTag("customer_demo_credentials_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NestifyPrimary.copy(alpha = 0.08f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Tap to Auto-fill Demo Customer Credentials",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = NestifyPrimary
            )
            Text(
              text = "ID: customer@nestify.demo  |  Pass: Nestify@123",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = NestifyPrimary
          ) {
            Text(
              text = "Fill",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Login ID Field
      OutlinedTextField(
        value = loginId,
        onValueChange = {
          loginId = it
          viewModel.clearAuthError()
        },
        label = { Text("Customer Login ID") },
        placeholder = { Text("customer@nestify.demo") },
        leadingIcon = {
          Icon(Icons.Default.Person, contentDescription = null, tint = NestifyPrimary)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("customer_login_id_input"),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Password Field
      OutlinedTextField(
        value = password,
        onValueChange = {
          password = it
          viewModel.clearAuthError()
        },
        label = { Text("Password") },
        leadingIcon = {
          Icon(Icons.Default.Lock, contentDescription = null, tint = NestifyPrimary)
        },
        trailingIcon = {
          IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
              imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
              contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
            )
          }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("customer_password_input"),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Remember Me & Forgot Password Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(
            checked = rememberMe,
            onCheckedChange = { rememberMe = it },
            colors = CheckboxDefaults.colors(checkedColor = NestifyPrimary)
          )
          Text(
            text = "Remember me",
            style = MaterialTheme.typography.bodySmall
          )
        }

        TextButton(onClick = { showForgotPassword = true }) {
          Text(
            text = "Forgot password?",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = NestifyPrimary
          )
        }
      }

      // Error Message Banner
      if (authError != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = NestifyError.copy(alpha = 0.1f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.WarningAmber,
              contentDescription = null,
              tint = NestifyError,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = authError!!,
              style = MaterialTheme.typography.bodySmall,
              color = NestifyError,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Login Button
      Button(
        onClick = {
          viewModel.loginCustomer(loginId.trim(), password)
        },
        enabled = !isAuthenticating,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("login_as_customer_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
      ) {
        if (isAuthenticating) {
          CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
          Spacer(modifier = Modifier.width(10.dp))
          Text("Authenticating...")
        } else {
          Text(
            text = "Login as Customer",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Create Customer Account
      OutlinedButton(
        onClick = {
          viewModel.navigateAuth(AuthScreen.CustomerRegister)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("create_customer_account_button"),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text(
          text = "Create Customer Account",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  if (showForgotPassword) {
    ForgotPasswordDialog(
      role = "Customer",
      initialLoginId = loginId,
      onDismiss = { showForgotPassword = false },
      onResetPassword = { id, newPass, role, onSuccess ->
        viewModel.resetPassword(id, newPass, role, onSuccess)
      }
    )
  }
}
