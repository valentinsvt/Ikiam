package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by DELL on 27/07/2014.
 */
public class Color {
    long id = 0;
    String fecha;
    String nombre;

    ColorDbHelper colorDbHelper;

    public Color(Context context) {
        colorDbHelper = new ColorDbHelper(context);
    }

    public Color(Context context, String nombre) {
        this.nombre = nombre;

        colorDbHelper = new ColorDbHelper(context);
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
            this.id = this.colorDbHelper.createColor(this);
        } else {
            this.id = this.colorDbHelper.updateColor(this);
        }
    }

    public static Color get(Context context, long id) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.getColor(id);
    }

    public static int count(Context context) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.countAllColores();
    }

    public static int countByNombre(Context context, String color) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.countColoresByNombre(color);
    }

    public static List<Color> list(Context context) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.getAllColores();
    }

    public static List<Color> findAllByNombre(Context context, String color) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.getAllColoresByNombre(color);
    }

    public static void empty(Context context) {
        ColorDbHelper e = new ColorDbHelper(context);
        e.deleteAllColores();
    }
}
