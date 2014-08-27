package com.nth.ikiam;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.nth.ikiam.utils.BusquedaDownloader;
import com.nth.ikiam.utils.CapturaUploader;
import com.nth.ikiam.utils.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DELL on 26/08/2014.
 */
public class DescargaBusquedaFragment extends Fragment implements View.OnClickListener {

    MapActivity context;

    ImageButton btnBuscar;

    EditText txtNombreComun;
    EditText txtNombreFamilia;
    EditText txtNombreGenero;
    EditText txtNombreEspecie;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
        View view = inflater.inflate(R.layout.descarga_busqueda_layout, container, false);

        txtNombreComun = (EditText) view.findViewById(R.id.descarga_busqueda_nombre_comun);
        txtNombreFamilia = (EditText) view.findViewById(R.id.descarga_busqueda_nombre_familia);
        txtNombreGenero = (EditText) view.findViewById(R.id.descarga_busqueda_nombre_genero);
        txtNombreEspecie = (EditText) view.findViewById(R.id.descarga_busqueda_nombre_especie);

        btnBuscar = (ImageButton) view.findViewById(R.id.descarga_busqueda_buscar_btn);
        btnBuscar.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnBuscar.getId()) {
            if (Utils.isNetworkAvailable(context)) {
                String nombreComun = txtNombreComun.getText().toString().trim();
                String nombreFamilia = txtNombreFamilia.getText().toString().trim();
                String nombreGenero = txtNombreGenero.getText().toString().trim();
                String nombreEspecie = txtNombreEspecie.getText().toString().trim();

                boolean ok = true;

                if (!nombreGenero.equals("") && nombreFamilia.equals("")) {
                    alerta(getString(R.string.captura_error_nombre_familia));
                    ok = false;
                }
                if (!nombreEspecie.equals("") && nombreGenero.equals("")) {
                    alerta(getString(R.string.captura_error_nombre_genero));
                    ok = false;
                }

                if (ok) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setTitle(getString(R.string.descarga_busqueda_buscando));
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();

                    ExecutorService queue = Executors.newSingleThreadExecutor();
                    queue.execute(new BusquedaDownloader(context, queue, 0, nombreComun, nombreFamilia, nombreGenero, nombreEspecie));
                }


            }
        }
    }


    private void alerta(String string) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

}