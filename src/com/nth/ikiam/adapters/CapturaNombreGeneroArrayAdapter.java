package com.nth.ikiam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Familia;
import com.nth.ikiam.db.Genero;

import java.util.List;

/**
 * Created by luz on 04/08/14.
 */
public class CapturaNombreGeneroArrayAdapter extends ArrayAdapter<Genero> {

    MapActivity context;
    List<Genero> generos;
    int layoutResourceId;

    public CapturaNombreGeneroArrayAdapter(MapActivity context, int layoutResourceId, List<Genero> generos) {
        super(context, layoutResourceId, generos);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.generos = generos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layoutResourceId, parent, false);

        // object item based on the position
        Genero objectItem = generos.get(position);

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) rowView.findViewById(R.id.captura_autocomplete_item_texto);
        textViewItem.setText(objectItem.nombre);

        // in case you want to add some style, you can do something like:
//        textViewItem.setBackgroundColor(Color.CYAN);

        return rowView;
    }
}
