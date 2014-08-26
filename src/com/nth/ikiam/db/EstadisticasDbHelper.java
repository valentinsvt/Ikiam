package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by DELL on 25/08/2014.
 */
public class EstadisticasDbHelper extends DbHelper {
    // Logcat tag
    private static final String LOG = "EstadisticasDbHelper";

    // ESPECIE Table - column names
    public static final String KEY_DISTANCIA_RECORRIDA = "distancia_recorrida";
    public static final String KEY_FOTOS_TOMADAS = "fotos_tomadas";
    public static final String KEY_FOTOS_SUBIDAS = "fotos_subidas";

    public static final String[] KEYS_ESTADISTICAS = {KEY_DISTANCIA_RECORRIDA, KEY_FOTOS_TOMADAS, KEY_FOTOS_SUBIDAS};

    public EstadisticasDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTADISTICAS);

        // create new tables
        onCreate(db);
    }

    public long createEstadisticas(Estadisticas estadisticas) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(estadisticas, true);

        // insert row
        long res = db.insert(TABLE_ESTADISTICAS, null, values);
        db.close();
        return res;
    }

    public Estadisticas getEstadisticas(long estadisticas_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ESTADISTICAS + " WHERE "
                + KEY_ID + " = " + estadisticas_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Estadisticas cl = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            cl = setDatos(c);
        }
        db.close();
        return cl;
    }

    public ArrayList<Estadisticas> getAllEstadisticases() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Estadisticas> estadisticases = new ArrayList<Estadisticas>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESTADISTICAS;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Estadisticas cl = setDatos(c);

                // adding to tags list
                estadisticases.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return estadisticases;
    }

    public int updateEstadisticas(Estadisticas estadisticas) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(estadisticas);

        // updating row
        int res = db.update(TABLE_ESTADISTICAS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(estadisticas.getId())});
        db.close();
        return res;
    }

    public void deleteEstadisticas(Estadisticas estadisticas) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ESTADISTICAS, KEY_ID + " = ?",
                new String[]{String.valueOf(estadisticas.id)});
        db.close();
    }

    public void deleteAllEstadisticases() {
        String sql = "DELETE FROM " + TABLE_ESTADISTICAS;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    private Estadisticas setDatos(Cursor c) {
        Estadisticas cl = new Estadisticas(this.context);
        cl.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        cl.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        cl.setDistanciaRecorrida(c.getDouble(c.getColumnIndex(KEY_DISTANCIA_RECORRIDA)));
        cl.setFotosTomadas(c.getInt(c.getColumnIndex(KEY_FOTOS_TOMADAS)));
        cl.setFotosSubidas(c.getInt(c.getColumnIndex(KEY_FOTOS_SUBIDAS)));
        return cl;
    }

    private ContentValues setValues(Estadisticas estadisticas, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_DISTANCIA_RECORRIDA, estadisticas.distanciaRecorrida);
        values.put(KEY_FOTOS_TOMADAS, estadisticas.fotosTomadas);
        values.put(KEY_FOTOS_SUBIDAS, estadisticas.fotosSubidas);
        return values;
    }

    private ContentValues setValues(Estadisticas estadisticas) {
        return setValues(estadisticas, false);
    }
}
