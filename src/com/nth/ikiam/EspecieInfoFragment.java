package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nth.ikiam.db.*;
import com.nth.ikiam.image.ImageUtils;
import com.nth.ikiam.utils.Utils;
import com.nth.ikiam.utils.UtilsUploaders;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * Created by DELL on 15/08/2014.
 */
public class EspecieInfoFragment extends Fragment implements Button.OnClickListener, View.OnTouchListener {
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

    Button btnMap;
    Button btnWeb;
    Button btnIkiam;

    Especie especie;
    //    List<Foto> fotos;
    List<Entry> entries;

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

        btnMap = (Button) view.findViewById(R.id.especie_info_map_btn);
        btnMap.setOnClickListener(this);
        btnWeb = (Button) view.findViewById(R.id.especie_info_web);
        btnWeb.setOnClickListener(this);
        btnIkiam = (Button) view.findViewById(R.id.especie_info_ikiam);
        btnIkiam.setOnClickListener(this);
        long especieId = getArguments().getLong("especie");
        especie = Especie.get(context, especieId);
        Genero genero = especie.getGenero(context);
        Familia familia = genero.getFamilia(context);

        String color1 = "", color2 = "";
        Color c1 = especie.getColor1(context);
        if (c1 != null) {
//            int id = getResources().getIdentifier("global_color_" + c1.nombre, "string", context.getPackageName());
//            color1 = id == 0 ? "" : (String) getResources().getText(id);
            color1 = Utils.getStringResourceByName(context, "global_color_" + c1.nombre);
        }
        Color c2 = especie.getColor2(context);
        if (c2 != null) {
//            int id = getResources().getIdentifier("global_color_" + c2.nombre, "string", context.getPackageName());
//            color2 = id == 0 ? "" : (", " + ((String) getResources().getText(id)));
            color2 = ", " + Utils.getStringResourceByName(context, "global_color_" + c2.nombre);
        }

        double altMin = 0, altMax = 0;
        boolean vert = false;

//        fotos = Foto.findAllByEspecie(context, especie);
        entries = Entry.findAllByEspecie(context, especie);
        //especie_info_foto5
        //(ImageView) view.findViewById(R.id.especie_info_imagen);
        //im.setVisibility(View.VISIBLE);

