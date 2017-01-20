package com.voqse.nixieclock.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.R;

public class ConfigurationActivity extends AppCompatActivity {

    private static final String EXTRA_WIDGET_ID = BuildConfig.APPLICATION_ID + ".EXTRA_WIDGET_ID";

    public static Intent newIntent(Context context, int widgetId) {
        return new Intent(context, ConfigurationActivity.class)
                .putExtra(EXTRA_WIDGET_ID, widgetId);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_configuration);

        int widgetId = getIntent().getIntExtra(EXTRA_WIDGET_ID, -1);
        Log.d("widgetId", "widgetId: " + widgetId);
    }
}
