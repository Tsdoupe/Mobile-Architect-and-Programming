package com.zybooks.inventory_app_doupe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    // UI Components
    RecyclerView recyclerView;
    Button addItemButton;
    ImageButton smsSettingsButton;

    // Inventory Data
    ArrayList<InventoryItem> inventoryList;
    InventoryAdapter inventoryAdapter;
    DatabaseHelper dbHelper;

    // SharedPreferences keys for managing SMS alert status
    private static final String PREFS_NAME = "AppPrefs";
    private static final String SMS_ENABLED_KEY = "smsEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        addItemButton = findViewById(R.id.addItemButton);
        smsSettingsButton = findViewById(R.id.smsSettingsButton);
        dbHelper = new DatabaseHelper(this);

        // Initialize inventory list
        inventoryList = new ArrayList<>();
        loadInventoryFromDatabase();

        // Sets up RecyclerView with adapter
        inventoryAdapter = new InventoryAdapter(inventoryList, dbHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(inventoryAdapter);

        // Navigates to Add Item Activity
        addItemButton.setOnClickListener(view -> {
            Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        // Navigate to SMS Settings
        smsSettingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(InventoryActivity.this, SMSActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Opening SMS Settings", Toast.LENGTH_SHORT).show();
        });
    }

    // Refreshes inventory list to show updates made in other activities
    @Override
    protected void onResume() {
        super.onResume();
        loadInventoryFromDatabase();
        inventoryAdapter.notifyDataSetChanged();
    }

    // Loads inventory items from the database and checks for low inventory alerts
    private void loadInventoryFromDatabase() {
        inventoryList.clear();
        Cursor cursor = dbHelper.getInventoryItems();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String sku = cursor.getString(1);
                String category = cursor.getString(2);
                int quantity = cursor.getInt(3);
                inventoryList.add(new InventoryItem(name, sku, category, quantity));

                // Check inventory and send SMS if needed
                checkAndSendLowInventoryAlert(name, quantity);
            }
            cursor.close();
        }
    }

    // Checks and sends low inventory alert
    private void checkAndSendLowInventoryAlert(String itemName, int quantity) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Checks if SMS preference is enabled
        boolean isSmsEnabled = prefs.getBoolean(SMS_ENABLED_KEY, false);

        String alertSentKey = "alert_sent_" + itemName;
        boolean alertAlreadySent = prefs.getBoolean(alertSentKey, false);

        SharedPreferences.Editor editor = prefs.edit();

        // If quantity is below 5 and an alert hasn't been sent, send it
        if (isSmsEnabled && quantity < 5 && !alertAlreadySent) {
            sendSmsNotification(itemName, quantity);
            editor.putBoolean(alertSentKey, true);
            editor.apply();
        }
        // If quantity is 5 or more, reset the alert so it can trigger again next time it drops
        else if (quantity >= 5 && alertAlreadySent) {
            editor.remove(alertSentKey);
            editor.apply();
        }
    }

    // Sends SMS Notifications
    private void sendSmsNotification(String itemName, int quantity) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Low Inventory Alert: " + itemName + " has only " + quantity + " items left!";
            String phoneNumber = "1234567890"; // Placeholder for emulator testing

            // Log SMS details for debugging
            android.util.Log.d("SMS_DEBUG", "Sending SMS to: " + phoneNumber + " | Message: " + message);

            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Low Inventory SMS Sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            android.util.Log.e("SMS_DEBUG", "Failed to send SMS: " + e.getMessage());
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

