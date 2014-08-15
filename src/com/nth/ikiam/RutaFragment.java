package com.nth.ikiam;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.UiLifecycleHelper;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.image.ImageUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Svt on 8/15/2014.
 */
public class RutaFragment extends Fragment implements Button.OnClickListener {
    MapActivity activity;
    private Button[] botones;
    List<Foto> fotos;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MapActivity) getActivity();
        view = inflater.inflate(R.layout.ruta_fragment, container, false);
        ImageView imagen = (ImageView) view.findViewById(R.id.ruta_img);
        TextView texto = (TextView) view.findViewById(R.id.txt_descripcion);
        texto.setText(activity.ruta.descripcion);
        ((TextView) view.findViewById(R.id.ruta_fecha)).setText(activity.ruta.fecha);
        fotos = Foto.findAllByRuta(activity, activity.ruta);
        Foto foto;
        if(fotos.size()>0) {
            foto = fotos.get(0);
            File imgFile = new File(foto.path);
            if (imgFile.exists()) {
                Bitmap myBitmap = ImageUtils.decodeBitmap(foto.path, 215, (int)Math.floor(215*0.5625));
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
                Bitmap myBitmap = ImageUtils.decodeBitmap(fotos.get(i).path, 100, (int)Math.floor(100*0.5625));
                im.setImageBitmap(myBitmap);
                im.setVisibility(View.VISIBLE);
            }
        }else{
            imagen.setMinimumWidth(150);
            imagen.setImageResource(R.drawable.ic_launcher);
        }

        ((TextView) view.findViewById(R.id.lbl_fotos)).setText(""+fotos.size()+" "+getString(R.string.ruta_lbl_foto));
        ((TextView) view.findViewById(R.id.lbl_valor_distancia)).setText("distancia");
        ((TextView) view.findViewById(R.id.lbl_valor_altura_1)).setText("altura");
        //botones[0] = (Button) view.findViewById(R.id.button_crear);/*Crear cuenta ikiam*/
        // botones[1] = (Button) view.findViewById(R.id.button_logout_ikiam); /*Logout ikiam*/
        //botones[2] = (Button) view.findViewById(R.id.button_login); /*login ikiam*/
        //botones[3] = (Button) view.findViewById(R.id.button_guardar); /*Guardar cuenta ikiam*/
        //botones[1] = (Button) view.findViewById(R.id.btnService);
        //for (int i = 0; i < botones.length; i++) {
        //  botones[i].setOnClickListener(this);
        //}

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == botones[0].getId()) {

        }
        if (v.getId() == botones[1].getId()) {

        }
        if (v.getId() == botones[2].getId()) {

        }
        if (v.getId() == botones[3].getId()) {

        }
    }
}
