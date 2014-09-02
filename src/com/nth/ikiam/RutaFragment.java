package com.nth.ikiam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.model.LatLng;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.image.ImageUtils;
import com.nth.ikiam.listeners.FieldListener;
import com.nth.ikiam.utils.FotoUploader;
import com.nth.ikiam.utils.RutaUploader;
import com.nth.ikiam.utils.Utils;
import com.nth.ikiam.utils.UtilsDistancia;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Svt on 8/15/2014.
 */
public class RutaFragment extends Fragment implements Button.OnClickListener, View.OnTouchListener, FieldListener {
    MapActivity activity;
    private Button[] botones;
    private ImageButton[] imgBotones;
    List<Foto> fotos;
    List<Coordenada> cords;
    List<ImageView> imgs;
    View view;
    private Button subir;
    private Button shareButton;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = false;
    private UiLifecycleHelper uiHelper;
    private static final int REAUTH_ACTIVITY_CODE = 100;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MapActivity) getActivity();
        view = inflater.inflate(R.layout.ruta_fragment, container, false);
        view.setOnTouchListener(this);
        uiHelper = new UiLifecycleHelper(activity, callback);
        uiHelper.onCreate(savedInstanceState);
        subir = (Button) view.findViewById(R.id.compartir);
        shareButton = (Button) view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.ruta.idRemoto == null) {
                    ExecutorService queue = Executors.newSingleThreadExecutor();
                    queue.execute(new RutaUploader(activity, activity.ruta, cords, fotos));
                } else {
                    publishStory();
                }

            }
        });
        //System.out.println("type "+activity.type);
        if (activity.type.equals("facebook") || activity.type.equals("-1")) {
            Session session = Session.getActiveSession();
            makeMeRequest(session);
            if (session != null && session.isOpened()) {
                shareButton.setVisibility(View.VISIBLE);
                subir.setVisibility(View.GONE);
            } else {
                shareButton.setVisibility(View.GONE);
                subir.setVisibility(View.VISIBLE);
            }
        } else {
            shareButton.setVisibility(View.GONE);
            subir.setVisibility(View.VISIBLE);
        }
        ImageView imagen = (ImageView) view.findViewById(R.id.ruta_img);
        TextView texto = (TextView) view.findViewById(R.id.txt_descripcion);
        texto.setText(activity.ruta.descripcion);
        ((TextView) view.findViewById(R.id.ruta_fecha)).setText(activity.ruta.fecha);
        imgs = new ArrayList<ImageView>();
        fotos = Foto.findAllByRuta(activity, activity.ruta);
        cords = Coordenada.findAllByRuta(activity, activity.ruta);
        double distancia = 0;
        double alturaMinima = 0;
        double alturaMaxima = 0;
        if (cords.size() > 2) {
            for (int i = 0; i < cords.size() - 1; i++) {
                Coordenada current = cords.get(i);
                Coordenada next = cords.get(i + 1);
                if (i == 0) {
                    alturaMinima = current.altitud;
                }

                if (alturaMinima > current.altitud)
                    alturaMinima = current.altitud;
                if (alturaMaxima < current.altitud)
                    alturaMaxima = current.altitud;
                if (alturaMinima > next.altitud)
                    alturaMinima = next.altitud;
                if (alturaMaxima < next.altitud)
                    alturaMaxima = next.altitud;
                distancia += UtilsDistancia.dist(current.getLatitud(), current.getLongitud(), next.getLatitud(), next.getLongitud());
            }
        }
        distancia = distancia * 1000; /*para sacar en metros*/
        distancia = Math.round(distancia * 100.0) / 100.0;
        Foto foto;
        int width = 215;
        if (fotos.size() > 0) {
            foto = fotos.get(0);
            File imgFile = new File(foto.path);
            if (imgFile.exists()) {
                Bitmap myBitmap = ImageUtils.decodeBitmap(foto.path, width, (int) Math.floor(width * 0.5625));
                imagen.setImageBitmap(myBitmap);
            }
            for (int i = 1; i < fotos.size(); i++) {
                if (i > 5)
                    break;
                int res = R.id.image_1;
                switch (i) {
                    case 1:
                        res = R.id.image_1;
                        break;
                    case 2:
                        res = R.id.image_2;
                        break;
                    case 3:
                        res = R.id.image_3;
                        break;
                    case 4:
                        res = R.id.image_4;
                        break;
                    case 5:
                        res = R.id.image_5;
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
        } else {
            imagen.setMinimumWidth(width);
            imagen.setMinimumHeight((int) Math.floor(width * 0.5625));
            imagen.setImageResource(R.drawable.ic_launcher);
        }

        ((TextView) view.findViewById(R.id.lbl_fotos)).setText("" + fotos.size() + " " + getString(R.string.ruta_lbl_foto));
        ((TextView) view.findViewById(R.id.lbl_valor_distancia)).setText("" + distancia + "m");
        ((TextView) view.findViewById(R.id.lbl_valor_altura_1)).setText("Min: " + alturaMinima + "m  -  Max: " + alturaMaxima + "m");
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
        if (savedInstanceState != null) {
            pendingPublishReauthorization =
                    savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        Utils.hideSoftKeyboard(this.getActivity());
        if (v.getId() == imgBotones[0].getId()) {

            String descripcion = ((EditText) view.findViewById(R.id.txt_descripcion)).getText().toString().trim();
            if (descripcion.length() > 0) {
                activity.ruta.descripcion = descripcion;
                activity.ruta.save();
                Toast.makeText(activity, getString(R.string.ruta_datos_guardados), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, getString(R.string.ruta_error_datos), Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == botones[0].getId()) {
            /*Ver en el mapa*/
            activity.showRuta(cords, fotos);
        }
        if (v.getId() == botones[1].getId()) {
            /*Subir*/
            ExecutorService queue = Executors.newSingleThreadExecutor();
            queue.execute(new RutaUploader(activity, activity.ruta, cords, fotos));
        }
        if (imgs.size() > 0) {
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
        if (imgs.size() > 1) {
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
        if (imgs.size() > 2) {
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
        if (imgs.size() > 3) {
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
        if (imgs.size() > 4) {
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

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        System.out.println("Session state change " + state);
        if (session != null && session.isOpened()) {
            shareButton.setVisibility(View.VISIBLE);
            if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                pendingPublishReauthorization = false;
                System.out.println("run del publish");
                publishStory();
            }
        } else if (state.isClosed()) {
            shareButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("activity result de ruta " + requestCode);
        if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {

                        // If the response is successful
                        System.out.println("make request " + session + "  active " + Session.getActiveSession());
                        if (session == Session.getActiveSession()) {
                            System.out.println("user? " + user);
                            if (user != null) {
                                //System.out.println("username ruta "+user.getUsername()+" name  "+user.getName());
                                //System.out.println("Make request "+user.getId()+"  "+user.getUsername()+"  "+user.getName());
                                activity.userId = user.getId();
                                activity.type = "facebook";
                                activity.name = user.getName();
                            } else {
                                System.out.println("no user? no session?");
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();
    }

    private void publishStory() {
        Session session = Session.getActiveSession();
        String link = "http://www.tedein.com.ec:8080/ikiamServer/ruta/publish/";
        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                System.out.println("no permisos " + permissions);
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(activity, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }
//            System.out.println("paso?");
            //AQUI ACHIEVEMENT
            activity.updateAchievement(activity.ACHIEV_SHARE);
            Bundle postParams = new Bundle();
            postParams.putString("name", getString(R.string.share_name));
            postParams.putString("caption", getString(R.string.share_caption));
            postParams.putString("description", getString(R.string.share_description));
            postParams.putString("link", link + activity.ruta.idRemoto);
            postParams.putString("picture", "http://sphotos-e.ak.fbcdn.net/hphotos-ak-prn2/222461_472847872799797_1288982685_n.png");

            Request.Callback callbackShare = new Request.Callback() {
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response
                            .getGraphObject()
                            .getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        System.out.println("JSON error!!!! " + e.getMessage());
                    }
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(getActivity()
                                        .getApplicationContext(),
                                error.getErrorMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity()
                                        .getApplicationContext(),
                                "Ruta compartida exitosamente",
                                Toast.LENGTH_LONG).show();
                    }
                }
            };

            Request request = new Request(session, "me/feed", postParams,
                    HttpMethod.POST, callbackShare);

            final RequestAsyncTask task = new RequestAsyncTask(request);
            final Activity a = activity;
            if (a != null) {
                a.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        task.execute();
                    }
                });
            }

        }

    }

    @Override
    public void fieldValueChanged(String fieldName, String newValue) {
        System.out.println(fieldName + " - got set with value - " + newValue);
        if (fieldName.equals("ruta_remote_id")) {
            System.out.println("tengo ruta id");
            if (!newValue.equals("-1"))
                if (activity.old_id == null) {
                    publishStory();
                }

        }
        if (fieldName.equals("errorMessage")) {
            System.out.println("entro aca " + newValue);
            if (!newValue.equals("")) {
                String msg = newValue.toString();
                activity.showToast(msg);
                activity.errorMessage = "";
            }
        }
    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Utils.hideSoftKeyboard(this.getActivity());
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
}
