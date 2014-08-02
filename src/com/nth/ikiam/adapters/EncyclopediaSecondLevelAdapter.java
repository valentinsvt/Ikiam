package com.nth.ikiam.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Familia;
import com.nth.ikiam.db.Genero;

import java.util.List;

/**
 * Created by luz on 01/08/14.
 */
public class EncyclopediaSecondLevelAdapter extends BaseExpandableListAdapter {

    String[] nivel2;
    String[] nivel3;
    int position;

    List<Genero> generos;

    Context context;

    public EncyclopediaSecondLevelAdapter(Context context, int position, List<Genero> generos, String[] nivel3) {
        this.context = context;
        this.position = position;
        this.generos = generos;
        this.nivel3 = nivel3;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Genero getGenero(int pos) {
        return generos.get(pos);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        TextView tv = new TextView(context);
//        tv.setText("child " + groupPosition + " " + nivel3[childPosition]);
//        tv.setTextColor(Color.BLACK);
//        tv.setTextSize(20);
//        tv.setPadding(15, 5, 5, 5);
//        tv.setBackgroundColor(Color.YELLOW);
//        tv.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        return tv;
        System.out.println(getGenero(position).nombre);
        List<Especie> especies = Especie.findAllByGenero(context, getGenero(position));
        Especie especie = especies.get(childPosition);
        String label = especie.nombre + " (" + especie.nombreComun + ")";
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.encyclopedia_nivel_3, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_lbl);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(label);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Genero genero = getGenero(position);
        System.out.println("pos=" + position + " gp=" + groupPosition + "  gen=" + genero.nombre + " count=" + Especie.countByGenero(context, genero));
        return Especie.countByGenero(context, genero);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "--" + groupPosition;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        TextView tv = new TextView(context);
//        tv.setText("-->Second Level " + groupPosition + " " + nivel2[position]);
//        tv.setTextColor(Color.BLACK);
//        tv.setTextSize(20);
//        tv.setPadding(12, 7, 7, 7);
//        tv.setBackgroundColor(Color.RED);
//        tv.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        return tv;
        String label = generos.get(position).nombre;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.encyclopedia_nivel_2, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_2_lbl);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(label);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

}