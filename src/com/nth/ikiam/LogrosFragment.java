package com.nth.ikiam;

import android.app.ListFragment;
import android.os.Bundle;
import com.nth.ikiam.adapters.BusquedaDownloadResultsEspeciesListAdapter;
import com.nth.ikiam.adapters.LogrosListAdapter;
import com.nth.ikiam.db.Logro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 01/09/2014.
 */
public class LogrosFragment extends ListFragment {

    MapActivity activity;
    List<Logro> logrosList;
    LogrosListAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        logrosList = Logro.findAllByTipoAndNotCompleto(activity, activity.ACHIEV_FOTOS);

        adapter = new LogrosListAdapter(getActivity(), logrosList);
        setListAdapter(adapter);
    }
}