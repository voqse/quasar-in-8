package com.voqse.nixieclock.timezone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.List;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class TimeZonePickerDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        if (!(getActivity() instanceof OnTimeZoneSelectedListener)) {
            throw new IllegalStateException("To use this dialog hosted activity must implement OnTimeZoneSelectedListener!");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle state) {
        return new AlertDialog.Builder(getActivity())
                .setItems(getTimeZonesArray(), this)
                .create();
    }

    private String[] getTimeZonesArray() {
        List<TimeZoneInfo> timeZones = TimeZones.getTimeZoneInfo(getActivity());
        String[] result = new String[timeZones.size()];
        int index = 0;
        for (TimeZoneInfo timeZoneInfo : timeZones) {
            result[index++] = TimeZones.format(timeZoneInfo);
        }
        return result;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        List<TimeZoneInfo> timeZones = TimeZones.getTimeZoneInfo(getActivity());
        TimeZoneInfo timeZone = timeZones.get(which);
        ((OnTimeZoneSelectedListener) getActivity()).onTimeZoneSelected(timeZone);
    }

    public interface OnTimeZoneSelectedListener {

        void onTimeZoneSelected(TimeZoneInfo timeZone);
    }
}
