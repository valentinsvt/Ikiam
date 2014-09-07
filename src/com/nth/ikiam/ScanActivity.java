package com.nth.ikiam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.SurfaceView;

import android.view.View;
import com.moodstocks.android.AutoScannerSession;
import com.moodstocks.android.Scanner;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.nth.ikiam.utils.UtilsUploaders;

/**
 * Created by Svt on 9/7/2014.
 */
public class ScanActivity extends Activity implements AutoScannerSession.Listener {
    private AutoScannerSession session = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
        System.out.println("se creo la actividad");
        SurfaceView preview = (SurfaceView)findViewById(R.id.preview);

        try {
            session = new AutoScannerSession(this, Scanner.get(), this, preview);
            session.setSearchOptions(Scanner.SearchOption.SMALLTARGET);
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraOpenFailed(Exception e) {

    }

    @Override
    public void onResult(Result result) {
        System.out.println("result "+result.getValue());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.resume();
            }
        });

        builder.setTitle(result.getType() == Result.Type.IMAGE ? "Especie:" : "Especie:");
        String value = result.getValue();
        String[] datos;
        String res="";
        String especie="";
        if(value!=null && value.length()>1){
            datos=value.split("_");
            if(datos.length>1){
                res=getString(R.string.scaner_encontrada);
                especie=datos[1];
                res+=" "+datos[1];
            }
        }
        final String esp = especie;
        builder.setPositiveButton("Ver información", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = UtilsUploaders.getIp() + "especie/show?nombre=" + esp;
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setMessage(res);
        builder.show();
    }

    @Override
    public void onWarning(String s) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        session.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        session.stop();
    }
}