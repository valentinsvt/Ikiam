package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 26/07/2014.
 */
public class FotoDbHelper extends DbHelper {

    // Logcat tag
    private static final String LOG = "FotoDbHelper";

    //  FOTO - column names
    private static final String KEY_ESPECIE_ID = "especie_id";
    private static final String KEY_ENTRY_ID = "entry_id";
    private static final String KEY_COORDENADA = "coordenada";
    private static final String KEY_KEYWORDS = "keywords";
    private static final String KEY_PATH = "path";
    private static final String KEY_UPLOADED = "uploaded";

    public static final String[] KEYS_FOTO = {KEY_ESPECIE_ID, KEY_ENTRY_ID, KEY_COORDENADA, KEY_KEYWORDS, KEY_PATH, KEY_UPLOADED};

    public FotoDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        long res = db.insert(TABLE_FOTO, null, values);
        db.close();
        return res;
    }

    public Foto getFoto(long foto_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FOTO + " WHERE "
                + KEY_ID + " = " + foto_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Foto ft = null;
        if (c != null) {
            c.moveToFirst();
            ft = setDatos(c);
        }
        db.close();
        return ft;
    }

    public int countAllFotos() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_FOTO;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int countFotosByEspecie(Especie especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_FOTO +
                " WHERE " + KEY_ESPECIE_ID + " = '" + especie.id + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int countFotosByUploaded(int uploaded) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_FOTO +
                " WHERE " + KEY_UPLOADED + " = '" + uploaded + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
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
        db.close();
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
        db.close();
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
        db.close();
        return fotos;
    }

    public List<Foto> getAllFotosByUploaded(int uploaded) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Foto> fotos = new ArrayList<Foto>();

        String selectQuery = "SELECT * FROM " + TABLE_FOTO +
                " WHERE " + KEY_UPLOADED + " = " + uploaded;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Foto f = setDatos(c);

                // adding to entry list
                fotos.add(f);
            } while (c.moveToNext());
        }
        db.close();
        return fotos;
    }

    public int updateFoto(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(foto);

        // updating row
        int res = db.update(TABLE_FOTO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(foto.getId())});
        db.close();
        return res;
    }

    public void deleteFoto(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOTO, KEY_ID + " = ?",
                new String[]{String.valueOf(foto.id)});
        db.close();
    }

    public void deleteAllFotos() {
        String sql = "DELETE FROM " + TABLE_FOTO;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    private Foto setDatos(Cursor c) {
        Foto f = new Foto(this.context);
        f.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        f.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        f.setEntry_id(c.getLong(c.getColumnIndex(KEY_ENTRY_ID)));
        f.setEspecie_id(c.getLong(c.getColumnIndex(KEY_ESPECIE_ID)));
        f.setCoordenada_id(c.getLong(c.getColumnIndex(KEY_COORDENADA)));
        f.setKeywords((c.getString(c.getColumnIndex(KEY_KEYWORDS))));
        f.setPath((c.getString(c.getColumnIndex(KEY_PATH))));
        f.setUploaded((c.getInt(c.getColumnIndex(KEY_UPLOADED))));
        return f;
    }

    private ContentValues setValues(Foto foto, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        if (foto.especie_id != null) {
            values.put(KEY_ESPECIE_ID, foto.especie_id);
        }
        if (foto.entry_id != null) {
            values.put(KEY_ENTRY_ID, foto.entry_id);
        }
        if (foto.coordenada_id != null) {
            values.put(KEY_COORDENADA, foto.coordenada_id);
        }
        values.put(KEY_KEYWORDS, foto.getKeywords());
        values.put(KEY_PATH, foto.getPath());
        values.put(KEY_UPLOADED, foto.getUploaded());
        return values;
    }

    private ContentValues setValues(Foto foto) {
        return setValues(foto, false);
    }

}
