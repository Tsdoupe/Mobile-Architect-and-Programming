package com.zybooks.inventory_app_doupe;

public class InventoryItem {

    // Private fields to store properties of an inventory item
    private String itemName;
    private String sku;
    private String category;
    private int quantity;

    // Constructor to initialize an InventoryItem
    public InventoryItem(String itemName, String sku, String category, int quantity) {
        this.itemName = itemName;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
    }
    // Getter method for item name
    public String getItemName() {
        return itemName;
    }

    // Getter method for SKU
    public String getSku() {
        return sku;
    }

    // Getter method for item category
    public String getCategory() {
        return category;
    }

    // Getter method for item quantity
    public int getQuantity() {
        return quantity;
    }
}
