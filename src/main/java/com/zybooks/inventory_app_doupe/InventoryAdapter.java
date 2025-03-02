package com.zybooks.inventory_app_doupe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.widget.Toast;
import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    // Inventory item list
    private ArrayList<InventoryItem> inventoryList;

    // Database helper instance
    private DatabaseHelper dbHelper;

    public InventoryAdapter(ArrayList<InventoryItem> inventoryList, DatabaseHelper dbHelper) {
        this.inventoryList = inventoryList;
        this.dbHelper = dbHelper;
    }


    // Creates the ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    // Binds data to the views inside the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);

        // Sets item details in the TextViews
        holder.itemTextView.setText(item.getItemName());
        holder.skuTextView.setText(item.getSku());
        holder.categoryTextView.setText(item.getCategory());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));

        // Handle Edit button click listener
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditItemActivity.class);
            intent.putExtra("itemName", item.getItemName());
            intent.putExtra("sku", item.getSku());
            intent.putExtra("category", item.getCategory());
            intent.putExtra("quantity", item.getQuantity());

            // Start with result callback
            ((InventoryActivity) v.getContext()).startActivityForResult(intent, 200);
        });

        // Handles delete button clock listener
        holder.deleteButton.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteInventoryItem(item.getSku());
            if (deleted) {
                inventoryList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, inventoryList.size());
                Toast.makeText(v.getContext(), item.getItemName() + " deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Returns the total number of items in the inventory
    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    // ViewHolder class that holds references to the UI components inside the item layout
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView, skuTextView, categoryTextView, quantityTextView;
        ImageButton editButton, deleteButton;

        // Constructor for the ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            skuTextView = itemView.findViewById(R.id.skuTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
