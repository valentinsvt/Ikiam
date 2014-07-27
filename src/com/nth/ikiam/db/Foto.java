package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by DELL on 26/07/2014.
 */
public class Foto {
    long id = 0;
    String fecha;
    Especie especie;
    double latitud;
    double longitud;
    String comentarios;
    String keywords;

    FotoDbHelper fotoDbHelper;

    public Foto(Context context) {
        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios, String keywords, double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.comentarios = comentarios;
        this.keywords = keywords;
        this.especie = especie;

        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios, String keywords) {
        this.comentarios = comentarios;
        this.keywords = keywords;
        this.especie = especie;

        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios) {
        this.comentarios = comentarios;
        this.especie = especie;

        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie) {
        this.especie = especie;

        fotoDbHelper = new FotoDbHelper(context);
    }

    //getters
    public Especie getEspecie() {
        return especie;
    }

    public long getId() {
        return id;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getComentarios() {
        return comentarios;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getFecha() {
        return fecha;
    }

    //setter
    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.fotoDbHelper.createFoto(this);
        } else {
            this.id = this.fotoDbHelper.updateFoto(this);
        }
    }

    public void delete() {
        this.fotoDbHelper.deleteFoto(this);
    }

    public static Foto get(Context context, long id) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getFoto(id);
    }


    public static List<Foto> list(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotos();
    }

    public static int count(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.countAllFotos();
    }

    public static int countByEspecie(Context context, Especie especie) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.countFotosByEspecie(especie);
    }

    public static List<Foto> findAllByEspecie(Context context, Especie especie) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByEspecie(especie);
    }

    public static List<Foto> findAllByKeyword(Context context, String keyword) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByKeyword(keyword);
    }

    public static void empty(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        e.deleteAllFotos();
    }

}
