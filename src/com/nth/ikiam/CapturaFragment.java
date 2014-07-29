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
import com.nth.ikiam.db.*;
import com.nth.ikiam.utils.GeoDegree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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
    private Spinner spinnerColor1;
    private Spinner spinnerColor2;

    private static final int GALLERY_REQUEST = 999;
    private static final int CAMERA_REQUEST = 1337;

    private static int screenWidth;
    private static int screenHeight;

    private String fotoPath;
    private double fotoLat;
    private double fotoLong;

    boolean hayFoto = false;

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.captura_layout, container, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

        ArrayList<Color> colores = Color.listString(context);

        spinnerColor1 = (Spinner) view.findViewById(R.id.captura_color1_spinner);
        spinnerColor1.setAdapter(new MyAdapter(getActivity(), colores));

        spinnerColor2 = (Spinner) view.findViewById(R.id.captura_color2_spinner);
        spinnerColor2.setAdapter(new MyAdapter(getActivity(), colores));

        selectedImage = (ImageView) view.findViewById(R.id.captura_chosen_image_view);
        lblInfo = (TextView) view.findViewById(R.id.captura_info_label);
        textoComentarios = (EditText) view.findViewById(R.id.captura_comentarios_txt);

        botones = new ImageButton[3];
        botones[0] = (ImageButton) view.findViewById(R.id.captura_gallery_btn);
        botones[1] = (ImageButton) view.findViewById(R.id.captura_camera_btn);
        botones[2] = (ImageButton) view.findViewById(R.id.captura_save_btn);
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
        if (v.getId() == botones[2].getId()) { // save
            if (hayFoto) {
                String[] keys = {"arbol", "corteza", "hoja", "flor", "fruta"};
                String comentarios = textoComentarios.getText().toString();
                Color col = (Color) spinnerColor1.getSelectedItem();
                System.out.println("************************************************");
                System.out.println(spinnerColor1.getSelectedItem());
                System.out.println(col.getId());
                System.out.println("************************************************");
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

                System.out.println("Save: <" + keywords + "> <" + comentarios + ">");
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
                MainActivity activity = (MainActivity) getActivity();
                Bitmap bitmap = getBitmapFromCameraData(data, activity);
                selectedImage.setImageBitmap(bitmap);

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                fotoPath = cursor.getString(columnIndex);

                try {
                    ExifInterface exif = new ExifInterface(fotoPath);
                    GeoDegree gd = new GeoDegree(exif);

                    System.out.println("************************************************************");
                    System.out.println(exif.getAttribute(ExifInterface.TAG_DATETIME));
                    System.out.println(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                    System.out.println(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                    System.out.println(exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE));
                    System.out.println(gd.getLatitudeE6());
                    System.out.println(gd.getLongitudeE6());
                    System.out.println("************************************************************");
                } catch (Exception e) {
                    alerta(getString(R.string.captura_error_tag_gps));
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