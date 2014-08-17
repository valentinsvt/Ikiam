package com.nth.ikiam;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nth.ikiam.adapters.CapturaColorSpinnerAdapter;
import com.nth.ikiam.db.Color;
import com.nth.ikiam.db.Entry;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by DELL on 13/08/2014.
 */
public class BusquedaFragment extends Fragment implements Button.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
    MapActivity context;

    ImageButton btnBuscar;
    ToggleButton[] toggles;
    String[] keys;
    String[] statusString;

    EditText nombreComunTxt;

    TextView lblInfo;

    HashMap<String, String> data;

    private Spinner spinnerColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
        View view = inflater.inflate(R.layout.busqueda_layout, container, false);
        Utils.checkColores(context);
        lblInfo = (TextView) view.findViewById(R.id.busqueda_info_lbl);
        nombreComunTxt = (EditText) view.findViewById(R.id.busqueda_nombre_comun_txt);
        nombreComunTxt.addTextChangedListener(this);
        btnBuscar = (ImageButton) view.findViewById(R.id.busqueda_buscar_btn);
        btnBuscar.setOnClickListener(this);

        data = new HashMap<String, String>();

        initSpinner(view);
        initToggles(view);
        updateStatus();
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

    private void updateStatus() {
        String info = "";
        data = new HashMap<String, String>();
        boolean plantaChecked = false;
        boolean animalChecked = false;
        int i = 0;
        for (ToggleButton toggle : toggles) {
            if (toggle.isChecked()) {
                if (!info.equals("")) {
                    info += ", ";
                }
                data.put("keyword_" + i, keys[i]);
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

        String color = spinnerColor.getSelectedItem().toString();
        if (!color.equals("none")) {
            if (info.equals("")) {
                info += getString(R.string.buscar_buscar) + " ";
            }
            data.put("color", color);
            info += getString(R.string.buscar_color) + " " + color + " ";
        }

        String nombreComun = nombreComunTxt.getText().toString().trim();
        if (!nombreComun.equals("")) {
            if (info.equals("")) {
                info += getString(R.string.buscar_buscar) + " ";
            } else {
                info += ", ";
            }
            data.put("nombreComun", nombreComun);
            info += getString(R.string.buscar_nombre_comun) + " " + nombreComun + " ";
        }

        lblInfo.setText(info);
    }

    @Override
    public void onClick(View v) {
        Utils.hideSoftKeyboard(this.getActivity());
        if (v.getId() == btnBuscar.getId()) {
            updateStatus();

//            List<Foto> fotos = Foto.busqueda(context, data);
            context.fotosBusqueda = Foto.busqueda(context, data);
//            for (Foto f : fotos) {
//                System.out.println("******" + f.id);
//            }
            data = new HashMap<String, String>();

            ListFragment fragment = new BusquedaResultsFragment();
//            Bundle args = new Bundle();
//            args.putLong("especie", -1);
//            fragment.setArguments(args);

            context.setTitle("Resultados");

            FragmentManager fragmentManager = context.getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


//            List<Entry> entries = Entry.busqueda(context, data);
//            data = new HashMap<String, String>();
//            context.entriesBusqueda = entries;
//
//            for (Entry entry : entries) {
//                System.out.println("*************   " + entry.getEspecie(context).nombreComun);
//            }


//            ListFragment fragment = new EncyclopediaEntriesFragment();
//            Bundle args = new Bundle();
//            args.putLong("especie", -1);
//            fragment.setArguments(args);
//
//            context.setTitle("Resultados");
//
//            FragmentManager fragmentManager = context.getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

//            System.out.println("HAY " + entries.size() + " ENTRIES EN EL RESULTADO");
        } else {
            updateStatus();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Utils.hideSoftKeyboard(this.getActivity());
        updateStatus();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Utils.hideSoftKeyboard(this.getActivity());
        updateStatus();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        updateStatus();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}