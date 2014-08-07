package com.nth.ikiam;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import java.util.Map;

/**
 * Created by Svt on 8/4/2014.
 */
public class Loginfragment extends Fragment implements Button.OnClickListener  {

    Context context;
    MapActivity activity;
    private ProfilePictureView profilePictureView;
    private TextView userNameView;
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

        context = getActivity().getApplicationContext();
        activity = (MapActivity) getActivity();
        View view = inflater.inflate(R.layout.login_layout, container, false);
        uiHelper = new UiLifecycleHelper(activity, callback);
        uiHelper.onCreate(savedInstanceState);
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        userNameView = (TextView) view.findViewById(R.id.selection_user_name);
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {

            makeMeRequest(session);
            if(!activity.userId.equals("-1")){
                RelativeLayout userPanel=(RelativeLayout)view.findViewById(R.id.user_info);
                userPanel.setVisibility(View.VISIBLE);
                RelativeLayout elegirCuenta=(RelativeLayout)view.findViewById(R.id.botones_cuenta);
                elegirCuenta.setVisibility(View.GONE);
                RelativeLayout cuentaForm=(RelativeLayout)view.findViewById(R.id.body);
                cuentaForm.setVisibility(View.GONE);
            }else{
                RelativeLayout userPanel=(RelativeLayout)view.findViewById(R.id.user_info);
                userPanel.setVisibility(View.GONE);
                RelativeLayout elegirCuenta=(RelativeLayout)view.findViewById(R.id.botones_cuenta);
                elegirCuenta.setVisibility(View.VISIBLE);
                RelativeLayout cuentaForm=(RelativeLayout)view.findViewById(R.id.body);
                cuentaForm.setVisibility(View.VISIBLE);
            }
        }else{
            RelativeLayout userPanel=(RelativeLayout)view.findViewById(R.id.user_info);
            userPanel.setVisibility(View.GONE);
            RelativeLayout elegirCuenta=(RelativeLayout)view.findViewById(R.id.botones_cuenta);
            elegirCuenta.setVisibility(View.VISIBLE);
            RelativeLayout cuentaForm=(RelativeLayout)view.findViewById(R.id.body);
            cuentaForm.setVisibility(View.VISIBLE);
            SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            if(!settings.getString("user","-1").equals("-1") && !settings.getString("type","-1").equals("ikiam")) {
                editor.putString("user", "-1");
                editor.putString("login", "-1");
                editor.putString("name", "-1");
                editor.putString("type", "-1");
                editor.commit();
            }
        }
        System.out.println("variables login "+activity.userId+"  name "+activity.name);


        return view;
    }
    @Override
    public void onClick(View view) {

    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            System.out.println("session state change");
            // Get the user's data.
            makeMeRequest(session);
        }else{
            /*aqui refresh ui*/
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
                                System.out.println("si user ? "+user.getId());
                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
                                profilePictureView.setProfileId(user.getId());
                                // Set the Textview's text to the user's name.
                                userNameView.setText(user.getName());
                                SharedPreferences settings = activity.getSharedPreferences(activity.PREFS_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("user", user.getId());
                                editor.putString("login",user.getUsername());
                                editor.putString("name", user.getName());
                                editor.putString("type", "facebook");
                                editor.commit();
                                //System.out.println("Make request "+user.getId()+"  "+user.getUsername()+"  "+user.getName());
                            }else{
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
