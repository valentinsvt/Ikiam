package com.nth.ikiam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Genero;

import java.util.List;

/**
 * Created by luz on 04/08/14.
 */
public class CapturaNombreEspecieArrayAdapter extends ArrayAdapter<Especie> {

    MapActivity context;
    List<Especie> especies;
    int layoutResourceId;

    public CapturaNombreEspecieArrayAdapter(MapActivity context, int layoutResourceId, List<Especie> especies) {
        super(context, layoutResourceId, especies);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.especies = especies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layoutResourceId, parent, false);

        // object item based on the position
        Especie objectItem = especies.get(position);

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) rowView.findViewById(R.id.captura_autocomplete_item_texto);
        textViewItem.setText(objectItem.nombre);

        // in case you want to add some style, you can do something like:
//        textViewItem.setBackgroundColor(Color.CYAN);

        return rowView;
    }
}
