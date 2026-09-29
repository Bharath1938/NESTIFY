import React from 'react';
import { View, Text, Image, StyleSheet, TouchableOpacity } from 'react-native';
import { Colors, Typography, Spacing, Shadows } from '../theme';
import { ServicePackage } from '../types';

interface PackageCardProps {
  packageData: ServicePackage;
  serviceTitle: string;
  rating?: number;
  reviewCount?: number;
  onPressAdd: () => void;
  onPressDetails?: () => void;
}

export const PackageCard: React.FC<PackageCardProps> = ({
  packageData,
  serviceTitle,
  rating = 4.8,
  reviewCount = 120,
  onPressAdd,
  onPressDetails,
}) => {
  return (
    <TouchableOpacity style={styles.card} onPress={onPressDetails} activeOpacity={0.9}>
      {packageData.imageUrl ? (
        <Image source={{ uri: packageData.imageUrl }} style={styles.image} resizeMode="cover" />
      ) : null}

      <View style={styles.content}>
        <View style={styles.headerRow}>
          <Text style={styles.serviceTitle}>{serviceTitle}</Text>
          <View style={styles.ratingBadge}>
            <Text style={styles.ratingText}>⭐ {rating}</Text>
            <Text style={styles.reviewCount}>({reviewCount})</Text>
          </View>
        </View>

        <Text style={styles.packageName}>{packageData.packageName}</Text>
        <Text style={styles.description} numberOfLines={2}>
          {packageData.description}
        </Text>

        <View style={styles.footerRow}>
          <View>
            <Text style={styles.priceText}>
              Starts ₹{packageData.startingPrice}
            </Text>
            <Text style={styles.durationText}>⏱ {packageData.estimatedDurationHours} hours</Text>
          </View>

          <TouchableOpacity style={styles.addBtn} onPress={onPressAdd} activeOpacity={0.8}>
            <Text style={styles.addBtnText}>Add +</Text>
          </TouchableOpacity>
        </View>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: Colors.surface,
    borderRadius: 16,
    overflow: 'hidden',
    marginBottom: Spacing.md,
    borderWidth: 1,
    borderColor: Colors.border,
    ...Shadows.small,
  },
  image: {
    width: '100%',
    height: 140,
  },
  content: {
    padding: Spacing.md,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  serviceTitle: {
    fontSize: Typography.fontSize.xs,
    fontWeight: Typography.fontWeight.bold,
    color: Colors.primary,
    textTransform: 'uppercase',
  },
  ratingBadge: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  ratingText: {
    fontSize: Typography.fontSize.xs,
    fontWeight: Typography.fontWeight.bold,
    color: Colors.textPrimary,
  },
  reviewCount: {
    fontSize: Typography.fontSize.xs,
    color: Colors.textMuted,
    marginLeft: 2,
  },
  packageName: {
    fontSize: Typography.fontSize.md,
    fontWeight: Typography.fontWeight.bold,
    color: Colors.textPrimary,
    marginTop: 2,
  },
  description: {
    fontSize: Typography.fontSize.sm,
    color: Colors.textSecondary,
    marginVertical: Spacing.xs,
  },
  footerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: Spacing.xs,
    paddingTop: Spacing.xs,
    borderTopWidth: 1,
    borderColor: Colors.border,
  },
  priceText: {
    fontSize: Typography.fontSize.md,
    fontWeight: Typography.fontWeight.bold,
    color: Colors.textPrimary,
  },
  durationText: {
    fontSize: Typography.fontSize.xs,
    color: Colors.textMuted,
  },
  addBtn: {
    backgroundColor: Colors.primaryLight,
    borderWidth: 1,
    borderColor: Colors.primary,
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.xs,
    borderRadius: 8,
  },
  addBtnText: {
    color: Colors.primary,
    fontWeight: Typography.fontWeight.bold,
    fontSize: Typography.fontSize.sm,
  },
});
