export interface Shoe {
  id: number;
  name: string;
  basePrice: number;
  gender: string;
  brand: Brand;
  category: Category;
}

export interface ProductModel {
  id: number;
  modelName: string;
  shoeName: string;
  brandName: string;
  categoryName: string;
  color: string;
  material?: string;
  price: number;
  imageUrl?: string;
  description?: string;
  availableSizes: string[];
  isInStock: boolean;
}

export interface ProductFilters {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
  brandIds?: number[];
  categoryIds?: number[];
  colors?: string[];
  sizes?: string[];
  minPrice?: number;
  maxPrice?: number;
  search?: string;
  inStock?: boolean;
}

export interface AvailableFilters {
  brands: { id: number; name: string; productCount: number }[];
  categories: { id: number; name: string; productCount: number }[];
  colors: string[];
  priceRange: { min: number; max: number };
}

export interface Brand {
  id: number;
  name: string;
  description?: string;
  isActive?: boolean;
  logoUrl?: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  isActive?: boolean;
}

export interface ShoeInventory {
  id: number;
  shoeModelId: number;
  size: string;
  quantityAvailable: number;
  quantityReserved: number;
  actualAvailableQuantity: number;
  inStock: boolean;
  available: boolean;
}

export interface ShoeModel {
  id: number;
  modelName: string;
  color: string;
  material?: string;
  sku: string;
  price: number;
  imageUrl: string;
  isActive: boolean;
  displayName: string;
  fullDisplayName: string;
  shoe: Shoe;
  availableSizes: ShoeInventory[];
}
