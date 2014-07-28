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
public class RutaDbHelper extends DbHelper {
    private static final String LOG = "RutaDbHelper";

    private static final String KEY_DESCRIPCION = "descripcion";
    private static final String[] KEYS_RUTA = {KEY_DESCRIPCION};

    public static final String CREATE_TABLE_RUTA = createTableSql(TABLE_RUTA, KEYS_RUTA);

    public RutaDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_RUTA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUTA);

        // create new tables
        onCreate(db);
    }

    public long createRuta(Ruta ruta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(ruta, true);

        // insert row
        return db.insert(TABLE_RUTA, null, values);
    }

    public Ruta getRuta(long ruta_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_RUTA + " WHERE "
                + KEY_ID + " = " + ruta_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        return setDatos(c);
    }

    public List<Ruta> getAllRutas() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Ruta> rutas = new ArrayList<Ruta>();
        String selectQuery = "SELECT  * FROM " + TABLE_RUTA;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Ruta r = setDatos(c);

                // adding to tags list
                rutas.add(r);
            } while (c.moveToNext());
        }
        return rutas;
    }



    public int countAllRutas() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_RUTA;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }



    public int updateRuta(Ruta ruta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(ruta);

        // updating row
        return db.update(TABLE_RUTA, values, KEY_ID + " = ?",
                new String[]{String.valueOf(ruta.getId())});
    }

    public void deleteRuta(Ruta ruta) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RUTA, KEY_ID + " = ?",
                new String[]{String.valueOf(ruta.id)});
    }

    public void deleteAllRutas() {
        String sql = "DELETE FROM " + TABLE_RUTA;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    private Ruta setDatos(Cursor c) {
        Ruta r = new Ruta(this.context);
        r.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        r.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        r.setDescripcion(c.getString(c.getColumnIndex(KEY_DESCRIPCION)));
        return r;
    }

    private ContentValues setValues(Ruta ruta, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_DESCRIPCION, ruta.descripcion);
        return values;
    }

    private ContentValues setValues(Ruta ruta) {
        return setValues(ruta, false);
    }
}
