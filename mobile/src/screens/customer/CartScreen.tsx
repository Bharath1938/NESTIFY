import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TextInput, TouchableOpacity } from 'react-native';
import { Card, Button } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';
import { ServicePackage } from '../../types';

export const CartScreen = ({ route, navigation }: any) => {
  const selectedPackage: ServicePackage = route.params?.package || {
    id: 'pkg_standard',
    serviceId: 's1',
    packageName: 'Standard Wardrobe Organization',
    startingPrice: 899,
    estimatedDurationHours: 3,
    description: 'Comprehensive clothes sorting, drawer organization, and shoe rack setup.',
    includedDetails: [],
    excludedDetails: [],
    imageUrl: '',
  };

  const [quantity, setQuantity] = useState(1);
  const [promoCode, setPromoCode] = useState('');
  const [discountAmount, setDiscountAmount] = useState(0);
  const [notes, setNotes] = useState('');
  const [photoUploaded, setPhotoUploaded] = useState(false);

  const subtotal = selectedPackage.startingPrice * quantity;
  const serviceFee = 100; // ₹100 Flat Service Fee
  const total = subtotal + serviceFee - discountAmount;

  const handleApplyPromo = () => {
    if (promoCode.toUpperCase() === 'NESTIFY200') {
      setDiscountAmount(200);
    } else {
      setDiscountAmount(0);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Your Organization Plan</Text>
      </View>

      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        {/* Package Item Card */}
        <Card style={styles.itemCard}>
          <Text style={styles.itemTitle}>{selectedPackage.packageName}</Text>
          <Text style={styles.itemSub}>{selectedPackage.description}</Text>

          <View style={styles.quantityRow}>
            <Text style={styles.unitPrice}>₹{selectedPackage.startingPrice} / unit</Text>
            <View style={styles.counter}>
              <TouchableOpacity
                style={styles.counterBtn}
                onPress={() => setQuantity(Math.max(1, quantity - 1))}
              >
                <Text style={styles.counterBtnText}>-</Text>
              </TouchableOpacity>
              <Text style={styles.quantityText}>{quantity}</Text>
              <TouchableOpacity
                style={styles.counterBtn}
                onPress={() => setQuantity(quantity + 1)}
              >
                <Text style={styles.counterBtnText}>+</Text>
              </TouchableOpacity>
            </View>
          </View>
        </Card>

        {/* Photo Assistance */}
        <Card style={styles.photoCard}>
          <Text style={styles.cardHeader}>Help us understand your space</Text>
          <Text style={styles.photoSub}>Upload 3-5 photos of the rooms/shelves to organize (optional)</Text>

          <TouchableOpacity
            style={styles.uploadBox}
            onPress={() => setPhotoUploaded(!photoUploaded)}
          >
            <Text style={styles.uploadIcon}>{photoUploaded ? '✅' : '📷'}</Text>
            <Text style={styles.uploadText}>
              {photoUploaded ? '3 Photos Attached (Tap to change)' : '+ Upload Photos from Gallery/Camera'}
            </Text>
          </TouchableOpacity>

          <Text style={styles.notesLabel}>Special Instructions / Notes:</Text>
          <TextInput
            style={styles.notesInput}
            placeholder="e.g. Please keep winter clothes separate in upper shelf."
            placeholderTextColor={Colors.textMuted}
            value={notes}
            onChangeText={setNotes}
            multiline
          />
        </Card>

        {/* Promo Code */}
        <Card style={styles.promoCard}>
          <Text style={styles.cardHeader}>Promo Code</Text>
          <View style={styles.promoRow}>
            <TextInput
              style={styles.promoInput}
              placeholder="Enter Code (e.g. NESTIFY200)"
              placeholderTextColor={Colors.textMuted}
              value={promoCode}
              onChangeText={setPromoCode}
              autoCapitalize="characters"
            />
            <TouchableOpacity style={styles.applyBtn} onPress={handleApplyPromo}>
              <Text style={styles.applyBtnText}>Apply</Text>
            </TouchableOpacity>
          </View>
          {discountAmount > 0 && (
            <Text style={styles.promoSuccessText}>✓ ₹{discountAmount} Promo Discount Applied!</Text>
          )}
        </Card>

        {/* Bill Summary */}
        <Card style={styles.billCard}>
          <Text style={styles.cardHeader}>Bill Summary</Text>
          <View style={styles.billRow}>
            <Text style={styles.billLabel}>Subtotal</Text>
            <Text style={styles.billValue}>₹{subtotal}</Text>
          </View>
          <View style={styles.billRow}>
            <Text style={styles.billLabel}>Service Fee</Text>
            <Text style={styles.billValue}>₹{serviceFee}</Text>
          </View>
          {discountAmount > 0 && (
            <View style={styles.billRow}>
              <Text style={styles.discountLabel}>Discount</Text>
              <Text style={styles.discountValue}>- ₹{discountAmount}</Text>
            </View>
          )}

          <View style={styles.totalDivider} />

          <View style={styles.billRow}>
            <Text style={styles.totalLabel}>Total Payable</Text>
            <Text style={styles.totalValue}>₹{total}</Text>
          </View>
        </Card>
      </ScrollView>

      {/* Checkout Bottom Bar */}
      <View style={styles.bottomBar}>
        <View>
          <Text style={styles.totalLabel}>Total: ₹{total}</Text>
          <Text style={styles.durationText}>⏱ ~{selectedPackage.estimatedDurationHours} hours</Text>
        </View>
        <Button
          title="Continue to Address"
          onPress={() =>
            navigation.navigate('CheckoutAddress', {
              cartItem: { package: selectedPackage, quantity, subtotal, serviceFee, discountAmount, total, notes },
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
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  content: { padding: Spacing.md, paddingBottom: 80 },
  itemCard: { padding: Spacing.md, marginBottom: Spacing.md },
  itemTitle: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  itemSub: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  quantityRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginTop: Spacing.sm },
  unitPrice: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  counter: { flexDirection: 'row', alignItems: 'center', backgroundColor: Colors.background, borderRadius: 8, padding: 2 },
  counterBtn: { width: 32, height: 32, backgroundColor: Colors.surface, borderRadius: 6, alignItems: 'center', justifyContent: 'center', borderWidth: 1, borderColor: Colors.border },
  counterBtnText: { fontSize: 18, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  quantityText: { paddingHorizontal: Spacing.md, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.md },
  photoCard: { padding: Spacing.md, marginBottom: Spacing.md },
  cardHeader: { fontSize: Typography.fontSize.sm, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginBottom: 2 },
  photoSub: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginBottom: Spacing.sm },
  uploadBox: { borderWidth: 1.5, borderColor: Colors.primary, borderStyle: 'dashed', borderRadius: 12, padding: Spacing.md, alignItems: 'center', backgroundColor: Colors.primaryLight },
  uploadIcon: { fontSize: 24, marginBottom: 4 },
  uploadText: { color: Colors.primary, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.xs },
  notesLabel: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.semibold, marginTop: Spacing.md, color: Colors.textPrimary },
  notesInput: { borderWidth: 1, borderColor: Colors.border, borderRadius: 8, padding: Spacing.xs, marginTop: 4, height: 60, textAlignVertical: 'top', fontSize: Typography.fontSize.sm },
  promoCard: { padding: Spacing.md, marginBottom: Spacing.md },
  promoRow: { flexDirection: 'row', marginTop: Spacing.xs },
  promoInput: { flex: 1, borderWidth: 1, borderColor: Colors.border, borderRadius: 8, paddingHorizontal: Spacing.sm, height: 44, fontSize: Typography.fontSize.sm },
  applyBtn: { backgroundColor: Colors.primary, borderRadius: 8, paddingHorizontal: Spacing.md, justifyContent: 'center', marginLeft: Spacing.xs },
  applyBtnText: { color: Colors.surface, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.sm },
  promoSuccessText: { color: Colors.success, fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, marginTop: Spacing.xs },
  billCard: { padding: Spacing.md, marginBottom: Spacing.md },
  billRow: { flexDirection: 'row', justifyContent: 'space-between', marginVertical: 3 },
  billLabel: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary },
  billValue: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary, fontWeight: Typography.fontWeight.medium },
  discountLabel: { fontSize: Typography.fontSize.sm, color: Colors.success },
  discountValue: { fontSize: Typography.fontSize.sm, color: Colors.success, fontWeight: Typography.fontWeight.bold },
  totalDivider: { height: 1, backgroundColor: Colors.border, marginVertical: Spacing.xs },
  totalLabel: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  totalValue: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.primary },
  durationText: { fontSize: Typography.fontSize.xs, color: Colors.textMuted },
  bottomBar: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', backgroundColor: Colors.surface, paddingHorizontal: Spacing.md, paddingVertical: Spacing.sm, borderTopWidth: 1, borderColor: Colors.border },
  continueBtn: { minWidth: 180 },
});
