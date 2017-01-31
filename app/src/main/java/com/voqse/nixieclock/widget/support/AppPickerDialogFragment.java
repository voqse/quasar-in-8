package com.voqse.nixieclock.widget.support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.clock.ExternalApp;

import java.util.List;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class AppPickerDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        if (!(getActivity() instanceof OnAppSelectedListener)) {
            throw new IllegalStateException("To use this dialog hosted activity must implement OnAppSelectedListener!");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle state) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_to_launch)
                .setAdapter(getAppListAdapter(), this)
                .create();
    }

    private ListAdapter getAppListAdapter() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> appList = packageManager.queryIntentActivities(mainIntent, 0);
        return new AppAdapter(getActivity(), appList, packageManager);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AppAdapter adapter = (AppAdapter) getAppListAdapter();
        ExternalApp app = which == 0 ?
                ExternalApp.DEFAULT_APP :
                new ExternalApp(adapter.getItem(which - 1), getActivity().getPackageManager());
        ((OnAppSelectedListener) getActivity()).onAppSelected(app);
    }

    public interface OnAppSelectedListener {

        void onAppSelected(ExternalApp externalApp);
    }

    private static class AppAdapter extends ArrayAdapter<ResolveInfo> {

        private final PackageManager packageManager;
        private final LayoutInflater layoutInflater;

        AppAdapter(Context context, List<ResolveInfo> appList, PackageManager packageManager) {
            super(context, 0, 0, appList);
            this.packageManager = packageManager;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return super.getCount() + 1;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            boolean newView = convertView == null;
            View view = newView ? layoutInflater.inflate(R.layout.item_list_app, parent, false) : convertView;
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            TextView nameTextView = newView ? (TextView) view.findViewById(R.id.nameTextView) : viewHolder.nameTextView;
            ImageView iconImageView = newView ? (ImageView) view.findViewById(R.id.iconImageView) : viewHolder.iconImageView;
            if (newView) {
                view.setTag(new ViewHolder(nameTextView, iconImageView));
            }

            boolean defaultValue = position == 0;
            ResolveInfo app = defaultValue ? null : getItem(position - 1);
            CharSequence appName = defaultValue ? getContext().getString(R.string.default_app_name) : app.loadLabel(packageManager);
            Drawable appIcon = defaultValue ? getContext().getResources().getDrawable(R.drawable.ic_default_clock) : app.loadIcon(packageManager);
            nameTextView.setText(appName);
            iconImageView.setImageDrawable(appIcon);

            return view;
        }

        private static final class ViewHolder {

            private final TextView nameTextView;
            private final ImageView iconImageView;

            private ViewHolder(TextView nameTextView, ImageView iconImageView) {
                this.nameTextView = nameTextView;
                this.iconImageView = iconImageView;
            }
        }
    }
}
