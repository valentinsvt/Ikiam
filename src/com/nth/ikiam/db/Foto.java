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
    Lugar lugar;
    Coordenada coordenada;
    String comentarios;
    String keywords;

    String path;

    FotoDbHelper fotoDbHelper;

    public Foto(Context context) {
        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios, String keywords, Coordenada coordenada) {
        this.coordenada = coordenada;
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

    public Coordenada getCoordenada() {
        return coordenada;
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

    public Lugar getLugar() {
        return lugar;
    }

    public String getPath() {
        return path;
    }

    //setter
    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCoordenada(Coordenada coordenada) {
        this.coordenada = coordenada;
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

    public void setLugar(Lugar lugar) {
        this.lugar = lugar;
    }

    public void setPath(String path) {
        this.path = path;
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

    public static int countByLugar(Context context, Lugar lugar) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.countFotosByLugar(lugar);
    }

    public static List<Foto> findAllByEspecie(Context context, Especie especie) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByEspecie(especie);
    }

    public static List<Foto> findAllByLugar(Context context, Lugar lugar) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByLugar(lugar);
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
