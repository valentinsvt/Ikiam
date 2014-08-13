package com.nth.ikiam;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.nth.ikiam.listeners.FieldListener;
import com.nth.ikiam.utils.LoginUploader;
import com.nth.ikiam.utils.UsuarioUploader;
import com.nth.ikiam.utils.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Svt on 8/4/2014.
 */
public class Loginfragment extends Fragment implements Button.OnClickListener, FieldListener {

    Context context;
    MapActivity activity;
    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private UiLifecycleHelper uiHelper;
    private static final int REAUTH_ACTIVITY_CODE = 100;
    View view;
    private Button[] botones;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void fieldValueChanged(String fieldName, String newValue) {
        System.out.println(fieldName + " - got set with value - " + newValue);
        if (fieldName.equals("type")) {
            if (newValue.equals("ikiam")) {
                SharedPreferences settings = context.getSharedPreferences(activity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("user", activity.userId);
                editor.putString("email", activity.email);
                editor.putString("name", activity.name);
                editor.putString("type", "ikiam");
                editor.commit();
                showIkiamView();
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

    public void showIkiamView() {
        final Activity a = activity;
        if (a != null) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) view.findViewById(R.id.selection_profile_pic_ikiam)).setImageResource(R.drawable.ic_launcher);
                    TextView userNameViewIkiam = (TextView) view.findViewById(R.id.selection_user_name_ikiam);
                    userNameViewIkiam.setText(activity.name + "\n" + activity.email);
                    RelativeLayout userPanel = (RelativeLayout) view.findViewById(R.id.user_info);
                    userPanel.setVisibility(View.GONE);
                    RelativeLayout userPanelIkiam = (RelativeLayout) view.findViewById(R.id.user_info_ikiam);
                    userPanelIkiam.setVisibility(View.VISIBLE);
                    RelativeLayout elegirCuenta = (RelativeLayout) view.findViewById(R.id.botones_cuenta);
                    elegirCuenta.setVisibility(View.GONE);
                    RelativeLayout cuentaForm = (RelativeLayout) view.findViewById(R.id.body);
                    cuentaForm.setVisibility(View.GONE);
                    RelativeLayout cuentaFormLogin = (RelativeLayout) view.findViewById(R.id.login_ikiam);
                    cuentaFormLogin.setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();
        activity = (MapActivity) getActivity();
        view = inflater.inflate(R.layout.login_layout, container, false);
        uiHelper = new UiLifecycleHelper(activity, callback);
        uiHelper.onCreate(savedInstanceState);
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        userNameView = (TextView) view.findViewById(R.id.selection_user_name);
        botones = new Button[4];

        botones[0] = (Button) view.findViewById(R.id.button_crear);/*Crear cuenta ikiam*/
        botones[1] = (Button) view.findViewById(R.id.button_logout_ikiam); /*Logout ikiam*/
        botones[2] = (Button) view.findViewById(R.id.button_login); /*login ikiam*/
        botones[3] = (Button) view.findViewById(R.id.button_guardar); /*Guardar cuenta ikiam*/
        //botones[1] = (Button) view.findViewById(R.id.btnService);
        for (int i = 0; i < botones.length; i++) {
            botones[i].setOnClickListener(this);
        }

        ((EditText) view.findViewById(R.id.email)).setText("juan@sinmiedo.com");
        ((EditText) view.findViewById(R.id.pass)).setText("qwe");
        ((EditText) view.findViewById(R.id.pass2)).setText("qwe");
        ((EditText) view.findViewById(R.id.nombre)).setText("juan");
        ((EditText) view.findViewById(R.id.apellido)).setText("sin miedo");
        if (activity.type.equals("ikiam")) {
            showIkiamView();
        } else {
            Session session = Session.getActiveSession();
            if (session != null && session.isOpened()) {

                makeMeRequest(session);
                if (!activity.userId.equals("-1")) {
                    RelativeLayout userPanel = (RelativeLayout) view.findViewById(R.id.user_info);
                    userPanel.setVisibility(View.VISIBLE);
                    RelativeLayout userPanelIkiam = (RelativeLayout) view.findViewById(R.id.user_info_ikiam);
                    userPanelIkiam.setVisibility(View.GONE);
                    RelativeLayout elegirCuenta = (RelativeLayout) view.findViewById(R.id.botones_cuenta);
                    elegirCuenta.setVisibility(View.GONE);
                    RelativeLayout cuentaForm = (RelativeLayout) view.findViewById(R.id.body);
                    cuentaForm.setVisibility(View.GONE);
                    RelativeLayout cuentaFormLogin = (RelativeLayout) view.findViewById(R.id.login_ikiam);
                    cuentaFormLogin.setVisibility(View.GONE);
                } else {
                    RelativeLayout userPanel = (RelativeLayout) view.findViewById(R.id.user_info);
                    userPanel.setVisibility(View.GONE);
                    RelativeLayout userPanelIkiam = (RelativeLayout) view.findViewById(R.id.user_info_ikiam);
                    userPanelIkiam.setVisibility(View.GONE);
                    RelativeLayout elegirCuenta = (RelativeLayout) view.findViewById(R.id.botones_cuenta);
                    elegirCuenta.setVisibility(View.VISIBLE);
                    RelativeLayout cuentaForm = (RelativeLayout) view.findViewById(R.id.body);
                    cuentaForm.setVisibility(View.GONE);
                    RelativeLayout loginForm = (RelativeLayout) view.findViewById(R.id.login_ikiam);
                    loginForm.setVisibility(View.GONE);
                    RelativeLayout cuentaFormLogin = (RelativeLayout) view.findViewById(R.id.login_ikiam);
                    cuentaFormLogin.setVisibility(View.GONE);
                }
            } else {
                RelativeLayout userPanel = (RelativeLayout) view.findViewById(R.id.user_info);
                userPanel.setVisibility(View.GONE);
                RelativeLayout userPanelIkiam = (RelativeLayout) view.findViewById(R.id.user_info_ikiam);
                userPanelIkiam.setVisibility(View.GONE);
                RelativeLayout elegirCuenta = (RelativeLayout) view.findViewById(R.id.botones_cuenta);
                elegirCuenta.setVisibility(View.VISIBLE);
                RelativeLayout cuentaForm = (RelativeLayout) view.findViewById(R.id.body);
                cuentaForm.setVisibility(View.GONE);
                RelativeLayout cuentaFormLogin = (RelativeLayout) view.findViewById(R.id.login_ikiam);
                cuentaFormLogin.setVisibility(View.GONE);
                SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (!settings.getString("user", "-1").equals("-1") && !settings.getString("type", "-1").equals("ikiam")) {
                    editor.putString("user", "-1");
                    editor.putString("login", "-1");
                    editor.putString("name", "-1");
                    editor.putString("type", "-1");
                    editor.commit();
                }
            }
        }

        System.out.println("variables login " + activity.userId + "  name " + activity.name);


        return view;
    }

    @Override
    public void onClick(View v) {
        // botones[0] = (Button) view.findViewById(R.id.button_crear);/*Crear cuenta ikiam*/
        // botones[1] = (Button) view.findViewById(R.id.button_logout_ikiam); /*Logout ikiam*/
        // botones[2] = (Button) view.findViewById(R.id.button_login); /*login ikiam*/
        // botones[3] = (Button) view.findViewById(R.id.button_guardar); /*Guardar cuenta ikiam*/
        Utils.hideSoftKeyboard(this.getActivity());
        if (v.getId() == botones[0].getId()) {
            RelativeLayout userPanel = (RelativeLayout) view.findViewById(R.id.user_info);
            userPanel.setVisibility(View.GONE);
            RelativeLayout userPanelIkiam = (RelativeLayout) view.findViewById(R.id.user_info_ikiam);
            userPanelIkiam.setVisibility(View.GONE);
            RelativeLayout elegirCuenta = (RelativeLayout) view.findViewById(R.id.botones_cuenta);
            elegirCuenta.setVisibility(View.GONE);
            RelativeLayout cuentaForm = (RelativeLayout) view.findViewById(R.id.body);
            cuentaForm.setVisibility(View.VISIBLE);
            RelativeLayout cuentaFormLogin = (RelativeLayout) view.findViewById(R.id.login_ikiam);
            cuentaFormLogin.setVisibility(View.VISIBLE);

        }
        if (v.getId() == botones[1].getId()) {
            /*logout de la cuenta ikiam*/
            RelativeLayout userPanel = (RelativeLayout) view.findViewById(R.id.user_info);
            userPanel.setVisibility(View.GONE);
            RelativeLayout userPanelIkiam = (RelativeLayout) view.findViewById(R.id.user_info_ikiam);
            userPanelIkiam.setVisibility(View.GONE);
            RelativeLayout elegirCuenta = (RelativeLayout) view.findViewById(R.id.botones_cuenta);
            elegirCuenta.setVisibility(View.VISIBLE);
            RelativeLayout cuentaForm = (RelativeLayout) view.findViewById(R.id.body);
            cuentaForm.setVisibility(View.GONE);
            RelativeLayout cuentaFormLogin = (RelativeLayout) view.findViewById(R.id.login_ikiam);
            cuentaFormLogin.setVisibility(View.GONE);
            SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("user", "-1");
            editor.putString("login", "-1");
            editor.putString("name", "-1");
            editor.putString("type", "-1");
            editor.putString("esCientifico", "-1");
            activity.setType("-1");
            activity.setUserId("-1");
            activity.esCientifico = "-1";
            activity.name = "-1";
//            System.out.println("LOGOUT: " + activity.esCientifico);
            editor.commit();
        }
        if (v.getId() == botones[2].getId()) {
           /*login ikiam*/
            /*aqui verificar los datos con la cuenta*/

            String email = ((EditText) view.findViewById(R.id.email_login)).getText().toString().trim();
            String pass = ((EditText) view.findViewById(R.id.pass_login)).getText().toString();
            StringBuffer hexString = new StringBuffer();
            if (email.length() > 0 && pass.length() > 0) {
                try {
                    MessageDigest m = java.security.MessageDigest.getInstance("MD5");
                    m.update(pass.getBytes());
                    byte messageDigest[] = m.digest();
                    for (int i = 0; i < messageDigest.length; i++)
                        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

                    ExecutorService queue = Executors.newSingleThreadExecutor();
                    queue.execute(new LoginUploader(activity, queue, 0, email, hexString.toString()));


                } catch (Exception e) {
                    System.out.println("error hexing " + e.getMessage());
                }
            }


        }
        if (v.getId() == botones[3].getId()) {


            String email = ((EditText) view.findViewById(R.id.email)).getText().toString().trim();
            String pass = ((EditText) view.findViewById(R.id.pass)).getText().toString();
            String confirm = ((EditText) view.findViewById(R.id.pass2)).getText().toString();
            String nombre = ((EditText) view.findViewById(R.id.nombre)).getText().toString().trim();
            String apellido = ((EditText) view.findViewById(R.id.apellido)).getText().toString().trim();

            if (pass.equals(confirm) && email.length() > 0 && nombre.length() > 0 && apellido.length() > 0) {
                StringBuffer hexString = new StringBuffer();
                try {
                    MessageDigest m = java.security.MessageDigest.getInstance("MD5");
                    m.update(pass.getBytes());
                    byte messageDigest[] = m.digest();
                    for (int i = 0; i < messageDigest.length; i++)
                        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

                } catch (Exception e) {
                    System.out.println("error del digest " + e.getMessage());
                }
                //System.out.println("email "+email+" "+hexString.toString()+" size"+hexString.toString().length()+" "+confirm+" "+nombre+" "+apellido);
                ExecutorService queue = Executors.newSingleThreadExecutor();
                queue.execute(new UsuarioUploader(activity, queue, 0, email, nombre, apellido, hexString.toString(), "ikiam"));

            } else {
                /*Mensaje de error de validacion.. probablemente con un toast*/
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Por favor llene todos los campos y revise que la contraseña y la verificación sean iguales", duration);
                toast.show();
            }

        }
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {

            RelativeLayout userPanel = (RelativeLayout) view.findViewById(R.id.user_info);
            userPanel.setVisibility(View.VISIBLE);
            RelativeLayout userPanelIkiam = (RelativeLayout) view.findViewById(R.id.user_info_ikiam);
            userPanelIkiam.setVisibility(View.GONE);
            RelativeLayout elegirCuenta = (RelativeLayout) view.findViewById(R.id.botones_cuenta);
            elegirCuenta.setVisibility(View.GONE);
            RelativeLayout cuentaForm = (RelativeLayout) view.findViewById(R.id.body);
            cuentaForm.setVisibility(View.GONE);
            // Get the user's data.
            makeMeRequest(session);
        } else {
            /*aqui refresh ui*/
            RelativeLayout userPanel = (RelativeLayout) view.findViewById(R.id.user_info);
            userPanel.setVisibility(View.GONE);
            RelativeLayout userPanelIkiam = (RelativeLayout) view.findViewById(R.id.user_info_ikiam);
            userPanelIkiam.setVisibility(View.GONE);
            RelativeLayout elegirCuenta = (RelativeLayout) view.findViewById(R.id.botones_cuenta);
            elegirCuenta.setVisibility(View.VISIBLE);
            RelativeLayout cuentaForm = (RelativeLayout) view.findViewById(R.id.body);
            cuentaForm.setVisibility(View.GONE);
            SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            if (!settings.getString("user", "-1").equals("-1") && !settings.getString("type", "-1").equals("ikiam")) {
                editor.putString("user", "-1");
                editor.putString("login", "-1");
                editor.putString("name", "-1");
                editor.putString("type", "-1");
                editor.commit();
                activity.userId = "-1";
                activity.name = "-1";
                activity.type = "-1";
            }

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
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                System.out.println("si user ? " + user.getId());
                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
                                profilePictureView.setProfileId(user.getId());
                                // Set the Textview's text to the user's name.
                                userNameView.setText(user.getName());
                                SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("user", user.getId());
                                editor.putString("login", user.getUsername());
                                editor.putString("name", user.getName());
                                editor.putString("type", "facebook");
                                activity.userId = user.getId();
                                activity.name = user.getUsername();
                                activity.type = "facebook";
                                editor.commit();
                                //System.out.println("Make request "+user.getId()+"  "+user.getUsername()+"  "+user.getName());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("activity result del login");
        if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
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
