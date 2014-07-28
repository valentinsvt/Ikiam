package com.nth.ikiam.db;

import android.content.Context;

import java.util.Date;

/**
 * Created by Svt on 7/27/2014.
 */
public class Ruta {
    long id=0;
    String descripcion;
    String fecha;
    RutaDbHelper rutaDbHelper;


    public Ruta(Context context, String descripcion){
        this.descripcion=descripcion;
        this.rutaDbHelper=new RutaDbHelper(context);
    }

    public Ruta(Context context){
        this.rutaDbHelper=new RutaDbHelper(context);
    }

    public static Ruta get(Context context,long id){
        RutaDbHelper rutaDbHelper = new RutaDbHelper(context);
        return rutaDbHelper.getRuta(id) ;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
