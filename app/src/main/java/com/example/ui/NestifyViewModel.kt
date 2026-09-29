package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AddressEntity
import com.example.data.local.BookingEntity
import com.example.data.local.NotificationEntity
import com.example.data.model.CatalogData
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.UserRole
import com.example.data.repository.NestifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AuthScreen {
  object Welcome : AuthScreen()
  object CustomerLogin : AuthScreen()
  object CustomerRegister : AuthScreen()
  object OrganizerLogin : AuthScreen()
  object OrganizerRegister : AuthScreen()
}

// Screen navigation targets
sealed class CustomerScreen {
  object Home : CustomerScreen()
  data class ServiceDetail(val service: ServiceItem) : CustomerScreen()
  data class BookingWizard(val service: ServiceItem) : CustomerScreen()
  data class BookingDetail(val bookingId: Long) : CustomerScreen()
  data class BeforeAfterReview(val bookingId: Long) : CustomerScreen()
  object BookingsList : CustomerScreen()
  object Notifications : CustomerScreen()
  object Profile : CustomerScreen()
}

sealed class OrganizerScreen {
  object Dashboard : OrganizerScreen()
  object Jobs : OrganizerScreen()
  data class ActiveJobWorkflow(val bookingId: Long) : OrganizerScreen()
  object Earnings : OrganizerScreen()
  object Profile : OrganizerScreen()
}

class NestifyViewModel(private val repository: NestifyRepository) : ViewModel() {

  // Auth State
  private val _currentAuthScreen = MutableStateFlow<AuthScreen>(AuthScreen.Welcome)
  val currentAuthScreen: StateFlow<AuthScreen> = _currentAuthScreen.asStateFlow()

  private val _currentUser = MutableStateFlow<com.example.data.local.UserEntity?>(null)
  val currentUser: StateFlow<com.example.data.local.UserEntity?> = _currentUser.asStateFlow()

  private val _authErrorMessage = MutableStateFlow<String?>(null)
  val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

  private val _isAuthenticating = MutableStateFlow(false)
  val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

  // Role
  private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
  val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

  // Navigation stacks
  private val _customerScreen = MutableStateFlow<CustomerScreen>(CustomerScreen.Home)
  val customerScreen: StateFlow<CustomerScreen> = _customerScreen.asStateFlow()

  private val _organizerScreen = MutableStateFlow<OrganizerScreen>(OrganizerScreen.Dashboard)
  val organizerScreen: StateFlow<OrganizerScreen> = _organizerScreen.asStateFlow()

  // Selected Category filter on Home
  private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
  val selectedCategory: StateFlow<ServiceCategory?> = _selectedCategory.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Repository Flows
  val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeBookings: StateFlow<List<BookingEntity>> = repository.activeBookings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val completedBookings: StateFlow<List<BookingEntity>> = repository.completedBookings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val addresses: StateFlow<List<AddressEntity>> = repository.addresses
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Booking Flow State
  var wizardStep = MutableStateFlow(1) // 1..8
  var wizardWardrobes = MutableStateFlow(2)
  var wizardCabinets = MutableStateFlow(6)
  var wizardVolume = MutableStateFlow("Standard")
  var wizardOrgType = MutableStateFlow("Color-Coded & Category")
  var wizardPhotosCount = MutableStateFlow(3)
  var wizardNotes = MutableStateFlow("Please keep kids clothes organized separately.")
  var wizardSelectedAddress = MutableStateFlow("742 Evergreen Terrace, Apt 4B")
  var wizardSelectedDate = MutableStateFlow("Tomorrow, Sep 26")
  var wizardSelectedTimeSlot = MutableStateFlow("10:00 AM - 1:30 PM")
  var wizardPaymentMethod = MutableStateFlow("Visa •••• 4242")
  var isPaymentProcessing = MutableStateFlow(false)

