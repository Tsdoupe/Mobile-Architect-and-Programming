package com.zybooks.inventory_app_doupe;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

public class SMSActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String SMS_ENABLED_KEY = "smsEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        // Initialize UI components
        TextView smsMessageTextView = findViewById(R.id.smsMessageTextView);
        SwitchCompat smsToggleSwitch = findViewById(R.id.smsToggleSwitch);

        // Display default message
        smsMessageTextView.setText(getString(R.string.sms_notification_message));

        // Set up the toolbar as the action bar
        Toolbar toolbar = findViewById(R.id.smsToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Inventory List");
        }

        // Load saved state for SMS toggle
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isSmsEnabled = prefs.getBoolean(SMS_ENABLED_KEY, false);
        smsToggleSwitch.setChecked(isSmsEnabled);

        // Handle toggle switch changes
        smsToggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!hasSmsPermission()) {
                    requestSmsPermission();
                } else {
                    saveSmsPreference(true);
                    Toast.makeText(this, "SMS Notifications Enabled", Toast.LENGTH_SHORT).show();
                }
            } else {
                saveSmsPreference(false);
                Toast.makeText(this, "SMS Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back to previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Check if SMS permission is granted
    private boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request SMS permission
    private void requestSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            new AlertDialog.Builder(this)
                    .setTitle("SMS Permission Required")
                    .setMessage("This app requires SMS permission to send notifications about low inventory.")
                    .setPositiveButton("Allow", (dialog, which) ->
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    // Save SMS notification preference
    private void saveSmsPreference(boolean enabled) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SMS_ENABLED_KEY, enabled);
        editor.apply();
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
