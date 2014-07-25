package com.nth.ikiam;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by DELL on 23/07/2014.
 */
public class CameraFragment extends Fragment implements Button.OnClickListener {

    private Button chooseBtn;
    private ImageView selectedImage;

    private static final int CAMERA_PIC_REQUEST = 1337;

    private static int screenWidth;
    private static int screenHeight;

    public CameraFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_layout, container, false);
        selectedImage = (ImageView) view.findViewById(R.id.camera_chosen_image_view);
        chooseBtn = (Button) view.findViewById(R.id.camera_btn);
        chooseBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), R.string.camera_app_not_available, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            MainActivity activity = (MainActivity) getActivity();

            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            screenHeight = displaymetrics.heightPixels;
            screenWidth = displaymetrics.widthPixels;

            Bitmap bitmap = getBitmapFromCameraData(data, activity);
            selectedImage.setImageBitmap(bitmap);

//            Bitmap imageData = (Bitmap) data.getExtras().get("data");
//            selectedImage.setImageBitmap(imageData);
        }/* else if (resultCode == Activity.RESULT_CANCELED) {

        }*/
    }

    /**
     * Use for decoding camera response data.
     *
     * @param data
     * @param context
     * @return
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

    //decodes image and scales it to reduce memory consumption
    private static Bitmap decodeFile(String path) {
        File f = new File(path);
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_W = screenWidth - 10;
            final int REQUIRED_H = screenHeight / 2;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
////            while (o.outWidth / scale / 2 >= REQUIRED_SIZz E && o.outHeight / scale / 2 >= REQUIRED_SIZE)
////                scale *= 2;
            while (o.outWidth / scale / 2 >= REQUIRED_W && o.outHeight / scale / 2 >= REQUIRED_H)
                scale *= 2;
//            int scale = 1;
//            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
//                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
//                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
//            }
//            int scale = 1;
//            if (o.outHeight > REQUIRED_H || o.outWidth > REQUIRED_W) {
//                if (o.outHeight > REQUIRED_H) {
//                    scale = (int) Math.pow(2, (int) Math.ceil(Math.log(REQUIRED_W /
//                            (double) o.outWidth) / Math.log(0.5)));
//                } else {
//                    scale = (int) Math.pow(2, (int) Math.ceil(Math.log(REQUIRED_H /
//                            (double) o.outHeight) / Math.log(0.5)));
//                }
//            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

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