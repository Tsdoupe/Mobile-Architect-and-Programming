package com.zybooks.inventory_app_doupe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String itemSku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        dbHelper = new DatabaseHelper(this);

        EditText itemNameEditText = findViewById(R.id.editItemNameEditText);
        EditText skuEditText = findViewById(R.id.editSkuEditText);
        EditText categoryEditText = findViewById(R.id.editCategoryEditText);
        EditText quantityEditText = findViewById(R.id.editQuantityEditText);
        Button saveButton = findViewById(R.id.saveEditButton);
        Button cancelButton = findViewById(R.id.cancelEditButton);

        // Populate fields with passed data
        Intent intent = getIntent();
        itemSku = intent.getStringExtra("sku");
        itemNameEditText.setText(intent.getStringExtra("itemName"));
        skuEditText.setText(itemSku);
        categoryEditText.setText(intent.getStringExtra("category"));
        quantityEditText.setText(String.valueOf(intent.getIntExtra("quantity", 0)));

        // SKU should remain uneditable since it's a primary key
        skuEditText.setEnabled(false);

        saveButton.setOnClickListener(v -> {
            String newName = itemNameEditText.getText().toString().trim();
            String newCategory = categoryEditText.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();

            if (newName.isEmpty() || newCategory.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            int newQuantity;
            try {
                newQuantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dbHelper.updateInventoryItem(itemSku, newName, newCategory, newQuantity);
            if (updated) {
                Log.d("EditItemActivity", "Update successful for SKU: " + itemSku);
                Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show();

                // Notify parent activity that the update was successful
                setResult(RESULT_OK);
                finish();
            } else {
                Log.e("EditItemActivity", "Failed to update item with SKU: " + itemSku);
                Toast.makeText(this, "Failed to update item!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> finish());
    }
}
