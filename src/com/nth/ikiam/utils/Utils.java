package com.nth.ikiam.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Color;

import java.io.File;

/**
 * Created by DELL on 30/07/2014.
 */
public class Utils {

    public static void openFragment(MapActivity context, Fragment fragment, String title) {
        openFragment(context, fragment, title, null);
    }

    public static void openFragment(MapActivity context, Fragment fragment, String title, Bundle args) {
        context.setTitle(title);
        FragmentManager fragmentManager = context.getFragmentManager();
        RelativeLayout mainLayout = (RelativeLayout) context.findViewById(R.id.rl2);
        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .hide(fragmentManager.findFragmentById(R.id.content_frame))
                    .addToBackStack("")
                    .commit();
            mainLayout.setVisibility(View.VISIBLE);
        } else {
            if (args != null) {
                fragment.setArguments(args);
            }
//                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack("")
                    .commit();
            mainLayout.setVisibility(LinearLayout.GONE);
        }
    }

    public static String getStringResourceByName(Context c, String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return c.getString(resId);
        }
    }

    public static String getPluralResourceByName(Context c, String aString, int quantity, String param1) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "plurals", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return c.getResources().getQuantityString(resId, quantity, param1);
        }
    }

    public static int getImageResourceByName(Context c, String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "drawable", packageName);
        return resId;
    }

    public static boolean isNetworkAvailable(Context context) {
        // Get Connectivity Manager class object from Systems Service
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get Network Info from connectivity Manager
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        Toast.makeText(context, context.getString(R.string.global_error_no_connection), Toast.LENGTH_LONG).show();
        return false;
    }

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
