package com.nth.ikiam;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nth.ikiam.adapters.*;
import com.nth.ikiam.capturaAutocomplete.CustomAutoCompleteView;
import com.nth.ikiam.db.*;
import com.nth.ikiam.listeners.*;
import com.nth.ikiam.utils.CapturaUploader;
import com.nth.ikiam.utils.GeoDegree;
import com.nth.ikiam.utils.ImageUtils;
import com.nth.ikiam.utils.Utils;
//import com.nth.ikiam.utils.GeoDegree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luz on 25/07/14.
 * <p/>
 * Based on the Example of loading an image into an image view using the image picker.
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 * http://www.airpair.com/android/android-image-picker-select-gallery-images
 * <p/>
 * image rotation
 * http://stackoverflow.com/questions/20478765/how-to-get-the-correct-orientation-of-the-image-selected-from-the-default-image/20480741#20480741
 * answered Dec 9 '13 at 21:04 by ramaral
 * <p/>
 * image resize
 * http://stackoverflow.com/questions/477572/strange-out-of-memory-issue-while-loading-an-image-to-a-bitmap-object
 * edited Jan 12 '12 at 12:33 by Fedor
 * and
 * edited Mar 20 at 6:18 by Thomas Vervest
 */
public class CapturaCientificoFragment extends Fragment implements Button.OnClickListener, FieldListener {

    private ImageButton[] botones;
    private ToggleButton[] toggles;
    String[] keys;
    String[] statusString;

    private ImageView selectedImage;

    private TextView lblInfo;

    private EditText textoComentarios;

    public CustomAutoCompleteView autocompleteNombreComun;
    public CustomAutoCompleteView autocompleteFamilia;
    public CustomAutoCompleteView autocompleteGenero;
    public CustomAutoCompleteView autocompleteEspecie;

    private Spinner spinnerColor1;
    private Spinner spinnerColor2;

    public CapturaNombreComunArrayAdapter nombreComunArrayAdapter;
    public CapturaNombreFamiliaArrayAdapter nombreFamiliaArrayAdapter;
    public CapturaNombreGeneroArrayAdapter nombreGeneroArrayAdapter;
    public CapturaNombreEspecieArrayAdapter nombreEspecieArrayAdapter;

    private static final int GALLERY_REQUEST = 999;
    private static final int CAMERA_REQUEST = 1337;

    private static int screenWidth;
    private static int screenHeight;

    private String fotoPath;
    private Double fotoLat;
    private Double fotoLong;

    boolean hayFoto = false;

    MapActivity context;
    private String pathFolder;
    private Bitmap bitmap;
    MapActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
//        pathFolder = getArguments().getString("pathFolder");

        pathFolder = Utils.getFolder(context);

        View view = inflater.inflate(R.layout.captura_cientifico_layout, container, false);

        checkColores();
        activity = (MapActivity) getActivity();
        screenHeight = activity.screenHeight;
        screenWidth = activity.screenWidth;
        selectedImage = (ImageView) view.findViewById(R.id.captura_chosen_image_view);
        lblInfo = (TextView) view.findViewById(R.id.captura_info_label);
        textoComentarios = (EditText) view.findViewById(R.id.captura_comentarios_txt);

