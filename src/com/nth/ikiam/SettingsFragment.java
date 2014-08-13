package com.nth.ikiam;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class SettingsFragment extends Fragment implements Button.OnClickListener, FieldListener {

    Button btnUpload;
    Button btnDownload;
    MapActivity context;

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
        updateUploadBtn();
        btnUpload.setOnClickListener(this);
        btnDownload.setOnClickListener(this);

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
}