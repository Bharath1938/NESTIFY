import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Card, Button, BeforeAfterSlider } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';

type Step = 'Accepted' | 'Navigate' | 'Arrived' | 'StartService' | 'BeforePhotos' | 'Organizing' | 'AfterPhotos' | 'Completed';

export const OrganizerJobDetailScreen = ({ route, navigation }: any) => {
  const job = route.params?.job || {
    id: 'j1',
    client: 'Jane Customer',
    service: 'Wardrobe Organization',
    package: 'Standard Wardrobe Organization',
    payout: 899,
    date: 'Today · 10:00 AM',
    address: '12 Example Street, Anna Nagar, Chennai',
    notes: 'Please keep winter clothes separate in upper shelf.',
  };

  const [step, setStep] = useState<Step>('Accepted');
  const [beforeUploaded, setBeforeUploaded] = useState(false);
  const [afterUploaded, setAfterUploaded] = useState(false);

  const getStepText = () => {
    switch (step) {
      case 'Accepted': return 'Step 1: Start Navigation to Client Address';
      case 'Navigate': return 'Step 2: Tap "Arrived at Location" when present';
      case 'Arrived': return 'Step 3: Begin Service & Upload Before Photos';
      case 'BeforePhotos': return 'Step 4: Take/Upload BEFORE Transformation Photos';
      case 'Organizing': return 'Step 5: Perform Home Organization Service';
      case 'AfterPhotos': return 'Step 6: Take/Upload AFTER Transformation Photos';
      case 'Completed': return 'Service Completed! Client Confirmed';
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity style={styles.backBtn} onPress={() => navigation.goBack()}>
          <Text style={styles.backText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>{job.service}</Text>
        <Text style={styles.sub}>{job.package} • Payout: ₹{job.payout}</Text>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        {/* Step Progress Header */}
        <Card style={styles.stepCard}>
          <Text style={styles.stepHeader}>{getStepText()}</Text>
        </Card>

        {/* Client & Address Info */}
        <Card style={styles.infoCard}>
          <Text style={styles.infoTitle}>Client & Location</Text>
          <Text style={styles.infoText}>👤 {job.client}</Text>
          <Text style={styles.infoText}>📍 {job.address}</Text>
          <Text style={styles.infoText}>📝 Note: "{job.notes}"</Text>
        </Card>

        {/* Guided Actions */}
        <Card style={styles.actionCard}>
          {step === 'Accepted' && (
            <Button
              title="🗺 Start Navigation (Google Maps)"
              onPress={() => setStep('Navigate')}
            />
          )}

          {step === 'Navigate' && (
            <Button
              title="📍 Arrived at Location"
              onPress={() => setStep('Arrived')}
            />
          )}

          {step === 'Arrived' && (
            <Button
              title="📸 Take / Upload Before Photos"
              onPress={() => setStep('BeforePhotos')}
            />
          )}

          {step === 'BeforePhotos' && (
            <View>
              <TouchableOpacity
                style={styles.photoUploadBtn}
                onPress={() => setBeforeUploaded(true)}
              >
                <Text style={styles.photoUploadText}>
                  {beforeUploaded ? '✓ Before Photos Uploaded' : '📷 Capture Before Photos'}
                </Text>
              </TouchableOpacity>
              <Button
                title="Start Service"
                disabled={!beforeUploaded}
                onPress={() => setStep('Organizing')}
                style={{ marginTop: Spacing.sm }}
              />
            </View>
          )}

          {step === 'Organizing' && (
            <View style={{ alignItems: 'center' }}>
              <Text style={styles.organizingStatus}>🟢 Service In Progress...</Text>
              <Button
                title="📸 Finish & Upload After Photos"
                onPress={() => setStep('AfterPhotos')}
                style={{ width: '100%', marginTop: Spacing.md }}
              />
            </View>
          )}

          {step === 'AfterPhotos' && (
            <View>
              <TouchableOpacity
                style={styles.photoUploadBtn}
                onPress={() => setAfterUploaded(true)}
              >
                <Text style={styles.photoUploadText}>
                  {afterUploaded ? '✓ After Photos Uploaded' : '📷 Capture After Photos'}
                </Text>
              </TouchableOpacity>
              <Button
                title="Complete Service"
                disabled={!afterUploaded}
                onPress={() => setStep('Completed')}
                style={{ marginTop: Spacing.sm }}
              />
            </View>
          )}

          {step === 'Completed' && (
            <View style={{ alignItems: 'center' }}>
              <Text style={styles.completedText}>🎉 Service Successfully Completed!</Text>
              <BeforeAfterSlider
                beforeImageUrl="https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80"
                afterImageUrl="https://images.unsplash.com/photo-1595428774223-ef52624120d2?auto=format&fit=crop&w=600&q=80"
              />
            </View>
          )}
        </Card>
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
  sub: { fontSize: Typography.fontSize.xs, color: Colors.primary, fontWeight: Typography.fontWeight.semibold, marginTop: 2 },
  content: { padding: Spacing.md },
  stepCard: { padding: Spacing.md, backgroundColor: Colors.primaryLight, marginBottom: Spacing.md },
  stepHeader: { fontSize: Typography.fontSize.sm, fontWeight: Typography.fontWeight.bold, color: Colors.primary, textAlign: 'center' },
  infoCard: { padding: Spacing.md, marginBottom: Spacing.md },
  infoTitle: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.textMuted, textTransform: 'uppercase', marginBottom: 4 },
  infoText: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary, marginVertical: 2 },
  actionCard: { padding: Spacing.md },
  photoUploadBtn: { backgroundColor: Colors.primaryLight, borderWidth: 1.5, borderColor: Colors.primary, borderStyle: 'dashed', borderRadius: 12, padding: Spacing.lg, alignItems: 'center' },
  photoUploadText: { color: Colors.primary, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.sm },
  organizingStatus: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.success, marginVertical: Spacing.md },
  completedText: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.success, marginBottom: Spacing.md },
});
