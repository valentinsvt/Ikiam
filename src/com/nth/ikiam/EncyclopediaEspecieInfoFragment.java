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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nth.ikiam.db.*;
import com.nth.ikiam.image.ImageUtils;
import com.nth.ikiam.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Created by DELL on 15/08/2014.
 */
public class EncyclopediaEspecieInfoFragment extends Fragment implements Button.OnClickListener, View.OnTouchListener {
    MapActivity context;

    TextView txtEspecieInfoNombreComun;
    TextView txtEspecieInfoNombreCientifico;

    TextView txtEspecieInfoFamilia;
    TextView txtEspecieInfoGenero;
    TextView txtEspecieInfoEspecie;

    TextView txtEspecieInfoColor1;
    TextView txtEspecieInfoColor2;

    TextView txtEspecieInfoAltura;

    TextView txtEspecieInfoFotos;

    ImageView imgEspecieInfoImagen;

    ImageView[] imageViews;
    int fotoPos;

    Especie especie;
    List<Foto> fotos;

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

        txtEspecieInfoFotos = (TextView) view.findViewById(R.id.especie_info_fotos);

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
            color2 = id == 0 ? "" : (", " + ((String) getResources().getText(id)));
        }

        double altMin = 0, altMax = 0;
        boolean vert = false;

        fotos = Foto.findAllByEspecie(context, especie);
        //especie_info_foto5
        //(ImageView) view.findViewById(R.id.especie_info_imagen);
        //im.setVisibility(View.VISIBLE);

        imageViews = new ImageView[5];
        imageViews[0] = (ImageView) view.findViewById(R.id.especie_info_foto1);
        imageViews[1] = (ImageView) view.findViewById(R.id.especie_info_foto2);
        imageViews[2] = (ImageView) view.findViewById(R.id.especie_info_foto3);
        imageViews[3] = (ImageView) view.findViewById(R.id.especie_info_foto4);
        imageViews[4] = (ImageView) view.findViewById(R.id.especie_info_foto5);

        int cantFotos = fotos.size();
        int showing = imageViews.length;
        String strMostrando = getString(R.string.especie_info_fotos, cantFotos, showing);
        txtEspecieInfoFotos.setText(strMostrando);

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
            for (Foto f : fotos) {
                imgFile = new File(f.path);
                if (imgFile.exists()) {
                    if (i < imageViews.length) {
                        Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 150, 150);
                        int w = myBitmap.getWidth();
                        int h = myBitmap.getHeight();
                        if (h > w) {
                            System.out.println("foto es VERT");
                        }
                        imageViews[i].setImageBitmap(myBitmap);
                        imageViews[i].setVisibility(View.VISIBLE);
                        imageViews[i].setOnClickListener(this);
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
                    i++;
                }
            }

