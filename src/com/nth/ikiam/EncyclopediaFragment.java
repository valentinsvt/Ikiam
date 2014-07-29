package com.nth.ikiam;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nth.ikiam.db.Foto;

/**
 * Created by DELL on 23/07/2014.
 */
public class EncyclopediaFragment extends Fragment {

    Context context;

    public EncyclopediaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.encyclopedia_fragment, container, false);

        System.out.println("Hay " + Foto.count(context) + " fotos guardadas");

        return view;
    }
}