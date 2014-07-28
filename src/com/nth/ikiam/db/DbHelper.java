package com.nth.ikiam.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DELL on 26/07/2014.
 */
public class DbHelper extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static String DB_PATH = "/data/data/com.tmm.android.chuck/databases/";
    private static final String DATABASE_NAME = "ikiamDb2";

    // Table Names
    protected static final String TABLE_COLOR = "colores";
    protected static final String TABLE_LUGAR = "lugares";
    protected static final String TABLE_FOTO = "fotos";
    protected static final String TABLE_FAMILIA = "familias";
    protected static final String TABLE_GENERO = "generos";
    protected static final String TABLE_ESPECIE = "especies";
    protected static final String TABLE_COORDENADA= "coordenada";
    protected static final String TABLE_RUTA= "ruta";

    // Common column names
    protected static final String KEY_ID = "id";
    protected static final String KEY_FECHA = "fecha";

    protected Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        System.out.print("DBHELPER ON CREATE");

        Coordenada c = new Coordenada(context);
        sqLiteDatabase.execSQL(c.coordenadaDbHelper.CREATE_TABLE_COORDENADA);
        Ruta r = new Ruta(context);
        sqLiteDatabase.execSQL(r.rutaDbHelper.CREATE_TABLE_RUTA);
        Foto foto = new Foto(context);
        sqLiteDatabase.execSQL(foto.fotoDbHelper.CREATE_TABLE_FOTO);
        Color co = new Color(context);
        sqLiteDatabase.execSQL(co.colorDbHelper.CREATE_TABLE_COLOR);
        Lugar l = new Lugar(context);
        sqLiteDatabase.execSQL(l.lugarDbHelper.CREATE_TABLE_LUGAR);
        Familia fa = new Familia(context);
        sqLiteDatabase.execSQL(fa.familiaDbHelper.CREATE_TABLE_FAMILIA);
        Genero g = new Genero(context);
        sqLiteDatabase.execSQL(g.generoDbHelper.CREATE_TABLE_GENERO);
        Especie e = new Especie(context);
        sqLiteDatabase.execSQL(e.especieDbHelper.CREATE_TABLE_ESPECIE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    /**
     * @param tableName:   el nombre de la tabla
     * @param columnNames: los campos
     * @return el sql
     */
    protected static String createTableSql(String tableName, String[] columnNames) {
        String sql = "CREATE TABLE " + tableName + " (" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_FECHA + " DATETIME";
        for (String c : columnNames) {
            if (c.startsWith("lat") || c.startsWith("long")) {
                sql += ", " + c + " REAL";
            } else {
                sql += ", " + c + " TEXT";
            }
        }
        sql += ")";
        //System.out.println("crear sql create " + sql);
        return sql;
    }

    /**
     * get datetime
     */
    protected static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected void logQuery(String log, String query) {
        Log.e(log, query);
    }
}
