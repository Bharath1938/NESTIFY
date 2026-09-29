package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NestifyError
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySuccess

@Composable
fun ForgotPasswordDialog(
  role: String, // "Customer" or "Organizer"
  initialLoginId: String = "",
  onDismiss: () -> Unit,
  onResetPassword: (loginId: String, newPass: String, role: String, onSuccess: () -> Unit) -> Unit
) {
  var loginId by remember { mutableStateOf(initialLoginId) }
  var newPassword by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isSuccess by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Box(
        modifier = Modifier
          .size(48.dp)
          .background(NestifyPrimary.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.LockReset,
          contentDescription = null,
          tint = if (isSuccess) NestifySuccess else NestifyPrimary,
          modifier = Modifier.size(24.dp)
        )
      }
    },
    title = {
      Text(
        text = if (isSuccess) "Password Reset Successful" else "Reset $role Password",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      if (isSuccess) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Your password has been securely updated. You can now log in with your new credentials.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Enter your $role Login ID and select a new secure password.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = loginId,
            onValueChange = {
              loginId = it
              errorMessage = null
            },
            label = { Text("$role Login ID") },
            placeholder = { Text(if (role == "Customer") "customer@nestify.demo" else "organizer@nestify.demo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = newPassword,
            onValueChange = {
              newPassword = it
              errorMessage = null
            },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
              confirmPassword = it
              errorMessage = null
            },
            label = { Text("Confirm New Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = errorMessage!!,
              color = NestifyError,
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    },
    confirmButton = {
      if (isSuccess) {
        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
        ) {
          Text("Done")
        }
      } else {
        Button(
          onClick = {
            if (loginId.isBlank()) {
              errorMessage = "Please enter your Login ID."
              return@Button
            }
            if (newPassword.length < 6) {
              errorMessage = "Password must be at least 6 characters."
              return@Button
            }
            if (newPassword != confirmPassword) {
              errorMessage = "Passwords do not match."
              return@Button
            }

            isLoading = true
            errorMessage = null
            onResetPassword(loginId, newPassword, role) {
              isLoading = false
              isSuccess = true
            }
          },
          enabled = !isLoading,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
        ) {
          if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
          } else {
            Text("Update Password")
          }
        }
      }
    },
    dismissButton = {
      if (!isSuccess) {
        TextButton(onClick = onDismiss) {
          Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
  )
}
