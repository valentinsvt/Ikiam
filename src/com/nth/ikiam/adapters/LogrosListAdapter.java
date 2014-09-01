package com.nth.ikiam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Logro;
import com.nth.ikiam.utils.Utils;

import java.net.URLDecoder;
import java.util.List;

/**
 * Created by DELL on 03/08/2014.
 */
public class LogrosListAdapter extends ArrayAdapter<Logro> {
    private final Context context;
    private final List<Logro> logros;

    String nombreCientifico;
    String nombreComun;
    String nombreFamilia;
    String nombreGenero;
    String nombreEspecie;
    String idEspecie;

    String color1;
    String color2;

    public LogrosListAdapter(Context context, List<Logro> logros) {
        super(context, R.layout.logros_row, logros);
        this.context = context;
        this.logros = logros;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            Logro logro = logros.get(position);

//            TextView itemColor2 = (TextView) convertView.findViewById(R.id.busqueda_download_result_color2);
//            itemColor2.setVisibility(View.GONE);
//            itemColor2.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
