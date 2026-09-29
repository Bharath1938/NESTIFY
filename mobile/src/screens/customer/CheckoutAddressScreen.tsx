import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Card, Button, InputField } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';
import { Address } from '../../types';

const MOCK_ADDRESSES: Address[] = [
  { id: 'a1', label: 'Home', houseNumberAndStreet: '12 Example Street, Anna Nagar', landmark: 'Near Tower Park', city: 'Chennai', zipCode: '600040', latitude: 13.0827, longitude: 80.2707, isDefault: true },
  { id: 'a2', label: 'Office', houseNumberAndStreet: '45 IT Park Road, OMR', landmark: 'Opposite Cyber Towers', city: 'Chennai', zipCode: '600096', latitude: 12.9716, longitude: 80.2452, isDefault: false },
];

export const CheckoutAddressScreen = ({ route, navigation }: any) => {
  const cartItem = route.params?.cartItem;
  const [selectedAddressId, setSelectedAddressId] = useState('a1');
  const [showAddModal, setShowAddModal] = useState(false);

  const [houseNumber, setHouseNumber] = useState('');
  const [landmark, setLandmark] = useState('');

  const selectedAddress = MOCK_ADDRESSES.find((a) => a.id === selectedAddressId) || MOCK_ADDRESSES[0];

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity style={styles.backBtn} onPress={() => navigation.goBack()}>
          <Text style={styles.backText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Where should we organize?</Text>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        <Text style={styles.sectionTitle}>Saved Addresses</Text>

        {MOCK_ADDRESSES.map((addr) => {
          const isSelected = addr.id === selectedAddressId;
          return (
            <TouchableOpacity
              key={addr.id}
              style={[styles.addressCard, isSelected ? styles.selectedCard : null]}
              onPress={() => setSelectedAddressId(addr.id)}
            >
              <View style={styles.row}>
                <Text style={styles.label}>{addr.label}</Text>
                {isSelected && <Text style={styles.checkText}>✓ Selected</Text>}
              </View>
              <Text style={styles.street}>{addr.houseNumberAndStreet}</Text>
              <Text style={styles.city}>{addr.landmark}, {addr.city} - {addr.zipCode}</Text>
            </TouchableOpacity>
          );
        })}

        <TouchableOpacity style={styles.addAddressBtn} onPress={() => setShowAddModal(!showAddModal)}>
          <Text style={styles.addAddressText}>+ Add New Address in Chennai</Text>
        </TouchableOpacity>

        {showAddModal && (
          <Card style={styles.addForm}>
            <InputField label="House No / Street" value={houseNumber} onChangeText={setHouseNumber} placeholder="e.g. Flat 302, Green Apartments" />
            <InputField label="Landmark" value={landmark} onChangeText={setLandmark} placeholder="e.g. Near Apollo Hospital" />
            <Button title="Save Address" onPress={() => setShowAddModal(false)} style={styles.saveBtn} />
          </Card>
        )}
      </ScrollView>

      <View style={styles.bottomBar}>
        <Button
          title="Continue to Date & Time"
          onPress={() => navigation.navigate('CheckoutSchedule', { cartItem, address: selectedAddress })}
          style={styles.continueBtn}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.md, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  backBtn: { marginBottom: Spacing.xs },
  backText: { color: Colors.primary, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.sm },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  content: { padding: Spacing.md },
  sectionTitle: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginBottom: Spacing.sm },
  addressCard: { backgroundColor: Colors.surface, borderRadius: 12, padding: Spacing.md, marginBottom: Spacing.sm, borderWidth: 1, borderColor: Colors.border },
  selectedCard: { borderColor: Colors.primary, borderWidth: 2, backgroundColor: Colors.primaryLight },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  label: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  checkText: { color: Colors.primary, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.xs },
  street: { fontSize: Typography.fontSize.sm, color: Colors.textSecondary, marginTop: 4 },
  city: { fontSize: Typography.fontSize.xs, color: Colors.textMuted, marginTop: 2 },
  addAddressBtn: { paddingVertical: Spacing.md, alignItems: 'center', backgroundColor: Colors.surface, borderRadius: 12, borderWidth: 1, borderColor: Colors.primary, borderStyle: 'dashed', marginTop: Spacing.xs },
  addAddressText: { color: Colors.primary, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.sm },
  addForm: { marginTop: Spacing.md, padding: Spacing.md },
  saveBtn: { marginTop: Spacing.md },
  bottomBar: { backgroundColor: Colors.surface, padding: Spacing.md, borderTopWidth: 1, borderColor: Colors.border },
  continueBtn: { width: '100%' },
});
