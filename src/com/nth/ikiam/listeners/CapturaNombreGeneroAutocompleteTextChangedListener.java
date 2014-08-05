package com.nth.ikiam.listeners;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import com.nth.ikiam.CapturaFragment;
import com.nth.ikiam.R;
import com.nth.ikiam.adapters.CapturaNombreEspecieArrayAdapter;
import com.nth.ikiam.adapters.CapturaNombreFamiliaArrayAdapter;
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
    Context context;
    CapturaFragment fragment;

    public CapturaNombreGeneroAutocompleteTextChangedListener(Context context, CapturaFragment fragment) {
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
            fragment.nombreGeneroArrayAdapter.notifyDataSetChanged();

            // get suggestions from the database
            List<Genero> myObjs = Genero.findAllByNombreLike(context, userInput.toString());

            // update the nombreComunArrayAdapter
            fragment.nombreGeneroArrayAdapter = new CapturaNombreGeneroArrayAdapter(context, R.layout.captura_autocomplete_list_item, myObjs);
            fragment.autocompleteGenero.setAdapter(fragment.nombreGeneroArrayAdapter);

            if (myObjs.size() == 1) {
                List<Especie> especies = Especie.findAllByGenero(context, myObjs.get(0));
                fragment.nombreEspecieArrayAdapter = new CapturaNombreEspecieArrayAdapter(context, R.layout.captura_autocomplete_list_item, especies);
                fragment.autocompleteEspecie.setAdapter(fragment.nombreEspecieArrayAdapter);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}