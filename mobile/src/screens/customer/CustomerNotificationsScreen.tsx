import React from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';
import { Card } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

const MOCK_NOTIFICATIONS = [
  { id: 'n1', title: 'Organizer Assigned', body: 'Alex has accepted your booking for Closet Organization.', time: '10 mins ago' },
  { id: 'n2', title: 'Status Update', body: 'Your Kitchen Pantry Optimization is now In Progress.', time: '2 hours ago' },
  { id: 'n3', title: 'Booking Confirmed', body: 'Your booking scheduled for March 20 at 10:00 AM is confirmed.', time: '1 day ago' },
];

export const CustomerNotificationsScreen = () => {
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Notifications</Text>
      </View>

      <FlatList
        data={MOCK_NOTIFICATIONS}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.list}
        renderItem={({ item }) => (
          <Card style={styles.card}>
            <View style={styles.row}>
              <Text style={styles.cardTitle}>{item.title}</Text>
              <Text style={styles.time}>{item.time}</Text>
            </View>
            <Text style={styles.body}>{item.body}</Text>
          </Card>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.lg, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  list: { padding: Spacing.md },
  card: { padding: Spacing.md, marginBottom: Spacing.sm },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  cardTitle: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  time: { fontSize: Typography.fontSize.xs, color: Colors.textMuted },
  body: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginTop: Spacing.xs },
});
