import React from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';
import { Card } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

const MOCK_PAYOUTS = [
  { id: 'p1', date: 'Mar 15, 2025', service: 'Closet Organization', amount: 120, status: 'Paid' },
  { id: 'p2', date: 'Mar 10, 2025', service: 'Pantry Optimization', amount: 150, status: 'Paid' },
  { id: 'p3', date: 'Mar 05, 2025', service: 'Garage Reset', amount: 200, status: 'Paid' },
];

export const OrganizerEarningsScreen = () => {
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Earnings Overview</Text>
      </View>

      <View style={styles.summaryContainer}>
        <Card style={styles.summaryCard}>
          <Text style={styles.summaryLabel}>Total Earnings This Month</Text>
          <Text style={styles.summaryAmount}>$1,840.00</Text>
        </Card>
      </View>

      <FlatList
        data={MOCK_PAYOUTS}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.list}
        renderItem={({ item }) => (
          <Card style={styles.card}>
            <View style={styles.row}>
              <Text style={styles.service}>{item.service}</Text>
              <Text style={styles.amount}>+${item.amount}</Text>
            </View>
            <View style={styles.row}>
              <Text style={styles.date}>{item.date}</Text>
              <Text style={styles.status}>{item.status}</Text>
            </View>
          </Card>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.lg, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.secondary },
  summaryContainer: { padding: Spacing.md },
  summaryCard: { padding: Spacing.lg, alignItems: 'center', backgroundColor: Colors.secondaryLight },
  summaryLabel: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.secondary },
  summaryAmount: { fontSize: 32, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginTop: 4 },
  list: { paddingHorizontal: Spacing.md },
  card: { padding: Spacing.md, marginBottom: Spacing.xs },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginVertical: 2 },
  service: { fontSize: Typography.fontSize.sm, fontWeight: Typography.fontWeight.semibold, color: Colors.textPrimary },
  amount: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.secondary },
  date: { fontSize: Typography.fontSize.xs, color: Colors.textMuted },
  status: { fontSize: Typography.fontSize.xs, color: Colors.success, fontWeight: Typography.fontWeight.bold },
});
