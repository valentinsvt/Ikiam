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
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MapActivity) getActivity();
        view = inflater.inflate(R.layout.ruta_fragment, container, false);
        ImageView imagen = (ImageView) view.findViewById(R.id.ruta_img);
        TextView texto = (TextView) view.findViewById(R.id.ruta_descripcion);
        texto.setText(activity.ruta.descripcion);
        List<Foto> fotos = Foto.findAllByRuta(activity, activity.ruta);
        Foto foto;
        if(fotos.size()>0) {
            foto = fotos.get(0);
            File imgFile = new File(foto.path);
            if (imgFile.exists()) {
                Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 100, 100);
                imagen.setImageBitmap(myBitmap);
            }
        }else{
            imagen.setImageResource(R.drawable.ic_launcher);
        }


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
