package com.nth.ikiam.db;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by DELL on 28/07/2014.
 */
public class Familia {
    public long id = 0;
    public String fecha;
    public String nombre;

    public FamiliaDbHelper familiaDbHelper;

    public Familia(Context context) {
        familiaDbHelper = new FamiliaDbHelper(context);
    }

    public Familia(Context context, String nombre) {
        this.nombre = nombre;

        familiaDbHelper = new FamiliaDbHelper(context);
    }

    //getters
    public long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    //setters
    public void setId(long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.familiaDbHelper.createFamilia(this);
        } else {
            this.id = this.familiaDbHelper.updateFamilia(this);
        }
    }

    public static Familia get(Context context, long id) {
        FamiliaDbHelper e = new FamiliaDbHelper(context);
        return e.getFamilia(id);
    }

    public static Familia getByNombreOrCreate(Context context, String nombreFamilia) {
        Familia familia;
        List<Familia> listFamilias = findAllByNombre(context, nombreFamilia);
        if (listFamilias.size() == 0) {
            familia = new Familia(context, nombreFamilia);
            familia.save();
        } else if (listFamilias.size() == 1) {
            familia = listFamilias.get(0);
        } else {
            Log.e("getByNombreOrCreate familia", "Se encontraron " + listFamilias.size() + " familias con nombre " + nombreFamilia);
            familia = listFamilias.get(0);
        }
        return familia;
    }

    public static int count(Context context) {
        FamiliaDbHelper e = new FamiliaDbHelper(context);
        return e.countAllFamilias();
    }

    public static int countByNombre(Context context, String familia) {
        FamiliaDbHelper e = new FamiliaDbHelper(context);
        return e.countFamiliasByNombre(familia);
    }

    public static List<Familia> list(Context context) {
        FamiliaDbHelper e = new FamiliaDbHelper(context);
        return e.getAllFamilias();
    }

    public static List<Familia> findAllByNombre(Context context, String familia) {
        FamiliaDbHelper e = new FamiliaDbHelper(context);
        return e.getAllFamiliasByNombre(familia);
    }

    public static void empty(Context context) {
        FamiliaDbHelper e = new FamiliaDbHelper(context);
        e.deleteAllFamilias();
    }
}
