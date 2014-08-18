package com.nth.ikiam.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by DELL on 26/07/2014.
 */
public class EspecieDbHelper extends DbHelper {

    // Logcat tag
    private static final String LOG = "EspecieDbHelper";

    // ESPECIE Table - column names
    public static final String KEY_NOMBRE_COMUN = "nombre_comun";
    public static final String KEY_NOMBRE_COMUN_NORM = "nombre_comun_norm";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_NOMBRE_NORM = "nombre_norm";
    public static final String KEY_COLOR1_ID = "color_id";
    public static final String KEY_COLOR2_ID = "color2_id";
    public static final String KEY_GENERO_ID = "genero_id";

    public static final String[] KEYS_ESPECIE = {KEY_NOMBRE_COMUN, KEY_NOMBRE_COMUN_NORM, KEY_NOMBRE, KEY_NOMBRE_NORM,
            KEY_GENERO_ID, KEY_COLOR1_ID, KEY_COLOR2_ID};

    public EspecieDbHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESPECIE);

        // create new tables
        onCreate(db);
    }

    public long createEspecie(Especie especie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(especie, true);

        // insert row
        long res = db.insert(TABLE_ESPECIE, null, values);
        db.close();
        return res;
    }

    public Especie getEspecie(long especie_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ESPECIE + " WHERE "
                + KEY_ID + " = " + especie_id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        Especie es = null;
        if (c.getCount() > 0) {
            c.moveToFirst();
            es = setDatos(c);
        }
        db.close();
        return es;
    }

    public List<Especie> getAllEspecies() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Especie> todos = new ArrayList<Especie>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESPECIE;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Especie es = setDatos(c);
                // adding to especie list
                todos.add(es);
            } while (c.moveToNext());
        }
        db.close();
        return todos;
    }

    public List<Especie> getBusqueda(Vector<String> keywords, String color, String nc) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Especie> todos = new ArrayList<Especie>();
        String sql;

        String select = "SELECT  e.* ";
        String from = " FROM " + TABLE_ESPECIE + " e";
        String joins = "";
        String where = "";
        String groupBy = " GROUP BY e." + KEY_ID;

        if (!color.equals("")) {
            joins += " LEFT JOIN " + TABLE_COLOR + " c1 ON e." + KEY_COLOR1_ID + " = c1." + KEY_ID;
            joins += " LEFT JOIN " + TABLE_COLOR + " c2 ON e." + KEY_COLOR2_ID + " = c2." + KEY_ID;

            if (where.equals("")) {
                where += " WHERE ";
            } else {
                where += " AND ";
            }
            where += " (c1." + ColorDbHelper.KEY_NOMBRE + " = '" + color + "'";
            where += " OR c2." + ColorDbHelper.KEY_NOMBRE + " = '" + color + "')";
        }

        if (!nc.equals("")) {
            if (where.equals("")) {
                where += " WHERE ";
            } else {
                where += " AND ";
            }
            where += "LOWER(e." + KEY_NOMBRE_COMUN_NORM + ") LIKE '%" + Normalizer.normalize(nc, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase() + "%' ";
        }

        if (keywords.size() > 0) {
            joins += " LEFT JOIN " + TABLE_FOTO + " f ON f." + FotoDbHelper.KEY_ESPECIE_ID + " = e." + KEY_ID;
            if (where.equals("")) {
                where += " WHERE ";
            } else {
                where += " AND ";
            }
            where += "(";
            boolean first = true;
            for (String keyword : keywords) {
                if (first) {
                    first = false;
                } else {
                    where += " OR ";
                }
                where += "f." + FotoDbHelper.KEY_KEYWORDS + " LIKE '%" + keyword + "%'";
            }

            where += ")";
        }

        sql = select + from + joins + where + groupBy;

        logQuery(LOG, sql);

        Cursor c = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Especie es = setDatos(c);
//                System.out.println("adding to especie list: " + es.id);
                todos.add(es);
            } while (c.moveToNext());
        }
        db.close();
        return todos;
    }

    public List<Especie> getAllEspeciesByColor(Color color) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Especie> todos = new ArrayList<Especie>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESPECIE +
                " WHERE " + KEY_COLOR1_ID + " = " + color.id +
                " OR " + KEY_COLOR2_ID + " = " + color.id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Especie es = setDatos(c);
                // adding to especie list
                todos.add(es);
            } while (c.moveToNext());
        }
        db.close();
        return todos;
    }

    public List<Especie> getAllEspeciesByGenero(Genero genero) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Especie> todos = new ArrayList<Especie>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESPECIE +
                " WHERE " + KEY_GENERO_ID + " = " + genero.id;

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Especie es = setDatos(c);
                // adding to especie list
                todos.add(es);
            } while (c.moveToNext());
        }
        db.close();
        return todos;
    }

    public List<Especie> getAllEspeciesByNombre(String especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Especie> todos = new ArrayList<Especie>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESPECIE +
                " WHERE " + KEY_NOMBRE + " = '" + especie + "'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Especie es = setDatos(c);
                // adding to especie list
                todos.add(es);
            } while (c.moveToNext());
        }
        db.close();
        return todos;
    }

    public List<Especie> getAllEspeciesByNombreComunLike(String especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Especie> todos = new ArrayList<Especie>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESPECIE +
                " WHERE LOWER(" + KEY_NOMBRE_COMUN_NORM + ") LIKE '%" + especie.toLowerCase() + "%'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Especie es = setDatos(c);
                // adding to especie list
                todos.add(es);
            } while (c.moveToNext());
        }
        db.close();
        return todos;
    }

    public List<Especie> getAllEspeciesByNombreLike(String especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Especie> todos = new ArrayList<Especie>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESPECIE +
                " WHERE LOWER(" + KEY_NOMBRE_NORM + ") LIKE '%" + especie.toLowerCase() + "%'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Especie es = setDatos(c);
                // adding to especie list
                todos.add(es);
            } while (c.moveToNext());
        }
        db.close();
        return todos;
    }

    public List<Especie> getAllEspeciesByNombreCientifico(String nombreCientifico) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Especie> todos = new ArrayList<Especie>();
        String selectQuery = "SELECT  * FROM " + TABLE_ESPECIE + " te, " + TABLE_GENERO + " tg" +
                " WHERE te." + KEY_GENERO_ID + " = tg." + KEY_ID +
                " AND tg." + KEY_NOMBRE + "||' '||te." + KEY_NOMBRE + " = '" + nombreCientifico + "'";

        logQuery(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Especie es = setDatos(c);
                // adding to especie list
                todos.add(es);
            } while (c.moveToNext());
        }
        db.close();
        return todos;
    }

    public int countAllEspecies() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_ESPECIE;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int countEspeciesByColor(Color color) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_ESPECIE +
                " WHERE " + KEY_COLOR1_ID + " = '" + color.id + "'" +
                " OR " + KEY_COLOR2_ID + " = '" + color.id + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int countEspeciesByGenero(Genero genero) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_ESPECIE +
                " WHERE " + KEY_GENERO_ID + " = '" + genero.id + "'";
        logQuery(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int countEspeciesByNombre(String especie) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) count FROM " + TABLE_ESPECIE +
                " WHERE " + KEY_NOMBRE + " = '" + especie + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public int countEspeciesByNombreCientifico(String nombreCientifico) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  count(*) FROM " + TABLE_ESPECIE + " te, " + TABLE_GENERO + " tg" +
                " WHERE te." + KEY_GENERO_ID + " = tg." + KEY_ID +
                " AND tg." + KEY_NOMBRE + "||' '||te." + KEY_NOMBRE + " = '" + nombreCientifico + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("count"));
            db.close();
            return count;
        }
        db.close();
        return 0;
    }

    public long updateEspecie(Especie especie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = setValues(especie);

        // updating row
        long res = db.update(TABLE_ESPECIE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(especie.getId())});
        db.close();
        return res;
    }

    public void deleteEspecie(Especie especie, boolean should_delete_all_fotos) {
        SQLiteDatabase db = this.getWritableDatabase();
        // before deleting tag
        // check if fotos under this especie should also be deleted
        if (should_delete_all_fotos) {
            // get all fotos under this especie
            List<Foto> allEspecieFotos = Foto.list(this.context);

            // delete all fotos
            for (Foto foto : allEspecieFotos) {
                // delete foto
                foto.delete();
            }
        }

        // now delete the tag
        db.delete(TABLE_ESPECIE, KEY_ID + " = ?",
                new String[]{String.valueOf(especie.getId())});
        db.close();
    }

    public void deleteAllEspecies() {
        String sql = "DELETE FROM " + TABLE_ESPECIE;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    private Especie setDatos(Cursor c) {
        Especie es = new Especie(this.context);
        es.setId(c.getLong((c.getColumnIndex(KEY_ID))));
        es.setNombreComun((c.getString(c.getColumnIndex(KEY_NOMBRE_COMUN))));
        es.setNombre(c.getString(c.getColumnIndex(KEY_NOMBRE)));
        es.setFecha(c.getString(c.getColumnIndex(KEY_FECHA)));
        es.setColor1_id(c.getLong(c.getColumnIndex(KEY_COLOR1_ID)));
        es.setColor2_id(c.getLong(c.getColumnIndex(KEY_COLOR2_ID)));
        es.setGenero_id(c.getLong(c.getColumnIndex(KEY_GENERO_ID)));
        return es;
    }

    private ContentValues setValues(Especie especie, boolean fecha) {
        ContentValues values = new ContentValues();
        if (fecha) {
            values.put(KEY_FECHA, getDateTime());
        }
        values.put(KEY_NOMBRE_COMUN, especie.nombreComun);
        values.put(KEY_NOMBRE_COMUN_NORM, especie.nombreComunNorm);
        values.put(KEY_NOMBRE, especie.nombre);
        values.put(KEY_NOMBRE_NORM, especie.nombreNorm);
        if (especie.color1_id != null) {
            values.put(KEY_COLOR1_ID, especie.color1_id);
        }
        if (especie.color2_id != null) {
            values.put(KEY_COLOR2_ID, especie.color2_id);
        }
        if (especie.genero_id != null) {
            values.put(KEY_GENERO_ID, especie.genero_id);
        }
        return values;
    }

    private ContentValues setValues(Especie especie) {
        return setValues(especie, false);
    }
}
