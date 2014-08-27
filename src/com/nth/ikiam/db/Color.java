package com.nth.ikiam.db;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 27/07/2014.
 */
public class Color {
    public long id = 0;
    public String fecha;
    public String nombre;

    ColorDbHelper colorDbHelper;

    public Color(Context context) {
        colorDbHelper = new ColorDbHelper(context);
    }

    public Color(Context context, String nombre) {
        this.nombre = nombre;

        colorDbHelper = new ColorDbHelper(context);
    }

    public Color(Context context, long id, String nombre) {
        this.id = id;
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

    public String toString() {
        return nombre;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.colorDbHelper.createColor(this);
        } else {
            this.colorDbHelper.updateColor(this);
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

    public static ArrayList<Color> list(Context context) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.getAllColores();
    }

    public static ArrayList<Color> listColores(Context context) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.getOnlyColores();
    }

    public static List<Color> findAllByNombre(Context context, String color) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.getAllColoresByNombre(color);
    }

    public static Color findByNombre(Context context, String color) {
        ColorDbHelper e = new ColorDbHelper(context);
        return e.getColorByNombre(color);
    }

    public static void empty(Context context) {
        ColorDbHelper e = new ColorDbHelper(context);
        e.deleteAllColores();
    }
}
