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
import java.util.List;

/**
 * Created by DELL on 03/08/2014.
 */
public class BusquedaDownloadResultsEspeciesListAdapter extends ArrayAdapter<Especie> {
    private final Context context;
    private final List<Especie> especies;

    public BusquedaDownloadResultsEspeciesListAdapter(Context context, List<Especie> especies) {
        super(context, R.layout.busqueda_download_results_row, especies);
        this.context = context;
        this.especies = especies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Especie especie = especies.get(position);
        Genero genero = especie.getGenero(context);
        String nombreComun = especie.nombreComun;
        String nombreCientifico = genero.nombre + " " + especie.nombre.toLowerCase();

        List<Foto> fotos = Foto.findAllByEspecie(context, especie);
        Foto foto = fotos.get(0);
        File imgFile = new File(foto.path);


        int cantFotos = Foto.countByEspecie(context, especie);
        String labelNombreCientifico = genero.nombre + " " + especie.nombre;
        String labelNombreComun = especie.nombreComun;
        String labelCantFotos = "" + cantFotos;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.busqueda_results_row, null);
        }
        TextView itemNombreCientifico = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_nombre_cientifico);
        TextView itemNombreComun = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_nombre_comun);
        TextView itemCantFotos = (TextView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_cant_fotos);
        ImageView itemFoto = (ImageView) convertView.findViewById(R.id.encyclopedia_group_item_nivel_3_image);

        itemNombreCientifico.setText(labelNombreCientifico);
        itemNombreComun.setText(labelNombreComun);
        itemCantFotos.setText(labelCantFotos);
        itemFoto.setImageBitmap(ImageUtils.decodeFile(foto.path, 100, 100, true));
        return convertView;
        /* **********************************************/

//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rowView = inflater.inflate(R.layout.busqueda_results_row, parent, false);
//        TextView textViewNCo = (TextView) rowView.findViewById(R.id.encyclopedia_entries_row_nombre_comun);
//        TextView textViewNCi = (TextView) rowView.findViewById(R.id.encyclopedia_entries_row_nombre_cientifico);
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.encyclopedia_entries_row_image);
//
//        textViewNCo.setText(nombreComun);
//        textViewNCi.setText(nombreCientifico);
//        if (imgFile.exists()) {
//            Bitmap myBitmap = ImageUtils.decodeFile(imgFile.getAbsolutePath(), 100, 100);
//            imageView.setImageBitmap(myBitmap);
//        }
//        return rowView;
    }

//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rowView = inflater.inflate(R.layout.busqueda_results_row, parent, false);
////        System.out.println(rowView);
//
////        TextView textViewNombre = (TextView) rowView.findViewById(R.id.encyclopedia_entries_row_nombre_comun);
//        TextView textViewNCo = (TextView) rowView.findViewById(R.id.encyclopedia_entries_row_nombre_comun);
//        TextView textViewNCi = (TextView) rowView.findViewById(R.id.encyclopedia_entries_row_nombre_cientifico);
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.encyclopedia_entries_row_image);
//
////        System.out.println("POS " + position + "  " + entries.size());
//
//        Especie selectedEspecie = especies.get(position);
//        Genero genero = selectedEspecie.getGenero(context);
//        String nombreComun = selectedEspecie.nombreComun;
//        String nombreCientifico = genero.nombre + " " + selectedEspecie.nombre.toLowerCase();
//
////        while (comentarios.length() < 150) {
////            comentarios += " Lorem ipsum dolor sit amet ";
////        }
//
////        if (nombreComun.length() > 127) {
////            nombreComun = nombreComun.substring(0, 127) + "...";
////        }
//
////        textViewNombre.setText(selectedEntry.getEspecie().getNombreCientifico() + " (" + selectedEntry.getEspecie().nombreComun + ")");
//        textViewNCo.setText(nombreComun);
//        textViewNCi.setText(nombreCientifico);
//
//        List<Foto> fotos = Foto.findAllByEspecie(context, selectedEspecie);
//        Foto foto = fotos.get(0);
//        File imgFile = new File(foto.path);
//        if (imgFile.exists()) {
////            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
////            Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 100, 100);
//            Bitmap myBitmap = ImageUtils.decodeFile(imgFile.getAbsolutePath(), 100, 100);
//            imageView.setImageBitmap(myBitmap);
//        }
//
//        // change the icon for Windows and iPhone
////        String s = values[position];
////        if (s.startsWith("iPhone")) {
////            imageView.setImageResource(R.drawable.no);
////        } else {
////            imageView.setImageResource(R.drawable.ok);
////        }
//
//        return rowView;
//    }

}
