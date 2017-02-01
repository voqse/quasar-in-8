package com.voqse.nixieclock.widget.support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;

import com.voqse.nixieclock.R;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ApplySettingsDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener {

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        if (!(getActivity() instanceof OnApplySettingsDialogClickListener)) {
            throw new IllegalStateException("To use this dialog hosted activity must implement OnApplySettingsDialogClickListener!");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle state) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.apply_setting_dialog_title)
                .setMessage(R.string.apply_setting_dialog_text)
                .setNegativeButton(R.string.no, this)
                .setPositiveButton(R.string.yes, this)
                .setNeutralButton(R.string.apply_setting_dialog_buy, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ((OnApplySettingsDialogClickListener) getActivity()).onApplySettingsClick(
                which == DialogInterface.BUTTON_NEUTRAL,
                which == DialogInterface.BUTTON_NEGATIVE,
                which == DialogInterface.BUTTON_POSITIVE
        );
    }

    public interface OnApplySettingsDialogClickListener {

        void onApplySettingsClick(boolean buyClicked, boolean noClicked, boolean yesClicked);

    }

}
