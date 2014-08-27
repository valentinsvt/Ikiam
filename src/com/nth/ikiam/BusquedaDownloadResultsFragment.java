package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.nth.ikiam.adapters.BusquedaDownloadResultsEspeciesListAdapter;
import com.nth.ikiam.adapters.BusquedaResultsEspeciesListAdapter;
import com.nth.ikiam.db.*;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 27/08/2014.
 */
public class BusquedaDownloadResultsFragment extends ListFragment {

    MapActivity activity;
    List<String> especiesList;
    BusquedaDownloadResultsEspeciesListAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        String[] especies = activity.strEspeciesList.split("&");
        especiesList = new ArrayList<String>();
        for (String especie : especies) {
            especiesList.add(especie);
        }

        adapter = new BusquedaDownloadResultsEspeciesListAdapter(getActivity(), especiesList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        final String selected = especiesList.get(position);

        final int positionToRemove = position;

        System.out.println("SELECTED:::::: " + selected);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.descarga_busqueda_download_confirmacion)
                .setTitle(R.string.descarga_busqueda_download_title);

        // Add the buttons
        builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                try {
                    String[] datos = selected.split(";");
                    String nombreComun = URLDecoder.decode(datos[0].trim(), "UTF-8");
                    String nombreFamilia = URLDecoder.decode(datos[1].trim(), "UTF-8");
                    String nombreGenero = URLDecoder.decode(datos[2].trim(), "UTF-8");
                    String nombreEspecie = URLDecoder.decode(datos[3].trim(), "UTF-8");
                    String idEspecie = datos[4];
                    String color1 = URLDecoder.decode(datos[5].trim(), "UTF-8");
                    String color2 = "";
                    String idFoto1 = "";
                    String idFoto2 = "";
                    String idFoto3 = "";
                    if (datos.length >= 7) {
                        color2 = URLDecoder.decode(datos[6].trim(), "UTF-8");
                    }
                    if (datos.length >= 8) {
                        idFoto1 = datos[7];
                    }
                    if (datos.length >= 9) {
                        idFoto2 = datos[8];
                    }
                    if (datos.length >= 10) {
                        idFoto3 = datos[0];
                    }

                    Familia nuevaFamilia = Familia.getByNombreOrCreate(activity, nombreFamilia);
                    Genero nuevoGenero = Genero.getByNombreOrCreate(activity, nombreGenero);
                    nuevoGenero.setFamilia(nuevaFamilia);
                    nuevoGenero.save();
                    Especie nuevaEspecie = Especie.getByNombreOrCreate(activity, nombreEspecie);
                    Color nuevoColor1 = Color.findByNombre(activity, color1);
                    Color nuevoColor2 = Color.findByNombre(activity, color2);
                    nuevaEspecie.nombreComun = nombreComun;
                    nuevaEspecie.setGenero(nuevoGenero);
                    nuevaEspecie.setColor1(nuevoColor1);
                    nuevaEspecie.setColor2(nuevoColor2);
                    nuevaEspecie.save();

                    especiesList.remove(positionToRemove);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(activity, getString(R.string.descarga_busqueda_download_ok), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

//        Especie selected = especiesList.get(position);
//        Fragment fragment = new EncyclopediaEspecieInfoFragment();
//        Bundle args = new Bundle();
//        args.putLong("especie", selected.id);
//        fragment.setArguments(args);
//
//        String nombre = selected.getNombreCientifico() + " (" + selected.nombreComun + ")";
//
//        activity.setTitle(nombre);
//
//        FragmentManager fragmentManager = activity.getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

//        Entry selectedEntry = entryList.get(position);
//        fotos = Foto.findAllByEntry(activity, selectedEntry);
//
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View myView = inflater.inflate(R.layout.encyclopedia_entries_dialog, null);
//
//        final ImageView img = (ImageView) myView.findViewById(R.id.encyclopedia_entries_foto);
//        final TextView com = (TextView) myView.findViewById(R.id.encyclopedia_entries_comentarios);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle(R.string.encyclopedia_entries_dialog_title);
//        builder.setView(myView);
//
//        builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.dismiss();
//            }
//        });
//
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
//        String comentarios = selectedEntry.comentarios;
////        while (comentarios.length() < 150) {
////            comentarios += " Lorem ipsum dolor sit amet ";
////        }
//        AlertDialog dialog = builder.create();
//        setFoto(img);
//        com.setText(comentarios);
//        dialog.show();
    }
}