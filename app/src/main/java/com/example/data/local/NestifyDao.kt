package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NestifyDao {

  // Bookings
  @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
  fun getAllBookings(): Flow<List<BookingEntity>>

  @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
  fun getBookingById(id: Long): Flow<BookingEntity?>

  @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
  suspend fun getBookingByIdDirect(id: Long): BookingEntity?

  @Query("SELECT * FROM bookings WHERE status IN ('CONFIRMED', 'ASSIGNED', 'ON_THE_WAY', 'ARRIVED', 'IN_PROGRESS') ORDER BY createdAt DESC")
  fun getActiveBookings(): Flow<List<BookingEntity>>

  @Query("SELECT * FROM bookings WHERE status = 'COMPLETED' ORDER BY createdAt DESC")
  fun getCompletedBookings(): Flow<List<BookingEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBooking(booking: BookingEntity): Long

  @Update
  suspend fun updateBooking(booking: BookingEntity)

  @Query("UPDATE bookings SET status = :status WHERE id = :id")
  suspend fun updateBookingStatus(id: Long, status: String)

  @Query("UPDATE bookings SET rating = :rating, reviewText = :reviewText, reviewTags = :reviewTags WHERE id = :id")
  suspend fun submitReview(id: Long, rating: Int, reviewText: String, reviewTags: String)

  @Query("UPDATE bookings SET isCustomerConfirmed = 1 WHERE id = :id")
  suspend fun confirmCompletion(id: Long)

  @Query("UPDATE bookings SET beforePhotosCount = :count WHERE id = :id")
  suspend fun updateBeforePhotosCount(id: Long, count: Int)

  @Query("UPDATE bookings SET afterPhotosCount = :count WHERE id = :id")
  suspend fun updateAfterPhotosCount(id: Long, count: Int)

  // Addresses
  @Query("SELECT * FROM addresses ORDER BY isDefault DESC, id ASC")
  fun getAllAddresses(): Flow<List<AddressEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAddress(address: AddressEntity): Long

  @Delete
  suspend fun deleteAddress(address: AddressEntity)

  @Query("UPDATE addresses SET isDefault = 0")
  suspend fun clearDefaultAddresses()

  @Query("UPDATE addresses SET isDefault = 1 WHERE id = :id")
  suspend fun setDefaultAddress(id: Long)

  // Notifications
  @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
  fun getAllNotifications(): Flow<List<NotificationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity): Long

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markNotificationRead(id: Long)

  @Query("UPDATE notifications SET isRead = 1")
  suspend fun markAllNotificationsRead()

  @Query("DELETE FROM notifications")
  suspend fun clearNotifications()

  // Users & Auth
  @Query("SELECT * FROM users WHERE LOWER(loginId) = LOWER(:loginId) LIMIT 1")
  suspend fun getUserByLoginId(loginId: String): UserEntity?

  @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
  suspend fun getUserById(id: Long): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity): Long

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("UPDATE users SET passwordHash = :passwordHash, updatedDate = :updatedDate WHERE userId = :userId")
  suspend fun updateUserPassword(userId: Long, passwordHash: String, updatedDate: Long)

  @Query("UPDATE users SET accessToken = :accessToken, refreshToken = :refreshToken, updatedDate = :updatedDate WHERE userId = :userId")
  suspend fun updateTokens(userId: Long, accessToken: String, refreshToken: String, updatedDate: Long)
}
