package com.nth.ikiam.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Familia;
import com.nth.ikiam.db.Genero;

import java.util.List;

/**
 * Created by luz on 01/08/14.
 */
public class EncyclopediaFirstLevelAdapter extends BaseExpandableListAdapter {

    String[] nivel2;
    String[] nivel3;
    Context context;

    List<Familia> familias;

    public EncyclopediaFirstLevelAdapter(Context context, List<Familia> familias, String[] nivel2, String[] nivel3) {
        this.context = context;
        this.familias = familias;
    }

    @Override
    public Object getChild(int arg0, int arg1) {
        return arg1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Familia getFamilia(int pos) {
        return familias.get(pos);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        System.out.println("pos: " + groupPosition + " familia: " + getFamilia(groupPosition).nombre);
        EncyclopediaSecondLevelListView secondLevelexplv = new EncyclopediaSecondLevelListView(context);
        List<Genero> generos = Genero.findAllByFamilia(context, getFamilia(groupPosition));
        secondLevelexplv.setAdapter(new EncyclopediaSecondLevelAdapter(context, childPosition, generos, nivel3));
        secondLevelexplv.setGroupIndicator(null);

//        secondLevelexplv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
////                Toast.makeText(context, "click 2", Toast.LENGTH_LONG).show();
//                String selected = (String) EncyclopediaSecondLevelAdapter.getChildS(groupPosition, childPosition);
//                Toast.makeText(context, selected, Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });

        return secondLevelexplv;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Familia familia = familias.get(groupPosition);
        return Genero.countByFamilia(context, familia);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return familias.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        TextView tv = new TextView(context);
//        tv.setText("->FirstLevel " + groupPosition + " " + nivel1[groupPosition]);
//        tv.setTextColor(Color.BLACK);
//        tv.setTextSize(20);
//        tv.setBackgroundColor(Color.CYAN);
//        tv.setPadding(10, 7, 7, 7);
//
//        return tv;
        String label = getFamilia(groupPosition).nombre;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.encyclopedia_nivel_1, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_1_lbl);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(label);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}