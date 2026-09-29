package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.screens.auth.CustomerLoginScreen
import com.example.ui.screens.auth.CustomerRegisterScreen
import com.example.ui.screens.auth.OrganizerLoginScreen
import com.example.ui.screens.auth.OrganizerRegisterScreen
import com.example.ui.screens.auth.WelcomeRoleSelectionScreen
import com.example.ui.screens.customer.CustomerActiveBookingDetailScreen
import com.example.ui.screens.customer.CustomerBeforeAfterScreen
import com.example.ui.screens.customer.CustomerBookingFlowScreen
import com.example.ui.screens.customer.CustomerBookingsListScreen
import com.example.ui.screens.customer.CustomerHomeScreen
import com.example.ui.screens.customer.CustomerNotificationsScreen
import com.example.ui.screens.customer.CustomerProfileScreen
import com.example.ui.screens.customer.CustomerServiceDetailScreen
import com.example.ui.screens.organizer.OrganizerActiveJobScreen
import com.example.ui.screens.organizer.OrganizerDashboardScreen
import com.example.ui.screens.organizer.OrganizerEarningsScreen
import com.example.ui.screens.organizer.OrganizerJobsScreen
import com.example.ui.screens.organizer.OrganizerProfileScreen
import com.example.ui.theme.NestifyPrimary

