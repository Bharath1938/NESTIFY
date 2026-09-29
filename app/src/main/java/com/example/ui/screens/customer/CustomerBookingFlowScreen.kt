package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogData
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.ui.CustomerScreen
import com.example.ui.NestifyViewModel
import com.example.ui.components.PhotoUploadSlots
import com.example.ui.components.PriceSummaryCard
import com.example.ui.theme.NestifyPrimary
import com.example.ui.theme.NestifySecondary
import com.example.ui.theme.NestifySuccess
import com.example.util.CameraPermissionRationaleDialog
import com.example.util.rememberCameraPermissionHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerBookingFlowScreen(
  service: ServiceItem,
  viewModel: NestifyViewModel,
  modifier: Modifier = Modifier
) {
  val cameraPermission = rememberCameraPermissionHandler {
    if (viewModel.wizardPhotosCount.value < 5) {
      viewModel.wizardPhotosCount.value++
    }
  }

  val step by viewModel.wizardStep.collectAsState()
  val wardrobes by viewModel.wizardWardrobes.collectAsState()
  val cabinets by viewModel.wizardCabinets.collectAsState()
  val volume by viewModel.wizardVolume.collectAsState()
  val orgType by viewModel.wizardOrgType.collectAsState()
  val photoCount by viewModel.wizardPhotosCount.collectAsState()
  val notes by viewModel.wizardNotes.collectAsState()
  val selectedAddress by viewModel.wizardSelectedAddress.collectAsState()
  val selectedDate by viewModel.wizardSelectedDate.collectAsState()
  val selectedSlot by viewModel.wizardSelectedTimeSlot.collectAsState()
  val paymentMethod by viewModel.wizardPaymentMethod.collectAsState()
  val isProcessing by viewModel.isPaymentProcessing.collectAsState()
  val addresses by viewModel.addresses.collectAsState()

  // Calculated Pricing
  val basePrice = service.startingPrice
  val addonPrice = if (service.category == ServiceCategory.BEDROOM && wardrobes > 2) {
    (wardrobes - 2) * 35.0
  } else if (service.category == ServiceCategory.KITCHEN && cabinets > 8) {
    (cabinets - 8) * 15.0
  } else {
    0.0
  }
  val serviceFee = 15.0
  val discount = 20.0
  val totalPrice = basePrice + addonPrice + serviceFee - discount

  BackHandler {
    if (step > 1) {
      viewModel.wizardStep.value = step - 1
    } else {
      viewModel.navigateCustomer(CustomerScreen.ServiceDetail(service))
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // 1. Top App Bar with Step Indicator
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 2.dp
    ) {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = {
                if (step > 1) viewModel.wizardStep.value = step - 1
                else viewModel.navigateCustomer(CustomerScreen.ServiceDetail(service))
              },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Step $step of 6 • ${getStepTitle(step)}",
                style = MaterialTheme.typography.labelSmall,
                color = NestifyPrimary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Text(
            text = "$${totalPrice.toInt()}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = NestifyPrimary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
          progress = { step / 6f },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = NestifyPrimary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
      }
    }

    // 2. Step Content Body
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      AnimatedContent(
        targetState = step,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "BookingStepAnimation"
      ) { targetStep ->
        when (targetStep) {
          1 -> Step1ConfigureRequirement(
            service = service,
            wardrobes = wardrobes,
            cabinets = cabinets,
            volume = volume,
            orgType = orgType,
            onWardrobesChange = { viewModel.wizardWardrobes.value = it },
            onCabinetsChange = { viewModel.wizardCabinets.value = it },
            onVolumeChange = { viewModel.wizardVolume.value = it },
            onOrgTypeChange = { viewModel.wizardOrgType.value = it }
          )
          2 -> Step2UploadPhotosAndNotes(
            photoCount = photoCount,
            notes = notes,
            onAddPhoto = {
              if (cameraPermission.isGranted) {
                if (viewModel.wizardPhotosCount.value < 5) {
                  viewModel.wizardPhotosCount.value++
                }
              } else {
                cameraPermission.requestPermission()
              }
            },
            onRemovePhoto = {
              if (viewModel.wizardPhotosCount.value > 0) {
                viewModel.wizardPhotosCount.value--
              }
            },
            onNotesChange = { viewModel.wizardNotes.value = it }
          )
          3 -> Step3SelectAddress(
            addresses = addresses,
            selectedAddress = selectedAddress,
            onAddressSelected = { viewModel.wizardSelectedAddress.value = it },
            onAddNewAddress = {
              viewModel.addAddress("Home", "124 Maple Wood Way", "Unit 3", "Near Cedar Park", false)
              viewModel.wizardSelectedAddress.value = "124 Maple Wood Way, Unit 3"
            }
          )
          4 -> Step4SelectDateTime(
            selectedDate = selectedDate,
            selectedSlot = selectedSlot,
            onDateSelected = { viewModel.wizardSelectedDate.value = it },
            onSlotSelected = { viewModel.wizardSelectedTimeSlot.value = it }
          )
          5 -> Step5ReviewEstimate(
            service = service,
            basePrice = basePrice,
            addonPrice = addonPrice,
            serviceFee = serviceFee,
            discount = discount,
            totalPrice = totalPrice,
            date = selectedDate,
            timeSlot = selectedSlot,
            address = selectedAddress,
            notes = notes
          )
          6 -> Step6PaymentAndConfirm(
            totalPrice = totalPrice,
            paymentMethod = paymentMethod,
            isProcessing = isProcessing,
            onSelectPaymentMethod = { viewModel.wizardPaymentMethod.value = it },
            onConfirmBooking = {
              viewModel.confirmAndPayBooking(service) { newId ->
                viewModel.navigateCustomer(CustomerScreen.BookingDetail(newId))
              }
            }
          )
          else -> Box(modifier = Modifier.fillMaxSize())
        }
      }
    }

    // 3. Bottom Action Bar (Next / Pay button)
    if (step < 6) {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Estimated Total",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "$${totalPrice.toInt()}",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = NestifyPrimary
            )
          }

          Button(
            onClick = { viewModel.wizardStep.value = step + 1 },
            modifier = Modifier
              .width(180.dp)
              .height(50.dp)
              .testTag("booking_next_step_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
          ) {
            Text(
              text = if (step == 5) "Proceed to Pay" else "Continue",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    CameraPermissionRationaleDialog(state = cameraPermission)
  }
}

fun getStepTitle(step: Int): String {
  return when (step) {
    1 -> "Configure Space"
    2 -> "Photos & Notes"
    3 -> "Service Address"
    4 -> "Date & Time"
    5 -> "Review Estimate"
    6 -> "Secure Payment"
    else -> ""
  }
}

@Composable
fun Step1ConfigureRequirement(
  service: ServiceItem,
  wardrobes: Int,
  cabinets: Int,
  volume: String,
  orgType: String,
  onWardrobesChange: (Int) -> Unit,
  onCabinetsChange: (Int) -> Unit,
  onVolumeChange: (String) -> Unit,
  onOrgTypeChange: (String) -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Tailor Your Requirement",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Answer a few quick questions so our organizer brings the exact tools & supplies.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    if (service.category == ServiceCategory.BEDROOM) {
      item {
        RequirementCounterCard(
          title = "Number of Wardrobes / Closets",
          subtitle = "Standard service includes up to 2 wardrobes",
          count = wardrobes,
          onIncrement = { onWardrobesChange(wardrobes + 1) },
          onDecrement = { if (wardrobes > 1) onWardrobesChange(wardrobes - 1) }
        )
      }
    } else if (service.category == ServiceCategory.KITCHEN) {
      item {
        RequirementCounterCard(
          title = "Number of Cabinets & Drawers",
          subtitle = "Standard includes up to 8 cabinets",
          count = cabinets,
          onIncrement = { onCabinetsChange(cabinets + 1) },
          onDecrement = { if (cabinets > 2) onCabinetsChange(cabinets - 1) }
        )
      }
    } else {
      item {
        RequirementCounterCard(
          title = "Estimated Room / Zone Size",
          subtitle = "Number of distinct areas to organize",
          count = wardrobes,
          onIncrement = { onWardrobesChange(wardrobes + 1) },
          onDecrement = { if (wardrobes > 1) onWardrobesChange(wardrobes - 1) }
        )
      }
    }

    // Item Volume selector
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Item Volume",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Light", "Standard", "Heavy Clutter").forEach { opt ->
              val isSelected = volume == opt
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { onVolumeChange(opt) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) NestifyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
              ) {
                Box(
                  modifier = Modifier.padding(vertical = 12.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = opt,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }
      }
    }

    // Organization Preference Style
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Preferred Organization Style",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))
          val styles = listOf(
            "Color-Coded & Category" to "Rainbow garment gradients and functional groupings",
            "Minimalist / Capsule" to "Keep essentials only with maximal negative space",
            "KonMari Vertical Fold" to "Stand items upright for instant 100% visibility"
          )
          styles.forEach { (name, desc) ->
            val isSelected = orgType == name
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) NestifyPrimary.copy(alpha = 0.08f) else Color.Transparent)
                .border(
                  1.dp,
                  if (isSelected) NestifyPrimary else MaterialTheme.colorScheme.outlineVariant,
                  RoundedCornerShape(12.dp)
                )
                .clickable { onOrgTypeChange(name) }
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .border(2.dp, if (isSelected) NestifyPrimary else Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                if (isSelected) {
                  Box(modifier = Modifier.size(10.dp).background(NestifyPrimary, CircleShape))
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = name,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = desc,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun RequirementCounterCard(
  title: String,
  subtitle: String,
  count: Int,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onDecrement,
          modifier = Modifier
            .size(36.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        ) {
          Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.onSurface)
        }
        Text(
          text = "$count",
          modifier = Modifier.padding(horizontal = 14.dp),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        IconButton(
          onClick = onIncrement,
          modifier = Modifier
            .size(36.dp)
            .background(NestifyPrimary, CircleShape)
        ) {
          Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
        }
      }
    }
  }
}

