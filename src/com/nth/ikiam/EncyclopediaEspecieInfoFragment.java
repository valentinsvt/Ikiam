package com.nth.ikiam;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nth.ikiam.db.*;
import com.nth.ikiam.image.ImageUtils;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Created by DELL on 15/08/2014.
 */
public class EncyclopediaEspecieInfoFragment extends Fragment {
    MapActivity context;

    TextView txtEspecieInfoNombreComun;
    TextView txtEspecieInfoNombreCientifico;

    TextView txtEspecieInfoFamilia;
    TextView txtEspecieInfoGenero;
    TextView txtEspecieInfoEspecie;

    TextView txtEspecieInfoColor1;
    TextView txtEspecieInfoColor2;

    TextView txtEspecieInfoAltura;

    ImageView imgEspecieInfoImagen;

    Especie especie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MapActivity) getActivity();
        View view = inflater.inflate(R.layout.encyclopedia_especie_info_layout, container, false);

        txtEspecieInfoNombreComun = (TextView) view.findViewById(R.id.especie_info_nombre_comun);
        txtEspecieInfoNombreCientifico = (TextView) view.findViewById(R.id.especie_info_nombre_cientifico);

        txtEspecieInfoFamilia = (TextView) view.findViewById(R.id.especie_info_familia);
        txtEspecieInfoGenero = (TextView) view.findViewById(R.id.especie_info_genero);
        txtEspecieInfoEspecie = (TextView) view.findViewById(R.id.especie_info_especie);

        txtEspecieInfoColor1 = (TextView) view.findViewById(R.id.especie_info_color1);
        txtEspecieInfoColor2 = (TextView) view.findViewById(R.id.especie_info_color2);

        txtEspecieInfoAltura = (TextView) view.findViewById(R.id.especie_info_altura);

        imgEspecieInfoImagen = (ImageView) view.findViewById(R.id.especie_info_imagen);

        long especieId = getArguments().getLong("especie");
        especie = Especie.get(context, especieId);
        Genero genero = especie.getGenero(context);
        Familia familia = genero.getFamilia(context);

        String color1 = "", color2 = "";
        Color c1 = especie.getColor1(context);
        if (c1 != null) {
            int id = getResources().getIdentifier("global_color_" + c1.nombre, "string", context.getPackageName());
            color1 = id == 0 ? "" : (String) getResources().getText(id);
        }
        Color c2 = especie.getColor2(context);
        if (c2 != null) {
            int id = getResources().getIdentifier("global_color_" + c2.nombre, "string", context.getPackageName());
            color2 = id == 0 ? "" : (", " + (String) getResources().getText(id));
        }

        double altMin = 0, altMax = 0;
        boolean vert = false;
        List<Foto> fotos = Foto.findAllByEspecie(context, especie);
        if (fotos.size() > 0) {
            Foto foto = fotos.get(0);
            Coordenada coord = foto.getCoordenada(context);
            altMin = coord.altitud;
            altMax = coord.altitud;
            File imgFile = new File(foto.path);
            if (imgFile.exists()) {
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 200, 200);
                int w = myBitmap.getWidth();
                int h = myBitmap.getHeight();
                if (h > w) {
                    vert = true;
                }
                imgEspecieInfoImagen.setImageBitmap(myBitmap);
            }
            int i = 0;
            //especie_info_relative_layout
            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.especie_info_relative_layout);
            Vector<ImageView> imageViews = new Vector<ImageView>();
            System.out.println("HAY " + fotos.size() + " FOTOS!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            for (Foto f : fotos) {
                imgFile = new File(f.path);
                if (imgFile.exists()) {
                    ImageView newImageView = new ImageView(context);
                    newImageView.setId(i);
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 150, 150);
//                    int w = myBitmap.getWidth();
//                    int h = myBitmap.getHeight();
//                    if (h > w) {
//                        System.out.println("foto " + i + " es VERT");
//                    }
                    newImageView.setImageBitmap(myBitmap);
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    p.addRule(RelativeLayout.BELOW, R.id.especie_info_altura_lbl);
                    if (i == 0) {
                        p.addRule(RelativeLayout.ALIGN_START, R.id.especie_info_altura_lbl);
                    } else {
                        p.addRule(RelativeLayout.RIGHT_OF, imageViews.get(i-1).getId());
                        p.setMargins(0, 0, 10, 0);
                    }
                    rl.addView(newImageView, p);

                    imageViews.add(newImageView);
                    i++;
                }
                coord = f.getCoordenada(context);
                if (coord != null && coord.altitud > 0) {
                    if (coord.altitud < altMin) {
                        altMin = coord.altitud;
                    }
                    if (coord.altitud > altMax) {
                        altMax = coord.altitud;
                    }
                }
            }

        }

        txtEspecieInfoNombreComun.setText(especie.nombreComun);
        txtEspecieInfoNombreCientifico.setText(genero.nombre + " " + especie.nombre.toLowerCase());

        txtEspecieInfoFamilia.setText(familia.nombre);
        txtEspecieInfoGenero.setText(genero.nombre);
        txtEspecieInfoEspecie.setText(especie.nombre);

        txtEspecieInfoColor1.setText(color1);
        txtEspecieInfoColor2.setText(color2);

        if (vert) {
            //la foto es vertical
            TextView t = (TextView) view.findViewById(R.id.especie_info_color_lbl);
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) t.getLayoutParams();
            p.addRule(RelativeLayout.ALIGN_START, R.id.especie_info_especie_lbl);
            t.setLayoutParams(p);
        } else {
            //la foto es horizontal
            TextView t = (TextView) view.findViewById(R.id.especie_info_color_lbl);
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) t.getLayoutParams();
            p.setMargins(0, 10, 0, 0);
            t.setLayoutParams(p);

            t = (TextView) view.findViewById(R.id.especie_info_altura_lbl);
            p = (RelativeLayout.LayoutParams) t.getLayoutParams();
            p.setMargins(0, 10, 0, 0);
            p.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            t.setLayoutParams(p);

            p = (RelativeLayout.LayoutParams) imgEspecieInfoImagen.getLayoutParams();
            p.setMargins(0, 10, 0, 0);
            imgEspecieInfoImagen.setLayoutParams(p);
        }

        txtEspecieInfoAltura.setText(getString(R.string.global_min) + ": " + altMin + " m. "
                + getString(R.string.global_max) + ": " + altMax + "m.");

        return view;
    }
}