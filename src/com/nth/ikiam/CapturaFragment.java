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

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nth.ikiam.adapters.CapturaColorSpinnerAdapter;
import com.nth.ikiam.db.*;
import com.nth.ikiam.utils.CapturaUploader;
import com.nth.ikiam.utils.GeoDegree;
import com.nth.ikiam.utils.ImageUploader;
import com.nth.ikiam.utils.Utils;
//import com.nth.ikiam.utils.GeoDegree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
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
public class CapturaFragment extends Fragment implements Button.OnClickListener {

    private ImageButton[] botones;
    private ToggleButton[] toggles;

    private ImageView selectedImage;

    private TextView lblInfo;

    private EditText textoComentarios;
    private EditText textoFamilia;
    private EditText textoGenero;
    private EditText textoEspecie;
    private EditText textoNombreComun;

    private Spinner spinnerColor1;
    private Spinner spinnerColor2;

    private static final int GALLERY_REQUEST = 999;
    private static final int CAMERA_REQUEST = 1337;

    private static int screenWidth;
    private static int screenHeight;

    private String fotoPath;
    private Double fotoLat;
    private Double fotoLong;

    boolean hayFoto = false;

    Context context;
    private String pathFolder;
    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
//        pathFolder = getArguments().getString("pathFolder");

        pathFolder = Utils.getFolder(context);

        if (Color.count(context) == 0) {
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
        }

