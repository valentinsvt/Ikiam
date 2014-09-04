package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.nth.ikiam.adapters.LogrosListAdapter;
import com.nth.ikiam.db.Logro;

import java.util.List;

/**
 * Created by DELL on 01/09/2014.
 */
public class LogrosFragment extends ListFragment {

    MapActivity activity;
    List<Logro> logrosList;
    LogrosListAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
        if (settings.getInt("logros", 0) == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.achievement_warning_text)
                    .setTitle(R.string.achievement_warning_titulo);
            // Add the buttons
            builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("logros", 1);
                    editor.commit();
                }
            });
            builder.setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            // Set other dialog properties
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        logrosList = Logro.list(activity);

        adapter = new LogrosListAdapter(activity, logrosList);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(R.string.logro_title);
    }
}