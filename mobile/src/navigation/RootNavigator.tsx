import React, { createContext, useContext, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { AuthNavigator } from './AuthNavigator';
import { CustomerTabNavigator } from './CustomerTabNavigator';
import { OrganizerTabNavigator } from './OrganizerTabNavigator';
import { User, AuthState } from '../types';

interface AuthContextType extends AuthState {
  login: (user: User, accessToken: string, refreshToken: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  isLoading: false,
  login: () => {},
  logout: () => {},
});

export const useAuth = () => useContext(AuthContext);

const Stack = createNativeStackNavigator();

export const RootNavigator: React.FC = () => {
  const [auth, setAuth] = useState<AuthState>({
    user: null,
    accessToken: null,
    refreshToken: null,
    isAuthenticated: false,
    isLoading: false,
  });

  const login = (user: User, accessToken: string, refreshToken: string) => {
    setAuth({
      user,
      accessToken,
      refreshToken,
      isAuthenticated: true,
      isLoading: false,
    });
  };

  const logout = () => {
    setAuth({
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
    });
  };

  return (
    <AuthContext.Provider value={{ ...auth, login, logout }}>
      <NavigationContainer>
        <Stack.Navigator screenOptions={{ headerShown: false }}>
          {!auth.isAuthenticated ? (
            <Stack.Screen name="Auth" component={AuthNavigator} />
          ) : auth.user?.role === 'Organizer' ? (
            <Stack.Screen name="OrganizerApp" component={OrganizerTabNavigator} />
          ) : (
            <Stack.Screen name="CustomerApp" component={CustomerTabNavigator} />
          )}
        </Stack.Navigator>
      </NavigationContainer>
    </AuthContext.Provider>
  );
};
