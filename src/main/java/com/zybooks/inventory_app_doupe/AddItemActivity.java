package com.zybooks.inventory_app_doupe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    // Allows DatabaseHelper to interact with the database
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Bind UI components
        EditText itemNameEditText = findViewById(R.id.addItemNameEditText);
        EditText skuEditText = findViewById(R.id.addSkuEditText);
        EditText categoryEditText = findViewById(R.id.addCategoryEditText);
        EditText quantityEditText = findViewById(R.id.addQuantityEditText);
        Button saveButton = findViewById(R.id.saveItemButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        // Handles the Save button clock event
        saveButton.setOnClickListener(v -> {
            // Gets user input values for each corresponding value
            String itemName = itemNameEditText.getText().toString().trim();
            String sku = skuEditText.getText().toString().trim();
            String category = categoryEditText.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();

            // Validates that all fields are filled in
            if (itemName.isEmpty() || sku.isEmpty() || category.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            try {
                // Converts quantity input to an integer
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                // Error message is input is not an integer
                Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inserts new item into the database
            boolean inserted = dbHelper.addInventoryItem(itemName, sku, category, quantity);
            if (inserted) {
                // Success message if item successfully added to database
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                // Return success result to trigger list refresh
                setResult(RESULT_OK);
                finish();
            } else {
                // Failure message if duplicated item, likely due to duplicated SKU
                Toast.makeText(this, "Failed to add item. SKU might already exist.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handles cancel button click event
        cancelButton.setOnClickListener(v -> finish());
    }
}
