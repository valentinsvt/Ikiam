package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by DELL on 26/07/2014.
 */
public class Foto {
    public long id = 0;
    public String fecha;
    public Especie especie;
    public Coordenada coordenada;
    public String keywords;
    public Entry entry;

    public String path;

    public int uploaded;

    FotoDbHelper fotoDbHelper;

    public Foto(Context context) {
        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios, String keywords, Coordenada coordenada) {
        this.coordenada = coordenada;
        this.keywords = keywords;
        this.especie = especie;

        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios, String keywords) {
        this.keywords = keywords;
        this.especie = especie;

        fotoDbHelper = new FotoDbHelper(context);
    }

    public Foto(Context context, Especie especie, String comentarios) {
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

    public String getKeywords() {
        return keywords;
    }

    public String getFecha() {
        return fecha;
    }

    public String getPath() {
        return path;
    }

    public Entry getEntry() {
        return entry;
    }

    public int getUploaded() {
        return uploaded;
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

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
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

    public static int countNotUploaded(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.countFotosByUploaded(0);
    }

    public static List<Foto> findAllByEspecie(Context context, Especie especie) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByEspecie(especie);
    }

    public static List<Foto> findAllByKeyword(Context context, String keyword) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByKeyword(keyword);
    }

    public static List<Foto> findAllNotUploaded(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        return e.getAllFotosByUploaded(0);
    }

    public static void empty(Context context) {
        FotoDbHelper e = new FotoDbHelper(context);
        e.deleteAllFotos();
    }

}
