export type UserRole = 'Customer' | 'Organizer' | 'Admin';

export type BookingStatus =
  | 'Pending'
  | 'Confirmed'
  | 'Assigned'
  | 'OnTheWay'
  | 'Arrived'
  | 'InProgress'
  | 'Completed'
  | 'CustomerConfirmed'
  | 'Cancelled';

export interface User {
  id: string;
  fullName: string;
  email: string;
  phone: string;
  role: UserRole;
  selectedCity?: string;
}

export interface ServiceCategory {
  id: string;
  name: string;
  slug: string;
  description: string;
  imageUrl: string;
}

export interface ServicePackage {
  id: string;
  serviceId: string;
  packageName: string; // e.g. "Basic Wardrobe Reset", "Standard Wardrobe Organization", "Premium Wardrobe Transformation"
  description: string;
  startingPrice: number;
  estimatedDurationHours: number;
  includedDetails: string[];
  excludedDetails: string[];
  imageUrl: string;
}

export interface Service {
  id: string;
  categoryId: string;
  title: string;
  shortDescription: string;
  fullDescription: string;
  imageUrl: string;
  rating: number;
  reviewCount: number;
  isFeatured?: boolean;
  isPopular?: boolean;
  packages: ServicePackage[];
}

export interface CartItem {
  package: ServicePackage;
  serviceTitle: string;
  quantity: number;
}

export interface Address {
  id: string;
  label: string; // Home, Work
  houseNumberAndStreet: string;
  landmark: string;
  city: string;
  zipCode: string;
  latitude: number;
  longitude: number;
  isDefault: boolean;
}

export interface BookingStatusAudit {
  id: string;
  bookingId: string;
  oldStatus?: BookingStatus;
  newStatus: BookingStatus;
  changedByUserId: string;
  changedAt: string;
  notes: string;
}

export interface BeforeAfterPhoto {
  id: string;
  bookingId: string;
  beforePhotoUrl: string;
  afterPhotoUrl: string;
  uploadedAt: string;
}

export interface Booking {
  id: string;
  customerId: string;
  organizerId?: string;
  addressId: string;
  status: BookingStatus;
  scheduledAt: string;
  estimatedDurationHours: number;
  customerNotes?: string;
  subtotalAmount: number;
  serviceFee: number;
  discountAmount: number;
  totalAmount: number;
  isPaid: boolean;
  paymentTransactionId?: string;
  createdAt: string;
  customer?: User;
  organizer?: User;
  address?: Address;
  items: CartItem[];
  statusAudits?: BookingStatusAudit[];
  photos?: BeforeAfterPhoto[];
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}
