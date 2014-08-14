package com.nth.ikiam.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by svt on 7/29/2014.
 */
public class ImageUtils {

    public static Bitmap decodeFile(String path,int screenWidth, int screenHeight) {
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
    public static Bitmap decodeFile(String path) {
        File f = new File(path);
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_W = 64 ;
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
    public static Bitmap decodeBitmap(InputStream stream,int w,int h) {

        try {
            //Decode image size
            Bitmap b = BitmapFactory.decodeStream(stream);
            return Bitmap.createScaledBitmap(b, w, h, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
