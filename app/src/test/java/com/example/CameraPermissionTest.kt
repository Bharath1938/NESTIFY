package com.example

import com.example.util.CameraPermissionStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraPermissionTest {

  @Test
  fun `verify camera permission status classes`() {
    val granted: CameraPermissionStatus = CameraPermissionStatus.Granted
    val denied: CameraPermissionStatus = CameraPermissionStatus.Denied
    val permanentlyDenied: CameraPermissionStatus = CameraPermissionStatus.PermanentlyDenied
    val notRequested: CameraPermissionStatus = CameraPermissionStatus.NotRequested

    assertTrue(granted is CameraPermissionStatus.Granted)
    assertTrue(denied is CameraPermissionStatus.Denied)
    assertTrue(permanentlyDenied is CameraPermissionStatus.PermanentlyDenied)
    assertTrue(notRequested is CameraPermissionStatus.NotRequested)
    assertFalse(denied is CameraPermissionStatus.Granted)
  }
}
