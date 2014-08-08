package com.nth.ikiam;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.utils.CapturaUploader;
import com.nth.ikiam.utils.Utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DELL on 23/07/2014.
 */
public class SettingsFragment extends Fragment implements Button.OnClickListener {

    Button btnUpload;
    Context context;

    public final static String EXTRA_MESSAGE = "com.nth.ikiam.MESSAGE";

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.settings_layout, container, false);
        btnUpload = (Button) view.findViewById(R.id.settings_btn_upload);
        int cantFotos = Foto.countNotUploaded(context);
        String toUpload = getString(R.string.settings_btn_upload, cantFotos);
        btnUpload.setText(toUpload);
        btnUpload.setOnClickListener(this);

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
        }
    }
}