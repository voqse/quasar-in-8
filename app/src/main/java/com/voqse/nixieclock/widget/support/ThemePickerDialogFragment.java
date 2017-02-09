package com.voqse.nixieclock.widget.support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.Theme;

/**
 * Диалог выбора темы виджета.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ThemePickerDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener {

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        if (!(getActivity() instanceof OnThemeSelectedListener)) {
            throw new IllegalStateException("To use this dialog hosted activity must implement OnThemeSelectedListener!");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle state) {
        return new AlertDialog.Builder(getActivity())
                .setItems(getThemeNames(), this)
                .setTitle(R.string.theme)
                .create();
    }

    private String[] getThemeNames() {
        Theme[] themes = Theme.values();
        String[] names = new String[themes.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = getString(themes[i].nameId);
        }
        return names;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Theme[] themes = Theme.values();
        Theme theme = themes[which];
        ((OnThemeSelectedListener) getActivity()).onThemeSelected(theme);
    }

    public interface OnThemeSelectedListener {

        void onThemeSelected(Theme theme);
    }
}
