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
import org.json.JSONArray;
import org.json.JSONObject;

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

//        System.out.println(activity.strEspeciesList);
        try {
            JSONArray arr = new JSONArray(activity.strEspeciesList);
            especiesList = new ArrayList<String>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                especiesList.add(obj.toString());
//                System.out.println(obj.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        String[] especies = activity.strEspeciesList.split("&");
//        especiesList = new ArrayList<String>();
//        for (String especie : especies) {
//            especiesList.add(especie);
//        }

        adapter = new BusquedaDownloadResultsEspeciesListAdapter(getActivity(), especiesList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final String selected = especiesList.get(position);
        final int positionToRemove = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.descarga_busqueda_download_confirmacion)
                .setTitle(R.string.descarga_busqueda_download_title);
// Add the buttons
        builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                try {
//                    System.out.println("**********************************" + selected);
                    JSONObject obj = new JSONObject(selected);

                    int cantFotos = 0;
                    String nombreComun = URLDecoder.decode(obj.getString("nombreComun"), "UTF-8");
                    String nombreFamilia = URLDecoder.decode(obj.getString("familia"), "UTF-8");
                    String nombreGenero = URLDecoder.decode(obj.getString("genero"), "UTF-8");
                    String nombreEspecie = URLDecoder.decode(obj.getString("especie"), "UTF-8");
                    String color1 = URLDecoder.decode(obj.getString("color1"), "UTF-8");
                    String color2 = URLDecoder.decode(obj.getString("color2"), "UTF-8");
                    Vector<String> fotosPaths = new Vector<String>();
                    Vector<String> fotosLats = new Vector<String>();
                    Vector<String> fotosLongs = new Vector<String>();
                    Vector<String> fotosAlts = new Vector<String>();
                    Vector<String> fotosComs = new Vector<String>();
                    Vector<String> fotosKeys = new Vector<String>();

                    JSONArray fotos = obj.getJSONArray("fotos");

                    for (int i = 0; i < fotos.length(); i++) {
                        JSONObject foto = fotos.getJSONObject(i);
                        fotosPaths.add(foto.getString("path"));
                        fotosLats.add(foto.getString("latitud"));
                        fotosLongs.add(foto.getString("longitud"));
                        fotosAlts.add(foto.getString("altitud"));
                        fotosComs.add(foto.getString("observaciones"));
                        fotosKeys.add(foto.getString("keywords"));
                        cantFotos++;
                    }

                    //http://www.tutorialspoint.com/android/android_json_parser.htm

                    Familia nuevaFamilia = Familia.getByNombreOrCreate(activity, nombreFamilia);
                    Genero nuevoGenero = Genero.getByNombreOrCreate(activity, nombreGenero);
                    nuevoGenero.setFamilia(nuevaFamilia);
                    nuevoGenero.save();
                    Especie nuevaEspecie = Especie.getByNombreOrCreate(activity, nombreEspecie);
                    Color nuevoColor1 = Color.findByNombre(activity, color1);
                    Color nuevoColor2 = Color.findByNombre(activity, color2);

                    nuevaEspecie.setNombreComun(nombreComun);
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
                    queue.execute(new FotoEspecieDownloader(activity, queue, 0, fotosPaths, fotosLats, fotosLongs, fotosAlts, fotosComs, fotosKeys, nuevaEspecie));

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
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(R.string.busqueda_title);
    }
}