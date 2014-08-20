package com.nth.ikiam.image;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.*;

/**
 * Created by svt on 7/29/2014.
 */
public class ImageUtils {


    /**
     * Use for decoding camera response data.
     *
     * @param data    : Intent
     * @param context : Context
     * @return : Bitmap
     */
    public static Bitmap getThumbnailFromCameraData(Intent data, Context context) {
        return getBitmapFromCameraData(data, context, 185, 185);
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
        return getBitmapFromCameraData(data, context, -1, -1);
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context, int w, int h) {
        boolean resize = false;
        if (w > 0 && h > 0) {
            resize = true;
        }
        Uri selectedImage = data.getData();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        try {
            ExifInterface exif = new ExifInterface(picturePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//            bitmap = rotateBitmap(bitmap, orientation);

            if (resize) {
//                int imageW = bitmap.getWidth();
//                int imageH = bitmap.getHeight();
//                int newW, newH;
//                if (imageW > imageH) {
//                    newW = w;
//                    newH = (w * imageH) / imageW;
//                } else {
//                    newH = h;
//                    newW = (h * imageW) / imageH;
//                }
//                System.out.println(">>>>>>>>>>>>>>> W=" + imageW + "         H=" + imageH);
//                System.out.println(">>>>>>>>>>>>>>> W=" + newW + "         H=" + newH);
//                bitmap = ImageUtils.decodeBitmap(picturePath, w, h);
                bitmap = decodeFile(picturePath, w, h);
//                bitmap = decodeFile(picturePath);
                bitmap = rotateBitmap(bitmap, orientation);
//                System.out.println(">>>>>>>>>>>>>>>***** W=" + bitmap.getWidth() + "         H=" + bitmap.getHeight());

                cursor.close();
            } else {
                bitmap = rotateBitmap(bitmap, orientation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeFile(picturePath);
        }
        return bitmap;
    }

    //    /**
//     * Decodes image and scales it to reduce memory consumption
//     * http://stackoverflow.com/questions/477572/strange-out-of-memory-issue-while-loading-an-image-to-a-bitmap-object
//     * edited Jan 12 '12 at 12:33 by Fedor
//     * and
//     * edited Mar 20 at 6:18 by Thomas Vervest
//     *
//     * @param path: image path
//     * @return: Bitmap
//     */
//    public static Bitmap decodeFile(String path, int screenWidth, int screenHeight) {
//        File f = new File(path);
//        try {
//            //Decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//
//            //The new size we want to scale to
//            final int REQUIRED_W = screenWidth - 10;
//            final int REQUIRED_H = screenHeight / 2;
//
//            //Find the correct scale value. It should be the power of 2.
//            int scale = 1;
//            while (o.outWidth / scale / 2 >= REQUIRED_W && o.outHeight / scale / 2 >= REQUIRED_H)
//                scale *= 2;
//
//            //Decode with inSampleSize
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize = scale;
//            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static Bitmap decodeFile(String path, int w, int h) {
        return decodeFile(path, w, h, false);
    }

    public static Bitmap decodeFile(String path, int w, int h, boolean force) {
//        System.out.println("Decode File Nuevo......" + w + " x " + h);
//        File f = new File(path);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            int imageW = bitmap.getWidth();
            int imageH = bitmap.getHeight();
            int newW = w;
            int newH = h;
//            int newW, newH;
            if (!force) {
                if (imageW > imageH) {
                    newW = w;
                    newH = (w * imageH) / imageW;
                } else {
                    newH = h;
                    newW = (h * imageW) / imageH;
                }
            }
            Bitmap photo = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            return photo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeFile(String path) {
        File f = new File(path);
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_W = 64;
            final int REQUIRED_H = 32;

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

            System.out.println("out");
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeBitmap(String path, int w, int h) {
        return decodeFile(path, w, h);
//        try {
//            File f = new File(path);
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            int imageW = bitmap.getWidth();
//            int imageH = bitmap.getHeight();
//            int newW, newH;
//            if (imageW > imageH) {
//                newW = w;
//                newH = (w * imageH) / imageW;
//            } else {
//                newH = h;
//                newW = (h * imageW) / imageH;
//            }
//            return decodeBitmap(new FileInputStream(f), newW, newH);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public static Bitmap decodeBitmap(InputStream stream, int w, int h) {
        try {
            //Decode image size
            Bitmap b = BitmapFactory.decodeStream(stream);
            return Bitmap.createScaledBitmap(b, w, h, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap[] dobleBitmap(InputStream stream, int w, int h, int w2, int h2) {
        try {
            //Decode image size
            Bitmap[] res = new Bitmap[2];
            Bitmap b = BitmapFactory.decodeStream(stream);
            res[0] = Bitmap.createScaledBitmap(b, w, h, false);
            res[1] = Bitmap.createScaledBitmap(b, w2, h2, false);
            return res;
        } catch (Exception e) {
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
