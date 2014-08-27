package com.nth.ikiam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by DELL on 26/08/2014.
 */
public class EstadisticasFragment extends Fragment {

    TextView txtFotosTomadas;
    TextView txtFotosSubidas;
    TextView txtFotosCompartidas;
    TextView txtDistanciaRecorrida;
    MapActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
        View view = inflater.inflate(R.layout.estadisticas_layout, container, false);

        txtFotosTomadas = (TextView) view.findViewById(R.id.estadisticas_fotos_tomadas);
        txtFotosSubidas = (TextView) view.findViewById(R.id.estadisticas_fotos_subidas);
        txtFotosCompartidas = (TextView) view.findViewById(R.id.estadisticas_fotos_compartidas);
        txtDistanciaRecorrida = (TextView) view.findViewById(R.id.estadisticas_distancia_recorrida);

        txtFotosTomadas.setText("" + (int) context.getAchievement(context.ACHIEV_FOTOS));
        txtFotosSubidas.setText("" + (int) context.getAchievement(context.ACHIEV_UPLOADS));
        txtFotosCompartidas.setText("" + (int) context.getAchievement(context.ACHIEV_SHARE));
        txtDistanciaRecorrida.setText("" + context.getAchievement(context.ACHIEV_DISTANCIA)+" m.");

        return view;
    }
}