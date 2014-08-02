package com.nth.ikiam;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;
import com.nth.ikiam.adapters.EncyclopediaFirstLevelAdapter;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Familia;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.db.Genero;

import java.util.List;

/**
 * Created by DELL on 23/07/2014.
 */
public class EncyclopediaFragment extends Fragment {

    Context context;
    String pathFolder;

    ExpandableListView expandableListView;

    public EncyclopediaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        pathFolder = getArguments().getString("pathFolder");

//        System.out.println("Folder: " + pathFolder);

        View view = inflater.inflate(R.layout.encyclopedia_layout, container, false);

//        System.out.println("Hay " + Foto.count(context) + " fotos guardadas");
//        for (Foto foto : Foto.list(context)) {
//            System.out.println(foto.getPath());
//        }

        String[] nivel2 = {"uno nivel 2", "dos nivel 2", "tres nivel 2", "cuatro nivel 2", "cinco nivel 2", "seis nivel 2"};
        String[] nivel3 = {"uno nivel 3", "dos nivel 3", "tres nivel 3", "cuatro nivel 3", "cinco nivel 3", "seis nivel 3"};

        List<Familia> familias = Familia.list(context);

        for (Familia familia : familias) {
            System.out.println("Familia: " + familia.nombre + " <" + familia.id + ">");
            List<Genero> generos = Genero.findAllByFamilia(context, familia);
            for (Genero genero : generos) {
                System.out.println("\tGenero: " + genero.nombre + " <" + familia.id + ">" + " <" + genero.id + ">");
                List<Especie> especies = Especie.findAllByGenero(context, genero);
                for (Especie especie : especies) {
                    System.out.println("\t\tEspecie: " + especie.nombre + " (" + especie.nombreComun + ")" + " <" + familia.id + ">" + " <" + genero.id + ">" + " <" + especie.id + ">");
                }
            }
        }


        expandableListView = (ExpandableListView) view.findViewById(R.id.encyclopedia_level_1);
        expandableListView.setAdapter(new EncyclopediaFirstLevelAdapter(context, familias, nivel2, nivel3));

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
//                final String selected = (String) ParentLevelAdapter.getChild(groupPosition, childPosition);
//
//                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        return view;
    }

}