package com.nth.ikiam.listeners;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import com.nth.ikiam.CapturaFragment;
import com.nth.ikiam.R;
import com.nth.ikiam.adapters.CapturaNombreComunArrayAdapter;
import com.nth.ikiam.db.Especie;

import java.util.List;

/**
 * Created by luz on 04/08/14.
 */
public class CapturaNombreComunAutocompleteTextChangedListener implements TextWatcher {

    public static final String TAG = "CapturaNombreComunAutocompleteTextChangedListener.java";
    Context context;
    CapturaFragment fragment;

    public CapturaNombreComunAutocompleteTextChangedListener(Context context, CapturaFragment fragment) {
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
            fragment.nombreComunArrayAdapter.notifyDataSetChanged();

            // get suggestions from the database
            List<Especie> myObjs = Especie.findAllByNombreComunLike(context, userInput.toString());

            // update the nombreComunArrayAdapter
            fragment.nombreComunArrayAdapter = new CapturaNombreComunArrayAdapter(context, R.layout.captura_autocomplete_list_item, myObjs);

            fragment.autocompleteNombreComun.setAdapter(fragment.nombreComunArrayAdapter);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}