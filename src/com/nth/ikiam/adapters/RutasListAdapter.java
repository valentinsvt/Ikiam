package com.nth.ikiam.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Entry;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.db.Ruta;
import com.nth.ikiam.image.ImageUtils;

import java.io.File;
import java.util.List;

/**
 * Created by DELL on 03/08/2014.
 */
public class RutasListAdapter extends ArrayAdapter<Ruta> {
    private final Context context;
    private final List<Ruta> rutas;

    public RutasListAdapter(Context context, List<Ruta> rutas) {
        super(context, R.layout.encylopedia_entries_row, rutas);
        this.context = context;
        this.rutas = rutas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.encylopedia_entries_row, parent, false);
//        System.out.println(rowView);

//        TextView textViewNombre = (TextView) rowView.findViewById(R.id.encyclopedia_rutas_row_nombre_comun);
        TextView textViewComentarios = (TextView) rowView.findViewById(R.id.encyclopedia_entries_row_comentarios);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.encyclopedia_entries_row_image);

//        System.out.println("POS " + position + "  " + rutas.size());

        Ruta selected = rutas.get(position);
        String titulo;
        titulo=selected.descripcion+"\n";
        titulo+=selected.fecha;


//        while (comentarios.length() < 150) {
//            comentarios += " Lorem ipsum dolor sit amet ";
//        }

        if (titulo.length() > 127) {
            titulo = titulo.substring(0, 127) + "...";
        }
        List<Foto> fotos = Foto.findAllByRuta(context, selected);
        Foto foto;
        titulo+="\n"+fotos.size()+" fotos";
        if(fotos.size()>0) {
            foto = fotos.get(0);
            File imgFile = new File(foto.path);
            if (imgFile.exists()) {
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 100, 100);
                imageView.setImageBitmap(myBitmap);
            }
        }else{
            imageView.setImageResource(R.drawable.ic_launcher);

        }
        textViewComentarios.setText(titulo);



        return rowView;
    }

}
