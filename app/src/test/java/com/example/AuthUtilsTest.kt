package com.example

import com.example.util.AuthUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUtilsTest {

  @Test
  fun `password hashing produces consistent sha256 output and verifies correctly`() {
    val password = "Nestify@123"
    val hash = AuthUtils.hashPassword(password)
    assertNotEquals(password, hash)
    assertTrue(AuthUtils.verifyPassword("Nestify@123", hash))
    assertFalse(AuthUtils.verifyPassword("WrongPassword", hash))
  }

  @Test
  fun `organizer password verifies correctly`() {
    val password = "Nestify@456"
    val hash = AuthUtils.hashPassword(password)
    assertTrue(AuthUtils.verifyPassword("Nestify@456", hash))
    assertFalse(AuthUtils.verifyPassword("Nestify@123", hash))
  }

  @Test
  fun `access tokens format correctly by role`() {
    val customerToken = AuthUtils.generateAccessToken(1L, "Customer")
    val organizerToken = AuthUtils.generateAccessToken(2L, "Organizer")
    assertTrue(customerToken.startsWith("nst_atk_customer_1_"))
    assertTrue(organizerToken.startsWith("nst_atk_organizer_2_"))
  }
}
