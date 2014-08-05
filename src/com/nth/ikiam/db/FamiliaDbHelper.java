package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 28/07/2014.
 */
public class FamiliaDbHelper extends DbHelper {

    private static final String LOG = "FamiliaDbHelper";

    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_NOMBRE_NORM = "nombre_norm";

    public static final String[] KEYS_FAMILIA = {KEY_NOMBRE, KEY_NOMBRE_NORM};

    public FamiliaDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAMILIA);

        // create new tables
        onCreate(db);
    }

    public long createFamilia(Familia familia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(familia, true);

        // insert row
        long res = db.insert(TABLE_FAMILIA, null, values);
        db.close();
        return res;
    }

    public Familia getFamilia(long familia_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FAMILIA + " WHERE "
                + KEY_ID + " = " + familia_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Familia fm = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            fm = setDatos(c);
        }
        db.close();
        return fm;
    }

    public List<Familia> getAllFamilias() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Familia> familias = new ArrayList<Familia>();
        String selectQuery = "SELECT  * FROM " + TABLE_FAMILIA +
                " ORDER BY " + KEY_NOMBRE;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Familia fm = setDatos(c);

                // adding to tags list
                familias.add(fm);
            } while (c.moveToNext());
        }
        db.close();
        return familias;
    }

    public List<Familia> getAllFamiliasByNombre(String familia) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Familia> familias = new ArrayList<Familia>();
        String selectQuery = "SELECT  * FROM " + TABLE_FAMILIA +
                " WHERE " + KEY_NOMBRE + " = '" + familia + "'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Familia fm = setDatos(c);

                // adding to tags list
                familias.add(fm);
            } while (c.moveToNext());
        }
        db.close();
        return familias;
    }

    public List<Familia> getAllFamiliasByNombreLike(String familia) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Familia> familias = new ArrayList<Familia>();
        String selectQuery = "SELECT  * FROM " + TABLE_FAMILIA +
                " WHERE LOWER(" + KEY_NOMBRE_NORM + ") LIKE '%" + familia.toLowerCase() + "%'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Familia fm = setDatos(c);

                // adding to tags list
                familias.add(fm);
            } while (c.moveToNext());
        }
        db.close();
        return familias;
    }

    public int countAllFamilias() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_FAMILIA;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int countFamiliasByNombre(String familia) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_FAMILIA +
                " WHERE " + KEY_NOMBRE + " = '" + familia + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int updateFamilia(Familia familia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(familia);

        // updating row
        int res = db.update(TABLE_FAMILIA, values, KEY_ID + " = ?",
                new String[]{String.valueOf(familia.getId())});
        db.close();
        return res;
    }

    public void deleteFamilia(Familia familia) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAMILIA, KEY_ID + " = ?",
                new String[]{String.valueOf(familia.id)});
        db.close();
    }

    public void deleteAllFamilias() {
        String sql = "DELETE FROM " + TABLE_FAMILIA;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    private Familia setDatos(Cursor c) {
        Familia fm = new Familia(this.context);
        fm.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        fm.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        fm.setNombre(c.getString(c.getColumnIndex(KEY_NOMBRE)));
        return fm;
    }

    private ContentValues setValues(Familia familia, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_NOMBRE, familia.nombre);
        values.put(KEY_NOMBRE_NORM, familia.nombreNorm);
        return values;
    }

    private ContentValues setValues(Familia familia) {
        return setValues(familia, false);
    }
}
