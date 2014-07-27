package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 27/07/2014.
 */
public class LugarDbHelper extends DbHelper {

    private static final String LOG = "LugarDbHelper";

    private static final String KEY_LUGAR = "lugar";
    private static final String[] KEYS_LUGAR = {KEY_LUGAR};

    private static final String CREATE_TABLE_LUGAR = createTableSql(TABLE_LUGAR, KEYS_LUGAR);

    public LugarDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_LUGAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUGAR);

        // create new tables
        onCreate(db);
    }

    public long createLugar(Lugar lugar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(lugar, true);

        // insert row
        return db.insert(TABLE_LUGAR, null, values);
    }

    public Lugar getLugar(long lugar_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_LUGAR + " WHERE "
                + KEY_ID + " = " + lugar_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        return setDatos(c);
    }

    public List<Lugar> getAllLugares() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Lugar> lugares = new ArrayList<Lugar>();
        String selectQuery = "SELECT  * FROM " + TABLE_LUGAR;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Lugar cl = setDatos(c);

                // adding to tags list
                lugares.add(cl);
            } while (c.moveToNext());
        }
        return lugares;
    }

    public List<Lugar> getAllLugaresByLugar(String lugar) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Lugar> lugares = new ArrayList<Lugar>();
        String selectQuery = "SELECT  * FROM " + TABLE_LUGAR +
                " WHERE " + KEY_LUGAR + " = '" + lugar + "'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Lugar cl = setDatos(c);

                // adding to tags list
                lugares.add(cl);
            } while (c.moveToNext());
        }
        return lugares;
    }

    public int updateLugar(Lugar lugar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(lugar);

        // updating row
        return db.update(TABLE_LUGAR, values, KEY_ID + " = ?",
                new String[]{String.valueOf(lugar.getId())});
    }

    public int countAllLugares() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_LUGAR;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int countLugaresByLugar(String lugar) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_LUGAR +
                " WHERE " + KEY_LUGAR + " = '" + lugar + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public void deleteLugar(Lugar lugar) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LUGAR, KEY_ID + " = ?",
                new String[]{String.valueOf(lugar.id)});
    }

    public void deleteAllLugares() {
        String sql = "DELETE FROM " + TABLE_LUGAR;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    private Lugar setDatos(Cursor c) {
        Lugar lg = new Lugar(this.context);
        lg.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        lg.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        lg.setLugar(c.getString(c.getColumnIndex(KEY_LUGAR)));
        return lg;
    }

    private ContentValues setValues(Lugar lugar, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_LUGAR, lugar.lugar);
        return values;
    }

    private ContentValues setValues(Lugar lugar) {
        return setValues(lugar, false);
    }
}
