package com.nth.ikiam.image;

import android.graphics.Bitmap;

/**
 * Created by Svt on 8/13/2014.
 */
public class AtraccionUi {
    public String id;
    public String nombre;
    public int likes;
    public Bitmap foto;

    public AtraccionUi(String nombre,Bitmap foto, int likes){
        this.nombre=nombre;
        this.foto=foto;
        this.likes=likes;
    }

}
