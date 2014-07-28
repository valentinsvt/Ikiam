package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Svt on 7/27/2014.
 */
public class CoordenadaDbHelper  extends DbHelper{

    private static final String LOG = "CoordenadaDbHelper";

    private static final String KEY_LONGITUD = "longitud";
    private static final String KEY_LATITUD = "latitud";
    private static final String KEY_RUTA = "ruta_id";
    private static final String[] KEYS_COORDENADA = {KEY_LATITUD,KEY_LONGITUD,KEY_RUTA};

    private static final String CREATE_TABLE_COORDENADA = createTableSql(TABLE_COORDENADA, KEYS_COORDENADA);

    public CoordenadaDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_COORDENADA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDENADA);

        // create new tables
        onCreate(db);
    }

    public long createCoordenada(Coordenada coordenada) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(coordenada, true);

        // insert row
        return db.insert(TABLE_COORDENADA, null, values);
    }

    public Coordenada getCoordenada(long coordenada_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_COORDENADA + " WHERE "
                + KEY_ID + " = " + coordenada_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        return setDatos(c);
    }

    public List<Coordenada> getAllCoordenadas() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Coordenada> coordenadas = new ArrayList<Coordenada>();
        String selectQuery = "SELECT  * FROM " + TABLE_COORDENADA;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Coordenada cl = setDatos(c);

                // adding to tags list
                coordenadas.add(cl);
            } while (c.moveToNext());
        }
        return coordenadas;
    }

    public List<Coordenada> getAllCoordenadasByRuta(Ruta ruta) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Coordenada> coordenadas = new ArrayList<Coordenada>();
        String selectQuery = "SELECT  * FROM " + TABLE_COORDENADA +
                " WHERE " + KEY_RUTA + " = " + ruta.id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Coordenada cl = setDatos(c);

                // adding to tags list
                coordenadas.add(cl);
            } while (c.moveToNext());
        }
        return coordenadas;
    }

    public int countAllCoordenadas() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_COORDENADA;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int countCoordenadasByRuta(Ruta ruta) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_COORDENADA +
                " WHERE " + KEY_RUTA + " = " + ruta.id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int updateCoordenada(Coordenada coordenada) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(coordenada);

        // updating row
        return db.update(TABLE_COORDENADA, values, KEY_ID + " = ?",
                new String[]{String.valueOf(coordenada.getId())});
    }

    public void deleteCoordenada(Coordenada coordenada) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COORDENADA, KEY_ID + " = ?",
                new String[]{String.valueOf(coordenada.id)});
    }

    public void deleteAllCoordenadaes() {
        String sql = "DELETE FROM " + TABLE_COORDENADA;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    private Coordenada setDatos(Cursor c) {
        Coordenada cord = new Coordenada(this.context);
        cord.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        cord.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        cord.setLatitud(c.getLong(c.getColumnIndex(KEY_LONGITUD)));
        cord.setLongitud(c.getLong(c.getColumnIndex(KEY_LONGITUD)));
        cord.setRuta(Ruta.get(context,c.getLong(c.getColumnIndex(KEY_RUTA))));
        return cord;
    }

    private ContentValues setValues(Coordenada coordenada, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_LATITUD, coordenada.latitud);
        values.put(KEY_LONGITUD, coordenada.longitud);
        values.put(KEY_RUTA,coordenada.ruta.id);
        return values;
    }

    private ContentValues setValues(Coordenada Coordenada) {
        return setValues(Coordenada, false);
    }
}
