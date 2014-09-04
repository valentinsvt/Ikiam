package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nth.ikiam.adapters.EncyclopediaEntriesListAdapter;
import com.nth.ikiam.db.Entry;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.image.ImageUtils;

import java.util.List;

/**
 * Created by DELL on 03/08/2014.
 */
public class EncyclopediaEntriesFragment extends ListFragment {

    MapActivity activity;
    List<Entry> entryList;

    int fotoPos = 0;
    String dialogTitle = "";
    List<Foto> fotos;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        long especieId = getArguments().getLong("especie");
        if (especieId != -1) {
            entryList = Entry.findAllByEspecie(activity, especieId);
        } else {
            entryList = activity.entriesBusqueda;
        }

        EncyclopediaEntriesListAdapter adapter = new EncyclopediaEntriesListAdapter(getActivity(), entryList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Entry selectedEntry = entryList.get(position);
        fotos = Foto.findAllByEntry(activity, selectedEntry);

        LayoutInflater inflater = activity.getLayoutInflater();
        View myView = inflater.inflate(R.layout.encyclopedia_entries_dialog, null);

        final ImageView img = (ImageView) myView.findViewById(R.id.encyclopedia_entries_foto);
        final TextView com = (TextView) myView.findViewById(R.id.encyclopedia_entries_comentarios);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle(R.string.encyclopedia_entries_dialog_title);
        builder.setView(myView);

        builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        if (fotos.size() > 1) {
            builder.setNeutralButton(R.string.dialog_btn_anterior, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (fotoPos > 0) {
                        fotoPos -= 1;
                        setFoto(img);
                    }
                }
            });

            builder.setPositiveButton(R.string.dialog_btn_siguiente, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (fotoPos < fotos.size() - 1) {
                        fotoPos += 1;
                        setFoto(img);
                    }
                }
            });
        }
        String comentarios = selectedEntry.comentarios;
//        while (comentarios.length() < 150) {
//            comentarios += " Lorem ipsum dolor sit amet ";
//        }
        AlertDialog dialog = builder.create();
        setFoto(img);
        com.setText(comentarios);
        dialog.show();
    }

    private void setFoto(ImageView img) {
        img.setImageBitmap(activity.getFotoDialog(fotos.get(fotoPos), activity.screenWidth, 300));
//        dialogTitle = R.string.encyclopedia_entries_dialog_title + " (" + (fotoPos + 1) + "/" + fotos.size() + ")";
//        dialogTitlegetResources().getQuantityString(R.plurals.especie_info_fotos, cantFotos, cantFotos, showing);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(R.string.encyclopedia_title);
    }


}