  fun navigateAuth(screen: AuthScreen) {
    _authErrorMessage.value = null
    _currentAuthScreen.value = screen
  }

  fun clearAuthError() {
    _authErrorMessage.value = null
  }

  fun loginCustomer(loginId: String, password: String) {
    viewModelScope.launch {
      _isAuthenticating.value = true
      _authErrorMessage.value = null
      kotlinx.coroutines.delay(600) // realistic network simulation
      val result = repository.loginCustomer(loginId, password)
      _isAuthenticating.value = false
      result.onSuccess { user ->
        _currentUser.value = user
        _currentRole.value = UserRole.CUSTOMER
        _customerScreen.value = CustomerScreen.Home
      }.onFailure { err ->
        _authErrorMessage.value = err.message ?: "Invalid Customer Login ID or Password."
      }
    }
  }

  fun loginOrganizer(loginId: String, password: String) {
    viewModelScope.launch {
      _isAuthenticating.value = true
      _authErrorMessage.value = null
      kotlinx.coroutines.delay(600) // realistic network simulation
      val result = repository.loginOrganizer(loginId, password)
      _isAuthenticating.value = false
      result.onSuccess { user ->
        _currentUser.value = user
        _currentRole.value = UserRole.ORGANIZER
        _organizerScreen.value = OrganizerScreen.Dashboard
      }.onFailure { err ->
        _authErrorMessage.value = err.message ?: "Invalid Organizer Login ID or Password."
      }
    }
  }

  fun registerCustomer(
    name: String,
    phone: String,
    email: String,
    loginId: String,
    password: String
  ) {
    viewModelScope.launch {
      _isAuthenticating.value = true
      _authErrorMessage.value = null
      kotlinx.coroutines.delay(600)
      val result = repository.registerCustomer(name, phone, email, loginId, password)
      _isAuthenticating.value = false
      result.onSuccess { user ->
        _currentUser.value = user
        _currentRole.value = UserRole.CUSTOMER
        _customerScreen.value = CustomerScreen.Home
      }.onFailure { err ->
        _authErrorMessage.value = err.message ?: "Registration failed."
      }
    }
  }

