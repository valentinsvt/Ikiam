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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by DELL on 23/07/2014.
 */

/**
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
public class GalleryFragment extends Fragment implements Button.OnClickListener {

    private Button chooseBtn;
    private ImageView selectedImage;

    private static final int IMAGE_PICKER_SELECT = 999;

    private static int screenWidth;
    private static int screenHeight;

    public GalleryFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_layout, container, false);

        chooseBtn = (Button) view.findViewById(R.id.gallery_choose_btn);
        selectedImage = (ImageView) view.findViewById(R.id.gallery_chosen_image_view);

        // Set OnItemClickListener so we can be notified on button clicks
        chooseBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // catch event that there's no activity to handle intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            //to get image and videos, I used a */"
//            intent.setType("*/*");
            startActivityForResult(intent, IMAGE_PICKER_SELECT);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), R.string.gallery_app_not_available, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Photo Selection result
     * Read more at http://www.airpair.com/android/android-image-picker-select-gallery-images#bok7edWCrB11olZ1.99
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            MainActivity activity = (MainActivity) getActivity();

            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            screenHeight = displaymetrics.heightPixels;
            screenWidth = displaymetrics.widthPixels;

            Bitmap bitmap = getBitmapFromCameraData(data, activity);
            selectedImage.setImageBitmap(bitmap);
        }
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