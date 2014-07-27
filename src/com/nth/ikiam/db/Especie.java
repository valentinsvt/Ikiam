package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by DELL on 26/07/2014.
 */
public class Especie {
    long id = 0;
    String fecha;
    String nombreComun;
    String nombreCientifico;
    String comentarios;

    Color colorFlor;
    Lugar lugar;

    EspecieDbHelper especieDbHelper;

    public Especie(Context context) {
        especieDbHelper = new EspecieDbHelper(context);
    }

    public Especie(Context context, String nombreComun) {
        this.nombreComun = nombreComun;

        especieDbHelper = new EspecieDbHelper(context);

    }

    public Especie(Context context, String nombreComun, String comentarios) {
        this.nombreComun = nombreComun;
        this.comentarios = comentarios;

        especieDbHelper = new EspecieDbHelper(context);
    }

    public Especie(Context context, String nombreComun, String nombreCientifico, String comentarios) {
        this.nombreComun = nombreComun;
        this.nombreCientifico = nombreCientifico;
        this.comentarios = comentarios;

        especieDbHelper = new EspecieDbHelper(context);
    }

    //getters
    public long getId() {
        return id;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public String getComentarios() {
        return comentarios;
    }

    public String getFecha() {
        return fecha;
    }

    public Color getColorFlor() {
        return colorFlor;
    }

    public Lugar getLugar() {
        return lugar;
    }

    //setters
    public void setId(long id) {
        this.id = id;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setColorFlor(Color colorFlor) {
        this.colorFlor = colorFlor;
    }

    public void setLugar(Lugar lugar) {
        this.lugar = lugar;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.especieDbHelper.createEspecie(this);
        } else {
            this.id = this.especieDbHelper.updateEspecie(this);
        }
    }

    public static Especie get(Context context, long id) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.getEspecie(id);
    }

    public static List<Especie> list(Context context) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.getAllEspecies();
    }

    public static List<Especie> findAllByColor(Context context, Color color) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.getAllEspeciesByColor(color);
    }

    public static int count(Context context) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.countAllEspecies();
    }

    public static int countByColor(Context context, Color color) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.countEspeciesByColor(color);
    }

    public static int countByLugar(Context context, Lugar lugar) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        return e.countEspeciesByLugar(lugar);
    }

    public static void empty(Context context) {
        EspecieDbHelper e = new EspecieDbHelper(context);
        e.deleteAllEspecies();
    }


}
