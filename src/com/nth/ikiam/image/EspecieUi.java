package com.nth.ikiam.image;

import android.graphics.Bitmap;

/**
 * Created by Svt on 8/31/2014.
 */
public class EspecieUi {

    public String id;
    public String nombre;
    public String nombreEspecie;
    public String desc;
    public int likes;
    public Bitmap foto;

    public EspecieUi(String nombre,String nombreEspecie,Bitmap foto, int likes,String desc){
        this.nombre=nombre;
        this.foto=foto;
        this.likes=likes;
        this.desc=desc;
        this.nombreEspecie=nombreEspecie;
    }
}
