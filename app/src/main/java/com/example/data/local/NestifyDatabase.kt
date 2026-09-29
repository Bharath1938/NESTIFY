package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.example.util.AuthUtils
import kotlinx.coroutines.launch

@Database(
  entities = [BookingEntity::class, AddressEntity::class, NotificationEntity::class, UserEntity::class],
  version = 2,
  exportSchema = false
)
abstract class NestifyDatabase : RoomDatabase() {

  abstract fun nestifyDao(): NestifyDao

  companion object {
    @Volatile
    private var INSTANCE: NestifyDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): NestifyDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          NestifyDatabase::class.java,
          "nestify_database"
        )
          .addCallback(NestifyDatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        scope.launch(Dispatchers.IO) {
          ensureDemoUsers(instance.nestifyDao())
        }
        instance
      }
    }

    suspend fun ensureDemoUsers(dao: NestifyDao) {
      // Ensure Demo Customer Account exists
      if (dao.getUserByLoginId("customer@nestify.demo") == null) {
        dao.insertUser(
          UserEntity(
            loginId = "customer@nestify.demo",
            passwordHash = AuthUtils.hashPassword("Nestify@123"),
            role = "Customer",
            name = "Demo Customer",
            phone = "+1 (555) 789-0123",
            email = "customer@nestify.demo",
            isActive = true,
            status = "Active"
          )
        )
      }

      // Ensure Demo Organizer Account exists
      if (dao.getUserByLoginId("organizer@nestify.demo") == null) {
        dao.insertUser(
          UserEntity(
            loginId = "organizer@nestify.demo",
            passwordHash = AuthUtils.hashPassword("Nestify@456"),
            role = "Organizer",
            name = "Demo Organizer",
            phone = "+1 (555) 234-8901",
            email = "organizer@nestify.demo",
            isActive = true,
            status = "Active",
            experience = "4 Years Experience",
            skills = "Wardrobe Optimization, Kitchen Workflows, Montessori Playrooms, Decanting Systems",
            serviceCategories = "Bedroom, Kitchen, Storage, Kids, Living",
            serviceAreas = "Downtown, Westside, Greenwood, Lakeview, Suburbs"
          )
        )
      }
    }

    private class NestifyDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.nestifyDao())
          }
        }
      }

      override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            ensureDemoUsers(database.nestifyDao())
          }
        }
      }

      suspend fun populateInitialData(dao: NestifyDao) {
        ensureDemoUsers(dao)
        // Prepopulate default addresses
        dao.insertAddress(
          AddressEntity(
            label = "Home",
            addressLine = "742 Evergreen Terrace",
            apartment = "Apt 4B",
            city = "Austin, TX 78701",
            landmark = "Near Riverside Park",
            isDefault = true
          )
        )
        dao.insertAddress(
          AddressEntity(
            label = "Office / Studio",
            addressLine = "350 South Congress Ave",
            apartment = "Suite 210",
            city = "Austin, TX 78704",
            landmark = "Above Magnolia Cafe",
            isDefault = false
          )
        )

        // Prepopulate an Active Booking (so user can experience live tracking immediately)
        dao.insertBooking(
          BookingEntity(
            serviceId = "wardrobe_org",
            serviceName = "Wardrobe Organization",
            category = "Bedroom",
            customerName = "Sarah Jenkins",
            customerPhone = "+1 (555) 789-0123",
            address = "742 Evergreen Terrace, Apt 4B, Austin, TX",
            landmark = "Gate code #4421",
            bookingDate = "Today",
            timeSlot = "10:00 AM - 1:30 PM",
            status = "IN_PROGRESS",
            customerNotes = "Please separate winter wool coats from daily workwear. Hangers are provided in boxes.",
            wardrobeCount = 2,
            itemVolume = "Standard",
            organizationType = "Color-Coded & Category",
            basePrice = 149.0,
            addonPrice = 30.0,
            serviceFee = 15.0,
            discount = 20.0,
            totalPrice = 174.0,
            paymentMethod = "Apple Pay / Visa •••• 4242",
            paymentStatus = "PAID",
            organizerName = "Elena Martinez",
            organizerRating = 4.95f,
            organizerPhone = "+1 (555) 234-8901",
            organizerDistance = "On Site (Working)",
            beforePhotosCount = 3,
            afterPhotosCount = 0,
            createdAt = System.currentTimeMillis() - 7200000L // 2 hours ago
          )
        )

        // Prepopulate a Completed Booking (so user can test Before/After slider & Reviews)
        dao.insertBooking(
          BookingEntity(
            serviceId = "kitchen_cabinets",
            serviceName = "Kitchen Cabinets & Zones",
            category = "Kitchen",
            customerName = "Sarah Jenkins",
            customerPhone = "+1 (555) 789-0123",
            address = "742 Evergreen Terrace, Apt 4B, Austin, TX",
            landmark = "Gate code #4421",
            bookingDate = "Sep 22, 2026",
            timeSlot = "02:00 PM - 06:00 PM",
            status = "COMPLETED",
            customerNotes = "Focus on the spices and cooking oils near the stove.",
            cabinetCount = 10,
            basePrice = 179.0,
            addonPrice = 25.0,
            serviceFee = 15.0,
            discount = 0.0,
            totalPrice = 219.0,
            paymentMethod = "Visa •••• 4242",
            paymentStatus = "PAID",
            organizerName = "Elena Martinez",
            organizerRating = 4.95f,
            organizerPhone = "+1 (555) 234-8901",
            organizerDistance = "Completed",
            beforePhotosCount = 4,
            afterPhotosCount = 4,
            rating = 5,
            reviewText = "Unbelievable difference! Elena labeled all jars and arranged our pantry so intuitively. Cooking is so peaceful now!",
            reviewTags = "Pantry Perfection, Super Clean, 10/10",
            createdAt = System.currentTimeMillis() - 259200000L, // 3 days ago
            isCustomerConfirmed = true
          )
        )

        // Prepopulate sample notifications
        val now = System.currentTimeMillis()
        dao.insertNotification(
          NotificationEntity(
            title = "Service in Progress",
            message = "Elena Martinez has started organizing your wardrobe. Estimated completion in 1 hour.",
            timestamp = now - 1800000L,
            type = "ORGANIZER",
            isRead = false,
            bookingId = 1L
          )
        )
        dao.insertNotification(
          NotificationEntity(
            title = "Organizer Arrived",
            message = "Elena Martinez has arrived at 742 Evergreen Terrace and captured initial before photos.",
            timestamp = now - 3600000L,
            type = "ORGANIZER",
            isRead = true,
            bookingId = 1L
          )
        )
        dao.insertNotification(
          NotificationEntity(
            title = "Booking Confirmed",
            message = "Your appointment for Wardrobe Organization is confirmed for today at 10:00 AM.",
            timestamp = now - 86400000L,
            type = "BOOKING",
            isRead = true,
            bookingId = 1L
          )
        )
        dao.insertNotification(
          NotificationEntity(
            title = "Welcome to Nestify!",
            message = "We organize your home so you don't have to. Enjoy $20 off your first booking with code NEST20.",
            timestamp = now - 172800000L,
            type = "SYSTEM",
            isRead = true
          )
        )

        // Prepopulate Demo Customer Account
        dao.insertUser(
          UserEntity(
            loginId = "customer@nestify.demo",
            passwordHash = AuthUtils.hashPassword("Nestify@123"),
            role = "Customer",
            name = "Demo Customer",
            phone = "+1 (555) 789-0123",
            email = "customer@nestify.demo",
            isActive = true,
            status = "Active"
          )
        )

        // Prepopulate Demo Organizer Account
        dao.insertUser(
          UserEntity(
            loginId = "organizer@nestify.demo",
            passwordHash = AuthUtils.hashPassword("Nestify@456"),
            role = "Organizer",
            name = "Demo Organizer",
            phone = "+1 (555) 234-8901",
            email = "organizer@nestify.demo",
            isActive = true,
            status = "Active",
            experience = "4 Years Experience",
            skills = "Wardrobe Optimization, Kitchen Workflows, Montessori Playrooms, Decanting Systems",
            serviceCategories = "Bedroom, Kitchen, Storage, Kids, Living",
            serviceAreas = "Downtown, Westside, Greenwood, Lakeview, Suburbs"
          )
        )
      }
    }
  }
}
