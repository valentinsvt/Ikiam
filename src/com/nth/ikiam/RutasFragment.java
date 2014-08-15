package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nth.ikiam.adapters.EncyclopediaEntriesListAdapter;
import com.nth.ikiam.adapters.RutasListAdapter;
import com.nth.ikiam.db.Entry;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.db.Ruta;

import java.util.List;

/**
 * Created by Svt on 8/15/2014.
 */
public class RutasFragment extends ListFragment {
    MapActivity activity;
    List<Ruta> list;
    String dialogTitle = "";


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        list = Ruta.list(activity);

        RutasListAdapter adapter = new RutasListAdapter(getActivity(), list);
        setListAdapter(adapter);
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Ruta selected = list.get(position);


    }


}
