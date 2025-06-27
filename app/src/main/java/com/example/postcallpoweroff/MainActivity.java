package com.example.postcallpoweroff;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int READ_PHONE_STATE_PERMISSION_CODE = 101;
    private Button permissionButton;
    private TextView permissionStatusTextView;

    private final ActivityResultLauncher<Intent> overlayPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                updatePermissionStatus();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionButton = findViewById(R.id.permissionButton);
        permissionStatusTextView = findViewById(R.id.permissionStatusTextView);

        permissionButton.setOnClickListener(v -> handlePermissionRequest());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionStatus();
    }

    private void handlePermissionRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_PERMISSION_CODE);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            overlayPermissionLauncher.launch(intent);
        } else {
            Toast.makeText(this, "All permissions are enabled!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePermissionStatus() {
        boolean phoneStateGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        boolean overlayGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this);

        if (phoneStateGranted && overlayGranted) {
            permissionStatusTextView.setText(R.string.permissions_granted);
            permissionStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.green_400));
            permissionButton.setEnabled(false);
            permissionButton.setText(R.string.permissions_enabled);
        } else if (!phoneStateGranted) {
            permissionStatusTextView.setText(R.string.phone_state_permission_needed);
            permissionStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        } else {
            permissionStatusTextView.setText(R.string.overlay_permission_needed);
            permissionStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_400));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PHONE_STATE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handlePermissionRequest();
            } else {
                Toast.makeText(this, "Phone State permission is required for the app to work.", Toast.LENGTH_LONG).show();
            }
            updatePermissionStatus();
        }
    }
}
