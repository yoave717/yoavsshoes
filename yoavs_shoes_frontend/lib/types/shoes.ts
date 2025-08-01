import { BaseType } from "./common";


export interface ShoeInventory extends BaseType {
  shoeModelId: number;
  size: string;
  quantityAvailable: number;
  quantityReserved: number;
  actualAvailableQuantity: number;
  inStock: boolean;
  available: boolean;
}

export interface ShoeModel extends BaseType {
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
  gender: string;
  brand: Brand;
  category: Category;
}

export interface ShoeInventoryView extends Shoe {
   totalStock: number;
   modelCount: number;
}

