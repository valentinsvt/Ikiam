package com.nth.ikiam;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by DELL on 23/07/2014.
 */
public class EncyclopediaFragment extends Fragment {

    Context context;
    String pathFolder;

    public EncyclopediaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        pathFolder = getArguments().getString("pathFolder");

        System.out.println("Folder: " + pathFolder);

        View view = inflater.inflate(R.layout.encyclopedia_layout, container, false);

        System.out.println("Hay " + Foto.count(context) + " fotos guardadas");
        for (Foto foto : Foto.list(context)) {
            System.out.println(foto.getPath());
        }

        return view;
    }

}