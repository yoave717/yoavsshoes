import { BaseType, PageFilter } from "./common";

export type Gender = 'MEN' | 'WOMEN' | 'UNISEX';

export interface ShoeInventory extends BaseType {
  shoeModelId: number;
  size: string;
  quantityAvailable: number;
  quantityReserved: number;
  actualAvailableQuantity: number;
  inStock: boolean;
  available: boolean;
}

export interface UpdateShoeInventoryRequest { 
  quantityAvailable: number;
  quantityReserved?: number;
}

export interface CreateShoeRequest {
  name: string;
  description?: string;
  basePrice: number;
  gender: Gender;
  brandId: number;
  categoryId: number;
  isActive?: boolean;
}

export interface CreateShoeModelRequest {
  shoeId: number;
  modelName: string;
  color: string;
  material?: string;
  sku: string;
  price: number;
  imageUrl?: string;
  isActive?: boolean;
}

export interface UpdateShoeModelRequest {
  modelName?: string;
  color?: string;
  material?: string;
  price?: number;
  imageUrl?: string;
  isActive?: boolean;
}

export interface CreateShoeInventoryRequest {
  shoeModelId: number;
  size: string;
  quantityAvailable: number;
  quantityReserved?: number;
}

export interface ShoeModel extends BaseType {
  shoeId: number;
  modelName: string;
  color: string;
  material?: string;
  sku: string;
  price: number;
  imageUrl?: string;
  isActive?: boolean;
  displayName: string;
  fullDisplayName: string;
  shoe: Shoe;
  availableSizes: ShoeInventory[];
}

export interface Brand extends BaseType {
  name: string;
  description?: string;
  isActive?: boolean;
  logoUrl?: string;
}

export interface Category extends BaseType {
  name: string;
  description?: string;
  isActive?: boolean;
}

export interface Shoe extends BaseType{
  name: string;
  basePrice: number;
  gender: Gender;
  brand: Brand;
  category: Category;
}

export interface ShoeModelInventoryView extends ShoeModel {
  totalStock: number;
}

export interface ShoeInventoryView extends Shoe {
   totalStock: number;
   modelCount: number;
}

export interface ShoesStats {
  totalShoes: number;
  totalModels: number;
  totalStock: number;
  lowStockShoes: number;
}

export interface ShoeFilters extends PageFilter {
  brandIds?: number[];
  categoryIds?: number[];
  minPrice?: number;
  maxPrice?: number;
  searchTerm?: string;
  inStock?: boolean;
  activeOnly?: boolean;
}

