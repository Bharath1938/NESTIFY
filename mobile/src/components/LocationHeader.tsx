import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Colors, Typography, Spacing } from '../theme';

interface LocationHeaderProps {
  currentCity?: string;
  addressLabel?: string;
  onPressChange?: () => void;
}

export const LocationHeader: React.FC<LocationHeaderProps> = ({
  currentCity = 'Chennai',
  addressLabel = 'Home - 12 Example Street',
  onPressChange,
}) => {
  return (
    <View style={styles.container}>
      <View style={styles.leftRow}>
        <Text style={styles.pinIcon}>📍</Text>
        <View style={styles.textContainer}>
          <Text style={styles.label}>Delivering/Serving at</Text>
          <Text style={styles.addressText} numberOfLines={1}>
            {currentCity} • <Text style={styles.streetText}>{addressLabel}</Text>
          </Text>
        </View>
      </View>
      <TouchableOpacity style={styles.changeBtn} onPress={onPressChange}>
        <Text style={styles.changeText}>Change</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: Colors.surface,
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    borderBottomWidth: 1,
    borderColor: Colors.border,
  },
  leftRow: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  pinIcon: {
    fontSize: 20,
    marginRight: Spacing.xs,
  },
  textContainer: {
    flex: 1,
  },
  label: {
    fontSize: Typography.fontSize.xs,
    color: Colors.textMuted,
    fontWeight: Typography.fontWeight.medium,
  },
  addressText: {
    fontSize: Typography.fontSize.sm,
    fontWeight: Typography.fontWeight.bold,
    color: Colors.textPrimary,
  },
  streetText: {
    fontWeight: Typography.fontWeight.regular,
    color: Colors.textSecondary,
  },
  changeBtn: {
    backgroundColor: Colors.primaryLight,
    paddingHorizontal: Spacing.sm,
    paddingVertical: Spacing.xs,
    borderRadius: 8,
    marginLeft: Spacing.xs,
  },
  changeText: {
    color: Colors.primary,
    fontSize: Typography.fontSize.xs,
    fontWeight: Typography.fontWeight.bold,
  },
});
