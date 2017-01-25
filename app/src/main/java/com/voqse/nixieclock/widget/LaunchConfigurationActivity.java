package com.voqse.nixieclock.widget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class LaunchConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        startActivity(new Intent(this, ConfigurationActivity.class));
        finish();
    }
}
