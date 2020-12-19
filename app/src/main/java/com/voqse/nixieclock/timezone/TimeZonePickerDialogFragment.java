package com.voqse.nixieclock.timezone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.utils.NixieUtils;

import java.util.List;
import java.util.TimeZone;

/**
 * Диалог выбора таймзоны. Список таймзон берется из timezones.xml.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class TimeZonePickerDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener {

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
                .setTitle(R.string.timezone)
                .create();
    }

    private CharSequence[] getTimeZonesArray() {
        List<TimeZoneInfo> timeZones = TimeZones.getTimeZoneInfo(getActivity());
        CharSequence[] result = new CharSequence[timeZones.size()];
        int index = 0;
        for (TimeZoneInfo timeZoneInfo : timeZones) {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneInfo.id);
            String offset = timeZone.getDisplayName(false, TimeZone.SHORT);
            result[index++] = NixieUtils.formatTwoLineText(timeZoneInfo.city, offset);
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
