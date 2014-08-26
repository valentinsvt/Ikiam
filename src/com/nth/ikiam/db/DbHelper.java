package com.nth.ikiam.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DELL on 26/07/2014.
 */
public class DbHelper extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 11;

    // Database Name
//    private static String DB_PATH = "/data/data/com.tmm.android.chuck/databases/";
    private static String DB_PATH = Environment.getExternalStorageDirectory().getPath() + "/Ikiam/db/";
    private static final String DATABASE_NAME = "ikiamDb.db";

    // Table Names
    protected static final String TABLE_COLOR = "colores";
    protected static final String TABLE_FOTO = "fotos";
    protected static final String TABLE_FAMILIA = "familias";
    protected static final String TABLE_GENERO = "generos";
    protected static final String TABLE_ESPECIE = "especies";
    protected static final String TABLE_COORDENADA = "coordenada";
    protected static final String TABLE_RUTA = "ruta";
    protected static final String TABLE_ENTRY = "entry";

    protected static final String TABLE_ESTADISTICAS = "estadisticas";

    // Common column names
    protected static final String KEY_ID = "id";
    protected static final String KEY_FECHA = "fecha";
    private static final String[] KEYS_COMMON = {KEY_ID, KEY_FECHA};

    protected Context context;

    public DbHelper(Context context) {
        super(context, DB_PATH + DATABASE_NAME, null, DATABASE_VERSION);
        new File(DB_PATH).mkdirs();
//        System.out.println(DB_PATH + DATABASE_NAME);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e("DBHELPER", "DBHELPER ON CREATE");

        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_COLOR, KEYS_COMMON, ColorDbHelper.KEYS_COLOR));
        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_COORDENADA, KEYS_COMMON, CoordenadaDbHelper.KEYS_COORDENADA));
        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_ESPECIE, KEYS_COMMON, EspecieDbHelper.KEYS_ESPECIE));
        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_FAMILIA, KEYS_COMMON, FamiliaDbHelper.KEYS_FAMILIA));
        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_FOTO, KEYS_COMMON, FotoDbHelper.KEYS_FOTO));
        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_GENERO, KEYS_COMMON, GeneroDbHelper.KEYS_GENERO));
        //sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_LUGAR, KEYS_COMMON, LugarDbHelper.KEYS_LUGAR));
        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_RUTA, KEYS_COMMON, RutaDbHelper.KEYS_RUTA));
        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_ENTRY, KEYS_COMMON, EntryDbHelper.KEYS_ENTRY));
        sqLiteDatabase.execSQL(DbCreator.createTableSql(TABLE_ESTADISTICAS, KEYS_COMMON, EstadisticasDbHelper.KEYS_ESTADISTICAS));
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


//    public void export() {
//        File f = new File(context.getFilesDir().getParent() + "/databases/" + DATABASE_NAME);
//        FileInputStream fis = null;
//        FileOutputStream fos = null;
//
//        try {
//            fis = new FileInputStream(f);
//            fos = new FileOutputStream("/mnt/sdcard/db_dump_" + DATABASE_NAME + ".db");
////            Environment.getExternalStorageDirectory().getPath();
//            while (true) {
//                int i = fis.read();
//                if (i != -1) {
//                    fos.write(i);
//                } else {
//                    break;
//                }
//            }
//            fos.flush();
//            Toast.makeText(context, "DB dump OK", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "DB dump ERROR", Toast.LENGTH_LONG).show();
//        } finally {
//            try {
//                fos.close();
//                fis.close();
//            } catch (IOException ioe) {
//            }
//        }
//    }

    protected void logQuery(String log, String query) {
//        Log.e(log, query);
    }
}
