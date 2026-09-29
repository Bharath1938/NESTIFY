import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Modal, TextInput } from 'react-native';
import { Card, StatusTimeline, BeforeAfterSlider, Button } from '../../components';
import { Colors, Typography, Spacing } from '../../theme';
import { BookingStatus } from '../../types';

export const BookingDetailScreen = ({ route, navigation }: any) => {
  const [status, setStatus] = useState<BookingStatus>('Completed');
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [rating, setRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');
  const [selectedTags, setSelectedTags] = useState<string[]>(['Professional', 'On Time']);

  const TAG_OPTIONS = ['Professional', 'On Time', 'Friendly', 'Great Organization', 'Detail Oriented'];

  const toggleTag = (tag: string) => {
    if (selectedTags.includes(tag)) {
      setSelectedTags(selectedTags.filter((t) => t !== tag));
    } else {
      setSelectedTags([...selectedTags, tag]);
    }
  };

  const handleConfirmCompletion = () => {
    setStatus('CustomerConfirmed');
    setShowReviewModal(true);
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity style={styles.backBtn} onPress={() => navigation.goBack()}>
          <Text style={styles.backText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Wardrobe Organization</Text>
        <Text style={styles.scheduledAt}>Saturday, 27 Sep · 10:00 AM</Text>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        {/* Status Timeline Tracking */}
        <Card style={styles.card}>
          <Text style={styles.sectionTitle}>Live Status Tracking</Text>
          <StatusTimeline currentStatus={status} />
        </Card>

        {/* Assigned Organizer Card */}
        <Card style={styles.organizerCard}>
          <View style={styles.organizerRow}>
            <View style={styles.avatarCircle}>
              <Text style={styles.avatarText}>P</Text>
            </View>
            <View style={styles.organizerDetails}>
              <Text style={styles.organizerName}>Priya Kumar</Text>
              <Text style={styles.organizerRating}>⭐ 4.9 (128 completed jobs)</Text>
            </View>
            <View style={styles.actionIcons}>
              <TouchableOpacity style={styles.iconBtn}>
                <Text style={styles.iconText}>📞</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.iconBtn}>
                <Text style={styles.iconText}>💬</Text>
              </TouchableOpacity>
            </View>
          </View>
        </Card>

        {/* Before / After Transformation */}
        <Card style={styles.card}>
          <Text style={styles.sectionTitle}>Transformation Photos</Text>
          <BeforeAfterSlider
            beforeImageUrl="https://images.unsplash.com/photo-1558882224-dda166733046?auto=format&fit=crop&w=600&q=80"
            afterImageUrl="https://images.unsplash.com/photo-1595428774223-ef52624120d2?auto=format&fit=crop&w=600&q=80"
          />

          {status === 'Completed' && (
            <View style={styles.confirmSection}>
              <Text style={styles.confirmHeader}>Your space is ready ✨</Text>
              <Button
                title="Confirm Completion & Review"
                onPress={handleConfirmCompletion}
                style={styles.confirmBtn}
              />
            </View>
          )}
        </Card>

        {/* Booking Details */}
        <Card style={styles.card}>
          <Text style={styles.sectionTitle}>Service Address</Text>
          <Text style={styles.detailText}>12 Example Street, Anna Nagar, Chennai - 600040</Text>

          <Text style={[styles.sectionTitle, { marginTop: Spacing.md }]}>Amount Paid</Text>
          <Text style={styles.detailText}>₹1,498 (Standard Wardrobe Organization Package)</Text>
        </Card>
      </ScrollView>

      {/* Review Modal */}
      <Modal visible={showReviewModal} transparent animationType="slide">
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>How was your experience?</Text>
            <Text style={styles.modalSub}>Rate Priya Kumar's organization service</Text>

            <View style={styles.starsRow}>
              {[1, 2, 3, 4, 5].map((s) => (
                <TouchableOpacity key={s} onPress={() => setRating(s)}>
                  <Text style={styles.starText}>{s <= rating ? '⭐' : '☆'}</Text>
                </TouchableOpacity>
              ))}
            </View>

            <Text style={styles.tagHeader}>What went well?</Text>
            <View style={styles.tagsContainer}>
              {TAG_OPTIONS.map((tag) => {
                const isSelected = selectedTags.includes(tag);
                return (
                  <TouchableOpacity
                    key={tag}
                    style={[styles.tagBadge, isSelected ? styles.selectedTagBadge : null]}
                    onPress={() => toggleTag(tag)}
                  >
                    <Text style={[styles.tagText, isSelected ? styles.selectedTagText : null]}>
                      {tag}
                    </Text>
                  </TouchableOpacity>
                );
              })}
            </View>

            <TextInput
              style={styles.reviewInput}
              placeholder="Tell us more about your transformed space..."
              placeholderTextColor={Colors.textMuted}
              value={reviewComment}
              onChangeText={setReviewComment}
              multiline
            />

            <Button
              title="Submit Review"
              onPress={() => setShowReviewModal(false)}
              style={styles.submitReviewBtn}
            />
          </View>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: Colors.background },
  header: { padding: Spacing.md, backgroundColor: Colors.surface, borderBottomWidth: 1, borderColor: Colors.border },
  backBtn: { marginBottom: Spacing.xs },
  backText: { color: Colors.primary, fontWeight: Typography.fontWeight.bold, fontSize: Typography.fontSize.sm },
  title: { fontSize: Typography.fontSize.xl, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  scheduledAt: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  content: { padding: Spacing.md },
  card: { padding: Spacing.md, marginBottom: Spacing.md },
  sectionTitle: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.primary, textTransform: 'uppercase', marginBottom: Spacing.xs },
  organizerCard: { padding: Spacing.md, marginBottom: Spacing.md, backgroundColor: Colors.primaryLight },
  organizerRow: { flexDirection: 'row', alignItems: 'center' },
  avatarCircle: { width: 48, height: 48, borderRadius: 24, backgroundColor: Colors.primary, alignItems: 'center', justifyContent: 'center' },
  avatarText: { color: Colors.surface, fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold },
  organizerDetails: { flex: 1, marginLeft: Spacing.sm },
  organizerName: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  organizerRating: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  actionIcons: { flexDirection: 'row' },
  iconBtn: { width: 36, height: 36, borderRadius: 18, backgroundColor: Colors.surface, alignItems: 'center', justifyContent: 'center', marginLeft: Spacing.xs },
  iconText: { fontSize: 16 },
  confirmSection: { marginTop: Spacing.md, alignItems: 'center' },
  confirmHeader: { fontSize: Typography.fontSize.md, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary, marginBottom: Spacing.xs },
  confirmBtn: { width: '100%' },
  detailText: { fontSize: Typography.fontSize.sm, color: Colors.textPrimary },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center', padding: Spacing.lg },
  modalContent: { backgroundColor: Colors.surface, borderRadius: 20, padding: Spacing.lg, alignItems: 'center' },
  modalTitle: { fontSize: Typography.fontSize.lg, fontWeight: Typography.fontWeight.bold, color: Colors.textPrimary },
  modalSub: { fontSize: Typography.fontSize.xs, color: Colors.textSecondary, marginTop: 2 },
  starsRow: { flexDirection: 'row', marginVertical: Spacing.md },
  starText: { fontSize: 32, marginHorizontal: 4 },
  tagHeader: { fontSize: Typography.fontSize.xs, fontWeight: Typography.fontWeight.bold, color: Colors.textSecondary, marginBottom: Spacing.xs, alignSelf: 'flex-start' },
  tagsContainer: { flexDirection: 'row', flexWrap: 'wrap', marginBottom: Spacing.md },
  tagBadge: { borderPadding: Spacing.xs, paddingHorizontal: Spacing.sm, paddingVertical: Spacing.xs, borderRadius: 16, borderWidth: 1, borderColor: Colors.border, margin: 3 },
  selectedTagBadge: { backgroundColor: Colors.primary, borderColor: Colors.primary },
  tagText: { fontSize: Typography.fontSize.xs, color: Colors.textPrimary },
  selectedTagText: { color: Colors.surface, fontWeight: Typography.fontWeight.bold },
  reviewInput: { width: '100%', borderWidth: 1, borderColor: Colors.border, borderRadius: 12, padding: Spacing.sm, height: 80, textAlignVertical: 'top', fontSize: Typography.fontSize.sm, marginBottom: Spacing.md },
  submitReviewBtn: { width: '100%' },
});
