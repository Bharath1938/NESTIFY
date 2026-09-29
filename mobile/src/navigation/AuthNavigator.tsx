import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { WelcomeRoleSelectionScreen } from '../screens/auth/WelcomeRoleSelectionScreen';
import { CustomerLoginScreen } from '../screens/auth/CustomerLoginScreen';
import { CustomerRegisterScreen } from '../screens/auth/CustomerRegisterScreen';
import { OrganizerLoginScreen } from '../screens/auth/OrganizerLoginScreen';
import { OrganizerRegisterScreen } from '../screens/auth/OrganizerRegisterScreen';

export type AuthStackParamList = {
  WelcomeRoleSelection: undefined;
  CustomerLogin: undefined;
  CustomerRegister: undefined;
  OrganizerLogin: undefined;
  OrganizerRegister: undefined;
};

const Stack = createNativeStackNavigator<AuthStackParamList>();

export const AuthNavigator: React.FC = () => {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false, animation: 'slide_from_right' }}>
      <Stack.Screen name="WelcomeRoleSelection" component={WelcomeRoleSelectionScreen} />
      <Stack.Screen name="CustomerLogin" component={CustomerLoginScreen} />
      <Stack.Screen name="CustomerRegister" component={CustomerRegisterScreen} />
      <Stack.Screen name="OrganizerLogin" component={OrganizerLoginScreen} />
      <Stack.Screen name="OrganizerRegister" component={OrganizerRegisterScreen} />
    </Stack.Navigator>
  );
};
