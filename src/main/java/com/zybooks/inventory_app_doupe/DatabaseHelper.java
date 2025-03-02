package com.zybooks.inventory_app_doupe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name & Version
    private static final String DATABASE_NAME = "InventoryApp.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Inventory Table
    private static final String TABLE_INVENTORY = "inventory";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_SKU = "sku";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_QUANTITY = "quantity";

    // Tag for logging
    private static final String TAG = "DatabaseHelper";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Create Inventory Table
        String createInventoryTable = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_SKU + " TEXT PRIMARY KEY, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_QUANTITY + " INTEGER)";
        db.execSQL(createInventoryTable);
    }

    // Called if database needs to be structurally updated later on
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    // Add new user with input validation
    public boolean addUser(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            Log.e(TAG, "Invalid username or password input");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Validate user credentials
    public boolean validateUser(String username, String password) {
        if (username == null || password == null) {
            Log.e(TAG, "Username or password cannot be null");
            return false;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return valid;
    }

    // Add inventory item
    public boolean addInventoryItem(String itemName, String sku, String category, int quantity) {
        if (sku == null || sku.isEmpty()) {
            Log.e(TAG, "SKU cannot be empty");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        // Check if SKU already exists
        Cursor cursor = db.query(TABLE_INVENTORY, null, COLUMN_SKU + "=?", new String[]{sku}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            Log.w(TAG, "SKU already exists: " + sku);
            return false;
        }
        cursor.close();

        // Insert the new inventory item
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_SKU, sku);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_QUANTITY, quantity);

        long result = db.insert(TABLE_INVENTORY, null, values);
        db.close();
        if (result == -1) {
            Log.e(TAG, "Failed to insert item with SKU: " + sku);
        }
        return result != -1;
    }

    // Retrieve all inventory items
    public Cursor getInventoryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_INVENTORY, null, null, null, null, null, COLUMN_ITEM_NAME);
    }

    // Update inventory item
    public boolean updateInventoryItem(String sku, String itemName, String category, int quantity) {
        if (sku == null || sku.isEmpty()) {
            Log.e(TAG, "SKU cannot be empty for update");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_QUANTITY, quantity);

        int rowsUpdated = db.update(TABLE_INVENTORY, values, COLUMN_SKU + "=?", new String[]{sku});
        db.close();

        if (rowsUpdated == 0) {
            Log.e(TAG, "Failed to update item with SKU: " + sku);
            return false;
        }
        return true;
    }

    // Delete inventory item
    public boolean deleteInventoryItem(String sku) {
        if (sku == null || sku.isEmpty()) {
            Log.e(TAG, "SKU cannot be empty for deletion");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_INVENTORY, COLUMN_SKU + "=?", new String[]{sku});
        db.close();

        if (rowsDeleted == 0) {
            Log.e(TAG, "Failed to delete item with SKU: " + sku);
            return false;
        }
        return true;
    }
}
