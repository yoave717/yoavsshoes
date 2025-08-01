import { PageFilter } from "./common";
import { Shoe, ShoeModel } from "./shoes";


export interface ProductFilters extends PageFilter {
  brandIds?: number[];
  categoryIds?: number[];
  colors?: string[];
  sizes?: string[];
  minPrice?: number;
  maxPrice?: number;
  search?: string;
  inStock?: boolean;
} ;

export interface AvailableFilters {
  brands: { id: number; name: string; productCount: number }[];
  categories: { id: number; name: string; productCount: number }[];
  colors: string[];
  priceRange: { min: number; max: number };
}




export interface ExtendedShoeModel extends ShoeModel {
  totalStock: number;
}

export interface ExtendedShoe extends Shoe {
  models: ExtendedShoeModel[];
  totalStock: number;
  totalModels: number;
}

export interface ShoeWithStockInfo extends Shoe {
  modelCount: number;
  totalStock: number;
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