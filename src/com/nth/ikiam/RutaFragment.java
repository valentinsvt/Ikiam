package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.maps.model.LatLng;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.image.ImageUtils;
import com.nth.ikiam.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Svt on 8/15/2014.
 */
public class RutaFragment extends Fragment implements Button.OnClickListener, View.OnTouchListener {
    MapActivity activity;
    private Button[] botones;
    private ImageButton[] imgBotones;
    List<Foto> fotos;
    List<Coordenada> cords;
    List<ImageView> imgs;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MapActivity) getActivity();
        view = inflater.inflate(R.layout.ruta_fragment, container, false);
        view.setOnTouchListener(this);
        ImageView imagen = (ImageView) view.findViewById(R.id.ruta_img);
        TextView texto = (TextView) view.findViewById(R.id.txt_descripcion);
        texto.setText(activity.ruta.descripcion);
        ((TextView) view.findViewById(R.id.ruta_fecha)).setText(activity.ruta.fecha);
        fotos = Foto.findAllByRuta(activity, activity.ruta);
        cords = Coordenada.findAllByRuta(activity,activity.ruta);
        imgs = new ArrayList<ImageView>();
        double distancia = 0;
        double alturaMinima = 0;
        double alturaMaxima = 0;
        if(cords.size()>2){
            for(int i = 0;i<cords.size()-1;i++){
                Coordenada current = cords.get(i);
                Coordenada next = cords.get(i+1);
                if(i==0){
                    alturaMinima = current.altitud;
                }

                if(alturaMinima>current.altitud)
                    alturaMinima=current.altitud;
                if(alturaMaxima<current.altitud)
                    alturaMaxima=current.altitud;
                if(alturaMinima>next.altitud)
                    alturaMinima=next.altitud;
                if(alturaMaxima<next.altitud)
                    alturaMaxima=next.altitud;
                distancia+=dist(current.getLatitud(),current.getLongitud(),next.getLatitud(),next.getLongitud());

            }
        }
        distancia=distancia*1000; /*para sacar en metros*/
        distancia= Math.round(distancia * 100.0) / 100.0;
        Foto foto;
        int width = 215;
        if(fotos.size()>0) {
            foto = fotos.get(0);
            File imgFile = new File(foto.path);
            if (imgFile.exists()) {
                Bitmap myBitmap = ImageUtils.decodeBitmap(foto.path, width, (int) Math.floor(width * 0.5625));
                imagen.setImageBitmap(myBitmap);
            }
            for(int i =1;i<fotos.size();i++){
                if(i>5)
                    break;
                int res = R.id.image_1;
                switch (i){
                    case 1:
                        res=R.id.image_1;
                        break;
                    case 2:
                        res=R.id.image_2;
                        break;
                    case 3:
                        res=R.id.image_3;
                        break;
                    case 4:
                        res=R.id.image_4;
                        break;
                    case 5:
                        res=R.id.image_5;
                        break;

                    default:
                        break;
                }
//                File file = new File(fotos.get(i).path);
                ImageView im = (ImageView) view.findViewById(res);
                im.setOnClickListener(this);
                imgs.add(im);
                Bitmap myBitmap = ImageUtils.decodeBitmap(fotos.get(i).path, 100, (int) Math.floor(100 * 0.5625));
                im.setImageBitmap(myBitmap);
                im.setVisibility(View.VISIBLE);
            }
        }else{
            imagen.setMinimumWidth(width);
            imagen.setMinimumHeight((int)Math.floor(width*0.5625));
            imagen.setImageResource(R.drawable.ic_launcher);
        }

        ((TextView) view.findViewById(R.id.lbl_fotos)).setText(""+fotos.size()+" "+getString(R.string.ruta_lbl_foto));
        ((TextView) view.findViewById(R.id.lbl_valor_distancia)).setText(""+distancia+"m");
        ((TextView) view.findViewById(R.id.lbl_valor_altura_1)).setText("Min: "+alturaMinima+"m  -  Max: "+alturaMaxima+"m");
        botones = new Button[2];
        imgBotones = new ImageButton[1];
        imgBotones[0] = (ImageButton) view.findViewById(R.id.btn_guardar_desc);
        botones[0] = (Button) view.findViewById(R.id.ver_mapa);
        botones[1] = (Button) view.findViewById(R.id.compartir);
        for (int i = 0; i < botones.length; i++) {
            botones[i].setOnClickListener(this);
        }
        for (int i = 0; i < imgBotones.length; i++) {
            imgBotones[i].setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Utils.hideSoftKeyboard(this.getActivity());
        if (v.getId() == imgBotones[0].getId()) {

            String descripcion = ((EditText) view.findViewById(R.id.txt_descripcion)).getText().toString().trim();
            if(descripcion.length()>0){
                activity.ruta.descripcion=descripcion;
                activity.ruta.save();
                Toast.makeText(activity, getString(R.string.ruta_datos_guardados), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, getString(R.string.ruta_error_datos), Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == botones[0].getId()) {
            /*Ver en el mapa*/
            activity.showRuta(cords,fotos);
        }
        if (v.getId() == botones[1].getId()) {

        }
        if(imgs.size()>0){
            if (v.getId() == imgs.get(0).getId()) {
                LayoutInflater inflater = activity.getLayoutInflater();
                View myView = inflater.inflate(R.layout.dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.ruta_lbl_fotos);
                builder.setView(myView);
                builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }


                });
                activity.dialog = builder.create();
                ImageView img = (ImageView) myView.findViewById(R.id.image);
                img.setImageBitmap(activity.getFotoDialog(fotos.get(1), activity.screenWidth, 300));
                activity.dialog.show();
            }
        }
        if(imgs.size()>1){
            if (v.getId() == imgs.get(1).getId()) {
                LayoutInflater inflater = activity.getLayoutInflater();
                View myView = inflater.inflate(R.layout.dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.ruta_lbl_fotos);
                builder.setView(myView);
                builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }


                });
                activity.dialog = builder.create();
                ImageView img = (ImageView) myView.findViewById(R.id.image);
                img.setImageBitmap(activity.getFotoDialog(fotos.get(2), activity.screenWidth, 300));
                activity.dialog.show();
            }
        }
        if(imgs.size()>2){
            if (v.getId() == imgs.get(2).getId()) {
                LayoutInflater inflater = activity.getLayoutInflater();
                View myView = inflater.inflate(R.layout.dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.ruta_lbl_fotos);
                builder.setView(myView);
                builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }


                });
                activity.dialog = builder.create();
                ImageView img = (ImageView) myView.findViewById(R.id.image);
                img.setImageBitmap(activity.getFotoDialog(fotos.get(3), activity.screenWidth, 300));
                activity.dialog.show();
            }
        }
        if(imgs.size()>3){
            if (v.getId() == imgs.get(3).getId()) {
                LayoutInflater inflater = activity.getLayoutInflater();
                View myView = inflater.inflate(R.layout.dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.ruta_lbl_fotos);
                builder.setView(myView);
                builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }


                });
                activity.dialog = builder.create();
                ImageView img = (ImageView) myView.findViewById(R.id.image);
                img.setImageBitmap(activity.getFotoDialog(fotos.get(4), activity.screenWidth, 300));
                activity.dialog.show();
            }
        }
        if(imgs.size()>4){
            if (v.getId() == imgs.get(4).getId()) {
                LayoutInflater inflater = activity.getLayoutInflater();
                View myView = inflater.inflate(R.layout.dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.ruta_lbl_fotos);
                builder.setView(myView);
                builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }


                });
                activity.dialog = builder.create();
                ImageView img = (ImageView) myView.findViewById(R.id.image);
                img.setImageBitmap(activity.getFotoDialog(fotos.get(5), activity.screenWidth, 300));
                activity.dialog.show();
            }
        }


    }

    public double dist(double lat1,double long1,double lat2,double long2){


        double R     = 6378.137;                          //Radio de la tierra en km
        double dLat  = radianes(lat2 - lat1);
        double dLong = radianes(long2 - long1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(radianes(lat1)) * Math.cos(radianes(lat2)) * Math.sin(dLong/2) * Math.sin(dLong/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;

        return d;                      //Retorna tres decimales
    }
    public double radianes(double x){
        return x*Math.PI/180;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Utils.hideSoftKeyboard(this.getActivity());
        return false;
    }
}