@Composable
fun Step2UploadPhotosAndNotes(
  photoCount: Int,
  notes: String,
  onAddPhoto: () -> Unit,
  onRemovePhoto: (Int) -> Unit,
  onNotesChange: (String) -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Upload Space Photos",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Snap 3 to 5 photos of your current space. Organizers review these beforehand to pack the perfect dividers and bins.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          PhotoUploadSlots(
            photoCount = photoCount,
            maxPhotos = 5,
            onAddPhoto = onAddPhoto,
            onRemovePhoto = onRemovePhoto
          )
        }
      }
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Instructions & Special Requests",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "e.g., 'Please keep children's clothes separate' or 'Handle vintage knitwear with care'",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { Text("Enter any notes for your organizer...") },
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .testTag("booking_notes_input"),
            shape = RoundedCornerShape(12.dp)
          )
        }
      }
    }
  }
}

@Composable
fun Step3SelectAddress(
  addresses: List<com.example.data.local.AddressEntity>,
  selectedAddress: String,
  onAddressSelected: (String) -> Unit,
  onAddNewAddress: () -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "Select Service Address",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Where should your professional organizer visit?",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    items(addresses) { address ->
      val full = "${address.addressLine}, ${address.apartment}"
      val isSelected = selectedAddress.contains(address.addressLine)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onAddressSelected(full) }
          .border(
            1.5.dp,
            if (isSelected) NestifyPrimary else Color.Transparent,
            RoundedCornerShape(16.dp)
          ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isSelected) NestifyPrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(NestifyPrimary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = NestifyPrimary)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = address.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              if (address.isDefault) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = NestifySuccess.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "DEFAULT",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 9.sp,
                    color = NestifySuccess,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
            Text(text = "${address.addressLine}, ${address.apartment}", style = MaterialTheme.typography.bodyMedium)
            if (address.landmark.isNotBlank()) {
              Text(text = "Landmark: ${address.landmark}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }

          if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NestifyPrimary)
          }
        }
      }
    }

    item {
      OutlinedButton(
        onClick = onAddNewAddress,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Add New Address")
      }
    }
  }
}

