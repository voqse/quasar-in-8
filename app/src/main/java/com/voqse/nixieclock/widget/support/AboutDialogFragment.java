package com.voqse.nixieclock.widget.support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.R;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class AboutDialogFragment extends DialogFragment implements View.OnClickListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle state) {
        return new AlertDialog.Builder(getActivity())
                .setView(getCustomView())
                .create();
    }

    private View getCustomView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_about, null, false);
        TextView versionTextView = (TextView) view.findViewById(R.id.versionTextView);
        view.findViewById(R.id.okButton).setOnClickListener(this);
        versionTextView.setText(getString(R.string.settings_version, BuildConfig.VERSION_NAME));
        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
