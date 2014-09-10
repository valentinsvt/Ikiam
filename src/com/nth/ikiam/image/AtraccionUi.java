package com.nth.ikiam.image;

import android.graphics.Bitmap;

/**
 * Created by Svt on 8/13/2014.
 */
public class AtraccionUi {
    public String id;
    public String nombre;
    public String descripcion;
    public String url;
    public int likes;
    public Bitmap foto;

    public AtraccionUi(String nombre,Bitmap foto, int likes,String url,String descripcion){
        this.nombre=nombre;
        this.foto=foto;
        this.likes=likes;
        this.url=url;
        this.descripcion=descripcion;
    }

}
