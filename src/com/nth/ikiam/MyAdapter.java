package com.nth.ikiam;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        View row = inflater.inflate(R.layout.row, parent, false);
        TextView label = (TextView) row.findViewById(R.id.company);
        label.setText(cur_obj.getNombre());
        TextView sub = (TextView) row.findViewById(R.id.sub);
//        sub.setText(cur_obj.getNombre());
        sub.setText(getStringResourceByName(cur_obj.getNombre()));

//        ImageView icon = (ImageView) row.findViewById(R.id.image);
//        icon.setImageResource(cur_obj.getImage_id());

        return row;
    }

    private String getStringResourceByName(String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "string", packageName);
        System.out.println(">>>>>> " + packageName);
        System.out.println("<<<<<<< " + c.getResources().getIdentifier("blanco", "string", packageName));
        System.out.println(">>>>>> " + aString + "     " + resId);
        if (resId == 0) {
            return aString;
        } else {
            System.out.println("??????? " + c.getString(resId));
            return c.getString(resId);
        }
    }
}
