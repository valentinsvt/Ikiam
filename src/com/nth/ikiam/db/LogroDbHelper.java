package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 25/08/2014.
 */
public class LogroDbHelper extends DbHelper {

    private static final String LOG = "LogroDbHelper";

    public static final String KEY_CODIGO = "codigo";
    public static final String KEY_CANTIDAD = "cantidad";
    public static final String KEY_COMPLETO = "completo";

    public static final String[] KEYS_LOGRO = {KEY_CODIGO, KEY_CANTIDAD, KEY_COMPLETO};

    public LogroDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGRO);

        // create new tables
        onCreate(db);
    }

    public long createLogro(Logro logro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(logro);

        // insert row
        long res = db.insert(TABLE_LOGRO, null, values);
        db.close();
        return res;
    }

    public Logro getLogro(long logro_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGRO + " WHERE "
                + KEY_ID + " = " + logro_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Logro cl = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            cl = setDatos(c);
        }
        db.close();
        return cl;
    }

    public ArrayList<Logro> getAllLogros() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Logro> logroes = new ArrayList<Logro>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGRO +
                " ORDER BY " + KEY_COMPLETO + " DESC, " + KEY_CODIGO + " ASC";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Logro cl = setDatos(c);

                // adding to tags list
                logroes.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return logroes;
    }

    public ArrayList<Logro> getAllLogrosByTipoAndNotCompleto(int codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Logro> logroes = new ArrayList<Logro>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGRO +
                " WHERE " + KEY_CODIGO + " = " + codigo + " AND " + KEY_COMPLETO + " = 0";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Logro cl = setDatos(c);

                // adding to tags list
                logroes.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return logroes;
    }

    public ArrayList<Logro> getAllLogrosCompletos() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Logro> logroes = new ArrayList<Logro>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGRO +
                " WHERE " + KEY_COMPLETO + " = 1";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Logro cl = setDatos(c);

                // adding to tags list
                logroes.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return logroes;
    }

    public int countLogrosCompletos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_LOGRO +
                " WHERE " + KEY_COMPLETO + " = 1";
        Cursor c = db.rawQuery(selectQuery, null);
        int cant = 0;
        if (c.moveToFirst()) {
            cant = c.getInt(c.getColumnIndex("count"));
        }
        db.close();
        return cant;
    }

    public int countLogros() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_LOGRO;
        Cursor c = db.rawQuery(selectQuery, null);
        int cant = 0;
        if (c.moveToFirst()) {
            cant = c.getInt(c.getColumnIndex("count"));
        }
        db.close();
        return cant;
    }

    public List<Logro> getLogroByCodigo(String logro) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Logro> logroes = new ArrayList<Logro>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGRO +
                " WHERE " + KEY_CODIGO + " = '" + logro + "'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Logro cl = setDatos(c);

                // adding to tags list
                logroes.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return logroes;
    }

    public int updateLogro(Logro logro) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(logro);

        // updating row
        int res = db.update(TABLE_LOGRO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(logro.getId())});
        db.close();
        return res;
    }

    public void deleteLogro(Logro logro) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGRO, KEY_ID + " = ?",
                new String[]{String.valueOf(logro.id)});
        db.close();
    }

    public void deleteAllLogros() {
        String sql = "DELETE FROM " + TABLE_LOGRO;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    private Logro setDatos(Cursor c) {
        Logro cl = new Logro(this.context);
        cl.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        cl.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        cl.setCodigo(c.getInt(c.getColumnIndex(KEY_CODIGO)));
        cl.setCantidad(c.getDouble(c.getColumnIndex(KEY_CANTIDAD)));
        cl.setCompleto(c.getInt(c.getColumnIndex(KEY_COMPLETO)));
        return cl;
    }

    private ContentValues setValues(Logro logro) {
        ContentValues values = new ContentValues();
        values.put(KEY_FECHA, getDateTime());
        values.put(KEY_CODIGO, logro.codigo);
        values.put(KEY_CANTIDAD, logro.cantidad);
        values.put(KEY_COMPLETO, logro.completo);
        return values;
    }
}
