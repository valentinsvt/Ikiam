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
import com.nth.ikiam.utils.FotoEspecieDownloader;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

//        System.out.println("SELECTED:::::: " + selected);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.descarga_busqueda_download_confirmacion)
                .setTitle(R.string.descarga_busqueda_download_title);

        // Add the buttons
        builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                try {
                    System.out.println("**********************************" + selected);
                    String[] datos = selected.split(";");
                    String nombreComun = URLDecoder.decode(datos[0].trim(), "UTF-8");
                    String nombreFamilia = URLDecoder.decode(datos[1].trim(), "UTF-8");
                    String nombreGenero = URLDecoder.decode(datos[2].trim(), "UTF-8");
                    String nombreEspecie = URLDecoder.decode(datos[3].trim(), "UTF-8");
                    String idEspecie = datos[4];
                    String color1 = URLDecoder.decode(datos[5].trim(), "UTF-8");
                    String color2 = "";
                    Vector<String> fotosPaths = new Vector<String>();
                    Vector<String> fotosLats = new Vector<String>();
                    Vector<String> fotosLongs = new Vector<String>();
                    Vector<String> fotosAlts = new Vector<String>();
                    Vector<String> fotosComs = new Vector<String>();

                    int cantFotos = 0;

                    if (datos.length >= 7) {
                        color2 = URLDecoder.decode(datos[6].trim(), "UTF-8");
                    }
                    if (datos.length >= 8) {
                        fotosPaths.add(datos[7]);
                        fotosLats.add(datos[8]);
                        fotosLongs.add(datos[9]);
                        fotosAlts.add(datos[10]);
                        fotosComs.add(datos[11]);

                        cantFotos++;
                    }
                    if (datos.length >= 13) {
                        fotosPaths.add(datos[12]);
                        fotosLats.add(datos[13]);
                        fotosLongs.add(datos[14]);
                        fotosAlts.add(datos[15]);
                        fotosComs.add(datos[16]);

                        cantFotos++;
                    }
                    if (datos.length >= 18) {
                        fotosPaths.add(datos[17]);
                        fotosLats.add(datos[18]);
                        fotosLongs.add(datos[19]);
                        fotosAlts.add(datos[20]);
                        fotosComs.add(datos[21]);

                        cantFotos++;
                    }/******************************************************/
                    if (datos.length >= 23) {
                        fotosPaths.add(datos[22]);
                        fotosLats.add(datos[23]);
                        fotosLongs.add(datos[24]);
                        fotosAlts.add(datos[25]);
                        fotosComs.add(datos[26]);

                        cantFotos++;
                    }
                    if (datos.length >= 28) {
                        fotosPaths.add(datos[27]);
                        fotosLats.add(datos[28]);
                        fotosLongs.add(datos[29]);
                        fotosAlts.add(datos[30]);
                        fotosComs.add(datos[31]);

                        cantFotos++;
                    }
                    if (datos.length >= 33) {
                        fotosPaths.add(datos[32]);
                        fotosLats.add(datos[33]);
                        fotosLongs.add(datos[34]);
                        fotosAlts.add(datos[35]);
                        fotosComs.add(datos[36]);

                        cantFotos++;
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
                    String texto = getString(R.string.descarga_busqueda_download_ok, nuevaEspecie.nombreComun);
                    if (cantFotos > 0) {
                        texto = getResources().getQuantityString(R.plurals.descarga_busqueda_download_ok, cantFotos, nuevaEspecie.nombreComun, cantFotos);
                    }
                    Toast.makeText(activity, texto, Toast.LENGTH_LONG).show();

                    ExecutorService queue = Executors.newSingleThreadExecutor();
                    queue.execute(new FotoEspecieDownloader(activity, queue, 0, fotosPaths, fotosLats, fotosLongs, fotosAlts, fotosComs, nuevaEspecie));

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

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(R.string.busqueda_title);
    }
}