import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Card, Button } from '../../components';
import { useAuth } from '../../navigation/RootNavigator';
import { Colors, Typography, Spacing } from '../../theme';

export const OrganizerProfileScreen = () => {
  const { user, logout } = useAuth();

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Organizer Profile</Text>
      </View>

      <View style={styles.content}>
        <Card style={styles.profileCard}>
          <View style={styles.avatarCircle}>
            <Text style={styles.avatarText}>{user?.fullName?.charAt(0) || 'O'}</Text>
          </View>
          <Text style={styles.userName}>{user?.fullName || 'Alex Organizer'}</Text>
          <Text style={styles.userEmail}>{user?.email || 'organizer@example.com'}</Text>
          <Text style={styles.userPhone}>{user?.phone || '+1 987 654 3210'}</Text>
        </Card>

        <Card style={styles.statsCard}>
          <Text style={styles.sectionHeader}>Organizer Stats</Text>
          <Text style={styles.statText}>Completed Jobs: 48</Text>
          <Text style={styles.statText}>Client Satisfaction Rating: 4.9 / 5.0 ★</Text>
        </Card>

        <Button title="Log Out" variant="outline" onPress={logout} style={styles.logoutButton} />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.lg, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.secondary },
  content: { padding: Spacing.md },
  profileCard: { alignItems: 'center', padding: Spacing.lg },
  avatarCircle: { width: 72, height: 72, borderRadius: 36, backgroundColor: Colors.secondaryLight, alignItems: 'center', justifyContent: 'center', marginBottom: Spacing.sm },
  avatarText: { fontSize: Typography.fontSize.xxl, fontWeight: Typography.fontWeight.bold, color: Colors.secondary },
  userName: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  userEmail: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginTop: 2 },
  userPhone: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginTop: 2 },
  statsCard: { marginTop: Spacing.md, padding: Spacing.md },
  sectionHeader: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.secondary, marginBottom: 4 },
  statText: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary, marginVertical: 2 },
  logoutButton: { marginTop: Spacing.xl },
});
