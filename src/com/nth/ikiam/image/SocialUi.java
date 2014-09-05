package com.nth.ikiam.image;

import android.graphics.Bitmap;

/**
 * Created by Svt on 9/4/2014.
 */
public class SocialUi {
    public String id;
    public String comentario;
    public String usuario;
    public int likes;
    public Bitmap foto;

    public SocialUi(String id,String comentario,String usuario,Bitmap foto, int likes){
        this.id=id;
        this.comentario=comentario;
        this.foto=foto;
        this.likes=likes;
        this.usuario=usuario;
    }
}
