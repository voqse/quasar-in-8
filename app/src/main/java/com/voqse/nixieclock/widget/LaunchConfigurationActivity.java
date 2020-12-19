package com.voqse.nixieclock.widget;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Просто запускающий {@link ConfigurationActivity}. Нужен, чтобы легко можно было удалять/возвращать иконку приложения в списке приложений
 *
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
