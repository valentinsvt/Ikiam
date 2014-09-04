package com.nth.ikiam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.nth.ikiam.db.Nota;
import com.nth.ikiam.utils.Utils;

/**
 * Created by DELL on 03/09/2014.
 */
public class NotaCreateFrgment extends Fragment implements Button.OnClickListener {
    MapActivity context;
    Button btnSave;
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

        return view;
        //http://dioma.deviantart.com/art/Textures-Paper-58028330?q=boost:popular+in:resources/textures+paper&qo=11
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnSave.getId()) {
            nota.titulo = txtTitulo.getText().toString().trim();
            nota.contenido = txtContenido.getText().toString().trim();
            nota.save();

            Fragment fragment = new NotepadFragment();
            Utils.openFragment(context, fragment, getString(R.string.notepad_title));

            Toast.makeText(getActivity(), getString(R.string.nota_create_saved), Toast.LENGTH_LONG).show();
        }
    }
}