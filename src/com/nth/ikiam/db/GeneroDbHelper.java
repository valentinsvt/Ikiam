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
public class GeneroDbHelper extends DbHelper {

    private static final String LOG = "GeneroDbHelper";

    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_FAMILIA_ID = "familia_id";
    private static final String[] KEYS_GENERO = {KEY_NOMBRE, KEY_FAMILIA_ID};

    private static final String CREATE_TABLE_GENERO = createTableSql(TABLE_GENERO, KEYS_GENERO);

    public GeneroDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_GENERO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERO);

        // create new tables
        onCreate(db);
    }

    public long createGenero(Genero genero) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(genero, true);

        // insert row
        return db.insert(TABLE_GENERO, null, values);
    }

    public Genero getGenero(long genero_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_GENERO + " WHERE "
                + KEY_ID + " = " + genero_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        return setDatos(c);
    }

    public List<Genero> getAllGeneros() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Genero> generos = new ArrayList<Genero>();
        String selectQuery = "SELECT  * FROM " + TABLE_GENERO;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Genero gn = setDatos(c);

                // adding to tags list
                generos.add(gn);
            } while (c.moveToNext());
        }
        return generos;
    }

    public List<Genero> getAllGenerosByNombre(String genero) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Genero> generos = new ArrayList<Genero>();
        String selectQuery = "SELECT  * FROM " + TABLE_GENERO +
                " WHERE " + KEY_NOMBRE + " = '" + genero + "'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Genero gn = setDatos(c);

                // adding to tags list
                generos.add(gn);
            } while (c.moveToNext());
        }
        return generos;
    }

    public List<Genero> getAllGenerosByFamilia(Familia familia) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Genero> generos = new ArrayList<Genero>();
        String selectQuery = "SELECT  * FROM " + TABLE_GENERO +
                " WHERE " + KEY_FAMILIA_ID + " = '" + familia.id + "'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Genero gn = setDatos(c);

                // adding to tags list
                generos.add(gn);
            } while (c.moveToNext());
        }
        return generos;
    }

    public int countAllGeneros() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_GENERO;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int countGenerosByNombre(String genero) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_GENERO +
                " WHERE " + KEY_NOMBRE + " = '" + genero + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int countGenerosByFamilia(Familia familia) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_GENERO +
                " WHERE " + KEY_FAMILIA_ID + " = '" + familia.id + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int updateGenero(Genero genero) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(genero);

        // updating row
        return db.update(TABLE_GENERO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(genero.getId())});
    }

    public void deleteGenero(Genero genero) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GENERO, KEY_ID + " = ?",
                new String[]{String.valueOf(genero.id)});
    }

    public void deleteAllGeneros() {
        String sql = "DELETE FROM " + TABLE_GENERO;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    private Genero setDatos(Cursor c) {
        Genero gn = new Genero(this.context);
        gn.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        gn.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        gn.setNombre(c.getString(c.getColumnIndex(KEY_NOMBRE)));
        gn.setFamilia(Familia.get(this.context, c.getLong(c.getColumnIndex(KEY_FAMILIA_ID))));
        return gn;
    }

    private ContentValues setValues(Genero genero, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_NOMBRE, genero.nombre);
        values.put(KEY_FAMILIA_ID, genero.familia.id);
        return values;
    }

    private ContentValues setValues(Genero genero) {
        return setValues(genero, false);
    }
}