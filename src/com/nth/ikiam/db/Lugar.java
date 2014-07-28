package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by DELL on 27/07/2014.
 */
public class Lugar {
    long id = 0;
    String fecha;
    String nombre;

    LugarDbHelper lugarDbHelper;

    public Lugar(Context context) {
        lugarDbHelper = new LugarDbHelper(context);
    }

    public Lugar(Context context, String color) {
        this.nombre = nombre;

        lugarDbHelper = new LugarDbHelper(context);
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
            this.id = this.lugarDbHelper.createLugar(this);
        } else {
            this.id = this.lugarDbHelper.updateLugar(this);
        }
    }

    public static Lugar get(Context context, long id) {
        LugarDbHelper e = new LugarDbHelper(context);
        return e.getLugar(id);
    }

    public static int count(Context context) {
        LugarDbHelper e = new LugarDbHelper(context);
        return e.countAllLugares();
    }

    public static int countByNombre(Context context, String lugar) {
        LugarDbHelper e = new LugarDbHelper(context);
        return e.countLugaresByNombre(lugar);
    }

    public static List<Lugar> list(Context context) {
        LugarDbHelper e = new LugarDbHelper(context);
        return e.getAllLugares();
    }

    public static List<Lugar> findAllByNOmbre(Context context, String lugar) {
        LugarDbHelper e = new LugarDbHelper(context);
        return e.getAllLugaresByNombre(lugar);
    }

    public static void empty(Context context) {
        LugarDbHelper e = new LugarDbHelper(context);
        e.deleteAllLugares();
    }

}