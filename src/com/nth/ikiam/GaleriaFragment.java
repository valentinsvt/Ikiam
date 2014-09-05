package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nth.ikiam.adapters.EncyclopediaEntriesListAdapter;
import com.nth.ikiam.db.Entry;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.utils.CapturaUploader;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DELL on 12/08/2014.
 */
public class GaleriaFragment extends ListFragment {

    MapActivity activity;
    List<Entry> entryList;

    int fotoPos = 0;
    String dialogTitle = "";
    List<Foto> fotos;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        entryList = Entry.findAllByEspecieIsNull(activity);

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
        String t = getResources().getQuantityString(R.plurals.encyclopedia_entries_dialog_title, fotos.size());
        builder.setTitle(t);
        builder.setView(myView);

        builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        if (fotos.size() > 0) {
            builder.setNeutralButton(R.string.ruta_subir, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setPositiveButton(R.string.dialog_btn_descargar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    String userId = activity.userId; //id (faceboook - fb id, ikiam db.id
                    if (userId != null && !userId.equals("-1")) {
                        for (Foto foto : fotos) {
                            if (foto.uploaded == 0) {
                                Toast.makeText(activity, getString(R.string.uploader_upload_pending), Toast.LENGTH_LONG).show();
                                ExecutorService queue = Executors.newSingleThreadExecutor();
                                queue.execute(new CapturaUploader(activity, queue, foto, 0));
                            } else {
                                Toast.makeText(activity, getString(R.string.uploader_upload_ok), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.uploader_login_error), Toast.LENGTH_LONG).show();
                        System.out.println("Login first!!!");
                        activity.selectItem(activity.LOGIN_POS_T);
                    }
                }
            });
        }

//        if (fotos.size() > 1) {
//            builder.setNeutralButton(R.string.dialog_btn_anterior, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    if (fotoPos > 0) {
//                        fotoPos -= 1;
//                        setFoto(img);
//                    }
//                }
//            });
//
//            builder.setPositiveButton(R.string.dialog_btn_siguiente, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    if (fotoPos < fotos.size() - 1) {
//                        fotoPos += 1;
//                        setFoto(img);
//                    }
//                }
//            });
//        }
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
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(R.string.gallery_title);
    }
}