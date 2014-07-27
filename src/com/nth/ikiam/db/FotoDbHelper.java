package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 26/07/2014.
 */
public class FotoDbHelper extends DbHelper {

    // Logcat tag
    private static final String LOG = "FotoDbHelper";

    //  FOTO - column names
    private static final String KEY_COMENTARIOS = "comentarios";
    private static final String KEY_ESPECIE_ID = "especie_id";
    private static final String KEY_LATITUD = "latitud";
    private static final String KEY_LONGITUD = "longitud";
    private static final String KEY_KEYWORDS = "keywords";
    private static final String[] KEYS_FOTO = {KEY_ESPECIE_ID, KEY_LATITUD, KEY_LONGITUD, KEY_KEYWORDS};

    // FOTO table create statement
    private static final String CREATE_TABLE_FOTO = createTableSql(TABLE_FOTO, KEYS_FOTO);

    public FotoDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_FOTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOTO);

        // create new tables
        onCreate(db);
    }

    public long createFoto(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(foto, true);

        // insert row
        return db.insert(TABLE_FOTO, null, values);
    }

    public Foto getFoto(long foto_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FOTO + " WHERE "
                + KEY_ID + " = " + foto_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        return setDatos(c);
    }

    public int countAllFotos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_FOTO;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int countFotosByEspecie(Especie especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_FOTO +
                " WHERE " + KEY_ESPECIE_ID + " = '" + especie.id + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public List<Foto> getAllFotos() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Foto> fotos = new ArrayList<Foto>();
        String selectQuery = "SELECT  * FROM " + TABLE_FOTO;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Foto f = setDatos(c);

                // adding to tags list
                fotos.add(f);
            } while (c.moveToNext());
        }
        return fotos;
    }

    public List<Foto> getAllFotosByEspecie(Especie especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Foto> fotos = new ArrayList<Foto>();

//        String selectQuery = "SELECT * FROM " + TABLE_FOTO + " tf, " + TABLE_ESPECIE + " te " +
//                "WHERE tf." + KEY_ESPECIE_ID + "=te." + KEY_ID +
//                " AND te." + KEY_ID + "='" + especie.id + "' ";

        String selectQuery = "SELECT * FROM " + TABLE_FOTO +
                " WHERE " + KEY_ESPECIE_ID + " = " + especie.id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Foto f = setDatos(c);

                // adding to foto list
                fotos.add(f);
            } while (c.moveToNext());
        }
        return fotos;
    }

    public List<Foto> getAllFotosByKeyword(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Foto> fotos = new ArrayList<Foto>();

        String selectQuery = "SELECT * FROM " + TABLE_FOTO +
                " WHERE " + KEY_KEYWORDS + " LIKE '%" + keyword + "%'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Foto f = setDatos(c);

                // adding to foto list
                fotos.add(f);
            } while (c.moveToNext());
        }
        return fotos;
    }

    public int updateFoto(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(foto);

        // updating row
        return db.update(TABLE_FOTO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(foto.getId())});
    }

    public void deleteFoto(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOTO, KEY_ID + " = ?",
                new String[]{String.valueOf(foto.id)});
    }

    public void deleteAllFotos() {
        String sql = "DELETE FROM " + TABLE_FOTO;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    private Foto setDatos(Cursor c) {
        Foto f = new Foto(this.context);
        f.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        f.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        f.setEspecie(Especie.get(this.context, c.getLong(c.getColumnIndex(KEY_ESPECIE_ID))));
        f.setLatitud((c.getDouble(c.getColumnIndex(KEY_LATITUD))));
        f.setLongitud((c.getDouble(c.getColumnIndex(KEY_LONGITUD))));
        f.setComentarios((c.getString(c.getColumnIndex(KEY_COMENTARIOS))));
        f.setKeywords((c.getString(c.getColumnIndex(KEY_KEYWORDS))));
        return f;
    }

    private ContentValues setValues(Foto foto, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_ESPECIE_ID, foto.especie.id);
        values.put(KEY_LATITUD, foto.getLatitud());
        values.put(KEY_LONGITUD, foto.getLongitud());
        values.put(KEY_COMENTARIOS, foto.getComentarios());
        values.put(KEY_KEYWORDS, foto.getKeywords());
        return values;
    }

    private ContentValues setValues(Foto foto) {
        return setValues(foto, false);
    }

}
