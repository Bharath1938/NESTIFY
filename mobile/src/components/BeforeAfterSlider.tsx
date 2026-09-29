import React, { useState } from 'react';
import { View, Image, StyleSheet, PanResponder, Text, Dimensions } from 'react-native';
import { Colors, Typography, Spacing, Shadows } from '../theme';

interface BeforeAfterSliderProps {
  beforeImageUrl: string;
  afterImageUrl: string;
  height?: number;
}

export const BeforeAfterSlider: React.FC<BeforeAfterSliderProps> = ({
  beforeImageUrl,
  afterImageUrl,
  height = 240,
}) => {
  const [sliderPosition, setSliderPosition] = useState(0.5);
  const screenWidth = Dimensions.get('window').width - Spacing.lg * 2;

  const panResponder = PanResponder.create({
    onStartShouldSetPanResponder: () => true,
    onPanResponderMove: (evt, gestureState) => {
      const newPos = gestureState.moveX / screenWidth;
      setSliderPosition(Math.max(0.05, Math.min(0.95, newPos)));
    },
  });

  return (
    <View style={[styles.container, { height }]}>
      {/* After Image (Background) */}
      <Image source={{ uri: afterImageUrl }} style={styles.image} resizeMode="cover" />
      <View style={[styles.badge, styles.afterBadge]}>
        <Text style={styles.badgeText}>AFTER</Text>
      </View>

      {/* Before Image (Clipped Overlay) */}
      <View style={[styles.beforeImageContainer, { width: `${sliderPosition * 100}%` }]}>
        <Image
          source={{ uri: beforeImageUrl }}
          style={[styles.image, { width: screenWidth }]}
          resizeMode="cover"
        />
        <View style={[styles.badge, styles.beforeBadge]}>
          <Text style={styles.badgeText}>BEFORE</Text>
        </View>
      </View>

      {/* Slider Line & Divider Handle */}
      <View
        style={[styles.handleContainer, { left: `${sliderPosition * 100}%` }]}
        {...panResponder.panHandlers}
      >
        <View style={styles.verticalLine} />
        <View style={styles.handleCircle}>
          <Text style={styles.handleText}>↔</Text>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    borderRadius: 16,
    overflow: 'hidden',
    position: 'relative',
    backgroundColor: Colors.border,
    ...Shadows.medium,
  },
  image: {
    width: '100%',
    height: '100%',
  },
  beforeImageContainer: {
    position: 'absolute',
    left: 0,
    top: 0,
    bottom: 0,
    overflow: 'hidden',
  },
  badge: {
    position: 'absolute',
    top: Spacing.sm,
    paddingHorizontal: Spacing.sm,
    paddingVertical: 4,
    borderRadius: 6,
  },
  beforeBadge: {
    left: Spacing.sm,
    backgroundColor: 'rgba(0,0,0,0.65)',
  },
  afterBadge: {
    right: Spacing.sm,
    backgroundColor: Colors.primary,
  },
  badgeText: {
    color: Colors.surface,
    fontSize: Typography.fontSize.xs,
    fontWeight: Typography.fontWeight.bold,
  },
  handleContainer: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    alignItems: 'center',
    justifyContent: 'center',
    marginLeft: -16,
  },
  verticalLine: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    width: 2,
    backgroundColor: Colors.surface,
  },
  handleCircle: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: Colors.surface,
    alignItems: 'center',
    justifyContent: 'center',
    ...Shadows.large,
  },
  handleText: {
    color: Colors.primary,
    fontWeight: Typography.fontWeight.bold,
    fontSize: Typography.fontSize.md,
  },
});
