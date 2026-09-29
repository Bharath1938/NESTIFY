import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { OrganizerDashboardScreen } from '../screens/organizer/OrganizerDashboardScreen';
import { OrganizerJobsScreen } from '../screens/organizer/OrganizerJobsScreen';
import { OrganizerJobDetailScreen } from '../screens/organizer/OrganizerJobDetailScreen';
import { OrganizerEarningsScreen } from '../screens/organizer/OrganizerEarningsScreen';
import { OrganizerProfileScreen } from '../screens/organizer/OrganizerProfileScreen';
import { Colors } from '../theme';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

const JobsStack = () => (
  <Stack.Navigator screenOptions={{ headerShown: false, animation: 'slide_from_right' }}>
    <Stack.Screen name="OrganizerJobsList" component={OrganizerJobsScreen} />
    <Stack.Screen name="OrganizerJobDetail" component={OrganizerJobDetailScreen} />
  </Stack.Navigator>
);

export const OrganizerTabNavigator: React.FC = () => {
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
      <Tab.Screen name="OrganizerDashboardTab" component={OrganizerDashboardScreen} options={{ tabBarLabel: 'Dashboard' }} />
      <Tab.Screen name="OrganizerJobsTab" component={JobsStack} options={{ tabBarLabel: 'Jobs' }} />
      <Tab.Screen name="OrganizerEarningsTab" component={OrganizerEarningsScreen} options={{ tabBarLabel: 'Earnings' }} />
      <Tab.Screen name="OrganizerProfileTab" component={OrganizerProfileScreen} options={{ tabBarLabel: 'Profile' }} />
    </Tab.Navigator>
  );
};