        View view = inflater.inflate(R.layout.captura_layout, container, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

        ArrayList<Color> colores = Color.listString(context);

        spinnerColor1 = (Spinner) view.findViewById(R.id.captura_color1_spinner);
        spinnerColor1.setAdapter(new CapturaColorSpinnerAdapter(getActivity(), colores));

        spinnerColor2 = (Spinner) view.findViewById(R.id.captura_color2_spinner);
        spinnerColor2.setAdapter(new CapturaColorSpinnerAdapter(getActivity(), colores));

        selectedImage = (ImageView) view.findViewById(R.id.captura_chosen_image_view);

        lblInfo = (TextView) view.findViewById(R.id.captura_info_label);

        textoComentarios = (EditText) view.findViewById(R.id.captura_comentarios_txt);
        textoFamilia = (EditText) view.findViewById(R.id.captura_nombre_familia_txt);
        textoGenero = (EditText) view.findViewById(R.id.captura_nombre_genero_txt);
        textoEspecie = (EditText) view.findViewById(R.id.captura_nombre_especie_txt);
        textoNombreComun = (EditText) view.findViewById(R.id.captura_nombre_comun_txt);

        botones = new ImageButton[4];
        botones[0] = (ImageButton) view.findViewById(R.id.captura_gallery_btn);
        botones[1] = (ImageButton) view.findViewById(R.id.captura_camera_btn);
        botones[2] = (ImageButton) view.findViewById(R.id.captura_save_btn);
        botones[3] = (ImageButton) view.findViewById(R.id.captura_save_upload_btn);
        for (ImageButton button : botones) {
            button.setOnClickListener(this);
        }
        toggles = new ToggleButton[5];
        toggles[0] = (ToggleButton) view.findViewById(R.id.captura_arbol_toggle);
        toggles[1] = (ToggleButton) view.findViewById(R.id.captura_corteza_toggle);
        toggles[2] = (ToggleButton) view.findViewById(R.id.captura_hoja_toggle);
        toggles[3] = (ToggleButton) view.findViewById(R.id.captura_flor_toggle);
        toggles[4] = (ToggleButton) view.findViewById(R.id.captura_fruta_toggle);
        for (ToggleButton toggle : toggles) {
            toggle.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == botones[0].getId()) { // galeria
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(intent, GALLERY_REQUEST);
            } else {
                alerta(getString(R.string.gallery_app_not_available));
            }
        }
        if (v.getId() == botones[1].getId()) { // camara
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            } else {
                alerta(getString(R.string.camera_app_not_available));
            }
        }
        if (v.getId() == botones[2].getId() || v.getId() == botones[3].getId()) { // save || upload
            if (hayFoto) {
                String[] keys = {"arbol", "corteza", "hoja", "flor", "fruta"};
                Color color1 = (Color) spinnerColor1.getSelectedItem();
                Color color2 = (Color) spinnerColor2.getSelectedItem();
                String nombreFamilia = textoFamilia.getText().toString().trim();
                String nombreGenero = textoGenero.getText().toString().trim();
                String nombreEspecie = textoEspecie.getText().toString().trim();
                String nombreComun = textoNombreComun.getText().toString().trim();
                String comentarios = textoComentarios.getText().toString().trim();
                String keywords = "";
                int i = 0;
                for (ToggleButton toggle : toggles) {
                    if (toggle.isChecked()) {
                        if (!keywords.equals("")) {
                            keywords += ", ";
                        }
                        keywords += keys[i];
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
                    if (color2 != null) {
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
                    Coordenada coordenada = new Coordenada(context, fotoLat, fotoLong);
                    foto.setCoordenada(coordenada);
                }

                File file = new File(pathFolder, fotoPath);
                fotoPath = file.getName();
                file = new File(pathFolder, fotoPath);
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
                foto.setPath(pathFolder + "/" + fotoPath);
                foto.setUploaded(0);
                foto.save();
                //System.out.println("foto: " + foto.id + "entry: " + entry.id + "  especie: " + especie.id + "   " + especie.nombre + "  (" + genero.nombre + " " + genero.id + ")" + "  (" + familia.nombre + " " + familia.id + ")");
                if (v.getId() == botones[3].getId()) {
                    // aqui hace upload al servidor.....
                    ExecutorService queue = Executors.newSingleThreadExecutor();
                    queue.execute(new CapturaUploader(getActivity(), queue, foto, 0));
                }
//                System.out.println("Save: <" + keywords + "> <" + comentarios + ">");
            } else {
                alerta(getString(R.string.captura_error_seleccion));
            }
        }
        if (v.getId() == toggles[0].getId()) { //tree
            updateStatus(toggles[0]);
        }
        if (v.getId() == toggles[1].getId()) { //bark
            updateStatus(toggles[1]);
        }
        if (v.getId() == toggles[2].getId()) { //leaf
            updateStatus(toggles[2]);
        }
        if (v.getId() == toggles[3].getId()) { //flower
            updateStatus(toggles[3]);
        }
        if (v.getId() == toggles[4].getId()) { //fruit
            updateStatus(toggles[4]);
        }
    }

    private void updateStatus(ToggleButton toggleButton) {
        if (hayFoto) {
            String info = "";
            String[] statusString = {getString(R.string.captura_tiene_arbol), getString(R.string.captura_tiene_corteza),
                    getString(R.string.captura_tiene_hoja), getString(R.string.captura_tiene_flor), getString(R.string.captura_tiene_fruta)};

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
                updateStatus(null);
                MapActivity activity = (MapActivity) getActivity();
                bitmap = getBitmapFromCameraData(data, activity);
                selectedImage.setImageBitmap(bitmap);

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
                } catch (Exception e) {
                    alerta(getString(R.string.captura_error_tag_gps));
                    fotoLat = null;
                    fotoLong = null;
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Use for decoding camera response data.
     *
     * @param data    : Intent
     * @param context : Context
     * @return : Bitmap
     */
    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {

        Uri selectedImage = data.getData();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        Bitmap bitmap = decodeFile(picturePath);
        try {
            ExifInterface exif = new ExifInterface(picturePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            bitmap = rotateBitmap(bitmap, orientation);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeFile(picturePath);
        }
        cursor.close();
        return bitmap;
    }

    /**
     * Decodes image and scales it to reduce memory consumption
     * http://stackoverflow.com/questions/477572/strange-out-of-memory-issue-while-loading-an-image-to-a-bitmap-object
     * edited Jan 12 '12 at 12:33 by Fedor
     * and
     * edited Mar 20 at 6:18 by Thomas Vervest
     *
     * @param path: image path
     * @return: Bitmap
     */
    private static Bitmap decodeFile(String path) {
        File f = new File(path);
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
//            final int REQUIRED_W = screenWidth / 4;
//            final int REQUIRED_H = screenWidth / 4;
            final int REQUIRED_W = 100;
            final int REQUIRED_H = 100;

//            System.out.println("reqW: " + REQUIRED_W + "    reqH: " + REQUIRED_H);

            //Find the correct scale value. It should be the power of 2.
            int scaleW = 1, scaleH = 1;
            while (o.outWidth / scaleW / 2 >= REQUIRED_W)
                scaleW *= 2;
            while (o.outHeight / scaleH / 2 >= REQUIRED_H)
                scaleH *= 2;

//            System.out.println("scaleW: " + scaleW + "    scaleH: " + scaleH);
//            System.out.println("scale: " + (Math.max(scaleH, scaleW)));

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = Math.min(scaleH, scaleW);
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * rotates image
     * http://stackoverflow.com/questions/20478765/how-to-get-the-correct-orientation-of-the-image-selected-from-the-default-image/20480741#20480741
     * answered Dec 9 '13 at 21:04 by ramaral
     *
     * @param bitmap:      Bitmap
     * @param orientation: orientation
     * @return: Bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        try {
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}