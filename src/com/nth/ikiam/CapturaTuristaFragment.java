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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.nth.ikiam.utils.GeoDegree;

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
public class CapturaTuristaFragment extends Fragment implements Button.OnClickListener, FieldListener, View.OnTouchListener {

    private ImageButton[] botones;

    private ImageView selectedImage;

    private TextView lblInfo;

    private EditText textoComentarios;

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

        View view = inflater.inflate(R.layout.captura_turista_layout, container, false);
        View scrollview = view.findViewById(R.id.captura_turista_scroll_view);
//        view.setOnTouchListener(this);
        scrollview.setOnTouchListener(this);

        activity = (MapActivity) getActivity();
        screenHeight = activity.screenHeight;
        screenWidth = activity.screenWidth;
        selectedImage = (ImageView) view.findViewById(R.id.captura_chosen_image_view);
        lblInfo = (TextView) view.findViewById(R.id.captura_info_label);
        textoComentarios = (EditText) view.findViewById(R.id.captura_comentarios_txt);

        initButtons(view);
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
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                String comentarios = textoComentarios.getText().toString().trim();

                Entry entry = new Entry(context);
                entry.setComentarios(comentarios);
                entry.setUploaded(0);
                entry.save();

                if (!deMapa) {
                    fotoSubir = new Foto(context);
                }

                if (entry != null) {
                    fotoSubir.setEntry(entry);
                }
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

                String nuevoNombre = "photo_" + fotoSubir.id + ".jpg";

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
                fotoSubir.setPath(pathFolder + "/" + nuevoNombre);
                fotoSubir.setUploaded(0);
                fotoSubir.save();
//                String msg = "Foto guardada";
                //System.out.println("foto: " + foto.id + "entry: " + entry.id + "  especie: " + especie.id + "   " + especie.nombre + "  (" + genero.nombre + " " + genero.id + ")" + "  (" + familia.nombre + " " + familia.id + ")");
                if (v.getId() == botones[3].getId()) {
                    String id = context.userId; //id (faceboook - fb id, ikiam db.id
                    if (id != null && !id.equals("-1")) {
                        // aqui hace upload al servidor.....
                        ExecutorService queue = Executors.newSingleThreadExecutor();
                        queue.execute(new CapturaUploader(context, queue, fotoSubir, 0));
                    } else {
                        //redirect a login
                        System.out.println("Login first!!!");
                        context.selectItem(context.LOGIN_POS);
                    }
//                    msg += " y subida ";
                } else {
                    alerta(getString(R.string.uploader_upload_success));
                }
                context.updateAchivement(context.ACHIEV_FOTOS);
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
            } else {
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

    private void resetForm() {
        selectedImage.setImageDrawable(null);
        textoComentarios.setText("");
        hayFoto = false;
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
                    fotoAlt = Double.parseDouble(exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE));
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
        Utils.hideSoftKeyboard(context);
        return false;
    }
}