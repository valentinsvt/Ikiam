package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 31/07/2014.
 */
public class EntryDbHelper extends DbHelper {

    // Logcat tag
    private static final String LOG = "EntryDbHelper";

    //  ENTRY - column names
    private static final String KEY_COMENTARIOS = "comentarios";
    private static final String KEY_ESPECIE_ID = "especie_id";
    private static final String KEY_UPLOADED = "uploaded";

    public static final String[] KEYS_ENTRY = {KEY_ESPECIE_ID, KEY_COMENTARIOS, KEY_UPLOADED};

    public EntryDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY);

        // create new tables
        onCreate(db);
    }

    public long createEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(entry, true);

        // insert row
        return db.insert(TABLE_ENTRY, null, values);
    }

    public Entry getEntry(long entry_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ENTRY + " WHERE "
                + KEY_ID + " = " + entry_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Entry en = null;
        if (c != null) {
            c.moveToFirst();
            en = setDatos(c);
        }

        return en;
    }

    public int countAllEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_ENTRY;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int countEntriesByEspecie(Especie especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_ENTRY +
                " WHERE " + KEY_ESPECIE_ID + " = '" + especie.id + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public int countEntriesByUploaded(int uploaded) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_ENTRY +
                " WHERE " + KEY_UPLOADED + " = '" + uploaded + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("count"));
        }
        return 0;
    }

    public List<Entry> getAllEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Entry> entries = new ArrayList<Entry>();
        String selectQuery = "SELECT  * FROM " + TABLE_ENTRY;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry e = setDatos(c);

                // adding to tags list
                entries.add(e);
            } while (c.moveToNext());
        }
        return entries;
    }

    public List<Entry> getAllEntriesByEspecie(Especie especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Entry> entries = new ArrayList<Entry>();

        String selectQuery = "SELECT * FROM " + TABLE_ENTRY +
                " WHERE " + KEY_ESPECIE_ID + " = " + especie.id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry e = setDatos(c);

                // adding to entry list
                entries.add(e);
            } while (c.moveToNext());
        }
        return entries;
    }

    public List<Entry> getAllEntriesByUploaded(int uploaded) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Entry> entries = new ArrayList<Entry>();

        String selectQuery = "SELECT * FROM " + TABLE_ENTRY +
                " WHERE " + KEY_UPLOADED + " = " + uploaded;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Entry e = setDatos(c);

                // adding to entry list
                entries.add(e);
            } while (c.moveToNext());
        }
        return entries;
    }

    public int updateEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(entry);

        // updating row
        return db.update(TABLE_ENTRY, values, KEY_ID + " = ?",
                new String[]{String.valueOf(entry.getId())});
    }

    public void deleteEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRY, KEY_ID + " = ?",
                new String[]{String.valueOf(entry.id)});
    }

    public void deleteAllEntries() {
        String sql = "DELETE FROM " + TABLE_ENTRY;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    private Entry setDatos(Cursor c) {
        Entry f = new Entry(this.context);
        f.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        f.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        f.setEspecie(Especie.get(this.context, c.getLong(c.getColumnIndex(KEY_ESPECIE_ID))));
        f.setComentarios((c.getString(c.getColumnIndex(KEY_COMENTARIOS))));
        f.setUploaded((c.getInt(c.getColumnIndex(KEY_UPLOADED))));
        return f;
    }

    private ContentValues setValues(Entry entry, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        if (entry.especie != null) {
            values.put(KEY_ESPECIE_ID, entry.especie.id);
        }
        values.put(KEY_COMENTARIOS, entry.getComentarios());
        values.put(KEY_UPLOADED, entry.getUploaded());
        return values;
    }

    private ContentValues setValues(Entry entry) {
        return setValues(entry, false);
    }

}
