import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { BookingStatus } from '../types';
import { Colors, Typography, Spacing } from '../theme';

interface StatusTimelineProps {
  currentStatus: BookingStatus;
}

const TIMELINE_STEPS: { status: BookingStatus; label: string }[] = [
  { status: 'Confirmed', label: 'Booking confirmed' },
  { status: 'Assigned', label: 'Organizer assigned' },
  { status: 'OnTheWay', label: 'On the way' },
  { status: 'Arrived', label: 'Arrived' },
  { status: 'InProgress', label: 'Service started' },
  { status: 'Completed', label: 'Completed' },
  { status: 'CustomerConfirmed', label: 'Customer confirmed' },
];

export const StatusTimeline: React.FC<StatusTimelineProps> = ({ currentStatus }) => {
  const getStepIndex = (status: BookingStatus) => {
    switch (status) {
      case 'Pending':
      case 'Confirmed': return 0;
      case 'Assigned': return 1;
      case 'OnTheWay': return 2;
      case 'Arrived': return 3;
      case 'InProgress': return 4;
      case 'Completed': return 5;
      case 'CustomerConfirmed': return 6;
      default: return -1;
    }
  };

  const activeIndex = getStepIndex(currentStatus);

  if (currentStatus === 'Cancelled') {
    return (
      <View style={styles.cancelledBadge}>
        <Text style={styles.cancelledText}>Booking Cancelled</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {TIMELINE_STEPS.map((step, index) => {
        const isPassed = index < activeIndex;
        const isCurrent = index === activeIndex;

        return (
          <View key={step.status} style={styles.stepRow}>
            <View style={styles.leftCol}>
              <View
                style={[
                  styles.dot,
                  isPassed ? styles.passedDot : isCurrent ? styles.currentDot : styles.futureDot,
                ]}
              >
                {isPassed && <Text style={styles.checkMark}>✓</Text>}
                {isCurrent && <View style={styles.innerDot} />}
              </View>
              {index < TIMELINE_STEPS.length - 1 && (
                <View style={[styles.line, isPassed ? styles.passedLine : styles.futureLine]} />
              )}
            </View>

            <View style={styles.rightCol}>
              <Text
                style={[
                  styles.label,
                  isCurrent ? styles.currentLabel : isPassed ? styles.passedLabel : styles.futureLabel,
                ]}
              >
                {step.label}
              </Text>
            </View>
          </View>
        );
      })}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingVertical: Spacing.sm,
  },
  stepRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    minHeight: 32,
  },
  leftCol: {
    alignItems: 'center',
    width: 28,
  },
  dot: {
    width: 20,
    height: 20,
    borderRadius: 10,
    alignItems: 'center',
    justifyContent: 'center',
  },
  passedDot: {
    backgroundColor: Colors.success,
  },
  currentDot: {
    backgroundColor: Colors.primary,
    borderWidth: 2,
    borderColor: Colors.primaryLight,
  },
  futureDot: {
    backgroundColor: Colors.surface,
    borderWidth: 2,
    borderColor: Colors.border,
  },
  innerDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: Colors.surface,
  },
  checkMark: {
    color: Colors.surface,
    fontSize: 10,
    fontWeight: Typography.fontWeight.bold,
  },
  line: {
    width: 2,
    flex: 1,
    marginVertical: 2,
  },
  passedLine: {
    backgroundColor: Colors.success,
  },
  futureLine: {
    backgroundColor: Colors.border,
  },
  rightCol: {
    flex: 1,
    paddingLeft: Spacing.xs,
    justifyContent: 'center',
  },
  label: {
    fontSize: Typography.fontSize.sm,
  },
  passedLabel: {
    color: Colors.textPrimary,
    fontWeight: Typography.fontWeight.medium,
  },
  currentLabel: {
    color: Colors.primary,
    fontWeight: Typography.fontWeight.bold,
  },
  futureLabel: {
    color: Colors.textMuted,
  },
  cancelledBadge: {
    backgroundColor: Colors.error + '1A',
    padding: Spacing.sm,
    borderRadius: 8,
    alignItems: 'center',
  },
  cancelledText: {
    color: Colors.error,
    fontWeight: Typography.fontWeight.bold,
  },
});
