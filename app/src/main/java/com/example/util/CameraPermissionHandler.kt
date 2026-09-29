package com.example.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary

/**
 * Represents the distinct states of the Camera permission lifecycle.
 */
sealed interface CameraPermissionStatus {
  object Granted : CameraPermissionStatus
  object NotRequested : CameraPermissionStatus
  object Denied : CameraPermissionStatus
  object PermanentlyDenied : CameraPermissionStatus
}

/**
 * Controller to trigger camera permission requests and observe status.
 */
@Stable
class CameraPermissionState internal constructor(
  val context: Context,
  status: CameraPermissionStatus,
  private val requestPermissionAction: () -> Unit
) {
  var status: CameraPermissionStatus by mutableStateOf(status)
    internal set

  val isGranted: Boolean
    get() = status is CameraPermissionStatus.Granted

  var showRationaleDialog: Boolean by mutableStateOf(false)
    internal set

  fun requestPermission() {
    if (isGranted) return
    requestPermissionAction()
  }

  fun dismissRationale() {
    showRationaleDialog = false
  }

  fun openAppSettings() {
    val intent = Intent(
      Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
      Uri.fromParts("package", context.packageName, null)
    ).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  }
}

/**
 * Remember and create a [CameraPermissionState] that manages the runtime request flow,
 * rationale display, and app settings navigation.
 */
@Composable
fun rememberCameraPermissionHandler(
  onPermissionGranted: (() -> Unit)? = null
): CameraPermissionState {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  fun isCameraGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
  }

  var initialStatus by remember {
    mutableStateOf(
      if (isCameraGranted()) CameraPermissionStatus.Granted
      else CameraPermissionStatus.NotRequested
    )
  }

  var requestTrigger by remember { mutableStateOf<(() -> Unit)?>(null) }

  val permissionState = remember(context) {
    CameraPermissionState(
      context = context,
      status = initialStatus,
      requestPermissionAction = { requestTrigger?.invoke() }
    )
  }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      permissionState.status = CameraPermissionStatus.Granted
      permissionState.showRationaleDialog = false
      onPermissionGranted?.invoke()
    } else {
      val activity = context.findActivity()
      val showRationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
      } ?: false

      if (showRationale) {
        permissionState.status = CameraPermissionStatus.Denied
        permissionState.showRationaleDialog = true
      } else {
        // Permanently denied (user checked "Don't ask again" or OS policy)
        permissionState.status = CameraPermissionStatus.PermanentlyDenied
        permissionState.showRationaleDialog = true
      }
    }
  }

  requestTrigger = {
    if (isCameraGranted()) {
      permissionState.status = CameraPermissionStatus.Granted
      onPermissionGranted?.invoke()
    } else {
      launcher.launch(Manifest.permission.CAMERA)
    }
  }

  // Synchronize status when app returns to foreground (e.g., from App Settings)
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        if (isCameraGranted()) {
          permissionState.status = CameraPermissionStatus.Granted
          permissionState.showRationaleDialog = false
        }
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  return permissionState
}

/**
 * Standard Dialog explaining camera requirement with actions for Denied and Permanently Denied.
 */
@Composable
fun CameraPermissionRationaleDialog(
  state: CameraPermissionState,
  title: String = "Camera Access Needed",
  description: String = "Nestify requires camera access to snap Before and After transformation photos of your home spaces.",
  onDismiss: (() -> Unit)? = null
) {
  if (state.showRationaleDialog && !state.isGranted) {
    val isPermanentlyDenied = state.status is CameraPermissionStatus.PermanentlyDenied

    AlertDialog(
      onDismissRequest = {
        state.dismissRationale()
        onDismiss?.invoke()
      },
      icon = {
        Box(
          modifier = Modifier
            .size(48.dp)
            .background(NestifyPrimary.copy(alpha = 0.12f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isPermanentlyDenied) Icons.Default.Settings else Icons.Default.CameraAlt,
            contentDescription = null,
            tint = NestifyPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
      },
      title = {
        Text(
          text = if (isPermanentlyDenied) "Permission Required in Settings" else title,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column {
          Text(
            text = if (isPermanentlyDenied) {
              "Camera permission was previously disabled. Please enable Camera permissions in App Settings to take photos for your organization session."
            } else {
              description
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (isPermanentlyDenied) {
              state.openAppSettings()
              state.dismissRationale()
            } else {
              state.dismissRationale()
              state.requestPermission()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("camera_permission_confirm_button")
        ) {
          Text(if (isPermanentlyDenied) "Open Settings" else "Grant Permission")
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            state.dismissRationale()
            onDismiss?.invoke()
          }
        ) {
          Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    )
  }
}

/**
 * Inline Card representation of camera permission for embedded layouts.
 */
@Composable
fun CameraPermissionInlineBanner(
  state: CameraPermissionState,
  modifier: Modifier = Modifier
) {
  if (!state.isGranted) {
    val isPermanentlyDenied = state.status is CameraPermissionStatus.PermanentlyDenied

    Card(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(NestifyPrimary.copy(alpha = 0.15f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isPermanentlyDenied) Icons.Default.Settings else Icons.Default.CameraAlt,
            contentDescription = null,
            tint = NestifyPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Enable Camera for Photos",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (isPermanentlyDenied) "Enable camera in app settings" else "Capture before & after space photos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = {
            if (isPermanentlyDenied) {
              state.openAppSettings()
            } else {
              state.requestPermission()
            }
          },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
        ) {
          Text(
            text = if (isPermanentlyDenied) "Settings" else "Enable",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

/**
 * Resolves Activity from current Compose Context, traversing ContextWrappers if needed.
 */
fun Context.findActivity(): Activity? {
  var currentContext = this
  while (currentContext is ContextWrapper) {
    if (currentContext is Activity) {
      return currentContext
    }
    currentContext = currentContext.baseContext
  }
  return null
}
