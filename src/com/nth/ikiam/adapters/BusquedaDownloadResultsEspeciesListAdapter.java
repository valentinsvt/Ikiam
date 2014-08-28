package com.nth.ikiam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.db.Genero;
import com.nth.ikiam.image.ImageUtils;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Color;
import com.nth.ikiam.utils.Utils;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by DELL on 03/08/2014.
 */
public class BusquedaDownloadResultsEspeciesListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> especies;

    String nombreCientifico;
    String nombreComun;
    String nombreFamilia;
    String nombreGenero;
    String nombreEspecie;
    String idEspecie;

    String color1;
    String color2;

    public BusquedaDownloadResultsEspeciesListAdapter(Context context, List<String> especies) {
        super(context, R.layout.busqueda_download_results_row, especies);
        this.context = context;
        this.especies = especies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            String especie = especies.get(position);
            String[] datos = especie.split(";");

            nombreComun = URLDecoder.decode(datos[0].trim(), "UTF-8");
            nombreFamilia = URLDecoder.decode(datos[1].trim(), "UTF-8");
            nombreGenero = URLDecoder.decode(datos[2].trim(), "UTF-8");
            nombreEspecie = URLDecoder.decode(datos[3].trim(), "UTF-8");
            idEspecie = datos[4];
            color1 = URLDecoder.decode(datos[5].trim(), "UTF-8");
            color2 = "";
            if (datos.length >= 7) {
                if (!datos[6].equals("-")) {
                    color2 = URLDecoder.decode(datos[6].trim(), "UTF-8");
                }
            }

            nombreCientifico = nombreGenero + " " + nombreEspecie.toLowerCase();

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.busqueda_download_results_row, null);
            }
            TextView itemNombreCientifico = (TextView) convertView.findViewById(R.id.busqueda_download_result_nombre_cientifico);
            TextView itemNombreComun = (TextView) convertView.findViewById(R.id.busqueda_download_result_nombre_comun);
            TextView itemNombreFamilia = (TextView) convertView.findViewById(R.id.busqueda_download_result_nombre_familia);
            TextView itemColor1 = (TextView) convertView.findViewById(R.id.busqueda_download_result_color1);
            TextView itemColor2 = (TextView) convertView.findViewById(R.id.busqueda_download_result_color2);

            itemNombreCientifico.setText(nombreCientifico);
            itemNombreComun.setText(nombreComun);
            itemNombreFamilia.setText(nombreFamilia);
//            System.out.println("COLOR 1 :" + color1 + ":       COLOR 2 :" + color2 + ":");
            itemColor1.setText(Utils.getStringResourceByName(context, "global_color_" + color1));
            if (color2.equals("")) {
                itemColor2.setText(color2);
                itemColor2.setVisibility(View.GONE);
            } else {
                itemColor2.setText(Utils.getStringResourceByName(context, "global_color_" + color2));
                itemColor2.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
