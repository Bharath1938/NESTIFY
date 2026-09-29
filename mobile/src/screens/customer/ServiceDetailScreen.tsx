import React from 'react';
import { View, Text, StyleSheet, ScrollView, Image, TouchableOpacity } from 'react-native';
import { Button, BeforeAfterSlider, Card } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

export const ServiceDetailScreen = ({ route, navigation }: any) => {
  const pkg = route.params?.package || {
    id: 'pkg_standard',
    packageName: 'Standard Wardrobe Organization',
    description: 'Comprehensive clothes sorting, drawer organization, and shoe rack setup.',
    startingPrice: 899,
    estimatedDurationHours: 3,
    includedDetails: ['Clothes sorting', 'Category arrangement', 'Folding & Hanging', 'Drawer organization', 'Shoe setup'],
    excludedDetails: ['Heavy furniture moving', 'Chemical laundry washing'],
    imageUrl: 'https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80',
  };

  const serviceTitle = route.params?.serviceTitle || 'Wardrobe Organization';

  return (
    <View style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Main Image Header */}
        <View style={styles.imageContainer}>
          <Image source={{ uri: pkg.imageUrl }} style={styles.image} resizeMode="cover" />
          <TouchableOpacity style={styles.backButton} onPress={() => navigation.goBack()}>
            <Text style={styles.backButtonText}>←</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.content}>
          <Text style={styles.serviceTitle}>{serviceTitle.toUpperCase()}</Text>
          <Text style={styles.packageName}>{pkg.packageName}</Text>

          <View style={styles.metaRow}>
            <Text style={styles.price}>₹{pkg.startingPrice}</Text>
            <Text style={styles.duration}>⏱ {pkg.estimatedDurationHours} hours estimated</Text>
            <Text style={styles.rating}>⭐ 4.8 (142 reviews)</Text>
          </View>

          {/* What's Included */}
          <Card style={styles.sectionCard}>
            <Text style={styles.sectionTitle}>What's Included</Text>
            {pkg.includedDetails.map((item: string, index: number) => (
              <Text key={index} style={styles.includedItem}>✓  {item}</Text>
            ))}
          </Card>

          {/* What's Not Included */}
          {pkg.excludedDetails && pkg.excludedDetails.length > 0 && (
            <Card style={styles.sectionCard}>
              <Text style={styles.sectionTitle}>What's Excluded</Text>
              {pkg.excludedDetails.map((item: string, index: number) => (
                <Text key={index} style={styles.excludedItem}>✕  {item}</Text>
              ))}
            </Card>
          )}

          {/* Transformation Before / After */}
          <View style={styles.sectionCard}>
            <Text style={styles.sectionTitle}>Before & After Transformation</Text>
            <BeforeAfterSlider
              beforeImageUrl="https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80"
              afterImageUrl="https://images.unsplash.com/photo-1595428774223-ef52624120d2?auto=format&fit=crop&w=600&q=80"
            />
          </View>

          {/* How It Works */}
          <Card style={styles.sectionCard}>
            <Text style={styles.sectionTitle}>How It Works</Text>
            <View style={styles.stepRow}>
              <Text style={styles.stepBadge}>1</Text>
              <Text style={styles.stepText}>Sort: Category decluttering</Text>
            </View>
            <View style={styles.stepRow}>
              <Text style={styles.stepBadge}>2</Text>
              <Text style={styles.stepText}>Organize: Systematize storage space</Text>
            </View>
            <View style={styles.stepRow}>
              <Text style={styles.stepBadge}>3</Text>
              <Text style={styles.stepText}>Label: Clear container tagging</Text>
            </View>
            <View style={styles.stepRow}>
              <Text style={styles.stepBadge}>4</Text>
              <Text style={styles.stepText}>Transform: Peaceful, clean room order</Text>
            </View>
          </Card>
        </View>
      </ScrollView>

      {/* Sticky Bottom Bar */}
      <View style={styles.bottomBar}>
        <View>
          <Text style={styles.bottomPriceLabel}>Total Price</Text>
          <Text style={styles.bottomPrice}>₹{pkg.startingPrice}</Text>
        </View>
        <Button
          title="Add to Booking"
          onPress={() => navigation.navigate('CartTab', { package: pkg })}
          style={styles.addBtn}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  imageContainer: { width: '100%', height: 220, position: 'relative' },
  image: { width: '100%', height: '100%' },
  backButton: { position: 'absolute', top: 16, left: 16, backgroundColor: 'rgba(0,0,0,0.5)', width: 36, height: 36, borderRadius: 18, alignItems: 'center', justifyContent: 'center' },
  backButtonText: { color: Colors.surface, fontSize: 18, fontWeight: Typography.fontWeight.bold },
  content: { padding: Spacing.md, paddingBottom: 80 },
  serviceTitle: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.primary, letterSpacing: 1 },
  packageName: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginVertical: 4 },
  metaRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginBottom: Spacing.md },
  price: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  duration: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary },
  rating: { fontSize: Typography.fontSize.xs, color: Colors.textPrimary, fontWeight: Typography.fontWeight.bold },
  sectionCard: { marginBottom: Spacing.md, padding: Spacing.md },
  sectionTitle: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginBottom: Spacing.sm },
  includedItem: { fontSize: Typography.fontSize.sm, color: Colors.success, marginVertical: 3 },
  excludedItem: { fontSize: Typography.fontSize.sm, color: Colors.error, marginVertical: 3 },
  stepRow: { flexDirection: 'row', alignItems: 'center', marginVertical: 4 },
  stepBadge: { width: 22, height: 22, borderRadius: 11, backgroundColor: Colors.primary, color: Colors.surface, textAlign: 'center', fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, marginRight: Spacing.sm, lineHeight: 22 },
  stepText: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary },
  bottomBar: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', backgroundColor: Colors.surface, paddingHorizontal: Spacing.md, paddingVertical: Spacing.sm, borderTopWidth: 1, borderColor: Colors.border },
  bottomPriceLabel: { fontSize: Typography.fontSize.xs, color: Colors.textMuted },
  bottomPrice: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  addBtn: { minWidth: 160 },
});
