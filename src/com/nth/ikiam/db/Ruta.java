package com.nth.ikiam.db;

import android.content.Context;

import java.util.Date;
import java.util.List;

/**
 * Created by Svt on 7/27/2014.
 */
public class Ruta {
    public long id=0;
    public String descripcion;
    public String fecha;
    public RutaDbHelper rutaDbHelper;


    public Ruta(Context context, String descripcion){
        this.descripcion=descripcion;
        this.rutaDbHelper=new RutaDbHelper(context);
    }

    public Ruta(Context context){
        this.rutaDbHelper=new RutaDbHelper(context);
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
    public void save() {
        if (this.id == 0) {
            this.id = this.rutaDbHelper.createRuta(this);
        } else {
            this.rutaDbHelper.updateRuta(this);
        }
    }

    public static Ruta get(Context context, long id) {
        RutaDbHelper e = new RutaDbHelper(context);
        return e.getRuta(id);
    }

    public static int count(Context context) {
        RutaDbHelper e = new RutaDbHelper(context);
        return e.countAllRutas();
    }


    public static List<Ruta> list(Context context) {
        RutaDbHelper e = new RutaDbHelper(context);
        return e.getAllRutas();
    }




}
