package com.nth.ikiam;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nth.ikiam.adapters.CapturaColorSpinnerAdapter;
import com.nth.ikiam.db.Color;
import com.nth.ikiam.db.Entry;
import com.nth.ikiam.db.Especie;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by DELL on 13/08/2014.
 */
public class BusquedaFragment extends Fragment implements Button.OnClickListener, AdapterView.OnItemSelectedListener,
        TextWatcher, View.OnTouchListener {
    MapActivity context;

    ImageButton btnBuscar;
    ToggleButton[] toggles;
    String[] keys;
    String[] statusString;

    EditText nombreComunTxt;

    TextView lblInfoKeywords;
    TextView lblInfoColor;
    TextView lblInfoNC;

    Vector<String> searchKeywords;
    String searchColor;
    String searchNC;

    private Spinner spinnerColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
        View view = inflater.inflate(R.layout.busqueda_layout, container, false);
        view.setOnTouchListener(this);
//        Utils.checkColores(context);

        lblInfoKeywords = (TextView) view.findViewById(R.id.busqueda_info_keywords_lbl);
        lblInfoColor = (TextView) view.findViewById(R.id.busqueda_info_color_lbl);
        lblInfoNC = (TextView) view.findViewById(R.id.busqueda_info_nc_lbl);

        nombreComunTxt = (EditText) view.findViewById(R.id.busqueda_nombre_comun_txt);
        nombreComunTxt.addTextChangedListener(this);
        btnBuscar = (ImageButton) view.findViewById(R.id.busqueda_buscar_btn);
        btnBuscar.setOnClickListener(this);

        searchKeywords = new Vector<String>();
        searchColor = "";
        searchNC = "";

        initSpinner(view);
        initToggles(view);
        updateAll();
        return view;
    }

    private void initSpinner(View view) {
        ArrayList<Color> colores = Color.list(context);
        spinnerColor = (Spinner) view.findViewById(R.id.busqueda_color_spinner);
        spinnerColor.setAdapter(new CapturaColorSpinnerAdapter(context, colores));
        spinnerColor.setOnItemSelectedListener(this);
    }

    private void initToggles(View view) {
        toggles = new ToggleButton[6];
        toggles[0] = (ToggleButton) view.findViewById(R.id.captura_arbol_toggle);
        toggles[1] = (ToggleButton) view.findViewById(R.id.captura_corteza_toggle);
        toggles[2] = (ToggleButton) view.findViewById(R.id.captura_hoja_toggle);
        toggles[3] = (ToggleButton) view.findViewById(R.id.captura_flor_toggle);
        toggles[4] = (ToggleButton) view.findViewById(R.id.captura_fruta_toggle);
        toggles[5] = (ToggleButton) view.findViewById(R.id.captura_animal_toggle);

        keys = new String[6];
        keys[0] = "arbol";
        keys[1] = "corteza";
        keys[2] = "hoja";
        keys[3] = "flor";
        keys[4] = "fruta";
        keys[5] = "animal";

        statusString = new String[6];
        statusString[0] = getString(R.string.buscar_tiene_arbol);
        statusString[1] = getString(R.string.buscar_tiene_corteza);
        statusString[2] = getString(R.string.buscar_tiene_hoja);
        statusString[3] = getString(R.string.buscar_tiene_flor);
        statusString[4] = getString(R.string.buscar_tiene_fruta);
        statusString[5] = getString(R.string.buscar_es_animal);

        for (ToggleButton toggle : toggles) {
            toggle.setOnClickListener(this);
        }
    }

    private void updateAll() {
        updateKeywords();
        updateColor();
        updateNC();
    }

    private void updateKeywords() {
        searchKeywords = new Vector<String>();
        String info = "";
        boolean plantaChecked = false;
        boolean animalChecked = false;
        int i = 0;
        for (ToggleButton toggle : toggles) {
            if (toggle.isChecked()) {
                if (!info.equals("")) {
                    info += ", ";
                }
                searchKeywords.add(keys[i]);
                if (i != 5) {
                    if (!plantaChecked) {
                        info += getString(R.string.buscar_donde) + " ";
                    }
                    plantaChecked = true;
                    info += statusString[i];
                } else {
                    animalChecked = true;
                }
            }
            i++;
        }

        if (plantaChecked && !animalChecked) {
            info = getString(R.string.buscar_buscar) + " " + getString(R.string.buscar_es_planta) + " " + info;
        }
        if (animalChecked && !plantaChecked) {
            info = getString(R.string.buscar_buscar) + " " + getString(R.string.buscar_es_animal) + " " + info;
        }
        if (animalChecked && plantaChecked) {
            info = getString(R.string.buscar_buscar) + " " + getString(R.string.buscar_es_animal_y_planta) + " " + info;
        }
        if (!animalChecked && !plantaChecked) {
            info = "";
        }
        if (!info.equals("")) {
            lblInfoKeywords.setVisibility(View.VISIBLE);
            lblInfoKeywords.setText(info);
        } else {
            lblInfoKeywords.setVisibility(View.GONE);
        }
    }

    private void updateColor() {
        String info = "";
        String color = spinnerColor.getSelectedItem().toString();
        if (!color.equals("none")) {
            searchColor = color;
//            info += getString(R.string.buscar_color) + " " + color + " ";
            int id = getResources().getIdentifier("global_color_" + color, "string", context.getPackageName());
            if (id > 0) {
                info = getString(R.string.buscar_buscar) + " " + getString(R.string.buscar_color) + " " + ((String) getResources().getText(id)).toLowerCase();
            }
        }
        if (!info.equals("")) {
            lblInfoColor.setVisibility(View.VISIBLE);
            lblInfoColor.setText(info);
        } else {
            lblInfoColor.setVisibility(View.GONE);
        }
    }

    private void updateNC() {
        String info = "";
        String nombreComun = nombreComunTxt.getText().toString().trim();
        if (!nombreComun.equals("")) {
            if (info.equals("")) {
                info += getString(R.string.buscar_buscar) + " ";
            } else {
                info += ", ";
            }
            searchNC = nombreComun;
            info += getString(R.string.buscar_nombre_comun) + " " + nombreComun + " ";
        }
        if (!info.equals("")) {
            lblInfoNC.setVisibility(View.VISIBLE);
            lblInfoNC.setText(info);
        } else {
            lblInfoNC.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Utils.hideSoftKeyboard(this.getActivity());
        if (v.getId() == btnBuscar.getId()) {
            updateAll();

//            System.out.println("Keywords: " + searchKeywords);
//            System.out.println("Color: " + searchColor);
//            System.out.println("NC: " + searchNC);

//            List<Especie> especies = Especie.busqueda(context, searchKeywords, searchColor, searchNC);
            context.especiesBusqueda = Especie.busqueda(context, searchKeywords, searchColor, searchNC);
            ListFragment fragment = new BusquedaResultsFragment();
            Utils.openFragment(context, fragment, getString(R.string.busqueda_title));
//            FragmentManager fragmentManager = context.getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

//            List<Foto> fotos = Foto.busqueda(context, data);
//            context.fotosBusqueda = Foto.busqueda(context, data);
//            for (Foto f : fotos) {
//                System.out.println("******" + f.id);
//            }
//            data = new HashMap<String, String>();

//            ListFragment fragment = new BusquedaResultsFragment();
//            Bundle args = new Bundle();
//            args.putLong("especie", -1);
//            fragment.setArguments(args);

//            context.setTitle("Resultados");
//
//            FragmentManager fragmentManager = context.getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } else {
            updateKeywords();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Utils.hideSoftKeyboard(this.getActivity());
        updateColor();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Utils.hideSoftKeyboard(this.getActivity());
        updateColor();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        updateNC();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Utils.hideSoftKeyboard(context);
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.setTitle(R.string.busqueda_title);
    }
}