import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, FlatList, TouchableOpacity, Image } from 'react-native';
import { LocationHeader, CategoryCard, PackageCard, BeforeAfterSlider, Card } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';
import { ServiceCategory, ServicePackage } from '../../types';

const CATEGORIES: ServiceCategory[] = [
  { id: 'c1', name: 'Wardrobe', slug: 'wardrobe', description: 'Clothes, drawers, seasonal sorting', imageUrl: 'https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=400&q=80' },
  { id: 'c2', name: 'Kitchen', slug: 'kitchen', description: 'Cabinets, pantry, containers', imageUrl: 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=400&q=80' },
  { id: 'c3', name: 'Storage', slug: 'storage', description: 'Store room, shelves, garage', imageUrl: 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=400&q=80' },
  { id: 'c4', name: 'Kids Room', slug: 'kids', description: 'Toys, books, study area', imageUrl: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=400&q=80' },
  { id: 'c5', name: 'Living Room', slug: 'living', description: 'Bookshelves, TV cabinet, drawers', imageUrl: 'https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=400&q=80' },
  { id: 'c6', name: 'Full Home', slug: 'fullhome', description: 'Multiple rooms complete reset', imageUrl: 'https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?auto=format&fit=crop&w=400&q=80' },
  { id: 'c7', name: 'Move-In', slug: 'movein', description: 'Unpacking and organizing new home', imageUrl: 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=400&q=80' },
  { id: 'c8', name: 'Move-Out', slug: 'moveout', description: 'Sorting before moving out', imageUrl: 'https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b?auto=format&fit=crop&w=400&q=80' },
];

const POPULAR_PACKAGES: { pkg: ServicePackage; title: string; rating: number; reviews: number }[] = [
  {
    title: 'Wardrobe Reset',
    rating: 4.8,
    reviews: 142,
    pkg: {
      id: 'p1',
      serviceId: 's1',
      packageName: 'Standard Wardrobe Organization',
      description: 'Categorized clothes sorting, folding, hanging, drawer arrangement & seasonal separation.',
      startingPrice: 899,
      estimatedDurationHours: 3,
      includedDetails: ['Clothes sorting', 'Category arrangement', 'Folding & Hanging', 'Drawer organization'],
      excludedDetails: ['Chemical washing', 'Heavy furniture moving'],
      imageUrl: 'https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80',
    },
  },
  {
    title: 'Kitchen Pantry Reset',
    rating: 4.9,
    reviews: 98,
    pkg: {
      id: 'p2',
      serviceId: 's2',
      packageName: 'Full Kitchen & Pantry Reset',
      description: 'Pantry container labeling, spice rack arrangement, jar categorization & expiry audit.',
      startingPrice: 1299,
      estimatedDurationHours: 4,
      includedDetails: ['Pantry labeling', 'Spice rack sorting', 'Drawer decluttering', 'Utensil arrangement'],
      excludedDetails: ['Appliance repair', 'Deep grease scrubbing'],
      imageUrl: 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=600&q=80',
    },
  },
];

export const CustomerHomeScreen = ({ navigation }: any) => {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <View style={styles.container}>
      <LocationHeader />

      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Hero & Search Header */}
        <View style={styles.heroSection}>
          <Text style={styles.heroTitle}>What would you like to organize?</Text>
          <Text style={styles.heroSubtitle}>Tell us what needs a little more space, order and calm.</Text>

          <View style={styles.searchBar}>
            <Text style={styles.searchIcon}>🔍</Text>
            <TextInput
              style={styles.searchInput}
              placeholder='Search "Wardrobe", "Kitchen", "Storage"...'
              placeholderTextColor={Colors.textMuted}
              value={searchQuery}
              onChangeText={setSearchQuery}
            />
          </View>
        </View>

        {/* Categories Horizontal Scroll */}
        <View style={styles.section}>
          <View style={styles.sectionHeader}>
            <Text style={styles.sectionTitle}>Categories</Text>
          </View>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.categoriesScroll}>
            {CATEGORIES.map((cat) => (
              <CategoryCard
                key={cat.id}
                category={cat}
                onPress={() => navigation.navigate('CategoryServices', { category: cat })}
              />
            ))}
          </ScrollView>
        </View>

        {/* New & Featured Banners */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>New & Featured</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.featuredScroll}>
            <TouchableOpacity style={[styles.featureBanner, { backgroundColor: '#5B5BD6' }]}>
              <Text style={styles.featureBadge}>FEATURED</Text>
              <Text style={styles.featureTitle}>Festival Home Reset</Text>
              <Text style={styles.featureSubtitle}>Complete multi-room declutter & organization</Text>
            </TouchableOpacity>

            <TouchableOpacity style={[styles.featureBanner, { backgroundColor: '#6C63FF' }]}>
              <Text style={styles.featureBadge}>POPULAR</Text>
              <Text style={styles.featureTitle}>Wardrobe Refresh</Text>
              <Text style={styles.featureSubtitle}>Seasonal clothing swap & hanger alignment</Text>
            </TouchableOpacity>
          </ScrollView>
        </View>

        {/* Popular Near You */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Popular Near You</Text>
          {POPULAR_PACKAGES.map((item) => (
            <PackageCard
              key={item.pkg.id}
              packageData={item.pkg}
              serviceTitle={item.title}
              rating={item.rating}
              reviewCount={item.reviews}
              onPressAdd={() => navigation.navigate('CartTab', { package: item.pkg })}
              onPressDetails={() => navigation.navigate('ServiceDetail', { package: item.pkg, serviceTitle: item.title })}
            />
          ))}
        </View>

        {/* Before & After Showcase */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Transformation Showcase</Text>
          <Text style={styles.sectionSub}>See actual Nestify organize transformations</Text>
          <BeforeAfterSlider
            beforeImageUrl="https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80"
            afterImageUrl="https://images.unsplash.com/photo-1595428774223-ef52624120d2?auto=format&fit=crop&w=600&q=80"
          />
        </View>

        {/* Why Nestify Promise */}
        <View style={[styles.section, { marginBottom: Spacing.xl }]}>
          <Card style={styles.promiseCard}>
            <Text style={styles.promiseTitle}>Why Nestify?</Text>
            <Text style={styles.promiseItem}>✨ Verified Professional Organizers</Text>
            <Text style={styles.promiseItem}>⏱ Booked on-demand at your convenience</Text>
            <Text style={styles.promiseItem}>📸 Before & After Photo Proof</Text>
          </Card>
        </View>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  heroSection: { padding: Spacing.md, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  heroTitle: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  heroSubtitle: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginTop: 2, marginBottom: Spacing.md },
  searchBar: { flexDirection: 'row', alignItems: 'center', backgroundColor: Colors.background, borderRadius: 12, paddingHorizontal: Spacing.sm, borderWidth: 1, borderColor: Colors.border },
  searchIcon: { fontSize: 16, marginRight: Spacing.xs },
  searchInput: { flex: 1, height: 44, fontSize: Typography.fontSize.sm, color: Colors.textPrimary },
  section: { paddingHorizontal: Spacing.md, marginTop: Spacing.md },
  sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: Spacing.xs },
  sectionTitle: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginBottom: Spacing.xs },
  sectionSub: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginBottom: Spacing.sm },
  categoriesScroll: { flexDirection: 'row' },
  featuredScroll: { flexDirection: 'row' },
  featureBanner: { width: 240, height: 110, borderRadius: 16, padding: Spacing.md, marginRight: Spacing.sm, justifyContent: 'center' },
  featureBadge: { color: Colors.surface, fontSize: 10, fontWeight: Typography.fontWeight.bold, opacity: 0.85 },
  featureTitle: { color: Colors.surface, fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, marginVertical: 2 },
  featureSubtitle: { color: 'rgba(255,255,255,0.9)', fontSize: Typography.fontSize.xs },
  promiseCard: { backgroundColor: Colors.primaryLight, padding: Spacing.md },
  promiseTitle: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.primary, marginBottom: Spacing.xs },
  promiseItem: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary, marginVertical: 2 },
});
