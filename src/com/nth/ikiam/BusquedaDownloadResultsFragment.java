package com.nth.ikiam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nth.ikiam.adapters.BusquedaDownloadResultsEspeciesListAdapter;
import com.nth.ikiam.adapters.BusquedaResultsEspeciesListAdapter;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Foto;

import java.util.List;

/**
 * Created by DELL on 27/08/2014.
 */
public class BusquedaDownloadResultsFragment extends ListFragment {

    MapActivity activity;
    List<String> especiesList;
    String strEspeciesList;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        String[] especies = strEspeciesList.split("&");

        for (String especie : especies) {
            especiesList.add(especie);
        }
//            String[] datos = especie.split(";");
//            /*
//            str += e.nombreComun + ";" + e.genero?.familia?.nombre + ";" + e.genero?.nombre + ";" + e.nombre + ";" + e.color1.color
//            if (e.color2) {
//                str += ";" + e.color2.color
//            }
//             */
//            String nombreComun = datos[0];
//            String nombreFamilia = datos[1];
//            String nombreGenero = datos[2];
//            String nombreEspecie = datos[3];
//            String color1 = datos[4];
//            String color2 = "";
//            if (datos.length == 6) {
//                color2 = datos[5];
//            }
//        }


//        especiesList = activity.especiesBusqueda;

//        for (Foto foto : fotoList) {
//            System.out.println("--------------------------- " + foto.id);
//        }

        BusquedaDownloadResultsEspeciesListAdapter adapter = new BusquedaDownloadResultsEspeciesListAdapter(getActivity(), especiesList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
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