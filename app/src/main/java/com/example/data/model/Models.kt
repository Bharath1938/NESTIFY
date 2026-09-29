package com.example.data.model

enum class UserRole {
  CUSTOMER,
  ORGANIZER
}

enum class BookingStatus(val label: String, val stepIndex: Int) {
  PENDING_ACCEPTANCE("Pending Acceptance", 0),
  CONFIRMED("Booking Confirmed", 1),
  ASSIGNED("Organizer Assigned", 2),
  ON_THE_WAY("On The Way", 3),
  ARRIVED("Organizer Arrived", 4),
  IN_PROGRESS("Service In Progress", 5),
  COMPLETED("Service Completed", 6),
  CANCELLED("Cancelled", -1)
}

enum class ServiceCategory(val displayName: String, val iconName: String) {
  BEDROOM("Bedroom", "Bed"),
  KITCHEN("Kitchen", "Kitchen"),
  STORAGE("Storage", "Inventory2"),
  KIDS("Kids Room", "ChildCare"),
  LIVING("Living Space", "Weekend"),
  SPECIAL("Special Moves", "HomeWork")
}

data class ServiceItem(
  val id: String,
  val name: String,
  val category: ServiceCategory,
  val tagline: String,
  val description: String,
  val startingPrice: Double,
  val estimatedHours: Double,
  val rating: Float,
  val reviewsCount: Int,
  val included: List<String>,
  val excluded: List<String>,
  val benefits: List<String>,
  val beforeSummary: String,
  val afterSummary: String,
  val popular: Boolean = false,
  val colorSeed: Long = 0xFF5B5BD6
)

data class CustomerReview(
  val id: String,
  val reviewerName: String,
  val rating: Float,
  val date: String,
  val comment: String,
  val tags: List<String>,
  val serviceName: String
)

data class OrganizerProfile(
  val id: String,
  val name: String,
  val rating: Float,
  val completedJobs: Int,
  val experienceYears: Int,
  val phone: String,
  val bio: String,
  val badges: List<String>,
  val skills: List<String>,
  val serviceAreas: List<String>,
  val hourlyRate: Double = 45.0,
  val isOnline: Boolean = true
)

data class EarningRecord(
  val id: String,
  val bookingId: Long,
  val serviceName: String,
  val date: String,
  val grossAmount: Double,
  val platformFee: Double,
  val netAmount: Double,
  val status: String = "Processed"
)
