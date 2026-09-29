import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Card, Button } from '../../components';
import { useAuth } from '../../navigation/RootNavigator';
import { Colors, Typography, Spacing } from '../../theme';

export const OrganizerDashboardScreen = ({ navigation }: any) => {
  const { user } = useAuth();

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.greeting}>Good morning, {user?.fullName || 'Priya'} 👋</Text>
        <Text style={styles.subGreeting}>Professional Organizer Portal • Chennai Central</Text>
      </View>

      <View style={styles.metricsContainer}>
        <Card style={styles.metricCard}>
          <Text style={styles.metricValue}>₹1,850</Text>
          <Text style={styles.metricLabel}>Today's Earnings</Text>
        </Card>
        <Card style={styles.metricCard}>
          <Text style={styles.metricValue}>3</Text>
          <Text style={styles.metricLabel}>Today's Jobs</Text>
        </Card>
        <Card style={styles.metricCard}>
          <Text style={styles.metricValue}>4.9 ★</Text>
          <Text style={styles.metricLabel}>Rating</Text>
        </Card>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Upcoming Job Today</Text>
        <Card style={styles.jobCard}>
          <View style={styles.timeBadge}>
            <Text style={styles.timeBadgeText}>10:00 AM TODAY</Text>
          </View>
          <Text style={styles.serviceName}>Wardrobe Organization (Standard)</Text>
          <Text style={styles.clientName}>Client: Jane Customer</Text>
          <Text style={styles.address}>📍 12 Example Street, Anna Nagar (4.2 km)</Text>
          <Text style={styles.earnings}>Net Payout: ₹899.00</Text>

          <Button
            title="View Job & Start Workflow"
            onPress={() => navigation.navigate('OrganizerJobsTab', { screen: 'OrganizerJobDetail', params: { jobId: 'j1' } })}
            style={styles.viewJobBtn}
          />
        </Card>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.lg, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  greeting: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  subGreeting: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  metricsContainer: { flexDirection: 'row', justifyContent: 'space-between', padding: Spacing.md },
  metricCard: { flex: 1, alignItems: 'center', marginHorizontal: 3, padding: Spacing.sm },
  metricValue: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  metricLabel: { fontSize: 10, color: Colors.textMuted, marginTop: 2 },
  section: { padding: Spacing.md },
  sectionTitle: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginBottom: Spacing.xs },
  jobCard: { padding: Spacing.md },
  timeBadge: { backgroundColor: Colors.primaryLight, paddingHorizontal: Spacing.sm, paddingVertical: 2, borderRadius: 6, selfAlign: 'flex-start', marginBottom: Spacing.xs },
  timeBadgeText: { color: Colors.primary, fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold },
  serviceName: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  clientName: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginTop: 2 },
  address: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginVertical: Spacing.xs },
  earnings: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.success, marginBottom: Spacing.md },
  viewJobBtn: { width: '100%' },
});
