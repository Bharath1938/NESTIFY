import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Button, InputField, Card } from '../../components';
import { useAuth } from '../../navigation/RootNavigator';
import { Colors, Typography, Spacing } from '../../theme';

export const OrganizerLoginScreen = ({ navigation }: any) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();

  const handleLogin = () => {
    login(
      { id: '2', fullName: 'Alex Organizer', email, phone: '+1987654321', role: 'Organizer' },
      'jwt_access_token_demo',
      'jwt_refresh_token_demo'
    );
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Organizer Portal</Text>
      <Text style={styles.subtitle}>Log in to manage active jobs & earnings</Text>

      <Card style={styles.card}>
        <InputField label="Email Address" value={email} onChangeText={setEmail} keyboardType="email-address" />
        <InputField label="Password" value={password} onChangeText={setPassword} secureTextEntry />

        <Button title="Log In" variant="secondary" onPress={handleLogin} style={styles.button} />

        <TouchableOpacity onPress={() => navigation.navigate('OrganizerRegister')} style={styles.linkContainer}>
          <Text style={styles.linkText}>New Pro Organizer? <Text style={styles.linkHighlight}>Apply Here</Text></Text>
        </TouchableOpacity>
      </Card>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background, padding: Spacing.lg, justifyContent: 'center' },
  title: { fontSize: Typography.fontSize.xxl, fontWeight: Typography.fontWeight.bold, color: Colors.secondary },
  subtitle: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginBottom: Spacing.lg },
  card: { padding: Spacing.lg },
  button: { marginTop: Spacing.md },
  linkContainer: { marginTop: Spacing.md, alignItems: 'center' },
  linkText: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary },
  linkHighlight: { color: Colors.secondary, fontWeight: Typography.fontWeight.bold },
});
