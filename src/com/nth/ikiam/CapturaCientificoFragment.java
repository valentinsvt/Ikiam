package com.nth.ikiam;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nth.ikiam.adapters.*;
import com.nth.ikiam.capturaAutocomplete.CustomAutoCompleteView;
import com.nth.ikiam.db.*;
import com.nth.ikiam.listeners.*;
import com.nth.ikiam.utils.CapturaUploader;
import com.nth.ikiam.utils.GeoDegree;
import com.nth.ikiam.image.ImageUtils;
import com.nth.ikiam.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class CapturaCientificoFragment extends Fragment implements Button.OnClickListener, FieldListener, View.OnTouchListener {

    private ImageButton[] botones;
    private ToggleButton[] toggles;
    String[] keys;
    String[] statusString;

    private ImageView selectedImage;

    private TextView lblInfo;

    private EditText textoComentarios;

    private CheckBox chkCautiverio;

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
    private Double fotoAlt;

    boolean hayFoto = false;
    boolean deMapa = false;

    MapActivity context;
    private String pathFolder;
    private Bitmap bitmap;
    MapActivity activity;
    Foto fotoSubir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
//        pathFolder = getArguments().getString("pathFolder");

        pathFolder = Utils.getFolder(context);

        View view = inflater.inflate(R.layout.captura_cientifico_layout, container, false);
        View scrollview = view.findViewById(R.id.captura_cientifico_scroll_view);
//        view.setOnTouchListener(this);
        scrollview.setOnTouchListener(this);

        Utils.checkColores(context);
        activity = (MapActivity) getActivity();
        screenHeight = activity.screenHeight;
        screenWidth = activity.screenWidth;
        selectedImage = (ImageView) view.findViewById(R.id.captura_chosen_image_view);
        lblInfo = (TextView) view.findViewById(R.id.captura_info_label);
        textoComentarios = (EditText) view.findViewById(R.id.captura_comentarios_txt);
        chkCautiverio = (CheckBox) view.findViewById(R.id.captura_cautiverio_check);

        initSpinners(view);
        initAutocompletes(view);
        initButtons(view);
        initToggles(view);

        if (context.imageToUpload != null) {
            fotoSubir = context.imageToUpload;
//            System.out.println("----------------- CAPTURA ------------------ " + fotoSubir.getCoordenada(context).latitud);
            Bitmap thumb = null;

            try {
                ExifInterface exif = new ExifInterface(fotoSubir.path);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                thumb = ImageUtils.getThumbnail(fotoSubir.path, false);
//                bitmap = BitmapFactory.decodeFile(fotoSubir.path);
                thumb = ImageUtils.rotateBitmap(thumb, orientation);

                bitmap = ImageUtils.getImage(fotoSubir.path);
                bitmap = ImageUtils.rotateBitmap(bitmap, orientation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (thumb != null) {
//                System.out.println("not null");
                selectedImage.setImageBitmap(thumb);
            } else {
                System.out.println("si null");
                selectedImage.setImageBitmap(ImageUtils.getThumbnail(fotoSubir.path, false));
            }
            hayFoto = true;
            deMapa = true;
        }

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
                String nombreEspecie = autocompleteEspecie.getText().toString().trim().toLowerCase();

                boolean ok = true;
                if (nombreComun.equals("")) {
                    ok = false;
                    alerta(getString(R.string.captura_error_nombre_comun));
                } else if (nombreFamilia.equals("")) {
                    ok = false;
                    alerta(getString(R.string.captura_error_nombre_familia));
                } else if (nombreGenero.equals("")) {
                    ok = false;
                    alerta(getString(R.string.captura_error_nombre_genero));
                } else if (nombreEspecie.equals("")) {
                    ok = false;
                    alerta(getString(R.string.captura_error_nombre_especie));
                }

                if (ok) {
                    String comentarios = textoComentarios.getText().toString().trim();
                    String keywords = "";
                    int i = 0;
                    boolean checked = false;
                    for (ToggleButton toggle : toggles) {
                        if (toggle.isChecked()) {
                            checked = true;
                            if (!keywords.equals("")) {
                                keywords += ", ";
                            }
                            keywords += keys[i];
//                        System.out.println("i=" + i + "   " + keys[i] + "     " + keywords);
                        }
                        i++;
                    }
                    if (!checked) {
                        alerta(getString(R.string.captura_error_keywords));
                    } else {
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
                            if (chkCautiverio.isChecked()) {
                                entry.cautiverio = 1;
                            } else {
                                entry.cautiverio = 0;
                            }
                            entry.save();
                        }

                        if (!deMapa) {
                            fotoSubir = new Foto(context);
                        }

                        if (especie != null) {
                            fotoSubir.setEspecie(especie);
                        }
                        if (entry != null) {
                            fotoSubir.setEntry(entry);
                        }
                        fotoSubir.setKeywords(keywords);
                        Coordenada coordenada = null;

                        if (deMapa) {
                            coordenada = fotoSubir.getCoordenada(context);
//                            System.out.println("<<<<>>>> " + coordenada.latitud + "     " + coordenada.longitud);
                        } else {
                            if (fotoLat != null && fotoLong != null) {
//                        System.out.println("COORDENADA::: " + fotoLat + "," + fotoLong);
                                coordenada = new Coordenada(context, fotoLat, fotoLong);
                                if (fotoAlt != null) {
                                    coordenada.setAltitud(fotoAlt);
                                }
                                coordenada.save();
                                fotoSubir.setCoordenada(coordenada);
                            }
                        }
                        fotoSubir.save();

                        String nuevoNombre;
                        if (genero != null && especie != null) {
                            nuevoNombre = genero.nombre + "_" + especie.nombre + "_" + fotoSubir.id;
                            nuevoNombre = nuevoNombre.replaceAll("[^a-zA-Z_0-9]", "_");
                        } else {
                            nuevoNombre = "na_na_" + fotoSubir.id;
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
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                                out.flush();
                                out.close();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //System.out.println("Path folder: " + pathFolder);
                        //System.out.println("Photo path: " + fotoPath);
//                foto.setPath(pathFolder + "/" + fotoPath);
                        fotoSubir.setPath(pathFolder + "/" + nuevoNombre);
                        fotoSubir.setUploaded(0);
                        fotoSubir.save();
//                String msg = "Foto guardada";
                        //System.out.println("foto: " + foto.id + "entry: " + entry.id + "  especie: " + especie.id + "   " + especie.nombre + "  (" + genero.nombre + " " + genero.id + ")" + "  (" + familia.nombre + " " + familia.id + ")");
                        if (v.getId() == botones[3].getId()) {
                            String id = context.userId; //id (faceboook - fb id, ikiam db.id
                            if (id != null && !id.equals("-1")) {
                                // aqui hace upload al servidor.....
                                if (coordenada == null) {
                                    if (!deMapa) {
                                        alerta(getString(R.string.captura_error_gps_upload));
                                    } else {
                                        ExecutorService queue = Executors.newSingleThreadExecutor();
                                        queue.execute(new CapturaUploader(context, queue, fotoSubir, 0));
                                    }
                                } else {
                                    ExecutorService queue = Executors.newSingleThreadExecutor();
                                    queue.execute(new CapturaUploader(context, queue, fotoSubir, 0));
                                }
                            } else {
                                //redirect a login
                                alerta(getString(R.string.uploader_login_error));
                                System.out.println("Login first!!!");
                                context.selectItem(context.LOGIN_POS);
                            }
//                    msg += " y subida ";
                        } else {
//                            alerta(getString(R.string.uploader_upload_success));
                        }
                        context.updateAchievement(context.ACHIEV_FOTOS);
//                alerta(msg + " exitosamente");
                        resetForm();
                        if (coordenada == null) {
                            if (!deMapa) {
                                context.fotoSinCoords = fotoSubir;
                                context.selectItem(context.MAP_POS);
                                alerta(getString(R.string.uploader_upload_map));
                            }
                        } else {
                            if (v.getId() != botones[3].getId()) {
                                alerta(getString(R.string.uploader_upload_success));
                            }
                            context.imageToUpload = null;
                        }

//                System.out.println("Save: <" + keywords + "> <" + comentarios + ">");
                    }
                }
            } else {
                alerta(getString(R.string.captura_error_seleccion));
            }
        } else {
            if (hayFoto) {
                updateStatus(v);
            } else {
                ToggleButton toggle = (ToggleButton) v;
                toggle.setChecked(false);
                alerta(getString(R.string.captura_error_seleccion));
            }
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
//        final List<Genero> generoList = Genero.list(context);
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
        autocompleteNombreComun.addTextChangedListener(new CapturaNombreComunAutocompleteTextChangedListener(context, this));
        // ObjectItemData has no value at first
        // set the custom ArrayAdapter
        nombreComunArrayAdapter = new CapturaNombreComunArrayAdapter(context, R.layout.captura_autocomplete_list_item, especieList);
        autocompleteNombreComun.setAdapter(nombreComunArrayAdapter);

        autocompleteFamilia = (CustomAutoCompleteView) view.findViewById(R.id.captura_autocomplete_nombre_familia);
        autocompleteGenero = (CustomAutoCompleteView) view.findViewById(R.id.captura_autocomplete_nombre_genero);
        autocompleteEspecie = (CustomAutoCompleteView) view.findViewById(R.id.captura_autocomplete_nombre_especie);

        final CapturaCientificoFragment thisFragment = this;

        autocompleteFamilia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//                System.out.println("CLICK: " + pos + "   ");

                RelativeLayout rl = (RelativeLayout) arg1;
                TextView tv = (TextView) rl.getChildAt(0);
                String txt = tv.getText().toString();
                Familia fam = null;
                for (Familia familia : familiaList) {
                    if (familia.nombre.equals(txt)) {
                        fam = familia;
                        break;
                    }
                }
                if (fam != null) {
//                    System.out.println("FAMILIA:::: " + fam.nombre);
                    final List<Genero> generos = Genero.findAllByFamilia(context, fam);
//                    for (Genero genero : generos) {
//                        System.out.println("<<<>>>>>>>>>>>>>> " + genero.nombre);
//                    }
                    autocompleteGenero.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//                System.out.println("CLICK: " + pos + "   " + especieList.get(pos).nombreComun);
                            RelativeLayout rl = (RelativeLayout) arg1;
                            TextView tv = (TextView) rl.getChildAt(0);
                            String txt = tv.getText().toString();
                            autocompleteGenero.setText(txt);
                            Genero gen = null;
                            for (Genero genero : generos) {
                                if (genero.nombre.equals(txt)) {
                                    gen = genero;
                                    break;
                                }
                            }

                            if (gen != null) {
                                final List<Especie> especies = Especie.findAllByGenero(context, gen);
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
                                autocompleteEspecie.addTextChangedListener(new CapturaNombreEspecieAutocompleteTextChangedListener(context, thisFragment, gen));
                                // ObjectItemData has no value at first
                                // set the custom ArrayAdapter
                                nombreEspecieArrayAdapter = new CapturaNombreEspecieArrayAdapter(context, R.layout.captura_autocomplete_list_item, especies);
                                autocompleteEspecie.setAdapter(nombreEspecieArrayAdapter);
                            }
                        }
                    });
                    // add the listener so it will tries to suggest while the user types
                    autocompleteGenero.addTextChangedListener(new CapturaNombreGeneroAutocompleteTextChangedListener(context, thisFragment, fam));
                    // ObjectItemData has no value at first
                    // set the custom ArrayAdapter
                    nombreGeneroArrayAdapter = new CapturaNombreGeneroArrayAdapter(context, R.layout.captura_autocomplete_list_item, generos);
                    autocompleteGenero.setAdapter(nombreGeneroArrayAdapter);
                }
                autocompleteFamilia.setText(txt);
            }
        });
        // add the listener so it will tries to suggest while the user types
        autocompleteFamilia.addTextChangedListener(new CapturaNombreFamiliaAutocompleteTextChangedListener(context, this));
        // ObjectItemData has no value at first
        // set the custom ArrayAdapter
        nombreFamiliaArrayAdapter = new CapturaNombreFamiliaArrayAdapter(context, R.layout.captura_autocomplete_list_item, familiaList);
        autocompleteFamilia.setAdapter(nombreFamiliaArrayAdapter);
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
        deMapa = false;
    }

    private void updateStatus(View view) {
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
                deMapa = false;
                hayFoto = true;
                updateStatus(null);
                MapActivity activity = (MapActivity) getActivity();
                Bitmap thumb = ImageUtils.getThumbnailFromCameraData(data, activity);
                selectedImage.setImageBitmap(thumb);
                bitmap = ImageUtils.getBitmapFromCameraData(data, activity);

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
                    String p = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
                    String[] parts = p.split("/");
                    Double alt = Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
//                    System.out.println("**************************" + alt + "     " + fotoLat + "       " + fotoLong);
                    fotoAlt = alt;
                    if (fotoLat != null && fotoLong != null) {
                        alerta(getString(R.string.captura_success_tag_gps));
                    } else {
                        alerta(getString(R.string.captura_error_tag_gps));
                    }
                } catch (Exception e) {
                    alerta(getString(R.string.captura_error_tag_gps));
                    fotoLat = null;
                    fotoLong = null;
                    fotoAlt = null;
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        System.out.println("ON TOUCH");
//        Utils.hideSoftKeyboard(context);
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(R.string.captura_title);
    }
}