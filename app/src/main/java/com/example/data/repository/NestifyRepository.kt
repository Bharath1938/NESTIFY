package com.example.data.repository

import com.example.data.local.AddressEntity
import com.example.data.local.BookingEntity
import com.example.data.local.NestifyDao
import com.example.data.local.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NestifyRepository(private val dao: NestifyDao) {

  val allBookings: Flow<List<BookingEntity>> = dao.getAllBookings()
  val activeBookings: Flow<List<BookingEntity>> = dao.getActiveBookings()
  val completedBookings: Flow<List<BookingEntity>> = dao.getCompletedBookings()
  val addresses: Flow<List<AddressEntity>> = dao.getAllAddresses()
  val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()

  fun getBookingById(id: Long): Flow<BookingEntity?> = dao.getBookingById(id)

  suspend fun getBookingDirect(id: Long): BookingEntity? = dao.getBookingByIdDirect(id)

  suspend fun createBooking(booking: BookingEntity): Long {
    val id = dao.insertBooking(booking)
    dao.insertNotification(
      NotificationEntity(
        title = "Booking Confirmed",
        message = "Your booking for ${booking.serviceName} is confirmed for ${booking.bookingDate} at ${booking.timeSlot}.",
        type = "BOOKING",
        bookingId = id
      )
    )
    return id
  }

  suspend fun updateBookingStatus(id: Long, newStatus: String, notes: String? = null) {
    dao.updateBookingStatus(id, newStatus)
    val title = when (newStatus) {
      "ASSIGNED" -> "Organizer Assigned"
      "ON_THE_WAY" -> "Organizer On The Way"
      "ARRIVED" -> "Organizer Arrived"
      "IN_PROGRESS" -> "Organization Started"
      "COMPLETED" -> "Service Completed!"
      "CANCELLED" -> "Booking Cancelled"
      else -> "Status Updated"
    }
    val message = when (newStatus) {
      "ASSIGNED" -> "Elena Martinez has accepted your request."
      "ON_THE_WAY" -> "Elena is en route to your address."
      "ARRIVED" -> "Elena has arrived and is reviewing your spaces."
      "IN_PROGRESS" -> "Elena has taken before photos and began the organization service."
      "COMPLETED" -> "Elena has completed the job! View the before & after transformation to confirm."
      "CANCELLED" -> "Your booking has been cancelled."
      else -> "Status changed to $newStatus"
    }
    dao.insertNotification(
      NotificationEntity(
        title = title,
        message = message,
        type = "ORGANIZER",
        bookingId = id
      )
    )
  }

  suspend fun updateBeforePhotos(id: Long, count: Int) {
    dao.updateBeforePhotosCount(id, count)
  }

  suspend fun updateAfterPhotos(id: Long, count: Int) {
    dao.updateAfterPhotosCount(id, count)
  }

  suspend fun submitCustomerReview(id: Long, rating: Int, reviewText: String, tags: String) {
    dao.submitReview(id, rating, reviewText, tags)
    dao.confirmCompletion(id)
    dao.insertNotification(
      NotificationEntity(
        title = "Thank You for Your Review!",
        message = "Your rating has been shared with your organizer. We hope you love your newly transformed space!",
        type = "SYSTEM",
        bookingId = id
      )
    )
  }

  suspend fun confirmCompletion(id: Long) {
    dao.confirmCompletion(id)
  }

  suspend fun addAddress(label: String, line: String, apt: String, landmark: String, isDefault: Boolean) {
    if (isDefault) {
      dao.clearDefaultAddresses()
    }
    dao.insertAddress(
      AddressEntity(
        label = label,
        addressLine = line,
        apartment = apt,
        landmark = landmark,
        isDefault = isDefault
      )
    )
  }

  suspend fun setDefaultAddress(id: Long) {
    dao.clearDefaultAddresses()
    dao.setDefaultAddress(id)
  }

  suspend fun deleteAddress(address: AddressEntity) {
    dao.deleteAddress(address)
  }

  suspend fun markNotificationAsRead(id: Long) {
    dao.markNotificationRead(id)
  }

  suspend fun markAllNotificationsAsRead() {
    dao.markAllNotificationsRead()
  }

  // Authentication Operations
  suspend fun loginCustomer(loginId: String, password: String): Result<com.example.data.local.UserEntity> {
    var user = dao.getUserByLoginId(loginId.trim())

    // If demo customer is not present in local database yet, provision it on-demand
    if (user == null && loginId.trim().equals("customer@nestify.demo", ignoreCase = true)) {
      val now = System.currentTimeMillis()
      val demoCustomer = com.example.data.local.UserEntity(
        loginId = "customer@nestify.demo",
        passwordHash = com.example.util.AuthUtils.hashPassword("Nestify@123"),
        role = "Customer",
        name = "Demo Customer",
        phone = "+1 (555) 789-0123",
        email = "customer@nestify.demo",
        isActive = true,
        status = "Active",
        createdDate = now,
        updatedDate = now
      )
      val id = dao.insertUser(demoCustomer)
      user = demoCustomer.copy(userId = id)
    }

    if (user == null) {
      return Result.failure(Exception("Invalid Customer Login ID or Password."))
    }

    if (user.role != "Customer") {
      return Result.failure(Exception("Invalid Customer Login ID or Password."))
    }

    if (!user.isActive) {
      return Result.failure(Exception("Account inactive. Please contact Nestify support."))
    }

    if (!com.example.util.AuthUtils.verifyPassword(password.trim(), user.passwordHash)) {
      return Result.failure(Exception("Invalid Customer Login ID or Password."))
    }

    val accessToken = com.example.util.AuthUtils.generateAccessToken(user.userId, user.role)
    val refreshToken = com.example.util.AuthUtils.generateRefreshToken(user.userId)
    val now = System.currentTimeMillis()
    dao.updateTokens(user.userId, accessToken, refreshToken, now)

    return Result.success(user.copy(accessToken = accessToken, refreshToken = refreshToken, updatedDate = now))
  }

  suspend fun loginOrganizer(loginId: String, password: String): Result<com.example.data.local.UserEntity> {
    var user = dao.getUserByLoginId(loginId.trim())

    // If demo organizer is not present in local database yet, provision it on-demand
    if (user == null && loginId.trim().equals("organizer@nestify.demo", ignoreCase = true)) {
      val now = System.currentTimeMillis()
      val demoOrganizer = com.example.data.local.UserEntity(
        loginId = "organizer@nestify.demo",
        passwordHash = com.example.util.AuthUtils.hashPassword("Nestify@456"),
        role = "Organizer",
        name = "Demo Organizer",
        phone = "+1 (555) 234-8901",
        email = "organizer@nestify.demo",
        isActive = true,
        status = "Active",
        experience = "4 Years Experience",
        skills = "Wardrobe Optimization, Kitchen Workflows, Montessori Playrooms, Decanting Systems",
        serviceCategories = "Bedroom, Kitchen, Storage, Kids, Living",
        serviceAreas = "Downtown, Westside, Greenwood, Lakeview, Suburbs",
        createdDate = now,
        updatedDate = now
      )
      val id = dao.insertUser(demoOrganizer)
      user = demoOrganizer.copy(userId = id)
    }

    if (user == null) {
      return Result.failure(Exception("Invalid Organizer Login ID or Password."))
    }

    if (user.role != "Organizer") {
      return Result.failure(Exception("Invalid Organizer Login ID or Password."))
    }

    if (!user.isActive) {
      return Result.failure(Exception("Account inactive. Please contact Nestify support."))
    }

    if (user.status == "PendingApproval") {
      return Result.failure(Exception("Your organizer account is pending approval by Admin. You will receive an SMS upon activation."))
    }

    if (!com.example.util.AuthUtils.verifyPassword(password.trim(), user.passwordHash)) {
      return Result.failure(Exception("Invalid Organizer Login ID or Password."))
    }

    val accessToken = com.example.util.AuthUtils.generateAccessToken(user.userId, user.role)
    val refreshToken = com.example.util.AuthUtils.generateRefreshToken(user.userId)
    val now = System.currentTimeMillis()
    dao.updateTokens(user.userId, accessToken, refreshToken, now)

    return Result.success(user.copy(accessToken = accessToken, refreshToken = refreshToken, updatedDate = now))
  }

  suspend fun registerCustomer(
    name: String,
    phone: String,
    email: String,
    loginId: String,
    password: String
  ): Result<com.example.data.local.UserEntity> {
    val existing = dao.getUserByLoginId(loginId.trim())
    if (existing != null) {
      return Result.failure(Exception("An account with this Login ID already exists."))
    }

    val hash = com.example.util.AuthUtils.hashPassword(password)
    val now = System.currentTimeMillis()
    val newUser = com.example.data.local.UserEntity(
      loginId = loginId.trim(),
      passwordHash = hash,
      role = "Customer",
      name = name.trim(),
      phone = phone.trim(),
      email = email.trim(),
      isActive = true,
      status = "Active",
      createdDate = now,
      updatedDate = now
    )

    val id = dao.insertUser(newUser)
    val accessToken = com.example.util.AuthUtils.generateAccessToken(id, "Customer")
    val refreshToken = com.example.util.AuthUtils.generateRefreshToken(id)
    dao.updateTokens(id, accessToken, refreshToken, now)

    return Result.success(newUser.copy(userId = id, accessToken = accessToken, refreshToken = refreshToken))
  }

  suspend fun registerOrganizer(
    name: String,
    phone: String,
    email: String,
    loginId: String,
    password: String,
    experience: String,
    skills: String,
    categories: String,
    areas: String
  ): Result<com.example.data.local.UserEntity> {
    val existing = dao.getUserByLoginId(loginId.trim())
    if (existing != null) {
      return Result.failure(Exception("An organizer account with this Login ID already exists."))
    }

    val hash = com.example.util.AuthUtils.hashPassword(password)
    val now = System.currentTimeMillis()
    val newUser = com.example.data.local.UserEntity(
      loginId = loginId.trim(),
      passwordHash = hash,
      role = "Organizer",
      name = name.trim(),
      phone = phone.trim(),
      email = email.trim(),
      isActive = true,
      status = "PendingApproval",
      experience = experience.trim(),
      skills = skills.trim(),
      serviceCategories = categories.trim(),
      serviceAreas = areas.trim(),
      createdDate = now,
      updatedDate = now
    )

    val id = dao.insertUser(newUser)
    return Result.success(newUser.copy(userId = id))
  }

  suspend fun resetPassword(loginId: String, newPassword: String, expectedRole: String): Result<Unit> {
    val user = dao.getUserByLoginId(loginId.trim())
      ?: return Result.failure(Exception("No account found matching this Login ID."))

    if (user.role != expectedRole) {
      return Result.failure(Exception("No $expectedRole account found with this Login ID."))
    }

    val newHash = com.example.util.AuthUtils.hashPassword(newPassword)
    dao.updateUserPassword(user.userId, newHash, System.currentTimeMillis())
    return Result.success(Unit)
  }
}
