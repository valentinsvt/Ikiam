package com.nth.ikiam.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Familia;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.db.Genero;
import com.nth.ikiam.image.ImageUtils;
//import com.nth.ikiam.utils.ImageLoader;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luz on 01/08/14.
 */
public class EncyclopediaSecondLevelAdapter extends BaseExpandableListAdapter {

    int position;

    List<Genero> generos;
    List<Especie> especies;
    MapActivity context;
    EncyclopediaSecondLevelListView vista;

//    int total;
//    Vector<Especie> vEspecies;
//    Vector<ImageView> vImageViews;

    public EncyclopediaSecondLevelAdapter(MapActivity context, int position, List<Genero> generos, EncyclopediaSecondLevelListView vista) {
        this.context = context;
        this.position = position;
        this.generos = generos;
        this.vista = vista;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Genero getGenero(int pos) {
        return generos.get(pos);
    }

    public Especie getEspecie(int pos) {
        System.out.println("wtf");
        List<Especie> especies = Especie.findAllByGenero(context, getGenero(position));
        return especies.get(pos);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Genero genero = getGenero(position);
        //List<Especie> especies = Especie.findAllByGenero(context, genero);
        Especie especie = especies.get(childPosition);

        Foto foto = Foto.findByEspecie(context, especie);

        int cantFotos = Foto.countByEspecie(context, especie);
        String labelNombreCientifico = genero.nombre + " " + especie.nombre;
        String labelNombreComun = especie.nombreComun;
        String labelCantFotos = "" + cantFotos;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.encyclopedia_nivel_3, null);
        }
        TextView itemNombreCientifico = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_nombre_cientifico);
        TextView itemNombreComun = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_nombre_comun);
        TextView itemCantFotos = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_cant_fotos);
        ImageView itemFoto = (ImageView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_image);

//        vEspecies.add(especie);
//        vImageViews.add(itemFoto);
//        if (especies.size() == total) {
//            ExecutorService queue = Executors.newSingleThreadExecutor();
//            queue.execute(new ImageLoader(context, vImageViews, vEspecies));
//        }

        itemNombreCientifico.setText(labelNombreCientifico);
        itemNombreComun.setText(labelNombreComun);
        itemCantFotos.setText(labelCantFotos);
        if (foto != null) {
            itemFoto.setImageBitmap(ImageUtils.decodeFile(foto.path, 100, 100, true));
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //System.out.println("count!!!");

        Genero genero = getGenero(position);
        especies = Especie.findAllByGenero(context, genero);
        vista.especies = especies;
//        total = especies.size();
//        vEspecies = new Vector<Especie>();
//        vImageViews = new Vector<ImageView>();
//        System.out.println("pos=" + position + " gp=" + groupPosition + "  gen=" + genero.nombre + " count=" + Especie.countByGenero(context, genero));
        return especies.size();
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
//        item.setTypeface(null, Typeface.BOLD);
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