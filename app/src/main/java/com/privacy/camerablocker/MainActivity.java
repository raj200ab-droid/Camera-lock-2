package com.privacy.camerablocker;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private DevicePolicyManager dpm;
    private ComponentName adminComponent;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, AdminReceiver.class);

        // UI Design in Java (No XML needed)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(50, 50, 50, 50);

        statusText = new TextView(this);
        statusText.setTextSize(18);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, 0, 0, 50);

        Button btnAdmin = new Button(this);
        btnAdmin.setText("1. Enable Admin Permission");

        Button btnDisableCam = new Button(this);
        btnDisableCam.setText("Turn OFF All Cameras");

        Button btnEnableCam = new Button(this);
        btnEnableCam.setText("Turn ON All Cameras");

        layout.addView(statusText);
        layout.addView(btnAdmin);
        layout.addView(btnDisableCam);
        layout.addView(btnEnableCam);

        setContentView(layout);
        updateStatus();

        // Admin Permission Button
        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Camera ON/OFF korar jonno ei permission dorkar.");
            startActivityForResult(intent, 1);
        });

        // Camera OFF Button
        btnDisableCam.setOnClickListener(v -> {
            if (dpm.isAdminActive(adminComponent)) {
                try {
                    dpm.setCameraDisabled(adminComponent, true);
                    updateStatus();
                    Toast.makeText(this, "All Cameras Disabled!", Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(this, "Error: Apnar Android version-e eta block kora hoyeche.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Age Admin Permission Enable korun!", Toast.LENGTH_SHORT).show();
            }
        });

        // Camera ON Button
        btnEnableCam.setOnClickListener(v -> {
            if (dpm.isAdminActive(adminComponent)) {
                dpm.setCameraDisabled(adminComponent, false);
                updateStatus();
                Toast.makeText(this, "All Cameras Enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Age Admin Permission Enable korun!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus() {
        if (dpm.isAdminActive(adminComponent)) {
            boolean isBlocked = dpm.getCameraDisabled(adminComponent);
            statusText.setText(isBlocked ? "Status: CAMERA BLOCKED 🔴" : "Status: CAMERA ACTIVE 🟢");
        } else {
            statusText.setText("Status: Admin Permission Nei ⚠️");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }
}
