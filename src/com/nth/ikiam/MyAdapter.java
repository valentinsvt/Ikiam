package com.nth.ikiam;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.nth.ikiam.db.Color;

import java.util.ArrayList;

/**
 * Created by luz on 28/07/14.
 */
public class MyAdapter extends BaseAdapter {

    Context c;
    ArrayList<Color> colores;

    public MyAdapter(Context context, ArrayList<Color> colores) {
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
        sub.setText(getStringResourceByName("global_color_" + cur_obj.getNombre()));
        return row;
    }

    private String getStringResourceByName(String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return c.getString(resId);
        }
    }
}
