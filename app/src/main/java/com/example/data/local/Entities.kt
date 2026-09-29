package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val serviceId: String,
  val serviceName: String,
  val category: String,
  val customerName: String = "Sarah Jenkins",
  val customerPhone: String = "+1 (555) 789-0123",
  val address: String,
  val landmark: String = "",
  val bookingDate: String,
  val timeSlot: String,
  val status: String = "CONFIRMED", // CONFIRMED, ASSIGNED, ON_THE_WAY, ARRIVED, IN_PROGRESS, COMPLETED, CANCELLED
  val customerNotes: String = "",
  val wardrobeCount: Int = 2,
  val itemVolume: String = "Standard",
  val organizationType: String = "Color-Coded & Category",
  val cabinetCount: Int = 8,
  val roomCount: Int = 1,
  val photoCount: Int = 3,
  val basePrice: Double,
  val addonPrice: Double = 0.0,
  val serviceFee: Double = 15.0,
  val discount: Double = 20.0,
  val totalPrice: Double,
  val paymentMethod: String = "Card ending in 4242",
  val paymentStatus: String = "PAID",
  val organizerName: String = "Elena Martinez",
  val organizerRating: Float = 4.95f,
  val organizerPhone: String = "+1 (555) 234-8901",
  val organizerDistance: String = "2.4 miles",
  val beforePhotosCount: Int = 3,
  val afterPhotosCount: Int = 3,
  val rating: Int = 0,
  val reviewText: String = "",
  val reviewTags: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val isCustomerConfirmed: Boolean = false
)

@Entity(tableName = "addresses")
data class AddressEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val label: String, // Home, Work, Vacation Home
  val addressLine: String,
  val apartment: String = "",
  val city: String = "Austin, TX",
  val landmark: String = "",
  val isDefault: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val message: String,
  val timestamp: Long = System.currentTimeMillis(),
  val type: String = "INFO", // BOOKING, ORGANIZER, PAYMENT, SYSTEM
  val isRead: Boolean = false,
  val bookingId: Long? = null
)

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey(autoGenerate = true) val userId: Long = 0,
  val loginId: String,
  val passwordHash: String,
  val role: String, // "Customer", "Organizer", "Admin"
  val name: String,
  val phone: String = "",
  val email: String = "",
  val isActive: Boolean = true,
  val status: String = "Active", // "Active", "PendingApproval"
  val experience: String = "",
  val skills: String = "",
  val serviceCategories: String = "",
  val serviceAreas: String = "",
  val accessToken: String? = null,
  val refreshToken: String? = null,
  val createdDate: Long = System.currentTimeMillis(),
  val updatedDate: Long = System.currentTimeMillis()
)
