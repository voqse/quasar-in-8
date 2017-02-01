package com.voqse.nixieclock.widget.support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;

import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.R;
import com.voqse.nixieclock.utils.NixieUtils;
import com.voqse.nixieclock.widget.Settings;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class DateFormatDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_WIDGET_ID = BuildConfig.APPLICATION_ID + ".ARG_WIDGET_ID";

    public static void show(FragmentManager fragmentManager, int widgetId) {
        Bundle args = new Bundle();
        args.putInt(ARG_WIDGET_ID, widgetId);
        DialogFragment dialogFragment = new DateFormatDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(fragmentManager, "DateFormatPicker");
    }

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
        int widgetId = getArguments().getInt(ARG_WIDGET_ID);
        Settings settings = new Settings(getActivity());
        String timeZone = settings.getWidgetOptions(widgetId).timeZoneId;
        return new String[]{
                NixieUtils.getCurrentDate(false, timeZone),
                NixieUtils.getCurrentDate(true, timeZone)
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
