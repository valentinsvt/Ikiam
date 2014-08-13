package com.nth.ikiam.listeners;

import android.text.Editable;
import android.text.TextWatcher;
import com.nth.ikiam.CapturaCientificoFragment;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.adapters.CapturaNombreEspecieArrayAdapter;
import com.nth.ikiam.db.Especie;

import java.util.List;

/**
 * Created by luz on 04/08/14.
 */
public class CapturaCNombreEspecieAutocompleteTextChangedListener implements TextWatcher {

    public static final String TAG = "CapturaNombreFamiliaAutocompleteTextChangedListener.java";
    MapActivity context;
    CapturaCientificoFragment fragment;

    public CapturaCNombreEspecieAutocompleteTextChangedListener(MapActivity context, CapturaCientificoFragment fragment) {
        this.context = context;
        this.fragment = fragment;
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
            fragment.nombreEspecieArrayAdapter.notifyDataSetChanged();

            // get suggestions from the database
            List<Especie> myObjs = Especie.findAllByNombreLike(context, userInput.toString());

            // update the nombreComunArrayAdapter
            fragment.nombreEspecieArrayAdapter = new CapturaNombreEspecieArrayAdapter(context, R.layout.captura_autocomplete_list_item, myObjs);
            fragment.autocompleteEspecie.setAdapter(fragment.nombreEspecieArrayAdapter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}