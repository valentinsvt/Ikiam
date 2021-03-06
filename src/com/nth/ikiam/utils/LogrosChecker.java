package com.nth.ikiam.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Color;
import com.nth.ikiam.db.Logro;

import java.util.List;

/**
 * Created by Svt on 8/25/2014.
 */
public class LogrosChecker implements Runnable {
    MapActivity activity;

    public final int ACHIEV_FOTOS = 1;
    public final int ACHIEV_DISTANCIA = 2;
    public final int ACHIEV_UPLOADS = 3;
    public final int ACHIEV_SHARE = 4;

    public LogrosChecker(MapActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        if (Logro.count(activity) == 0) {
            Double cantidades[] = {1d, 5d, 10d, 50d, 100d};
            for (Double cantidad : cantidades) {
                Logro logro = new Logro(activity);
                logro.codigo = ACHIEV_FOTOS;
                logro.cantidad = cantidad;
                logro.completo = 0;
                logro.save();
                Logro logro2 = new Logro(activity);
                logro2.codigo = ACHIEV_UPLOADS;
                logro2.cantidad = cantidad;
                logro2.completo = 0;
                logro2.save();
                Logro logro3 = new Logro(activity);
                logro3.codigo = ACHIEV_SHARE;
                logro3.cantidad = cantidad;
                logro3.completo = 0;
                logro3.save();
                System.out.println("fotos - uploads - shares : " + cantidad);
            }
            Double cantidades2[] = {1000d, 2000d, 3000d, 5000d, 10000d};
            for (Double cantidad : cantidades2) {
                Logro logro = new Logro(activity);
                logro.codigo = ACHIEV_DISTANCIA;
                logro.cantidad = cantidad;
                logro.completo = 0;
                logro.save();
                System.out.println("distancia : " + cantidad);
            }
        }
        if (Color.count(activity) == 0) {
            Color c0 = new Color(activity, "none");
            c0.save();
            Color c1 = new Color(activity, "azul");
            c1.save();
            Color c2 = new Color(activity, "cafe");
            c2.save();
            Color c3 = new Color(activity, "verde");
            c3.save();
            Color c4 = new Color(activity, "naranja");
            c4.save();
            Color c5 = new Color(activity, "rosa");
            c5.save();
            Color c6 = new Color(activity, "violeta");
            c6.save();
            Color c7 = new Color(activity, "rojo");
            c7.save();
            Color c8 = new Color(activity, "blanco");
            c8.save();
            Color c9 = new Color(activity, "amarillo");
            c9.save();
            Color c10 = new Color(activity, "negro");
            c10.save();
        }
    }
}
