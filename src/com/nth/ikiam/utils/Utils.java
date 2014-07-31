package com.nth.ikiam.utils;

import android.content.Context;
import android.os.Environment;
import com.nth.ikiam.R;

import java.io.File;

/**
 * Created by DELL on 30/07/2014.
 */
public class Utils {

    public static String getFolder(Context context) {
        String pathFolder;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            pathFolder = Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name);
//            System.out.println("1" + Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name));
        } else {
            /* save the folder in internal memory of phone */
            pathFolder = "/data/data/" + context.getPackageName() + File.separator + context.getString(R.string.app_name);
//            System.out.println("2" + "/data/data/" + context.getPackageName() + File.separator + context.getString(R.string.app_name));
        }
        File folder = new File(pathFolder);
        folder.mkdirs();
        return pathFolder;
    }
}