  fun registerOrganizer(
    name: String,
    phone: String,
    email: String,
    loginId: String,
    password: String,
    experience: String,
    skills: String,
    categories: String,
    areas: String,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      _isAuthenticating.value = true
      _authErrorMessage.value = null
      kotlinx.coroutines.delay(600)
      val result = repository.registerOrganizer(
        name, phone, email, loginId, password, experience, skills, categories, areas
      )
      _isAuthenticating.value = false
      result.onSuccess {
        onSuccess()
      }.onFailure { err ->
        _authErrorMessage.value = err.message ?: "Organizer registration failed."
      }
    }
  }

  fun resetPassword(
    loginId: String,
    newPassword: String,
    role: String,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      _isAuthenticating.value = true
      _authErrorMessage.value = null
      val result = repository.resetPassword(loginId, newPassword, role)
      _isAuthenticating.value = false
      result.onSuccess {
        onSuccess()
      }.onFailure { err ->
        _authErrorMessage.value = err.message ?: "Password reset failed."
      }
    }
  }

  fun logout() {
    _currentUser.value = null
    _currentAuthScreen.value = AuthScreen.Welcome
    _authErrorMessage.value = null
    _customerScreen.value = CustomerScreen.Home
    _organizerScreen.value = OrganizerScreen.Dashboard
  }

  fun navigateCustomer(screen: CustomerScreen) {
    _customerScreen.value = screen
  }

  fun navigateOrganizer(screen: OrganizerScreen) {
    _organizerScreen.value = screen
  }

  fun setCategoryFilter(category: ServiceCategory?) {
    _selectedCategory.value = if (_selectedCategory.value == category) null else category
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  // Booking Creation
  fun startBooking(service: ServiceItem) {
    wizardStep.value = 1
    wizardWardrobes.value = 2
    wizardCabinets.value = 6
    wizardVolume.value = "Standard"
    wizardOrgType.value = "Color-Coded & Category"
    wizardPhotosCount.value = 3
    wizardNotes.value = ""
    _customerScreen.value = CustomerScreen.BookingWizard(service)
  }

  fun confirmAndPayBooking(service: ServiceItem, onComplete: (Long) -> Unit) {
    viewModelScope.launch {
      isPaymentProcessing.value = true
      kotlinx.coroutines.delay(1200) // Realistic payment processing simulation
      isPaymentProcessing.value = false

      val basePrice = service.startingPrice
      val addonPrice = if (wizardWardrobes.value > 2) (wizardWardrobes.value - 2) * 35.0 else 0.0
      val serviceFee = 15.0
      val discount = 20.0
      val total = basePrice + addonPrice + serviceFee - discount

      val newBooking = BookingEntity(
        serviceId = service.id,
        serviceName = service.name,
        category = service.category.displayName,
        customerName = "Sarah Jenkins",
        customerPhone = "+1 (555) 789-0123",
        address = wizardSelectedAddress.value,
        bookingDate = wizardSelectedDate.value,
        timeSlot = wizardSelectedTimeSlot.value,
        status = "CONFIRMED",
        customerNotes = wizardNotes.value,
        wardrobeCount = wizardWardrobes.value,
        cabinetCount = wizardCabinets.value,
        itemVolume = wizardVolume.value,
        organizationType = wizardOrgType.value,
        photoCount = wizardPhotosCount.value,
        basePrice = basePrice,
        addonPrice = addonPrice,
        serviceFee = serviceFee,
        discount = discount,
        totalPrice = total,
        paymentMethod = wizardPaymentMethod.value,
        paymentStatus = "PAID",
        organizerName = "Elena Martinez",
        organizerRating = 4.95f,
        organizerPhone = "+1 (555) 234-8901",
        organizerDistance = "2.4 miles",
        beforePhotosCount = 0,
        afterPhotosCount = 0
      )

      val id = repository.createBooking(newBooking)
      onComplete(id)
    }
  }

  // Status transitions
  fun advanceBookingStatus(bookingId: Long, nextStatus: String) {
    viewModelScope.launch {
      repository.updateBookingStatus(bookingId, nextStatus)
    }
  }

  fun updateBeforePhotos(bookingId: Long, count: Int) {
    viewModelScope.launch {
      repository.updateBeforePhotos(bookingId, count)
    }
  }

  fun updateAfterPhotos(bookingId: Long, count: Int) {
    viewModelScope.launch {
      repository.updateAfterPhotos(bookingId, count)
    }
  }

  fun submitReview(bookingId: Long, rating: Int, reviewText: String, tags: String) {
    viewModelScope.launch {
      repository.submitCustomerReview(bookingId, rating, reviewText, tags)
    }
  }

  fun confirmCompletion(bookingId: Long) {
    viewModelScope.launch {
      repository.confirmCompletion(bookingId)
    }
  }

  fun addAddress(label: String, line: String, apt: String, landmark: String, isDefault: Boolean) {
    viewModelScope.launch {
      repository.addAddress(label, line, apt, landmark, isDefault)
    }
  }

  fun setDefaultAddress(id: Long) {
    viewModelScope.launch {
      repository.setDefaultAddress(id)
    }
  }

  fun deleteAddress(address: AddressEntity) {
    viewModelScope.launch {
      repository.deleteAddress(address)
    }
  }

  fun markNotificationAsRead(id: Long) {
    viewModelScope.launch {
      repository.markNotificationAsRead(id)
    }
  }

  fun markAllNotificationsAsRead() {
    viewModelScope.launch {
      repository.markAllNotificationsAsRead()
    }
  }
}

class NestifyViewModelFactory(private val repository: NestifyRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(NestifyViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return NestifyViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
