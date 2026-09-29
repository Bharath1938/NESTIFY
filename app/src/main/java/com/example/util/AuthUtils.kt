package com.example.util

import java.security.MessageDigest
import java.util.UUID

object AuthUtils {

  private const val SALT = "NestifySecureSalt_2026_Key"

  fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest((SALT + password).toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  fun verifyPassword(password: String, storedHash: String): Boolean {
    val computed = hashPassword(password)
    return computed.equals(storedHash, ignoreCase = true)
  }

  fun generateAccessToken(userId: Long, role: String): String {
    return "nst_atk_${role.lowercase()}_${userId}_${UUID.randomUUID()}"
  }

  fun generateRefreshToken(userId: Long): String {
    return "nst_rtk_${userId}_${UUID.randomUUID()}"
  }
}