        initSpinners(view);
        initAutocompletes(view);
        initButtons(view);
        initToggles(view);
        return view;
    }

    @Override
    public void onClick(View v) {

        Utils.hideSoftKeyboard(this.getActivity());

        if (v.getId() == botones[0].getId()) { // galeria
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, GALLERY_REQUEST);
            } else {
                alerta(getString(R.string.gallery_app_not_available));
            }
        } else if (v.getId() == botones[1].getId()) { // camara
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            } else {
                alerta(getString(R.string.camera_app_not_available));
            }
        } else if (v.getId() == botones[2].getId() || v.getId() == botones[3].getId()) { // save || upload
            if (hayFoto) {
                Color color1 = (Color) spinnerColor1.getSelectedItem();
                Color color2 = (Color) spinnerColor2.getSelectedItem();
                String nombreComun = autocompleteNombreComun.getText().toString().trim();
                String nombreFamilia = autocompleteFamilia.getText().toString().trim();
                String nombreGenero = autocompleteGenero.getText().toString().trim();
                String nombreEspecie = autocompleteEspecie.getText().toString().trim();

                String comentarios = textoComentarios.getText().toString().trim();
                String keywords = "";
                int i = 0;
                for (ToggleButton toggle : toggles) {
                    if (toggle.isChecked()) {
                        if (!keywords.equals("")) {
                            keywords += ", ";
                        }
                        keywords += keys[i];
//                        System.out.println("i=" + i + "   " + keys[i] + "     " + keywords);
                    }
                    i++;
                }
                Familia familia = null;
                Genero genero = null;
                Especie especie = null;
                Entry entry = null;
                if (!nombreFamilia.equals("")) {
                    familia = Familia.getByNombreOrCreate(context, nombreFamilia);
                }
                if (!nombreGenero.equals("")) {
                    genero = Genero.getByNombreOrCreate(context, nombreGenero);
                    if (familia != null) {
                        genero.setFamilia(familia);
                        genero.save();
                    }
                }
                if (!nombreEspecie.equals("")) {
                    especie = Especie.getByNombreOrCreate(context, nombreEspecie);
                    if (genero != null) {
                        especie.setGenero(genero);
                    }
                    if (color1 != null) {
                        especie.setColor1(color1);
                    }
                    if (color2 != null && !color2.nombre.equals("none")) {
                        especie.setColor2(color2);
                    }
                    if (!nombreComun.equals("")) {
                        especie.setNombreComun(nombreComun);
                    }
                    especie.save();

                    entry = new Entry(context);
                    entry.setEspecie(especie);
                    entry.setComentarios(comentarios);
                    entry.setUploaded(0);
                    entry.save();
                }

                Foto foto = new Foto(context);
                if (especie != null) {
                    foto.setEspecie(especie);
                }
                if (entry != null) {
                    foto.setEntry(entry);
                }
                foto.setKeywords(keywords);
                if (fotoLat != null && fotoLong != null) {
                    System.out.println("COORDENADA::: " + fotoLat + "," + fotoLong);
                    Coordenada coordenada = new Coordenada(context, fotoLat, fotoLong);
                    coordenada.save();
                    foto.setCoordenada(coordenada);
                }
                foto.save();

                String nuevoNombre;
                if (genero != null && especie != null) {
                    nuevoNombre = genero.nombre + "_" + especie.nombre + "_" + foto.id;
                    nuevoNombre = nuevoNombre.replaceAll("[^a-zA-Z_0-9]", "_");
                } else {
                    nuevoNombre = "na_na_" + foto.id;
                }
                nuevoNombre += ".jpg";

//                File file = new File(pathFolder, fotoPath);
//                fotoPath = file.getName();
                File file = new File(pathFolder, nuevoNombre);
////                if (file.exists()) {
////                    file.delete();
////                }
                try {
                    if (!file.exists()) {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("Path folder: " + pathFolder);
                //System.out.println("Photo path: " + fotoPath);
//                foto.setPath(pathFolder + "/" + fotoPath);
                foto.setPath(pathFolder + "/" + nuevoNombre);
                foto.setUploaded(0);
                foto.save();
//                String msg = "Foto guardada";
                //System.out.println("foto: " + foto.id + "entry: " + entry.id + "  especie: " + especie.id + "   " + especie.nombre + "  (" + genero.nombre + " " + genero.id + ")" + "  (" + familia.nombre + " " + familia.id + ")");
                if (v.getId() == botones[3].getId()) {
                    String id = context.userId; //id (faceboook - fb id, ikiam db.id
                    if (id != null && !id.equals("-1")) {
                        // aqui hace upload al servidor.....
                        ExecutorService queue = Executors.newSingleThreadExecutor();
                        queue.execute(new CapturaUploader(context, queue, foto, 0));
                    } else {
                        //redirect a login
                        System.out.println("Login first!!!");
                        context.selectItem(context.LOGIN_POS);
                    }
//                    msg += " y subida ";
                } else {
                    alerta(getString(R.string.uploader_upload_success));
                }
//                alerta(msg + " exitosamente");
                resetForm();
//                System.out.println("Save: <" + keywords + "> <" + comentarios + ">");
            } else {
                alerta(getString(R.string.captura_error_seleccion));
            }
        } else {
            for (ToggleButton toggle : toggles) {
                updateStatus(v, toggle);
            }
        }
    }

    private void checkColores() {
//        Color.empty(context);
//        Familia.empty(context);
//        Genero.empty(context);
//        Especie.empty(context);
//        Entry.empty(context);
//        Foto.empty(context);
//        Coordenada.empty(context);

        if (Color.count(context) == 0) {
            Color c0 = new Color(context, "none");
            c0.save();
            Color c1 = new Color(context, "azul");
            c1.save();
            Color c2 = new Color(context, "cafe");
            c2.save();
            Color c3 = new Color(context, "verde");
            c3.save();
            Color c4 = new Color(context, "naranja");
            c4.save();
            Color c5 = new Color(context, "rosa");
            c5.save();
            Color c6 = new Color(context, "violeta");
            c6.save();
            Color c7 = new Color(context, "rojo");
            c7.save();
            Color c8 = new Color(context, "blanco");
            c8.save();
            Color c9 = new Color(context, "amarillo");
            c9.save();
            Color c10 = new Color(context, "negro");
            c10.save();
        }
    }

    private void initButtons(View view) {
        botones = new ImageButton[4];
        botones[0] = (ImageButton) view.findViewById(R.id.captura_gallery_btn);
        botones[1] = (ImageButton) view.findViewById(R.id.captura_camera_btn);
        botones[2] = (ImageButton) view.findViewById(R.id.captura_save_btn);
        botones[3] = (ImageButton) view.findViewById(R.id.captura_save_upload_btn);
        for (ImageButton button : botones) {
            button.setOnClickListener(this);
        }
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
        statusString[0] = getString(R.string.captura_tiene_arbol);
        statusString[1] = getString(R.string.captura_tiene_corteza);
        statusString[2] = getString(R.string.captura_tiene_hoja);
        statusString[3] = getString(R.string.captura_tiene_flor);
        statusString[4] = getString(R.string.captura_tiene_fruta);
        statusString[5] = getString(R.string.captura_es_animal);

        for (ToggleButton toggle : toggles) {
            toggle.setOnClickListener(this);
        }
    }

    private void initSpinners(View view) {
        ArrayList<Color> colores1 = Color.listColores(context);
        ArrayList<Color> colores2 = Color.list(context);

        spinnerColor1 = (Spinner) view.findViewById(R.id.captura_color1_spinner);
        spinnerColor1.setAdapter(new CapturaColorSpinnerAdapter(context, colores1));

        spinnerColor2 = (Spinner) view.findViewById(R.id.captura_color2_spinner);
        spinnerColor2.setAdapter(new CapturaColorSpinnerAdapter(context, colores2));
    }

    private void initAutocompletes(View view) {

        final List<Familia> familiaList = Familia.list(context);
        final List<Genero> generoList = Genero.list(context);
        final List<Especie> especieList = Especie.list(context);

        autocompleteNombreComun = (CustomAutoCompleteView) view.findViewById(R.id.captura_autocomplete_nombre_comun);
        autocompleteNombreComun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//                System.out.println("CLICK: " + pos + "   " + especieList.get(pos).nombreComun);
                RelativeLayout rl = (RelativeLayout) arg1;
                TextView tv = (TextView) rl.getChildAt(0);
                autocompleteNombreComun.setText(tv.getText().toString());

                String selection = tv.getText().toString();
                Especie selected = null;

                for (Especie especie : especieList) {
                    if (especie.nombreComun.equals(selection)) {
                        selected = especie;
                        break;
                    }
                }
                if (selected != null) {
                    autocompleteFamilia.setText(selected.getGenero(context).getFamilia(context).nombre);
                    autocompleteGenero.setText(selected.getGenero(context).nombre);
                    autocompleteEspecie.setText(selected.nombre);
                }
            }
        });
        // add the listener so it will tries to suggest while the user types
        autocompleteNombreComun.addTextChangedListener(new CapturaCNombreComunAutocompleteTextChangedListener(context, this));
        // ObjectItemData has no value at first
        // set the custom ArrayAdapter
        nombreComunArrayAdapter = new CapturaNombreComunArrayAdapter(context, R.layout.captura_autocomplete_list_item, especieList);
        autocompleteNombreComun.setAdapter(nombreComunArrayAdapter);

        autocompleteFamilia = (CustomAutoCompleteView) view.findViewById(R.id.captura_autocomplete_nombre_familia);
        autocompleteFamilia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//                System.out.println("CLICK: " + pos + "   " + especieList.get(pos).nombreComun);
                RelativeLayout rl = (RelativeLayout) arg1;
                TextView tv = (TextView) rl.getChildAt(0);
                autocompleteFamilia.setText(tv.getText().toString());
            }
        });
        // add the listener so it will tries to suggest while the user types
        autocompleteFamilia.addTextChangedListener(new CapturaCNombreFamiliaAutocompleteTextChangedListener(context, this));
        // ObjectItemData has no value at first
        // set the custom ArrayAdapter
        nombreFamiliaArrayAdapter = new CapturaNombreFamiliaArrayAdapter(context, R.layout.captura_autocomplete_list_item, familiaList);
        autocompleteFamilia.setAdapter(nombreFamiliaArrayAdapter);

        autocompleteGenero = (CustomAutoCompleteView) view.findViewById(R.id.captura_autocomplete_nombre_genero);
        autocompleteGenero.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//                System.out.println("CLICK: " + pos + "   " + especieList.get(pos).nombreComun);
                RelativeLayout rl = (RelativeLayout) arg1;
                TextView tv = (TextView) rl.getChildAt(0);
                autocompleteGenero.setText(tv.getText().toString());
            }
        });
        // add the listener so it will tries to suggest while the user types
        autocompleteGenero.addTextChangedListener(new CapturaCNombreGeneroAutocompleteTextChangedListener(context, this));
        // ObjectItemData has no value at first
        // set the custom ArrayAdapter
        nombreGeneroArrayAdapter = new CapturaNombreGeneroArrayAdapter(context, R.layout.captura_autocomplete_list_item, generoList);
        autocompleteGenero.setAdapter(nombreGeneroArrayAdapter);

        autocompleteEspecie = (CustomAutoCompleteView) view.findViewById(R.id.captura_autocomplete_nombre_especie);
        autocompleteEspecie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//                System.out.println("CLICK: " + pos + "   " + especieList.get(pos).nombreComun);
                RelativeLayout rl = (RelativeLayout) arg1;
                TextView tv = (TextView) rl.getChildAt(0);
                autocompleteEspecie.setText(tv.getText().toString());
            }
        });
        // add the listener so it will tries to suggest while the user types
        autocompleteEspecie.addTextChangedListener(new CapturaCNombreEspecieAutocompleteTextChangedListener(context, this));
        // ObjectItemData has no value at first
        // set the custom ArrayAdapter
        nombreEspecieArrayAdapter = new CapturaNombreEspecieArrayAdapter(context, R.layout.captura_autocomplete_list_item, especieList);
        autocompleteEspecie.setAdapter(nombreEspecieArrayAdapter);
    }

    private void resetForm() {
        selectedImage.setImageDrawable(null);
        for (ToggleButton toggle : toggles) {
            toggle.setChecked(false);
        }
        spinnerColor1.setSelection(0);
        spinnerColor2.setSelection(0);

        autocompleteNombreComun.setText("");
        autocompleteFamilia.setText("");
        autocompleteGenero.setText("");
        autocompleteEspecie.setText("");

        textoComentarios.setText("");
        hayFoto = false;
    }

    private void updateStatus(View view, ToggleButton toggleButton) {
        if (hayFoto) {

            if (view != null) {
                if (view.getId() == toggles[5].getId()) { // si es animal se desactivan todos los otros
                    for (int i = 0; i < 4; i++) {
                        toggles[i].setChecked(false);
                    }
                } else {
                    //desactiva el de animal
                    toggles[5].setChecked(false);
                }
            }

            String info = "";

            int i = 0;
            for (ToggleButton toggle : toggles) {
                if (toggle.isChecked()) {
                    if (!info.equals("")) {
                        info += ", ";
                    }
                    info += statusString[i];
                }
                i++;
            }
//            lblInfo.setText(info);
        } else {
            if (toggleButton != null) {
                toggleButton.setChecked(false);
            }
            alerta(getString(R.string.captura_error_seleccion));
        }
    }

    private void alerta(String string) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    /**
     * Photo Selection result
     * Read more at http://www.airpair.com/android/android-image-picker-select-gallery-images#bok7edWCrB11olZ1.99
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST || requestCode == CAMERA_REQUEST) {
                hayFoto = true;
                updateStatus(null, null);
                MapActivity activity = (MapActivity) getActivity();
                Bitmap thumb = ImageUtils.getBitmapFromCameraData(data, activity, true);
                selectedImage.setImageBitmap(thumb);
                bitmap = ImageUtils.getBitmapFromCameraData(data, activity, false);

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                fotoPath = cursor.getString(columnIndex);

                try {
                    ExifInterface exif = new ExifInterface(fotoPath);
                    GeoDegree gd = new GeoDegree(exif);
                    fotoLat = gd.getLatitude();
                    fotoLong = gd.getLongitude();
                    if (fotoLat != null && fotoLong != null) {
                        alerta(getString(R.string.captura_success_tag_gps));
                    } else {
                        alerta(getString(R.string.captura_error_tag_gps));
                    }
                } catch (Exception e) {
                    alerta(getString(R.string.captura_error_tag_gps));
                    fotoLat = null;
                    fotoLong = null;
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void fieldValueChanged(String fieldName, String newValue) {
        if (fieldName.equals("errorMessage")) {
            if (!newValue.equals("")) {
                String msg = newValue.toString();
                context.showToast(msg);
                context.errorMessage = "";
            }
        }
    }
}