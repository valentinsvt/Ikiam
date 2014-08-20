package com.nth.ikiam.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.nth.ikiam.image.ImageUtils;

import java.lang.ref.WeakReference;

/**
 * Created by luz on 20/08/14.
 */
public class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String data = "";

    public ImageLoaderTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        data = params[0];
        return ImageUtils.decodeFile(data, 100, 100);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}