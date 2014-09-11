package com.nth.ikiam.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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

    public AchievementChecker(MapActivity activity, int tipo, float cant) {
        this.activity = activity;
        this.tipo = tipo;
        this.cant = cant;
    }

    @Override
    public void run() {

        String felicidades = activity.getString(R.string.achievement_felicidades);

        List<Logro> logros = Logro.findAllByTipoAndNotCompleto(activity, this.tipo);
        for (Logro logro : logros) {
            if (cant >= logro.cantidad) {
                String strAchiev = "";
//                if (pluralId > 0) {
//                    strAchiev = activity.getResources().getQuantityString(pluralId, (int) cant, cant);
                strAchiev = Utils.getPluralResourceByName(activity, "achievement_" + tipo, logro.cantidad.intValue(), "" + logro.cantidad.intValue());
//                }
                logro.setCompleto(1);
                logro.save();
                String titulo = Utils.getStringResourceByName(activity, "achievement_" + tipo + "_titulo_" + logro.cantidad.intValue());
                activity.titulo = titulo;
                SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("titulo", titulo);
                editor.commit();
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this.activity)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(activity.getString(R.string.achievement_titulo) + ": " + titulo)
                                .setContentText(felicidades + " " + strAchiev);
                // Creates an explicit intent for an Activity in your app
//                Intent resultIntent = new Intent(this.activity, MapActivity.class);

                Intent resultIntent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
                if (resultIntent != null) {
                    resultIntent.setAction("" + activity.INTENT_LOGRO);
                }

                mBuilder.setAutoCancel(true);
                mBuilder.setLights(Color.BLUE, 500, 500);
                long[] pattern = {1000, 500};
                mBuilder.setVibrate(pattern);
                mBuilder.setSound(Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.glassbell));

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
