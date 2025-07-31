// Standard API Response wrapper (matches backend StandardResponse<T>)
export interface StandardResponse<T> {
  success: boolean;
  message: string;
  data: T;
  metadata?: Record<string, unknown>;
  timestamp: string;
}

// Pagination structure (matches backend PageResponse<T>)
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
  numberOfElements: number;
}

// Legacy pagination structure (for backward compatibility)
export interface PageData<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
  numberOfElements: number;
}
