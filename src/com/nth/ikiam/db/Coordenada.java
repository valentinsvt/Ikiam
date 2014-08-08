package com.nth.ikiam.db;

import android.content.Context;

import java.util.List;

/**
 * Created by Svt on 7/27/2014.
 */
public class Coordenada {
    public long id = 0;
    public double latitud;
    public double longitud;
    public Long ruta_id;
    public CoordenadaDbHelper coordenadaDbHelper;
    public String fecha;
    Context context;

    public Coordenada(Context context, double latitud, double longitud, Ruta ruta) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.coordenadaDbHelper = new CoordenadaDbHelper(context);
        this.ruta_id = ruta.id;
        this.context = context;
    }

    public Coordenada(Context context, double latitud, double longitud, long ruta) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.coordenadaDbHelper = new CoordenadaDbHelper(context);
        this.ruta_id = ruta;
        this.context = context;
    }

    public Coordenada(Context context, double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.coordenadaDbHelper = new CoordenadaDbHelper(context);
        this.context = context;
    }

    public Coordenada(Context context) {
        coordenadaDbHelper = new CoordenadaDbHelper(context);
        this.context = context;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public Ruta getRuta(Context context) {
        if (ruta_id != null) {
            return Ruta.get(context, ruta_id);
        } else {
            return null;
        }
    }

    public Long getRuta_id() {
        return ruta_id;
    }

    public void setRuta(Ruta ruta) {
        this.ruta_id = ruta.id;
    }

    public void setRuta_id(Long ruta_id) {
        this.ruta_id = ruta_id;
    }

    public void save() {
        if (this.id == 0) {
            this.id = this.coordenadaDbHelper.createCoordenada(this);
        } else {
            this.coordenadaDbHelper.updateCoordenada(this);
        }
    }

    public static Coordenada get(Context context, long id) {
        CoordenadaDbHelper e = new CoordenadaDbHelper(context);
        return e.getCoordenada(id);
    }

    public static int count(Context context) {
        CoordenadaDbHelper e = new CoordenadaDbHelper(context);
        return e.countAllCoordenadas();
    }


    public static List<Coordenada> list(Context context) {
        CoordenadaDbHelper e = new CoordenadaDbHelper(context);
        return e.getAllCoordenadas();
    }

    public static List<Coordenada> findAllByRuta(Context context, Ruta ruta) {
        CoordenadaDbHelper e = new CoordenadaDbHelper(context);
        return e.getAllCoordenadasByRuta(ruta);
    }

    public static void empty(Context context) {
        CoordenadaDbHelper e = new CoordenadaDbHelper(context);
        e.deleteAllCoordenadas();
    }

}
