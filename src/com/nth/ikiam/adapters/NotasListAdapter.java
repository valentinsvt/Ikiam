package com.nth.ikiam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.Nota;
import com.nth.ikiam.utils.Utils;

import java.net.URLDecoder;
import java.util.List;

/**
 * Created by DELL on 03/08/2014.
 */
public class NotasListAdapter extends ArrayAdapter<Nota> {
    private final Context context;
    private final List<Nota> notas;

    public NotasListAdapter(Context context, List<Nota> notas) {
        super(context, R.layout.notas_row, notas);
        this.context = context;
        this.notas = notas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            Nota nota = notas.get(position);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.notas_row, null);
            }
            TextView itemTitulo = (TextView) convertView.findViewById(R.id.notas_row_titulo);
            TextView itemContenido = (TextView) convertView.findViewById(R.id.notas_row_contenido);
            TextView itemFecha = (TextView) convertView.findViewById(R.id.notas_row_fecha);
            TextView itemCoords = (TextView) convertView.findViewById(R.id.notas_row_coords);

            String contenido = "";

            if (nota.contenido != null) {
                String[] parts = nota.contenido.split("\\n");
                contenido = parts[0];
                if (contenido.length() >= 99) {
                    contenido = contenido.substring(0, 99) + "…";
                } else {
                    if (parts.length > 1) {
                        contenido += "…";
                    }
                }
            }

            itemTitulo.setText(nota.titulo);
            itemContenido.setText(contenido);
            itemFecha.setText(nota.fecha);
            Coordenada coords = nota.getCoordenada(context);
            if (coords != null) {
                itemCoords.setText(context.getString(R.string.nota_en_mapa));
                itemCoords.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