//            int i = 0;
//            //especie_info_relative_layout
//            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.especie_info_relative_layout);
//            Vector<ImageView> imageViews = new Vector<ImageView>();
//            System.out.println("HAY " + fotos.size() + " FOTOS!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            for (Foto f : fotos) {
//                imgFile = new File(f.path);
//                if (imgFile.exists()) {
//                    ImageView newImageView = new ImageView(context);
//                    newImageView.setId(i);
////            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 150, 150);
////                    int w = myBitmap.getWidth();
////                    int h = myBitmap.getHeight();
////                    if (h > w) {
////                        System.out.println("foto " + i + " es VERT");
////                    }
//                    newImageView.setImageBitmap(myBitmap);
//                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT);
//                    p.addRule(RelativeLayout.BELOW, R.id.especie_info_altura_lbl);
//                    if (i == 0) {
//                        p.addRule(RelativeLayout.ALIGN_START, R.id.especie_info_altura_lbl);
//                    } else {
//                        p.addRule(RelativeLayout.RIGHT_OF, imageViews.get(i - 1).getId());
//                        p.setMargins(0, 0, 10, 0);
//                    }
//                    rl.addView(newImageView, p);
//
//                    imageViews.add(newImageView);
//                    i++;
//                }
//                coord = f.getCoordenada(context);
//                if (coord != null && coord.altitud > 0) {
//                    if (coord.altitud < altMin) {
//                        altMin = coord.altitud;
//                    }
//                    if (coord.altitud > altMax) {
//                        altMax = coord.altitud;
//                    }
//                }
//            }

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

    @Override
    public void onClick(View view) {
        Utils.hideSoftKeyboard(this.getActivity());
        int i;
        for (i = 0; i < imageViews.length; i++) {
            if (view.getId() == imageViews[i].getId()) {
                break;
            }
        }
        fotoPos = i;
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(v)
                .setNeutralButton(R.string.dialog_btn_cerrar, null) //Set to null. We override the onclick
                .setTitle(getString(R.string.encyclopedia_entries_dialog_title) + " (" + (fotoPos + 1) + "/" + fotos.size() + ")");
        if (fotos.size() > 1) {
            builder.setPositiveButton(R.string.dialog_btn_siguiente, null)
                    .setNegativeButton(R.string.dialog_btn_anterior, null);
        }
        final AlertDialog d = builder.create();
        final ImageView img = (ImageView) v.findViewById(R.id.image);
        setFoto(img);
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button cerrar = d.getButton(AlertDialog.BUTTON_NEUTRAL);
                cerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });
                Button anterior = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                anterior.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (fotoPos > 0) {
                            fotoPos -= 1;
                        } else {
                            fotoPos = fotos.size() - 1;
                        }
                        setFoto(img);
                        d.setTitle(getString(R.string.encyclopedia_entries_dialog_title) + " (" + (fotoPos + 1) + "/" + fotos.size() + ")");
                    }
                });
                Button siguiente = d.getButton(AlertDialog.BUTTON_POSITIVE);
                siguiente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (fotoPos < fotos.size() - 1) {
                            fotoPos += 1;
                        } else {
                            fotoPos = 0;
                        }
                        setFoto(img);
                        d.setTitle(getString(R.string.encyclopedia_entries_dialog_title) + " (" + (fotoPos + 1) + "/" + fotos.size() + ")");
                    }
                });
            }
        });
        d.show();
//        LayoutInflater inflater = context.getLayoutInflater();
//        View myView = inflater.inflate(R.layout.dialog, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(R.string.ruta_lbl_fotos);
//        builder.setView(myView);
//        final ImageView img = (ImageView) myView.findViewById(R.id.image);
//
//        if (fotos.size() > 1) {
//            builder.setNegativeButton(R.string.dialog_btn_anterior, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    System.out.println("ANTERIOR");
////                    if (fotoPos > 0) {
////                        fotoPos -= 1;
////                        setFoto(img);
////                    }
//                }
//            });
//
//            builder.setPositiveButton(R.string.dialog_btn_siguiente, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                    System.out.println("SIGUIENTE");
////                    if (fotoPos < fotos.size() - 1) {
////                        fotoPos += 1;
////                        setFoto(img);
////                    }
//                }
//            });
//        }
//        builder.setNeutralButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                System.out.println("CERRAR");
////                dialog.dismiss();
//            }
//        });
//        context.dialog = builder.create();
////        img.setImageBitmap(context.getFotoDialog(fotos.get(i), context.screenWidth, 300));
//        setFoto(img);
//        context.dialog.show();
    }

    private void setFoto(ImageView img) {
        System.out.println("SET FOTO " + fotoPos);
        img.setImageBitmap(context.getFotoDialog(fotos.get(fotoPos), context.screenWidth, 300));
//        dialogTitle = R.string.encyclopedia_entries_dialog_title + " (" + (fotoPos + 1) + "/" + fotos.size() + ")";
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Utils.hideSoftKeyboard(this.getActivity());
        return false;
    }
}