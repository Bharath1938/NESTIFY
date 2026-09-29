import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Button, Card } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

export const WelcomeRoleSelectionScreen = ({ navigation }: any) => {
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.logo}>Nestify</Text>
        <Text style={styles.tagline}>Home Organizing & Space Optimization</Text>
      </View>

      <View style={styles.content}>
        <Card style={styles.roleCard}>
          <Text style={styles.roleTitle}>Looking for Organizing Services?</Text>
          <Text style={styles.roleSubtitle}>Transform your living space with professional organizers.</Text>
          <Button
            title="Continue as Customer"
            onPress={() => navigation.navigate('CustomerLogin')}
            style={styles.button}
          />
        </Card>

        <Card style={styles.roleCard}>
          <Text style={styles.roleTitle}>Are you a Professional Organizer?</Text>
          <Text style={styles.roleSubtitle}>Connect with clients, manage bookings, and earn seamlessly.</Text>
          <Button
            title="Continue as Organizer"
            variant="secondary"
            onPress={() => navigation.navigate('OrganizerLogin')}
            style={styles.button}
          />
        </Card>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
    padding: Spacing.lg,
    justifyContent: 'space-between',
  },
  header: {
    marginTop: Spacing.xxl,
    alignItems: 'center',
  },
  logo: {
    fontSize: 36,
    fontWeight: Typography.fontWeight.bold,
    color: Colors.primary,
  },
  tagline: {
    fontSize: Typography.fontSize.sm,
    color: Colors.textSecondary,
    marginTop: Spacing.xs,
  },
  content: {
    marginBottom: Spacing.xl,
  },
  roleCard: {
    marginBottom: Spacing.lg,
    padding: Spacing.lg,
  },
  roleTitle: {
    fontSize: Typography.fontSize.lg,
    fontWeight: Typography.fontWeight.bold,
    color: Colors.textPrimary,
  },
  roleSubtitle: {
    fontSize: Typography.fontSize.sm,
    color: Colors.textSecondary,
    marginVertical: Spacing.sm,
  },
  button: {
    marginTop: Spacing.sm,
  },
});
