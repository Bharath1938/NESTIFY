import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { PackageCard } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

export const CategoryServicesScreen = ({ route, navigation }: any) => {
  const category = route.params?.category || { name: 'Wardrobe', slug: 'wardrobe' };

  const PACKAGES = [
    {
      id: 'pkg_basic',
      serviceId: 's_wardrobe',
      packageName: 'Basic Wardrobe Reset',
      description: 'Quick sorting and folding for single closet or wardrobe unit.',
      startingPrice: 599,
      estimatedDurationHours: 2,
      includedDetails: ['Sorting clothes', 'Folding', 'Hanging arrangement'],
      excludedDetails: ['Shoe rack arrangement', 'Seasonal vacuum packing'],
      imageUrl: 'https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80',
    },
    {
      id: 'pkg_standard',
      serviceId: 's_wardrobe',
      packageName: 'Standard Wardrobe Organization',
      description: 'Comprehensive clothes sorting, drawer organization, and shoe rack setup.',
      startingPrice: 899,
      estimatedDurationHours: 3,
      includedDetails: ['Clothes sorting', 'Category arrangement', 'Folding & Hanging', 'Drawer organization', 'Shoe setup'],
      excludedDetails: ['Heavy furniture moving'],
      imageUrl: 'https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80',
    },
    {
      id: 'pkg_premium',
      serviceId: 's_wardrobe',
      packageName: 'Premium Wardrobe Transformation',
      description: 'Full master bedroom closet overhaul, label tagging, seasonal separation & accessory organization.',
      startingPrice: 1299,
      estimatedDurationHours: 5,
      includedDetails: ['Master closet overhaul', 'Label tagging', 'Seasonal separation', 'Accessory storage', 'Drawer inserts setup'],
      excludedDetails: [],
      imageUrl: 'https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80',
    },
  ];

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity style={styles.backBtn} onPress={() => navigation.goBack()}>
          <Text style={styles.backText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>{category.name} Organization</Text>
        <Text style={styles.ratingText}>⭐ 4.8 (210 reviews)</Text>
      </View>

      <ScrollView contentContainerStyle={styles.list} showsVerticalScrollIndicator={false}>
        <Text style={styles.sectionHeader}>Choose a package</Text>
        {PACKAGES.map((pkg) => (
          <PackageCard
            key={pkg.id}
            packageData={pkg}
            serviceTitle={category.name}
            rating={4.8}
            reviewCount={210}
            onPressAdd={() => navigation.navigate('CartTab', { package: pkg })}
            onPressDetails={() => navigation.navigate('ServiceDetail', { package: pkg, serviceTitle: category.name })}
          />
        ))}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.md, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  backBtn: { marginBottom: Spacing.xs },
  backText: { color: Colors.primary, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.sm },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  ratingText: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  list: { padding: Spacing.md },
  sectionHeader: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginBottom: Spacing.sm },
});
