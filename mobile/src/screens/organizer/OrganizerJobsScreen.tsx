import React from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';
import { Card, Button } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

const MOCK_REQUESTS = [
  {
    id: 'j1',
    client: 'Jane Customer',
    service: 'Wardrobe Organization',
    package: 'Standard Wardrobe Organization',
    payout: 899,
    date: 'Today · 10:00 AM',
    distance: '4.2 km',
    address: '12 Example Street, Anna Nagar',
    notes: 'Please keep winter clothes separate in upper shelf.',
    durationHours: 3,
  },
  {
    id: 'j2',
    client: 'Robert Johnson',
    service: 'Kitchen Pantry Reset',
    package: 'Full Kitchen & Pantry Reset',
    payout: 1299,
    date: 'Tomorrow · 02:00 PM',
    distance: '6.5 km',
    address: '45 IT Park Road, OMR',
    notes: 'Please label all spice jars and grain containers.',
    durationHours: 4,
  },
];

export const OrganizerJobsScreen = ({ navigation }: any) => {
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Available Job Requests</Text>
      </View>

      <FlatList
        data={MOCK_REQUESTS}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.list}
        renderItem={({ item }) => (
          <Card style={styles.card}>
            <View style={styles.row}>
              <Text style={styles.service}>{item.service}</Text>
              <Text style={styles.payout}>₹{item.payout}</Text>
            </View>
            <Text style={styles.pkg}>{item.package}</Text>
            <Text style={styles.client}>Client: {item.client}</Text>
            <Text style={styles.date}>⏰ {item.date} (~{item.durationHours} hrs)</Text>
            <Text style={styles.location}>📍 {item.address} ({item.distance})</Text>

            {item.notes ? (
              <View style={styles.notesBox}>
                <Text style={styles.notesText}>Note: "{item.notes}"</Text>
              </View>
            ) : null}

            <View style={styles.actionRow}>
              <Button
                title="View Execution Steps"
                onPress={() => navigation.navigate('OrganizerJobDetail', { job: item })}
                style={styles.btn}
              />
            </View>
          </Card>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.md, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  list: { padding: Spacing.md },
  card: { padding: Spacing.md, marginBottom: Spacing.md },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  service: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  payout: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.success },
  pkg: { fontSize: Typography.fontSize.xs, color: Colors.primary, fontWeight: Typography.fontWeight.semibold, marginTop: 2 },
  client: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginTop: 4 },
  date: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginTop: 2 },
  location: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginTop: 2 },
  notesBox: { backgroundColor: Colors.primaryLight, padding: Spacing.xs, borderRadius: 6, marginTop: Spacing.xs },
  notesText: { fontSize: Typography.fontSize.xs, color: Colors.primary, fontStyle: 'italic' },
  actionRow: { marginTop: Spacing.md },
  btn: { width: '100%' },
});
