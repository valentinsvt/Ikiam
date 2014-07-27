package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by DELL on 27/07/2014.
 */
public class Lugar {
    long id = 0;
    String fecha;
    String lugar;

    LugarDbHelper lugarDbHelper;

    public Lugar(Context context) {
        lugarDbHelper = new LugarDbHelper(context);
    }

    public Lugar(Context context, String color) {
        this.lugar = lugar;

        lugarDbHelper = new LugarDbHelper(context);
    }

    //getters
    public long getId() {
        return id;
    }

    public String getLugar() {
        return lugar;
    }

    public String getFecha() {
        return fecha;
    }

    //setters
    public void setId(long id) {
        this.id = id;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
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

    public static int countByLugar(Context context, String lugar) {
        LugarDbHelper e = new LugarDbHelper(context);
        return e.countLugaresByLugar(lugar);
    }

    public static List<Lugar> list(Context context) {
        LugarDbHelper e = new LugarDbHelper(context);
        return e.getAllLugares();
    }

    public static List<Lugar> findAllByLugar(Context context, String lugar) {
        LugarDbHelper e = new LugarDbHelper(context);
        return e.getAllLugaresByLugar(lugar);
    }

    public static void empty(Context context) {
        LugarDbHelper e = new LugarDbHelper(context);
        e.deleteAllLugares();
    }

}
