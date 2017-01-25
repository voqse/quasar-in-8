package com.voqse.nixieclock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.voqse.nixieclock.widget.ConfigurationActivity;

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
