package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 27/07/2014.
 */
public class NotaDbHelper extends DbHelper {

    private static final String LOG = "NotaDbHelper";

    public static final String KEY_TITULO = "titulo";
    public static final String KEY_CONTENIDO = "contenido";

    public static final String[] KEYS_NOTA = {KEY_TITULO, KEY_CONTENIDO};

    public NotaDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTA);

        // create new tables
        onCreate(db);
    }

    public long createNota(Nota nota) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(nota, true);

        // insert row
        long res = db.insert(TABLE_NOTA, null, values);
        db.close();
        return res;
    }

    public Nota getNota(long nota_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTA + " WHERE "
                + KEY_ID + " = " + nota_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Nota cl = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            cl = setDatos(c);
        }
        db.close();
        return cl;
    }

    public ArrayList<Nota> getAllNotas() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Nota> notaes = new ArrayList<Nota>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTA;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Nota cl = setDatos(c);

                // adding to tags list
                notaes.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return notaes;
    }

    public int countAllNotas() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_NOTA;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int updateNota(Nota nota) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(nota);

        // updating row
        int res = db.update(TABLE_NOTA, values, KEY_ID + " = ?",
                new String[]{String.valueOf(nota.getId())});
        db.close();
        return res;
    }

    public void deleteNota(Nota nota) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTA, KEY_ID + " = ?",
                new String[]{String.valueOf(nota.id)});
        db.close();
    }

    public void deleteAllNotaes() {
        String sql = "DELETE FROM " + TABLE_NOTA;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    private Nota setDatos(Cursor c) {
        Nota cl = new Nota(this.context);
        cl.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        cl.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        cl.setTitulo(c.getString(c.getColumnIndex(KEY_TITULO)));
        cl.setContenido(c.getString(c.getColumnIndex(KEY_CONTENIDO)));
        return cl;
    }

    private ContentValues setValues(Nota nota, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_TITULO, nota.titulo);
        values.put(KEY_CONTENIDO, nota.contenido);
        return values;
    }

    private ContentValues setValues(Nota nota) {
        return setValues(nota, false);
    }
}

