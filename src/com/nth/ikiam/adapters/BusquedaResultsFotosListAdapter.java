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
import com.nth.ikiam.image.ImageUtils;

import java.io.File;
import java.util.List;

/**
 * Created by DELL on 03/08/2014.
 */
public class BusquedaResultsFotosListAdapter extends ArrayAdapter<Foto> {
    private final Context context;
    private final List<Foto> fotos;

    public BusquedaResultsFotosListAdapter(Context context, List<Foto> fotos) {
        super(context, R.layout.encylopedia_entries_row, fotos);
        this.context = context;
        this.fotos = fotos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.busqueda_results_row, parent, false);
//        System.out.println(rowView);

//        TextView textViewNombre = (TextView) rowView.findViewById(R.id.encyclopedia_entries_row_nombre_comun);
        TextView textViewComentarios = (TextView) rowView.findViewById(R.id.encyclopedia_entries_row_comentarios);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.encyclopedia_entries_row_image);

//        System.out.println("POS " + position + "  " + entries.size());

        Foto selectedFoto = fotos.get(position);
//        String comentarios = selectedEntry.comentarios;

//        while (comentarios.length() < 150) {
//            comentarios += " Lorem ipsum dolor sit amet ";
//        }

//        if (comentarios.length() > 127) {
//            comentarios = comentarios.substring(0, 127) + "...";
//        }
//
//        Foto foto = Foto.findAllByEntry(context, selectedEntry).get(0);

//        textViewNombre.setText(selectedEntry.getEspecie().getNombreCientifico() + " (" + selectedEntry.getEspecie().nombreComun + ")");
//        textViewComentarios.setText(comentarios);

        File imgFile = new File(selectedFoto.path);
        if (imgFile.exists()) {
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 100, 100);
            imageView.setImageBitmap(myBitmap);
        }

        // change the icon for Windows and iPhone
//        String s = values[position];
//        if (s.startsWith("iPhone")) {
//            imageView.setImageResource(R.drawable.no);
//        } else {
//            imageView.setImageResource(R.drawable.ok);
//        }

        return rowView;
    }

}
