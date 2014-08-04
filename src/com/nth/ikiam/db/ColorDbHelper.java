package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 27/07/2014.
 */
public class ColorDbHelper extends DbHelper {

    private static final String LOG = "ColorDbHelper";

    private static final String KEY_NOMBRE = "nombre";

    public static final String[] KEYS_COLOR = {KEY_NOMBRE};

    public ColorDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLOR);

        // create new tables
        onCreate(db);
    }

    public long createColor(Color color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(color, true);

        // insert row
        long res = db.insert(TABLE_COLOR, null, values);
        db.close();
        return res;
    }

    public Color getColor(long color_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_COLOR + " WHERE "
                + KEY_ID + " = " + color_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Color cl = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            cl = setDatos(c);
        }
        db.close();
        return cl;
    }

    public ArrayList<Color> getAllColores() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Color> colores = new ArrayList<Color>();
        String selectQuery = "SELECT  * FROM " + TABLE_COLOR;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Color cl = setDatos(c);

                // adding to tags list
                colores.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return colores;
    }

    public ArrayList<Color> getOnlyColores() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Color> colores = new ArrayList<Color>();
        String selectQuery = "SELECT  * FROM " + TABLE_COLOR +
                " WHERE " + KEY_NOMBRE + " <> 'none'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Color cl = setDatos(c);

                // adding to tags list
                colores.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return colores;
    }

    public List<Color> getAllColoresByNombre(String color) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Color> colores = new ArrayList<Color>();
        String selectQuery = "SELECT  * FROM " + TABLE_COLOR +
                " WHERE " + KEY_NOMBRE + " = '" + color + "'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Color cl = setDatos(c);

                // adding to tags list
                colores.add(cl);
            } while (c.moveToNext());
        }
        db.close();
        return colores;
    }

    public int countAllColores() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_COLOR;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int countColoresByNombre(String color) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_COLOR +
                " WHERE " + KEY_NOMBRE + " = '" + color + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        db.close();
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int updateColor(Color color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(color);

        // updating row
        int res = db.update(TABLE_COLOR, values, KEY_ID + " = ?",
                new String[]{String.valueOf(color.getId())});
        db.close();
        return res;
    }

    public void deleteColor(Color color) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COLOR, KEY_ID + " = ?",
                new String[]{String.valueOf(color.id)});
        db.close();
    }

    public void deleteAllColores() {
        String sql = "DELETE FROM " + TABLE_COLOR;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    private Color setDatos(Cursor c) {
        Color cl = new Color(this.context);
        cl.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        cl.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        cl.setNombre(c.getString(c.getColumnIndex(KEY_NOMBRE)));
        return cl;
    }

    private ContentValues setValues(Color color, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_NOMBRE, color.nombre);
        return values;
    }

    private ContentValues setValues(Color color) {
        return setValues(color, false);
    }
}

