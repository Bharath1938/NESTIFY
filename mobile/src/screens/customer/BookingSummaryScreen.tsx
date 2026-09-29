import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { Card, Button } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

export const BookingSummaryScreen = ({ route, navigation }: any) => {
  const { cartItem, address, schedule, organizerPreference } = route.params || {};
  const [loading, setLoading] = useState(false);
  const [isConfirmed, setIsConfirmed] = useState(false);

  const total = cartItem?.total || 1498;

  const handleConfirmAndPay = () => {
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      setIsConfirmed(true);
    }, 1500);
  };

  if (isConfirmed) {
    return (
      <View style={styles.successContainer}>
        <Text style={styles.successEmoji}>🎉</Text>
        <Text style={styles.successTitle}>Your organization is booked!</Text>
        <Text style={styles.successSub}>
          {cartItem?.package?.packageName || 'Wardrobe Organization'}
        </Text>
        <Text style={styles.successDate}>
          {schedule?.date || 'Saturday, 27 Sep'} · {schedule?.time || '10:00 AM'}
        </Text>

        <Card style={styles.organizerMatchCard}>
          <Text style={styles.matchedTitle}>Assigned Organizer</Text>
          <Text style={styles.organizerName}>Priya Kumar ⭐ 4.9</Text>
          <Text style={styles.organizerSub}>We'll notify you when your organizer is on the way.</Text>
        </Card>

        <Button
          title="View Booking Details & Live Status"
          onPress={() => navigation.navigate('CustomerBookingsTab', { screen: 'BookingDetail', params: { bookingId: 'b_new' } })}
          style={styles.viewBookingBtn}
        />
        <Button
          title="Back to Home"
          variant="outline"
          onPress={() => navigation.navigate('CustomerHomeTab')}
          style={styles.homeBtn}
        />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity style={styles.backBtn} onPress={() => navigation.goBack()}>
          <Text style={styles.backText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Your Booking Summary</Text>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        <Card style={styles.card}>
          <Text style={styles.sectionTitle}>Selected Package</Text>
          <Text style={styles.pkgTitle}>{cartItem?.package?.packageName || 'Standard Wardrobe Organization'}</Text>
          <Text style={styles.pkgSub}>{cartItem?.package?.description}</Text>
        </Card>

        <Card style={styles.card}>
          <Text style={styles.sectionTitle}>Service Address</Text>
          <Text style={styles.addressLabel}>{address?.label || 'Home'} - {address?.houseNumberAndStreet || '12 Example Street, Anna Nagar'}</Text>
          <Text style={styles.addressCity}>{address?.city || 'Chennai'} - {address?.zipCode || '600040'}</Text>
        </Card>

        <Card style={styles.card}>
          <Text style={styles.sectionTitle}>Date & Scheduled Time</Text>
          <Text style={styles.scheduleText}>📅 {schedule?.date || 'Saturday, 27 Sep'}</Text>
          <Text style={styles.scheduleText}>⏰ {schedule?.time || '10:00 AM'} (~{cartItem?.package?.estimatedDurationHours || 3} hours)</Text>
        </Card>

        <Card style={styles.card}>
          <Text style={styles.sectionTitle}>Payment Breakdown</Text>
          <View style={styles.row}>
            <Text style={styles.label}>Subtotal</Text>
            <Text style={styles.value}>₹{cartItem?.subtotal || 1598}</Text>
          </View>
          <View style={styles.row}>
            <Text style={styles.label}>Service Fee</Text>
            <Text style={styles.value}>₹{cartItem?.serviceFee || 100}</Text>
          </View>
          {cartItem?.discountAmount > 0 && (
            <View style={styles.row}>
              <Text style={styles.discountLabel}>Discount Promo</Text>
              <Text style={styles.discountValue}>- ₹{cartItem?.discountAmount}</Text>
            </View>
          )}
          <View style={styles.divider} />
          <View style={styles.row}>
            <Text style={styles.totalLabel}>Total Payable</Text>
            <Text style={styles.totalValue}>₹{total}</Text>
          </View>
        </Card>
      </ScrollView>

      <View style={styles.bottomBar}>
        <Button
          title={loading ? 'Processing Payment...' : `Confirm & Pay ₹${total}`}
          loading={loading}
          onPress={handleConfirmAndPay}
          style={styles.payBtn}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.md, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  backBtn: { marginBottom: Spacing.xs },
  backText: { color: Colors.primary, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.sm },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  content: { padding: Spacing.md },
  card: { padding: Spacing.md, marginBottom: Spacing.sm },
  sectionTitle: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.primary, textTransform: 'uppercase', marginBottom: 4 },
  pkgTitle: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  pkgSub: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  addressLabel: { fontSize: Typography.fontSize.sm, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  addressCity: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginTop: 2 },
  scheduleText: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary, marginVertical: 2 },
  row: { flexDirection: 'row', justifyContent: 'space-between', marginVertical: 2 },
  label: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary },
  value: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary, fontWeight: Typography.fontWeight.medium },
  discountLabel: { fontSize: Typography.fontSize.sm, color: Colors.success },
  discountValue: { fontSize: Typography.fontSize.sm, color: Colors.success, fontWeight: Typography.fontWeight.bold },
  divider: { height: 1, backgroundColor: Colors.border, marginVertical: Spacing.xs },
  totalLabel: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  totalValue: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  bottomBar: { backgroundColor: Colors.surface, padding: Spacing.md, borderTopWidth: 1, borderColor: Colors.border },
  payBtn: { width: '100%' },
  successContainer: { flex: 1, backgroundColor: Colors.background, padding: Spacing.lg, justifyContent: 'center', alignItems: 'center' },
  successEmoji: { fontSize: 64, marginBottom: Spacing.sm },
  successTitle: { fontSize: Typography.fontSize.xxl, fontWeight: Typography.fontWeight.bold, color: Colors.primary, textAlign: 'center' },
  successSub: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginTop: Spacing.xs },
  successDate: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginTop: 2 },
  organizerMatchCard: { width: '100%', marginVertical: Spacing.lg, padding: Spacing.md, alignItems: 'center', backgroundColor: Colors.primaryLight },
  matchedTitle: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  organizerName: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginTop: 2 },
  organizerSub: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginTop: 2, textAlign: 'center' },
  viewBookingBtn: { width: '100%', marginBottom: Spacing.sm },
  homeBtn: { width: '100%' },
});
