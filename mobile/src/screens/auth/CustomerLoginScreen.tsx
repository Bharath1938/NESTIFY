import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Button, InputField, Card } from '../../components';
import { useAuth } from '../../navigation/RootNavigator';
import { Colors, Typography, Spacing } from '../../theme';

export const CustomerLoginScreen = ({ navigation }: any) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();

  const handleLogin = () => {
    // Mock successful authentication
    login(
      { id: '1', fullName: 'Jane Customer', email, phone: '+1234567890', role: 'Customer' },
      'jwt_access_token_demo',
      'jwt_refresh_token_demo'
    );
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome Back</Text>
      <Text style={styles.subtitle}>Log in to manage your home organizing bookings</Text>

      <Card style={styles.card}>
        <InputField label="Email Address" value={email} onChangeText={setEmail} keyboardType="email-address" />
        <InputField label="Password" value={password} onChangeText={setPassword} secureTextEntry />

        <Button title="Log In" onPress={handleLogin} style={styles.button} />

        <TouchableOpacity onPress={() => navigation.navigate('CustomerRegister')} style={styles.linkContainer}>
          <Text style={styles.linkText}>Don't have an account? <Text style={styles.linkHighlight}>Register</Text></Text>
        </TouchableOpacity>
      </Card>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background, padding: Spacing.lg, justifyContent: 'center' },
  title: { fontSize: Typography.fontSize.xxl, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  subtitle: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginBottom: Spacing.lg },
  card: { padding: Spacing.lg },
  button: { marginTop: Spacing.md },
  linkContainer: { marginTop: Spacing.md, alignItems: 'center' },
  linkText: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary },
  linkHighlight: { color: Colors.primary, fontWeight: Typography.fontWeight.bold },
});
