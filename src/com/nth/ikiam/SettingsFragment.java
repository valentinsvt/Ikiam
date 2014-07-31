package com.nth.ikiam;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by DELL on 23/07/2014.
 */
public class SettingsFragment extends Fragment {

    Button btn;
    Context context;

    public final static String EXTRA_MESSAGE = "com.nth.ikiam.MESSAGE";

    public SettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.settings_layout, container, false);

        return view;
    }

}