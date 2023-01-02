package com.example.notesapp.noteTypes;

import androidx.appcompat.app.AppCompatActivity;
import com.example.notesapp.R;
import com.example.notesapp.services.FloatingViewService;
import com.example.notesapp.services.TapAccessibilityService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

public class AutoClickerActivity extends AppCompatActivity {


    int clicks = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auto_clicker);

        Button finger_button = findViewById(R.id.finger_service);
        Button clicker_button = findViewById(R.id.clicker);

        if (!Settings.canDrawOverlays(AutoClickerActivity.this)){
            Toast.makeText(AutoClickerActivity.this, "Please Enable Display over other apps", Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if(!isAccessibilityServiceEnabled(this, TapAccessibilityService.class)){
            Toast.makeText(AutoClickerActivity.this, "please enable accessibility service", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        clicker_button.setOnClickListener(view -> {
            clicks++;
            clicker_button.setText(String.valueOf(clicks));
        });

        finger_button.setOnClickListener(view -> getApplicationContext().startService(new Intent(AutoClickerActivity.this, FloatingViewService.class)));

    }
    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }
}