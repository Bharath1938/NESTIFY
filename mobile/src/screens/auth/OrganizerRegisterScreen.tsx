import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView } from 'react-native';
import { Button, InputField, Card } from '../../components';
import { useAuth } from '../../navigation/RootNavigator';
import { Colors, Typography, Spacing } from '../../theme';

export const OrganizerRegisterScreen = ({ navigation }: any) => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useAuth();

  const handleRegister = () => {
    login(
      { id: '2', fullName, email, phone, role: 'Organizer' },
      'jwt_access_token_demo',
      'jwt_refresh_token_demo'
    );
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Join as Organizer</Text>
      <Text style={styles.subtitle}>Partner with Nestify to grow your business</Text>

      <Card style={styles.card}>
        <InputField label="Full Name" value={fullName} onChangeText={setFullName} />
        <InputField label="Email Address" value={email} onChangeText={setEmail} keyboardType="email-address" />
        <InputField label="Phone Number" value={phone} onChangeText={setPhone} keyboardType="phone-pad" />
        <InputField label="Password" value={password} onChangeText={setPassword} secureTextEntry />

        <Button title="Submit Application" variant="secondary" onPress={handleRegister} style={styles.button} />

        <TouchableOpacity onPress={() => navigation.navigate('OrganizerLogin')} style={styles.linkContainer}>
          <Text style={styles.linkText}>Already registered? <Text style={styles.linkHighlight}>Log In</Text></Text>
        </TouchableOpacity>
      </Card>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flexGrow: 1, backgroundColor: Colors.background, padding: Spacing.lg, justifyContent: 'center' },
  title: { fontSize: Typography.fontSize.xxl, fontWeight: Typography.fontWeight.bold, color: Colors.secondary },
  subtitle: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginBottom: Spacing.lg },
  card: { padding: Spacing.lg },
  button: { marginTop: Spacing.md },
  linkContainer: { marginTop: Spacing.md, alignItems: 'center' },
  linkText: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary },
  linkHighlight: { color: Colors.secondary, fontWeight: Typography.fontWeight.bold },
});
