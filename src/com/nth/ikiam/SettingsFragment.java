package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.listeners.FieldListener;
import com.nth.ikiam.utils.CapturaUploader;
import com.nth.ikiam.utils.Utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DELL on 23/07/2014.
 */
public class SettingsFragment extends Fragment implements Button.OnClickListener, FieldListener, CompoundButton.OnCheckedChangeListener {

    Button btnUpload;
    Button btnDownload;
    Button btnEstadisticas;
    Button btnResetEstadisticas;
    MapActivity context;

    CheckBox checkAchievements;

    public SettingsFragment() {

    }

    public void updateUploadButton() {
        if (context != null) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int cantFotos = Foto.countNotUploaded(context);
                    String toUpload = getString(R.string.settings_btn_upload, cantFotos);
                    btnUpload.setText(toUpload);
                }
            });
        }
    }

    private void updateUploadBtn() {
        int cantFotos = Foto.countNotUploaded(context);
        String toUpload = getString(R.string.settings_btn_upload, cantFotos);
        btnUpload.setText(toUpload);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
        View view = inflater.inflate(R.layout.settings_layout, container, false);
        btnUpload = (Button) view.findViewById(R.id.settings_btn_upload);
        btnDownload = (Button) view.findViewById(R.id.settings_btn_download);
        btnEstadisticas = (Button) view.findViewById(R.id.settings_btn_estadisticas);
        btnResetEstadisticas = (Button) view.findViewById(R.id.settings_btn_reset_achievements);

        checkAchievements = (CheckBox) view.findViewById(R.id.settings_chk_achievements);
        SharedPreferences settings = context.getSharedPreferences(context.PREFS_NAME, 0);
        int logros = settings.getInt("logros", 0);
        if (logros == 1) {
            checkAchievements.setChecked(true);
        } else {
            checkAchievements.setChecked(false);
        }
        checkAchievements.setOnCheckedChangeListener(this);

        updateUploadBtn();
        btnUpload.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        btnEstadisticas.setOnClickListener(this);
        btnResetEstadisticas.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Utils.hideSoftKeyboard(this.getActivity());
        if (view.getId() == btnUpload.getId()) { // upload
            List<Foto> porSubir = Foto.findAllNotUploaded(context);
            for (Foto foto : porSubir) {
                ExecutorService queue = Executors.newSingleThreadExecutor();
                queue.execute(new CapturaUploader(context, queue, foto, 0));
            }
        } else if (view.getId() == btnDownload.getId()) { // download
            Fragment fragment = new DescargaBusquedaFragment();

            context.setTitle(getString(R.string.settings_descargar));

            FragmentManager fragmentManager = context.getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack("")
                    .commit();
        } else if (view.getId() == btnEstadisticas.getId()) {
            Fragment fragment = new EstadisticasFragment();

            context.setTitle(getString(R.string.settings_estadisticas));

            FragmentManager fragmentManager = context.getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack("")
                    .commit();
        } else if (view.getId() == btnResetEstadisticas.getId()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.estadisticas_reset_confirmacion)
                    .setTitle(R.string.settings_reset_achievements);

            // Add the buttons
            builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    context.setAchivement(context.ACHIEV_DISTANCIA, 0f);
                    context.setAchivement(context.ACHIEV_FOTOS, 0f);
                    context.setAchivement(context.ACHIEV_UPLOADS, 0f);
                    context.setAchivement(context.ACHIEV_SHARE, 0f);

                    Toast.makeText(context, getString(R.string.estadisticas_reset_ok), Toast.LENGTH_LONG).show();
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
    }

    @Override
    public void fieldValueChanged(String fieldName, String newValue) {
        if (fieldName.equals("errorMessage")) {
            if (!newValue.equals("")) {
                String msg = newValue.toString();
                context.showToast(msg);
                context.errorMessage = "";
                updateUploadButton();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int val = 0;
        if (checkAchievements.isChecked()) {
            val = 1;
        }
        SharedPreferences settings = context.getSharedPreferences(context.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("logros", val);
        editor.commit();
    }
}