@Composable
fun Step4SelectDateTime(
  selectedDate: String,
  selectedSlot: String,
  onDateSelected: (String) -> Unit,
  onSlotSelected: (String) -> Unit
) {
  val dates = listOf(
    "Today" to "Sep 25",
    "Tomorrow" to "Sep 26",
    "Saturday" to "Sep 27",
    "Sunday" to "Sep 28",
    "Monday" to "Sep 29"
  )

  val timeSlots = listOf(
    "09:00 AM - 12:30 PM" to "Morning Slot • Fast Availability",
    "10:00 AM - 1:30 PM" to "Mid-Day Slot • Most Popular",
    "02:00 PM - 05:30 PM" to "Afternoon Slot • Peaceful Finish",
    "04:00 PM - 07:30 PM" to "Evening Slot • After Work"
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Select Date & Time",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Real-time verified organizer availability in your area.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      Text(
        text = "Choose Date",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(dates) { (dayLabel, dateStr) ->
          val full = "$dayLabel, $dateStr"
          val isSelected = selectedDate.contains(dateStr) || selectedDate == dayLabel
          Surface(
            modifier = Modifier
              .width(90.dp)
              .clip(RoundedCornerShape(14.dp))
              .clickable { onDateSelected(full) },
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) NestifyPrimary else MaterialTheme.colorScheme.surface,
            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Column(
              modifier = Modifier.padding(vertical = 14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = dayLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = dateStr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    item {
      Text(
        text = "Available Time Slots",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(10.dp))
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        timeSlots.forEach { (slot, desc) ->
          val isSelected = selectedSlot == slot
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) NestifyPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
              .border(
                1.5.dp,
                if (isSelected) NestifyPrimary else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(14.dp)
              )
              .clickable { onSlotSelected(slot) }
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = slot,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) NestifyPrimary else MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            if (isSelected) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NestifyPrimary)
            }
          }
        }
      }
    }
  }
}

