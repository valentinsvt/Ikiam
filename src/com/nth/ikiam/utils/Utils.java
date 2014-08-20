package com.nth.ikiam.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Color;

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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void checkColores(MapActivity context) {
//        Color.empty(context);
//        Familia.empty(context);
//        Genero.empty(context);
//        Especie.empty(context);
//        Entry.empty(context);
//        Foto.empty(context);
//        Coordenada.empty(context);

        if (Color.count(context) == 0) {
            Color c0 = new Color(context, "none");
            c0.save();
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
            Color c10 = new Color(context, "negro");
            c10.save();
        }
    }
}
