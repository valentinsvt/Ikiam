package com.nth.ikiam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.facebook.UiLifecycleHelper;

/**
 * Created by Svt on 8/15/2014.
 */
public class RutaFragment extends Fragment implements Button.OnClickListener {
    MapActivity activity;
    private Button[] botones;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MapActivity) getActivity();
        view = inflater.inflate(R.layout.ruta_fragment, container, false);



        //botones[0] = (Button) view.findViewById(R.id.button_crear);/*Crear cuenta ikiam*/
       // botones[1] = (Button) view.findViewById(R.id.button_logout_ikiam); /*Logout ikiam*/
        //botones[2] = (Button) view.findViewById(R.id.button_login); /*login ikiam*/
        //botones[3] = (Button) view.findViewById(R.id.button_guardar); /*Guardar cuenta ikiam*/
        //botones[1] = (Button) view.findViewById(R.id.btnService);
        //for (int i = 0; i < botones.length; i++) {
          //  botones[i].setOnClickListener(this);
        //}

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == botones[0].getId()) {

        }
        if (v.getId() == botones[1].getId()) {

        }
        if (v.getId() == botones[2].getId()) {

        }
        if (v.getId() == botones[3].getId()) {

        }
    }
}