@Composable
fun Step5ReviewEstimate(
  service: ServiceItem,
  basePrice: Double,
  addonPrice: Double,
  serviceFee: Double,
  discount: Double,
  totalPrice: Double,
  date: String,
  timeSlot: String,
  address: String,
  notes: String
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Review Booking Details",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Please verify your appointment schedule and price calculation.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "Appointment Schedule", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(10.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$date • $timeSlot", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = NestifyPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = address, style = MaterialTheme.typography.bodyMedium)
          }
          if (notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Notes: \"$notes\"",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    item {
      PriceSummaryCard(
        basePrice = basePrice,
        addonPrice = addonPrice,
        serviceFee = serviceFee,
        discount = discount,
        totalPrice = totalPrice
      )
    }
  }
}

@Composable
fun Step6PaymentAndConfirm(
  totalPrice: Double,
  paymentMethod: String,
  isProcessing: Boolean,
  onSelectPaymentMethod: (String) -> Unit,
  onConfirmBooking: () -> Unit
) {
  val paymentMethods = listOf(
    "Visa •••• 4242" to "Credit / Debit Card (Instant)",
    "Apple Pay / Google Pay" to "One-Tap Encrypted Checkout",
    "Pay After Service" to "Charge card after you confirm transformation"
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Secure Payment",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "256-bit SSL encrypted. You will not be charged until the appointment is confirmed.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        paymentMethods.forEach { (name, sub) ->
          val isSelected = paymentMethod == name
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) NestifyPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
              .border(
                1.5.dp,
                if (isSelected) NestifyPrimary else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(14.dp)
              )
              .clickable { onSelectPaymentMethod(name) }
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.CreditCard, contentDescription = null, tint = NestifyPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(text = name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
              Text(text = sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NestifyPrimary)
            }
          }
        }
      }
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = NestifySuccess, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Zero-Risk Guarantee: Cancel or reschedule up to 2 hours before the service with full refund.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = onConfirmBooking,
        enabled = !isProcessing,
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("confirm_and_pay_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NestifyPrimary)
      ) {
        if (isProcessing) {
          CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
          Spacer(modifier = Modifier.width(10.dp))
          Text("Authorizing Payment & Assigning Pro...")
        } else {
          Text(
            text = "Pay $${totalPrice.toInt()} & Confirm Booking",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}