        imageViews = new ImageView[6];
        imageViews[0] = (ImageView) view.findViewById(R.id.especie_info_foto1);
        imageViews[1] = (ImageView) view.findViewById(R.id.especie_info_foto2);
        imageViews[2] = (ImageView) view.findViewById(R.id.especie_info_foto3);
        imageViews[3] = (ImageView) view.findViewById(R.id.especie_info_foto4);
        imageViews[4] = (ImageView) view.findViewById(R.id.especie_info_foto5);
        imageViews[5] = (ImageView) view.findViewById(R.id.especie_info_foto6);

//        int cantFotos = fotos.size();
        int cantFotos = entries.size();
        int showing = Math.min(imageViews.length, cantFotos);
        String strMostrando = getResources().getQuantityString(R.plurals.especie_info_fotos, cantFotos, cantFotos, showing);
//        if (cantFotos == 1) {
//            strMostrando = getString(R.string.especie_info_foto, cantFotos, showing);
//        } else {
//            strMostrando = getString(R.string.especie_info_fotos, cantFotos, showing);
//        }
        txtEspecieInfoFotos.setText(strMostrando);

//        if (fotos.size() > 0) {
        if (entries.size() > 0) {
//            Foto foto = fotos.get(0);
            Entry entry = entries.get(0);
            List<Foto> fotos = Foto.findAllByEntry(context, entry);
            if (fotos != null) {
                Foto foto = fotos.get(0);
                Coordenada coord = foto.getCoordenada(context);
                if (coord != null) {
                    altMin = coord.altitud;
                    altMax = coord.altitud;
                }
                File imgFile = new File(foto.path);
                if (imgFile.exists()) {
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    Bitmap myBitmap = ImageUtils.decodeBitmap(imgFile.getAbsolutePath(), 200, 200);
                    Bitmap myBitmap = ImageUtils.decodeFile(imgFile.getAbsolutePath(), 200, 200);
                    int w = myBitmap.getWidth();
                    int h = myBitmap.getHeight();
                    if (h > w) {
                        vert = true;
                    }
                    imgEspecieInfoImagen.setImageBitmap(myBitmap);
                }

                int screenWidth = context.screenWidth - 40;
                int currentWidth = 0;
                int idPrev = 0;
                int highest = 0;
                int idHighest = 0;

                int i = 0;
                for (Entry e : entries) {
                    List<Foto> fotos1 = Foto.findAllByEntry(context, e);
                    if (fotos1 != null) {
                        if(fotos1.size()>0){
                            Foto f = fotos1.get(0);
                            imgFile = new File(f.path);
                            if (imgFile.exists()) {
                                if (i < imageViews.length) {
                                    ImageView curIV = imageViews[i];
                                    if (currentWidth == 0) {
                                        idPrev = 0;
                                    }
                                    Bitmap myBitmap = ImageUtils.decodeFile(imgFile.getAbsolutePath(), 100, 100, true);
                                    int w = myBitmap.getWidth();
                                    int h = myBitmap.getHeight();
//                                if (h > w) {
//                                    System.out.println("foto es VERT");
//                                }
                                    curIV.setImageBitmap(myBitmap);
                                    curIV.setVisibility(View.VISIBLE);
                                    curIV.setOnClickListener(this);
                               /* currentWidth += (w + 30);
                                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) curIV.getLayoutParams();
                                if (idPrev > 0) {
                                    p.addRule(RelativeLayout.RIGHT_OF, idPrev);
//                                    p.setMargins(0, highest + 15, 0, 0);
                                }
                                if (currentWidth > screenWidth) {
                                    p.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                                    p.addRule(RelativeLayout.BELOW, idHighest);
                                    p.setMargins(0, highest + 25, 0, 0);
                                    currentWidth = 0;
                                }
                                curIV.setLayoutParams(p);
                                idPrev = curIV.getId();
                                if (h > highest) {
                                    highest = h;
                                    idHighest = curIV.getId();
                                }
                                 */
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

    @Override
    public void onClick(View view) {
        Utils.hideSoftKeyboard(this.getActivity());
        boolean band = false;
        if (view.getId() == btnMap.getId()) {
            //System.out.println("Mostrar mapa de la especie: " + especie.nombre);
            context.mostrarEspecie(especie);
            band = true;
        }
        if (view.getId() == btnWeb.getId()) {
            String url = "http://en.wikipedia.org/wiki/" + especie.nombre;
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(myIntent);
            band = true;
        }
        if (view.getId() == btnIkiam.getId()) {
            String url = UtilsUploaders.getIp() + "especie/show?nombre=" + especie.nombre;
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(myIntent);
            band = true;
        }
        if (!band) {
            int i;
            for (i = 0; i < imageViews.length; i++) {
                if (view.getId() == imageViews[i].getId()) {
                    break;
                }
            }
            fotoPos = i;
            LayoutInflater inflater = context.getLayoutInflater();
            View v = inflater.inflate(R.layout.especie_info_entry_dialog, null);
            final String t = getResources().getQuantityString(R.plurals.encyclopedia_entries_dialog_title, entries.size());
            final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(v)
                    .setNeutralButton(R.string.dialog_btn_cerrar, null) //Set to null. We override the onclick
                    .setTitle(t + " (" + (fotoPos + 1) + "/" + entries.size() + ")");
            if (entries.size() > 1) {
                builder.setPositiveButton(R.string.dialog_btn_siguiente, null)
                        .setNegativeButton(R.string.dialog_btn_anterior, null);
            }
            final AlertDialog d = builder.create();
            final ImageView img = (ImageView) v.findViewById(R.id.especie_info_dialog_image);
            final EditText txt = (EditText) v.findViewById(R.id.especie_info_dialog_comentarios);
            setFoto(img, txt);
            d.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button cerrar = d.getButton(AlertDialog.BUTTON_NEUTRAL);
                    cerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Entry entry = entries.get(fotoPos);
                            entry.comentarios = txt.getText().toString().trim();
                            entry.save();
                            d.dismiss();
                        }
                    });
                    Button anterior = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                    anterior.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Entry entry = entries.get(fotoPos);
                            entry.comentarios = txt.getText().toString().trim();
                            entry.save();
                            if (fotoPos > 0) {
                                fotoPos -= 1;
                            } else {
                                fotoPos = entries.size() - 1;
                            }
                            setFoto(img, txt);
                            d.setTitle(t + " (" + (fotoPos + 1) + "/" + entries.size() + ")");
                        }
                    });
                    Button siguiente = d.getButton(AlertDialog.BUTTON_POSITIVE);
                    siguiente.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Entry entry = entries.get(fotoPos);
                            entry.comentarios = txt.getText().toString().trim();
                            entry.save();
                            if (fotoPos < entries.size() - 1) {
                                fotoPos += 1;
                            } else {
                                fotoPos = 0;
                            }
                            setFoto(img, txt);
                            d.setTitle(t + " (" + (fotoPos + 1) + "/" + entries.size() + ")");
                        }
                    });
                }
            });
            d.show();
        }
    }

    private void setFoto(ImageView img, EditText txt) {
//        System.out.println("SET FOTO " + fotoPos);
        Entry entry = entries.get(fotoPos);
        List<Foto> fotos = Foto.findAllByEntry(context, entry);
        if (fotos != null) {
            String comentarios = entry.comentarios;
//            while (comentarios.length() < 15000) {
//                comentarios += " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce ultricies luctus imperdiet. Pellentesque libero erat, laoreet ac magna sit amet, blandit vulputate nisl. Nam dignissim non velit eget cursus. Aenean dui metus, vehicula a leo quis, tincidunt gravida est. Fusce semper nec purus quis consectetur. Vestibulum risus felis, accumsan vitae nulla eu, fringilla vulputate lectus. Nam scelerisque magna vel sollicitudin molestie. Nulla venenatis ipsum sem, nec dignissim lacus vestibulum eget. ";
//            }
            if (comentarios != null) {
                comentarios = comentarios.trim();
            }

            Foto foto = fotos.get(0);
//            img.setImageBitmap(context.getFotoDialog(foto, context.screenWidth, 300));
            img.setImageBitmap(ImageUtils.decodeFile(foto.path, context.screenWidth, 300));
            if (comentarios == null || comentarios.equals("")) {
                txt.setVisibility(View.GONE);
            } else {
                txt.setText(comentarios);
                txt.setSelection(txt.getText().length());
                txt.setVisibility(View.VISIBLE);
            }
//        dialogTitle = R.string.encyclopedia_entries_dialog_title + " (" + (fotoPos + 1) + "/" + fotos.size() + ")";
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Utils.hideSoftKeyboard(this.getActivity());
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.setTitle(R.string.especie_info_title);
    }
}