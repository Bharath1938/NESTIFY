import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { CustomerHomeScreen } from '../screens/customer/CustomerHomeScreen';
import { CategoryServicesScreen } from '../screens/customer/CategoryServicesScreen';
import { ServiceDetailScreen } from '../screens/customer/ServiceDetailScreen';
import { CustomerBookingsListScreen } from '../screens/customer/CustomerBookingsListScreen';
import { BookingDetailScreen } from '../screens/customer/BookingDetailScreen';
import { CartScreen } from '../screens/customer/CartScreen';
import { CheckoutAddressScreen } from '../screens/customer/CheckoutAddressScreen';
import { CheckoutScheduleScreen } from '../screens/customer/CheckoutScheduleScreen';
import { BookingSummaryScreen } from '../screens/customer/BookingSummaryScreen';
import { CustomerProfileScreen } from '../screens/customer/CustomerProfileScreen';
import { Colors } from '../theme';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

const HomeStack = () => (
  <Stack.Navigator screenOptions={{ headerShown: false, animation: 'slide_from_right' }}>
    <Stack.Screen name="HomeMain" component={CustomerHomeScreen} />
    <Stack.Screen name="CategoryServices" component={CategoryServicesScreen} />
    <Stack.Screen name="ServiceDetail" component={ServiceDetailScreen} />
    <Stack.Screen name="CheckoutAddress" component={CheckoutAddressScreen} />
    <Stack.Screen name="CheckoutSchedule" component={CheckoutScheduleScreen} />
    <Stack.Screen name="BookingSummary" component={BookingSummaryScreen} />
  </Stack.Navigator>
);

const BookingsStack = () => (
  <Stack.Navigator screenOptions={{ headerShown: false, animation: 'slide_from_right' }}>
    <Stack.Screen name="BookingsList" component={CustomerBookingsListScreen} />
    <Stack.Screen name="BookingDetail" component={BookingDetailScreen} />
  </Stack.Navigator>
);

export const CustomerTabNavigator: React.FC = () => {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: Colors.primary,
        tabBarInactiveTintColor: Colors.textMuted,
        tabBarStyle: {
          backgroundColor: Colors.surface,
          borderTopColor: Colors.border,
          height: 60,
          paddingBottom: 8,
          paddingTop: 8,
        },
      }}
    >
      <Tab.Screen name="CustomerHomeTab" component={HomeStack} options={{ tabBarLabel: 'Home' }} />
      <Tab.Screen name="CustomerBookingsTab" component={BookingsStack} options={{ tabBarLabel: 'Bookings' }} />
      <Tab.Screen name="CartTab" component={CartScreen} options={{ tabBarLabel: 'Cart' }} />
      <Tab.Screen name="CustomerProfileTab" component={CustomerProfileScreen} options={{ tabBarLabel: 'Profile' }} />
    </Tab.Navigator>
  );
};
