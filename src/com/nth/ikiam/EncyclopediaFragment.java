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
import com.nth.ikiam.utils.Utils;

import java.util.List;

/**
 * Created by DELL on 23/07/2014.
 */
public class EncyclopediaFragment extends Fragment {

    MapActivity context;
    String pathFolder;

    ExpandableListView expandableListView;

    public EncyclopediaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
//        pathFolder = getArguments().getString("pathFolder");

        pathFolder = Utils.getFolder(context);
        Utils.hideSoftKeyboard(this.getActivity());


//        System.out.println("Folder: " + pathFolder);

        View view = inflater.inflate(R.layout.encyclopedia_layout, container, false);

//        System.out.println("Hay " + Foto.count(context) + " fotos guardadas");
//        for (Foto foto : Foto.list(context)) {
//            System.out.println(foto.getPath());
//        }

        List<Familia> familias = Familia.list(context);

        expandableListView = (ExpandableListView) view.findViewById(R.id.encyclopedia_level_1);
        expandableListView.setAdapter(new EncyclopediaFirstLevelAdapter(context, this, familias));

//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
//                final String selected = (String) ParentLevelAdapter.getChild(groupPosition, childPosition);
//
//                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });

        return view;
    }

}