@Composable
fun NestifyApp(
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val currentAuthScreen by viewModel.currentAuthScreen.collectAsState()
  val customerScreen by viewModel.customerScreen.collectAsState()
  val organizerScreen by viewModel.organizerScreen.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val unreadNotifications = notifications.count { !it.isRead }

  // If not logged in, enforce authentication flow
  if (currentUser == null) {
    Scaffold(
      modifier = modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (currentAuthScreen) {
          is AuthScreen.Welcome -> WelcomeRoleSelectionScreen(viewModel = viewModel)
          is AuthScreen.CustomerLogin -> CustomerLoginScreen(viewModel = viewModel)
          is AuthScreen.CustomerRegister -> CustomerRegisterScreen(viewModel = viewModel)
          is AuthScreen.OrganizerLogin -> OrganizerLoginScreen(viewModel = viewModel)
          is AuthScreen.OrganizerRegister -> OrganizerRegisterScreen(viewModel = viewModel)
        }
      }
    }
    return
  }

  // Authenticated state: Strict role separation
  val isCustomer = currentUser?.role == "Customer"

  val showCustomerBottomNav = isCustomer && when (customerScreen) {
    CustomerScreen.Home,
    CustomerScreen.BookingsList,
    CustomerScreen.Notifications,
    CustomerScreen.Profile -> true
    else -> false
  }

  val showOrganizerBottomNav = !isCustomer && when (organizerScreen) {
    OrganizerScreen.Dashboard,
    OrganizerScreen.Jobs,
    OrganizerScreen.Earnings,
    OrganizerScreen.Profile -> true
    else -> false
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      // Clean top system spacing
      Box(modifier = Modifier.statusBarsPadding())
    },
    bottomBar = {
      if (showCustomerBottomNav) {
        CustomerBottomNavigation(
          currentScreen = customerScreen,
          unreadNotifications = unreadNotifications,
          onNavigate = { screen -> viewModel.navigateCustomer(screen) },
          modifier = Modifier.navigationBarsPadding()
        )
      } else if (showOrganizerBottomNav) {
        OrganizerBottomNavigation(
          currentScreen = organizerScreen,
          onNavigate = { screen -> viewModel.navigateOrganizer(screen) },
          modifier = Modifier.navigationBarsPadding()
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (isCustomer) {
        // Enforce customer-only screens
        when (val screen = customerScreen) {
          is CustomerScreen.Home -> CustomerHomeScreen(viewModel = viewModel)
          is CustomerScreen.ServiceDetail -> CustomerServiceDetailScreen(service = screen.service, viewModel = viewModel)
          is CustomerScreen.BookingWizard -> CustomerBookingFlowScreen(service = screen.service, viewModel = viewModel)
          is CustomerScreen.BookingDetail -> CustomerActiveBookingDetailScreen(bookingId = screen.bookingId, viewModel = viewModel)
          is CustomerScreen.BeforeAfterReview -> CustomerBeforeAfterScreen(bookingId = screen.bookingId, viewModel = viewModel)
          is CustomerScreen.BookingsList -> CustomerBookingsListScreen(viewModel = viewModel)
          is CustomerScreen.Notifications -> CustomerNotificationsScreen(viewModel = viewModel)
          is CustomerScreen.Profile -> CustomerProfileScreen(viewModel = viewModel)
        }
      } else {
        // Enforce organizer-only screens
        when (val screen = organizerScreen) {
          is OrganizerScreen.Dashboard -> OrganizerDashboardScreen(viewModel = viewModel)
          is OrganizerScreen.Jobs -> OrganizerJobsScreen(viewModel = viewModel)
          is OrganizerScreen.ActiveJobWorkflow -> OrganizerActiveJobScreen(bookingId = screen.bookingId, viewModel = viewModel)
          is OrganizerScreen.Earnings -> OrganizerEarningsScreen(viewModel = viewModel)
          is OrganizerScreen.Profile -> OrganizerProfileScreen(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun CustomerBottomNavigation(
  currentScreen: CustomerScreen,
  unreadNotifications: Int,
  onNavigate: (CustomerScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.fillMaxWidth(),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp
  ) {
    // 1. Home
    NavigationBarItem(
      selected = currentScreen is CustomerScreen.Home,
      onClick = { onNavigate(CustomerScreen.Home) },
      icon = {
        Icon(
          imageVector = if (currentScreen is CustomerScreen.Home) Icons.Default.Home else Icons.Outlined.Home,
          contentDescription = "Home"
        )
      },
      label = { Text("Home", fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NestifyPrimary,
        selectedTextColor = NestifyPrimary,
        indicatorColor = NestifyPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_customer_home")
    )

    // 2. Bookings
    NavigationBarItem(
      selected = currentScreen is CustomerScreen.BookingsList,
      onClick = { onNavigate(CustomerScreen.BookingsList) },
      icon = {
        Icon(
          imageVector = if (currentScreen is CustomerScreen.BookingsList) Icons.Default.FormatListBulleted else Icons.Outlined.FormatListBulleted,
          contentDescription = "Bookings"
        )
      },
      label = { Text("Bookings", fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NestifyPrimary,
        selectedTextColor = NestifyPrimary,
        indicatorColor = NestifyPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_customer_bookings")
    )

    // 3. Messages / Notifications
    NavigationBarItem(
      selected = currentScreen is CustomerScreen.Notifications,
      onClick = { onNavigate(CustomerScreen.Notifications) },
      icon = {
        BadgedBox(
          badge = {
            if (unreadNotifications > 0) {
              Badge(containerColor = Color(0xFFEF4444)) {
                Text("$unreadNotifications")
              }
            }
          }
        ) {
          Icon(
            imageVector = if (currentScreen is CustomerScreen.Notifications) Icons.Default.Notifications else Icons.Outlined.Notifications,
            contentDescription = "Notifications"
          )
        }
      },
      label = { Text("Updates", fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NestifyPrimary,
        selectedTextColor = NestifyPrimary,
        indicatorColor = NestifyPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_customer_notifications")
    )

    // 4. Profile
    NavigationBarItem(
      selected = currentScreen is CustomerScreen.Profile,
      onClick = { onNavigate(CustomerScreen.Profile) },
      icon = {
        Icon(
          imageVector = if (currentScreen is CustomerScreen.Profile) Icons.Default.Person else Icons.Outlined.Person,
          contentDescription = "Profile"
        )
      },
      label = { Text("Profile", fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NestifyPrimary,
        selectedTextColor = NestifyPrimary,
        indicatorColor = NestifyPrimary.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_customer_profile")
    )
  }
}

@Composable
fun OrganizerBottomNavigation(
  currentScreen: OrganizerScreen,
  onNavigate: (OrganizerScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.fillMaxWidth(),
    containerColor = Color(0xFF1E293B),
    tonalElevation = 6.dp
  ) {
    // 1. Dashboard
    NavigationBarItem(
      selected = currentScreen is OrganizerScreen.Dashboard,
      onClick = { onNavigate(OrganizerScreen.Dashboard) },
      icon = {
        Icon(
          imageVector = if (currentScreen is OrganizerScreen.Dashboard) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
          contentDescription = "Dashboard"
        )
      },
      label = { Text("Dashboard", color = Color.White, fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NestifyPrimary,
        selectedTextColor = Color.White,
        unselectedIconColor = Color.LightGray,
        unselectedTextColor = Color.LightGray,
        indicatorColor = NestifyPrimary.copy(alpha = 0.25f)
      ),
      modifier = Modifier.testTag("nav_organizer_dashboard")
    )

    // 2. Jobs
    NavigationBarItem(
      selected = currentScreen is OrganizerScreen.Jobs,
      onClick = { onNavigate(OrganizerScreen.Jobs) },
      icon = {
        Icon(
          imageVector = if (currentScreen is OrganizerScreen.Jobs) Icons.Default.Work else Icons.Outlined.WorkOutline,
          contentDescription = "Jobs"
        )
      },
      label = { Text("Jobs", color = Color.White, fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NestifyPrimary,
        selectedTextColor = Color.White,
        unselectedIconColor = Color.LightGray,
        unselectedTextColor = Color.LightGray,
        indicatorColor = NestifyPrimary.copy(alpha = 0.25f)
      ),
      modifier = Modifier.testTag("nav_organizer_jobs")
    )

    // 3. Earnings
    NavigationBarItem(
      selected = currentScreen is OrganizerScreen.Earnings,
      onClick = { onNavigate(OrganizerScreen.Earnings) },
      icon = {
        Icon(
          imageVector = if (currentScreen is OrganizerScreen.Earnings) Icons.Default.AttachMoney else Icons.Outlined.AttachMoney,
          contentDescription = "Earnings"
        )
      },
      label = { Text("Earnings", color = Color.White, fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NestifyPrimary,
        selectedTextColor = Color.White,
        unselectedIconColor = Color.LightGray,
        unselectedTextColor = Color.LightGray,
        indicatorColor = NestifyPrimary.copy(alpha = 0.25f)
      ),
      modifier = Modifier.testTag("nav_organizer_earnings")
    )

    // 4. Profile
    NavigationBarItem(
      selected = currentScreen is OrganizerScreen.Profile,
      onClick = { onNavigate(OrganizerScreen.Profile) },
      icon = {
        Icon(
          imageVector = if (currentScreen is OrganizerScreen.Profile) Icons.Default.Person else Icons.Outlined.Person,
          contentDescription = "Profile"
        )
      },
      label = { Text("Profile", color = Color.White, fontWeight = FontWeight.SemiBold) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = NestifyPrimary,
        selectedTextColor = Color.White,
        unselectedIconColor = Color.LightGray,
        unselectedTextColor = Color.LightGray,
        indicatorColor = NestifyPrimary.copy(alpha = 0.25f)
      ),
      modifier = Modifier.testTag("nav_organizer_profile")
    )
  }
}
