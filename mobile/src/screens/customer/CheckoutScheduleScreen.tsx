import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Card, Button } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

const DATES = [
  { day: 'Fri', date: '26', full: 'Friday, 26 Sep' },
  { day: 'Sat', date: '27', full: 'Saturday, 27 Sep' },
  { day: 'Sun', date: '28', full: 'Sunday, 28 Sep' },
  { day: 'Mon', date: '29', full: 'Monday, 29 Sep' },
];

const TIME_SLOTS = [
  '09:00 AM',
  '10:00 AM',
  '11:00 AM',
  '12:00 PM',
  '02:00 PM',
  '03:00 PM',
  '04:00 PM',
];

export const CheckoutScheduleScreen = ({ route, navigation }: any) => {
  const { cartItem, address } = route.params || {};
  const [selectedDate, setSelectedDate] = useState(DATES[1].full);
  const [selectedTime, setSelectedTime] = useState('10:00 AM');
  const [organizerPreference, setOrganizerPreference] = useState('Best Available (⭐ 4.9)');

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity style={styles.backBtn} onPress={() => navigation.goBack()}>
          <Text style={styles.backText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>When should we come?</Text>
        <Text style={styles.durationBadge}>⏱ Estimated Duration: ~{cartItem?.package?.estimatedDurationHours || 3} hours</Text>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        {/* Date Selector */}
        <Text style={styles.sectionTitle}>Select Date</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.datesRow}>
          {DATES.map((item) => {
            const isSelected = item.full === selectedDate;
            return (
              <TouchableOpacity
                key={item.date}
                style={[styles.dateCard, isSelected ? styles.selectedDateCard : null]}
                onPress={() => setSelectedDate(item.full)}
              >
                <Text style={[styles.dayText, isSelected ? styles.selectedDateText : null]}>{item.day}</Text>
                <Text style={[styles.dateNum, isSelected ? styles.selectedDateText : null]}>{item.date}</Text>
              </TouchableOpacity>
            );
          })}
        </ScrollView>

        {/* Time Slot Grid */}
        <Text style={styles.sectionTitle}>Select Start Time</Text>
        <View style={styles.timeGrid}>
          {TIME_SLOTS.map((slot) => {
            const isSelected = slot === selectedTime;
            return (
              <TouchableOpacity
                key={slot}
                style={[styles.timeSlot, isSelected ? styles.selectedTimeSlot : null]}
                onPress={() => setSelectedTime(slot)}
              >
                <Text style={[styles.slotText, isSelected ? styles.selectedSlotText : null]}>{slot}</Text>
              </TouchableOpacity>
            );
          })}
        </View>

        {/* Professional Assignment */}
        <Card style={styles.organizerCard}>
          <Text style={styles.organizerHeader}>Professional Organizer Assignment</Text>
          <Text style={styles.organizerSub}>Nestify automatically matches you with the best available skill-verified organizer in Chennai Central.</Text>

          <View style={styles.prefOption}>
            <Text style={styles.prefText}>Matching Preference: <Text style={styles.prefBold}>{organizerPreference}</Text></Text>
          </View>
        </Card>
      </ScrollView>

      <View style={styles.bottomBar}>
        <Button
          title="Review Booking Summary"
          onPress={() =>
            navigation.navigate('BookingSummary', {
              cartItem,
              address,
              schedule: { date: selectedDate, time: selectedTime },
              organizerPreference,
            })
          }
          style={styles.continueBtn}
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
  durationBadge: { fontSize: Typography.fontSize.xs, color: Colors.primary, fontWeight: Typography.fontWeight.bold, marginTop: 2 },
  content: { padding: Spacing.md },
  sectionTitle: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginVertical: Spacing.xs },
  datesRow: { flexDirection: 'row', marginBottom: Spacing.md },
  dateCard: { width: 70, height: 70, borderRadius: 12, backgroundColor: Colors.surface, borderWidth: 1, borderColor: Colors.border, alignItems: 'center', justifyContent: 'center', marginRight: Spacing.xs },
  selectedDateCard: { backgroundColor: Colors.primary, borderColor: Colors.primary },
  dayText: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary },
  dateNum: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  selectedDateText: { color: Colors.surface },
  timeGrid: { flexDirection: 'row', flexWrap: 'wrap', marginBottom: Spacing.md },
  timeSlot: { width: '31%', paddingVertical: Spacing.sm, borderRadius: 8, backgroundColor: Colors.surface, borderWidth: 1, borderColor: Colors.border, alignItems: 'center', margin: '1%', marginBottom: Spacing.xs },
  selectedTimeSlot: { backgroundColor: Colors.primary, borderColor: Colors.primary },
  slotText: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  selectedSlotText: { color: Colors.surface },
  organizerCard: { padding: Spacing.md, backgroundColor: Colors.primaryLight },
  organizerHeader: { fontSize: Typography.fontSize.sm, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  organizerSub: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  prefOption: { marginTop: Spacing.sm, borderTopWidth: 1, borderColor: Colors.border, paddingTop: Spacing.xs },
  prefText: { fontSize: Typography.fontSize.xs, color: Colors.textPrimary },
  prefBold: { fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  bottomBar: { backgroundColor: Colors.surface, padding: Spacing.md, borderTopWidth: 1, borderColor: Colors.border },
  continueBtn: { width: '100%' },
});
