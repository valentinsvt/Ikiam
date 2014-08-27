package com.nth.ikiam.adapters;

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

/**
 * Created by luz on 28/07/14.
 */
public class CapturaColorSpinnerAdapter extends BaseAdapter {

    MapActivity c;
    ArrayList<Color> colores;

    public CapturaColorSpinnerAdapter(MapActivity context, ArrayList<Color> colores) {
        super();
        this.c = context;
        this.colores = colores;
    }

    @Override
    public int getCount() {
        return colores.size();
    }

    @Override
    public Object getItem(int position) {
        return colores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Color cur_obj = colores.get(position);
        LayoutInflater inflater = ((Activity) c).getLayoutInflater();
        View row = inflater.inflate(R.layout.captura_select_row, parent, false);
        TextView sub = (TextView) row.findViewById(R.id.captura_row_color_label);
        sub.setText(Utils.getStringResourceByName(c, "global_color_" + cur_obj.getNombre()));
        return row;
    }
}
