package com.shoestore.entity.shoe;

public interface IShoeInventoryView {
    Shoe getShoe();
    Long getModelCount();
    Long getTotalStock();
}
