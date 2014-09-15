package com.nth.ikiam.listeners;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import com.nth.ikiam.CapturaCientificoFragment;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.adapters.CapturaNombreEspecieArrayAdapter;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Genero;

import java.util.List;

/**
 * Created by luz on 04/08/14.
 */
public class CapturaNombreEspecieAutocompleteTextChangedListener implements TextWatcher {

    public static final String TAG = "CapturaNombreFamiliaAutocompleteTextChangedListener.java";
    MapActivity context;
    CapturaCientificoFragment fragment;
    Genero genero;

    public CapturaNombreEspecieAutocompleteTextChangedListener(MapActivity context, CapturaCientificoFragment fragment, Genero genero) {
        this.context = context;
        this.fragment = fragment;
        this.genero = genero;
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
//            System.out.println("User input: " + userInput);

            // update the adapater
            fragment.nombreEspecieArrayAdapter.notifyDataSetChanged();

            // get suggestions from the database
            List<Especie> myObjs = Especie.findAllByGeneroAndNombreLike(context, genero, userInput.toString());

            // update the nombreComunArrayAdapter
            fragment.nombreEspecieArrayAdapter = new CapturaNombreEspecieArrayAdapter(context, R.layout.captura_autocomplete_list_item, myObjs);
            fragment.autocompleteEspecie.setAdapter(fragment.nombreEspecieArrayAdapter);

            if (myObjs.size() == 1) {
                fragment.autocompleteNombreComun.setText(myObjs.get(0).nombreComun);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}