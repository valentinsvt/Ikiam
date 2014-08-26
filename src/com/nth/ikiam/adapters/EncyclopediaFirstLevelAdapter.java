package com.nth.ikiam.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nth.ikiam.*;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Familia;
import com.nth.ikiam.db.Genero;
import com.nth.ikiam.utils.Utils;

import java.util.List;

/**
 * Created by luz on 01/08/14.
 */
public class EncyclopediaFirstLevelAdapter extends BaseExpandableListAdapter {

    MapActivity activity;
    EncyclopediaFragment fragment;

    List<Familia> familias;
//    List<Genero> generos;

    public EncyclopediaFirstLevelAdapter(MapActivity activity, EncyclopediaFragment fragment, List<Familia> familias) {
        this.familias = familias;
        this.activity = activity;
        this.fragment = fragment;
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
//        System.out.println("pos: " + groupPosition + " familia: " + getFamilia(groupPosition).nombre);
        EncyclopediaSecondLevelListView secondLevelexplv = new EncyclopediaSecondLevelListView(activity);
//        Utils.setListViewHeightBasedOnChildren(secondLevelexplv);
        final List<Genero> generos = Genero.findAllByFamilia(activity, getFamilia(groupPosition));
        secondLevelexplv.setAdapter(new EncyclopediaSecondLevelAdapter(activity, childPosition, generos, secondLevelexplv));
        secondLevelexplv.setGroupIndicator(null);
        final int gp = childPosition;

        secondLevelexplv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(context, "click 2", Toast.LENGTH_LONG).show();
                List<Especie> especies = Especie.findAllByGenero(activity, generos.get(gp));
                Especie selected = especies.get(childPosition);

//                ListFragment fragment = new EncyclopediaEntriesFragment();
                Fragment fragment = new EncyclopediaEspecieInfoFragment();
                Bundle args = new Bundle();
                args.putLong("especie", selected.id);
                fragment.setArguments(args);

                String nombre = selected.getNombreCientifico() + " (" + selected.nombreComun + ")";

                activity.setTitle(nombre);

                FragmentManager fragmentManager = activity.getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack("")
                        .commit();

//                Toast.makeText(activity, selected.nombreComun, Toast.LENGTH_LONG).show();
//                try {
//                    fragmentManager.beginTransaction().add(fragment, "encyclopedia").
//                            replace(R.id.content_frame, fragment).commit();
//                } catch (Exception e) {
//                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//                    e.printStackTrace();
//                }


                /*
                getSupportFragmentManager().beginTransaction()
                           .add(detailFragment, "detail")
                           // Add this transaction to the back stack
                           .addToBackStack()
                           .commit();
                 */

                return true;
            }
        });

        return secondLevelexplv;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Familia familia = familias.get(groupPosition);
//        generos = Genero.findAllByFamilia(activity, familia);
//        return generos.size();
        return Genero.countByFamilia(activity, familia);
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
            LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.encyclopedia_nivel_1, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_1_lbl);
//        item.setTypeface(null, Typeface.BOLD);
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