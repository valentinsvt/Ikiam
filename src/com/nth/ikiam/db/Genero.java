package com.nth.ikiam.db;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by DELL on 28/07/2014.
 */
public class Genero {
    public long id = 0;
    public String fecha;
    public Familia familia;
    public String nombre;

    public GeneroDbHelper generoDbHelper;

    public Genero(Context context) {
        generoDbHelper = new GeneroDbHelper(context);
    }

    public Genero(Context context, String nombre) {
        this.nombre = nombre;

        generoDbHelper = new GeneroDbHelper(context);
    }

    public Genero(Context context, Familia familia, String nombre) {
        this.familia = familia;
        this.nombre = nombre;

        generoDbHelper = new GeneroDbHelper(context);
    }

    //getters
    public long getId() {
        return id;
    }

    public Familia getFamilia() {
        return familia;
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

    public void setFamilia(Familia familia) {
        this.familia = familia;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.generoDbHelper.createGenero(this);
        } else {
            this.id = this.generoDbHelper.updateGenero(this);
        }
    }

    public static Genero get(Context context, long id) {
        GeneroDbHelper e = new GeneroDbHelper(context);
        return e.getGenero(id);
    }

    public static Genero getByNombreOrCreate(Context context, String nombreGenero) {
        Genero genero;
        List<Genero> listGeneros = findAllByNombre(context, nombreGenero);
        if (listGeneros.size() == 0) {
            genero = new Genero(context, nombreGenero);
            genero.save();
        } else if (listGeneros.size() == 1) {
            genero = listGeneros.get(0);
        } else {
            Log.e("getByNombreOrCreate genero", "Se encontraron " + listGeneros.size() + " generos con nombre " + nombreGenero);
            genero = listGeneros.get(0);
        }
        return genero;
    }

    public static int count(Context context) {
        GeneroDbHelper e = new GeneroDbHelper(context);
        return e.countAllGeneros();
    }

    public static int countByNombre(Context context, String familia) {
        GeneroDbHelper e = new GeneroDbHelper(context);
        return e.countGenerosByNombre(familia);
    }

    public static List<Genero> list(Context context) {
        GeneroDbHelper e = new GeneroDbHelper(context);
        return e.getAllGeneros();
    }

    public static List<Genero> findAllByNombre(Context context, String familia) {
        GeneroDbHelper e = new GeneroDbHelper(context);
        return e.getAllGenerosByNombre(familia);
    }

    public static void empty(Context context) {
        GeneroDbHelper e = new GeneroDbHelper(context);
        e.deleteAllGeneros();
    }
}
