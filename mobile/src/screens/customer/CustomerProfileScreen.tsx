import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Card, Button } from '../../components';
import { useAuth } from '../../navigation/RootNavigator';
import { Colors, Typography, Spacing } from '../../theme';

export const CustomerProfileScreen = () => {
  const { user, logout } = useAuth();

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Account Profile</Text>
      </View>

      <View style={styles.content}>
        <Card style={styles.profileCard}>
          <View style={styles.avatarCircle}>
            <Text style={styles.avatarText}>{user?.fullName?.charAt(0) || 'C'}</Text>
          </View>
          <Text style={styles.userName}>{user?.fullName || 'Customer'}</Text>
          <Text style={styles.userEmail}>{user?.email || 'customer@example.com'}</Text>
          <Text style={styles.userPhone}>{user?.phone || '+1 234 567 8900'}</Text>
        </Card>

        <Card style={styles.settingsCard}>
          <Text style={styles.sectionHeader}>Default Saved Address</Text>
          <Text style={styles.addressText}>742 Evergreen Terrace, Springfield, OR 97477</Text>
        </Card>

        <Button title="Log Out" variant="outline" onPress={logout} style={styles.logoutButton} />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.lg, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  content: { padding: Spacing.md },
  profileCard: { alignItems: 'center', padding: Spacing.lg },
  avatarCircle: { width: 72, height: 72, borderRadius: 36, backgroundColor: Colors.primaryLight, alignItems: 'center', justifyContent: 'center', marginBottom: Spacing.sm },
  avatarText: { fontSize: Typography.fontSize.xxl, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  userName: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  userEmail: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginTop: 2 },
  userPhone: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginTop: 2 },
  settingsCard: { marginTop: Spacing.md, padding: Spacing.md },
  sectionHeader: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.primary, marginBottom: 4 },
  addressText: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary },
  logoutButton: { marginTop: Spacing.xl },
});
