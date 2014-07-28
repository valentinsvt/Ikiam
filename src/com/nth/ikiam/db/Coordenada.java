package com.nth.ikiam.db;

import android.content.Context;

/**
 * Created by Svt on 7/27/2014.
 */
public class Coordenada {
    long id=0;
    long latitud;
    long longitud;
    Ruta ruta;
    CoordenadaDbHelper coordenadaDbHelper;
    String fecha;

    public Coordenada(Context context,long latitud,long longitud,Ruta ruta){
        this.latitud=latitud;
        this.longitud=longitud;
        this.coordenadaDbHelper = new CoordenadaDbHelper(context);
        this.ruta=ruta;

    }
    public Coordenada(Context context,long latitud,long longitud,long ruta){
        this.latitud=latitud;
        this.longitud=longitud;
        this.coordenadaDbHelper = new CoordenadaDbHelper(context);
        this.ruta=Ruta.get(context,ruta);

    }
    public Coordenada(Context context) {
        coordenadaDbHelper = new CoordenadaDbHelper(context);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLatitud() {
        return latitud;
    }

    public void setLatitud(long latitud) {
        this.latitud = latitud;
    }

    public long getLongitud() {
        return longitud;
    }

    public void setLongitud(long longitud) {
        this.longitud = longitud;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public String getFecha() {
        return fecha;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }
}
