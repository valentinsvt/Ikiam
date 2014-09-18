package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.nth.ikiam.db.*;
import com.nth.ikiam.utils.FotoEspecieDownloader;
import com.nth.ikiam.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DELL on 03/09/2014.
 */
public class NotaCreateFrgment extends Fragment implements Button.OnClickListener {
    MapActivity context;
    Button btnSave;
    Button btnMap;
    TextView txtTitulo;
    TextView txtContenido;
    Nota nota;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
        Utils.hideSoftKeyboard(context);
        View view = inflater.inflate(R.layout.nota_create_layout, container, false);

        long notaId = getArguments().getLong("nota");
        if (notaId > -1) {
            nota = Nota.get(context, notaId);
        } else {
            nota = new Nota(context);
        }
//        nota = new Nota(context);
        txtTitulo = (TextView) view.findViewById(R.id.nota_create_title);
        txtContenido = (TextView) view.findViewById(R.id.nota_create_contenido);

        txtTitulo.setText(nota.titulo);
        txtContenido.setText(nota.contenido);

        btnSave = (Button) view.findViewById(R.id.nota_create_btn_save);
        btnSave.setOnClickListener(this);
        btnMap = (Button) view.findViewById(R.id.nota_create_btn_map);
        if (notaId > -1 && nota.getCoordenada(context) != null) {
            btnMap.setVisibility(View.VISIBLE);
            btnMap.setOnClickListener(this);
        } else {
            btnMap.setVisibility(View.GONE);
        }

        return view;
        //http://dioma.deviantart.com/art/Textures-Paper-58028330?q=boost:popular+in:resources/textures+paper&qo=11
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnSave.getId()) {
            nota.titulo = txtTitulo.getText().toString().trim();
            nota.contenido = txtContenido.getText().toString().trim();
            nota.save();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.nota_confirm_mapa_contenido)
                    .setTitle(R.string.nota_confirm_mapa_titulo);
// Add the buttons
            builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    context.notaSinCoords = nota;
                    context.selectItem(context.MAP_POS);

                    Toast.makeText(getActivity(), getString(R.string.nota_locate_map), Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    Fragment fragment = new NotepadFragment();
                    Utils.openFragment(context, fragment, getString(R.string.notepad_title));

                    Toast.makeText(getActivity(), getString(R.string.nota_create_saved), Toast.LENGTH_LONG).show();
                }
            });
            // Set other dialog properties
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (view.getId() == btnMap.getId()) {
            context.ubicarNotas();
            context.selectItem(context.MAP_POS);
        }
    }
}