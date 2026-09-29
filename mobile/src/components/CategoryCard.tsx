import React from 'react';
import { View, Text, Image, StyleSheet, TouchableOpacity } from 'react-native';
import { Colors, Typography, Spacing, Shadows } from '../theme';
import { ServiceCategory } from '../types';

interface CategoryCardProps {
  category: ServiceCategory;
  onPress: () => void;
}

export const CategoryCard: React.FC<CategoryCardProps> = ({ category, onPress }) => {
  return (
    <TouchableOpacity style={styles.card} onPress={onPress} activeOpacity={0.85}>
      <Image source={{ uri: category.imageUrl }} style={styles.image} resizeMode="cover" />
      <View style={styles.overlay} />
      <View style={styles.textContainer}>
        <Text style={styles.title}>{category.name}</Text>
        <Text style={styles.description} numberOfLines={1}>
          {category.description}
        </Text>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  card: {
    width: 140,
    height: 160,
    borderRadius: 16,
    overflow: 'hidden',
    backgroundColor: Colors.surface,
    marginRight: Spacing.sm,
    ...Shadows.small,
  },
  image: {
    width: '100%',
    height: '100%',
  },
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.32)',
  },
  textContainer: {
    position: 'absolute',
    bottom: Spacing.sm,
    left: Spacing.sm,
    right: Spacing.sm,
  },
  title: {
    color: Colors.surface,
    fontSize: Typography.fontSize.sm,
    fontWeight: Typography.fontWeight.bold,
  },
  description: {
    color: 'rgba(255,255,255,0.85)',
    fontSize: 10,
    marginTop: 2,
  },
});
