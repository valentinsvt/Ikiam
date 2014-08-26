package com.nth.ikiam.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.nth.ikiam.MapActivity;
import com.nth.ikiam.R;
import com.nth.ikiam.db.Logro;

import java.util.List;

/**
 * Created by Svt on 8/25/2014.
 */
public class AchievementChecker implements Runnable {
    MapActivity activity;
    int tipo;
    float cant;
    public final int ACHIEV_FOTOS = 1;
    public final int ACHIEV_DISTANCIA = 2;
    public final int ACHIEV_UPLOADS = 3;
    public final int ACHIEV_SHARE = 4;

    public AchievementChecker(MapActivity activity, int tipo, float cant) {
        this.activity = activity;
        this.tipo = tipo;
        this.cant = cant;
    }

    @Override
    public void run() {

        int pluralId = 0;

        switch (tipo) {
            case ACHIEV_FOTOS:
                pluralId = R.plurals.achievement_foto;
                break;
            case ACHIEV_DISTANCIA:
                pluralId = R.plurals.achievement_distancia;
                break;
            case ACHIEV_UPLOADS:
                pluralId = R.plurals.achievement_upload;
                break;
            case ACHIEV_SHARE:
                pluralId = R.plurals.achievement_share;
                break;
        }

        List<Logro> logros = Logro.findAllByTipoAndNotCompleto(activity, this.tipo);
        for (Logro logro : logros) {
            if (cant >= logro.cantidad) {
                String strAchiev = "";
                if (pluralId > 0) {
                    strAchiev = activity.getResources().getQuantityString(pluralId, (int) cant, cant);
                }
                logro.setCompleto(1);
                logro.save();
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this.activity)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(activity.getString(R.string.achievement_titulo))
                                .setContentText(strAchiev);
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this.activity, MapActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.activity);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MapActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) this.activity.getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(1, mBuilder.build());
            }
        }


    }
}
