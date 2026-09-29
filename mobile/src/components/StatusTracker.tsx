import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { BookingStatus } from '../types';
import { Colors, Typography, Spacing } from '../theme';

interface StatusTrackerProps {
  currentStatus: BookingStatus;
}

const STEPS: { status: BookingStatus; label: string }[] = [
  { status: 'Pending', label: 'Pending' },
  { status: 'Confirmed', label: 'Confirmed' },
  { status: 'InProgress', label: 'In Progress' },
  { status: 'Completed', label: 'Completed' },
];

export const StatusTracker: React.FC<StatusTrackerProps> = ({ currentStatus }) => {
  const getStepIndex = (status: BookingStatus) => {
    switch (status) {
      case 'Pending': return 0;
      case 'Confirmed': return 1;
      case 'InProgress': return 2;
      case 'Completed': return 3;
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
      {STEPS.map((step, index) => {
        const isCompleted = index <= activeIndex;
        const isCurrent = index === activeIndex;

        return (
          <React.Fragment key={step.status}>
            <View style={styles.stepContainer}>
              <View
                style={[
                  styles.circle,
                  isCompleted ? styles.completedCircle : styles.pendingCircle,
                  isCurrent ? styles.currentCircle : null,
                ]}
              >
                <Text style={[styles.circleText, isCompleted ? styles.completedText : null]}>
                  {index + 1}
                </Text>
              </View>
              <Text
                style={[
                  styles.label,
                  isCompleted ? styles.completedLabel : styles.pendingLabel,
                  isCurrent ? styles.currentLabel : null,
                ]}
              >
                {step.label}
              </Text>
            </View>
            {index < STEPS.length - 1 && (
              <View style={[styles.line, index < activeIndex ? styles.completedLine : styles.pendingLine]} />
            )}
          </React.Fragment>
        );
      })}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: Spacing.md,
  },
  stepContainer: {
    alignItems: 'center',
    flex: 1,
  },
  circle: {
    width: 28,
    height: 28,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: Spacing.xs,
  },
  pendingCircle: {
    backgroundColor: Colors.border,
  },
  completedCircle: {
    backgroundColor: Colors.primary,
  },
  currentCircle: {
    borderWidth: 2,
    borderColor: Colors.secondary,
  },
  circleText: {
    fontSize: Typography.fontSize.xs,
    fontWeight: Typography.fontWeight.bold,
    color: Colors.textMuted,
  },
  completedText: {
    color: Colors.surface,
  },
  label: {
    fontSize: 10,
    textAlign: 'center',
  },
  pendingLabel: {
    color: Colors.textMuted,
  },
  completedLabel: {
    color: Colors.textPrimary,
    fontWeight: Typography.fontWeight.semibold,
  },
  currentLabel: {
    color: Colors.primary,
    fontWeight: Typography.fontWeight.bold,
  },
  line: {
    height: 2,
    flex: 1,
    marginTop: -16,
  },
  pendingLine: {
    backgroundColor: Colors.border,
  },
  completedLine: {
    backgroundColor: Colors.primary,
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
    fontSize: Typography.fontSize.sm,
  },
});
