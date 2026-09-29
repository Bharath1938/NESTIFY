import React from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';
import { Card, StatusTracker, BeforeAfterSlider } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';
import { Booking } from '../../types';

const MOCK_BOOKINGS: Booking[] = [
  {
    id: 'b1',
    customerId: '1',
    serviceId: 's1',
    status: 'InProgress',
    scheduledAt: '2025-03-20 10:00 AM',
    address: '742 Evergreen Terrace, Springfield',
    latitude: 37.7749,
    longitude: -122.4194,
    totalAmount: 120,
    createdAt: '2025-03-18',
    service: {
      id: 's1',
      title: 'Closet & Wardrobe Organization',
      description: 'Declutter and organize closets',
      category: 'Bedroom',
      price: 120,
      estimatedHours: 3,
      imageUrl: '',
    },
    photos: [
      {
        id: 'p1',
        bookingId: 'b1',
        beforePhotoUrl: 'https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80',
        afterPhotoUrl: 'https://images.unsplash.com/photo-1595428774223-ef52624120d2?auto=format&fit=crop&w=600&q=80',
        uploadedAt: '2025-03-20',
      },
    ],
  },
  {
    id: 'b2',
    customerId: '1',
    serviceId: 's2',
    status: 'Completed',
    scheduledAt: '2025-03-15 02:00 PM',
    address: '742 Evergreen Terrace, Springfield',
    latitude: 37.7749,
    longitude: -122.4194,
    totalAmount: 150,
    createdAt: '2025-03-12',
    service: {
      id: 's2',
      title: 'Kitchen & Pantry Optimization',
      description: 'Labeling & arranging pantry',
      category: 'Kitchen',
      price: 150,
      estimatedHours: 4,
      imageUrl: '',
    },
  },
];

export const CustomerBookingsListScreen = () => {
  const renderBookingCard = ({ item }: { item: Booking }) => (
    <Card style={styles.card}>
      <Text style={styles.serviceTitle}>{item.service?.title || 'Home Organizing Service'}</Text>
      <Text style={styles.scheduledAt}>Scheduled: {item.scheduledAt}</Text>
      <Text style={styles.address}>📍 {item.address}</Text>

      <StatusTracker currentStatus={item.status} />

      {item.photos && item.photos.length > 0 && (
        <View style={styles.photoContainer}>
          <Text style={styles.photoHeader}>Transformation Photos:</Text>
          <BeforeAfterSlider
            beforeImageUrl={item.photos[0].beforePhotoUrl}
            afterImageUrl={item.photos[0].afterPhotoUrl}
          />
        </View>
      )}

      <View style={styles.footer}>
        <Text style={styles.amount}>Total: ${item.totalAmount}</Text>
      </View>
    </Card>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>My Bookings</Text>
      </View>

      <FlatList
        data={MOCK_BOOKINGS}
        keyExtractor={(item) => item.id}
        renderItem={renderBookingCard}
        contentContainerStyle={styles.list}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.lg, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  list: { padding: Spacing.md },
  card: { padding: Spacing.md, marginBottom: Spacing.md },
  serviceTitle: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  scheduledAt: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  address: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginVertical: Spacing.xs },
  photoContainer: { marginTop: Spacing.md },
  photoHeader: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginBottom: Spacing.xs },
  footer: { borderTopWidth: 1, borderColor: Colors.border, paddingTop: Spacing.xs, marginTop: Spacing.sm, alignItems: 'flex-end' },
  amount: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
});
