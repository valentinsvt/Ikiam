package com.nth.ikiam.listeners;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import com.nth.ikiam.CapturaCientificoFragment;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.adapters.CapturaNombreEspecieArrayAdapter;
import com.nth.ikiam.adapters.CapturaNombreGeneroArrayAdapter;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Familia;
import com.nth.ikiam.db.Genero;

import java.util.List;

/**
 * Created by luz on 04/08/14.
 */
public class CapturaNombreGeneroAutocompleteTextChangedListener implements TextWatcher {

    public static final String TAG = "CapturaNombreFamiliaAutocompleteTextChangedListener.java";
    MapActivity context;
    CapturaCientificoFragment fragment;
    Familia familia;

    public CapturaNombreGeneroAutocompleteTextChangedListener(MapActivity context, CapturaCientificoFragment fragment, Familia familia) {
        this.context = context;
        this.fragment = fragment;
        this.familia = familia;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        try {

            // if you want to see in the logcat what the user types
//            Log.e(TAG, "User input: " + userInput);

            // update the adapater
            fragment.nombreGeneroArrayAdapter.notifyDataSetChanged();

            // get suggestions from the database
//            System.out.println("FAMILIA   " + familia.nombre);
            List<Genero> myObjs = Genero.findAllByFamiliaAndNombreLike(context, familia, userInput.toString());

            // update the nombreComunArrayAdapter
            fragment.nombreGeneroArrayAdapter = new CapturaNombreGeneroArrayAdapter(context, R.layout.captura_autocomplete_list_item, myObjs);
            fragment.autocompleteGenero.setAdapter(fragment.nombreGeneroArrayAdapter);

            if (myObjs.size() == 1) {
                fragment.autocompleteEspecie.addTextChangedListener(new CapturaNombreEspecieAutocompleteTextChangedListener(context, fragment, myObjs.get(0)));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}