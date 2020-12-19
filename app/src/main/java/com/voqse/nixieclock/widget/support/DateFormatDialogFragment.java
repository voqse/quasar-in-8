package com.voqse.nixieclock.widget.support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.utils.NixieUtils;

/**
 * Диалог выбора формата даты.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class DateFormatDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener {

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        if (!(getActivity() instanceof OnDateFormatSelectedListener)) {
            throw new IllegalStateException("To use this dialog hosted activity must implement OnDateFormatSelectedListener!");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle state) {
        return new AlertDialog.Builder(getActivity())
                .setItems(getDateFormats(), this)
                .setTitle(R.string.date_format)
                .create();
    }

    private String[] getDateFormats() {
        return new String[]{
                NixieUtils.getNewYerDate(false),
                NixieUtils.getNewYerDate(true)
        };
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        boolean monthFirst = which != 0;
        ((OnDateFormatSelectedListener) getActivity()).onDateFormatSelected(monthFirst);
    }

    public interface OnDateFormatSelectedListener {

        void onDateFormatSelected(boolean monthFirst);
    }
}
