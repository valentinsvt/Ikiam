package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.nth.ikiam.adapters.BusquedaDownloadResultsEspeciesListAdapter;
import com.nth.ikiam.adapters.NotasListAdapter;
import com.nth.ikiam.db.*;
import com.nth.ikiam.utils.FotoEspecieDownloader;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luz on 03/09/14.
 */
public class NotepadFragment extends ListFragment {

    MapActivity activity;
    List<Nota> notasList;
    NotasListAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        adapter = new NotasListAdapter(getActivity(), notasList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Nota selected = notasList.get(position);

    }
}