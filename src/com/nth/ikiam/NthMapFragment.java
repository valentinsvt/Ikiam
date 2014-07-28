package com.nth.ikiam;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.Ruta;
import com.nth.ikiam.dialogs.NthMapDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;

/**
 * Created by DELL on 23/07/2014.
 */
public class NthMapFragment extends Fragment implements Button.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener {
    private Button chooseBtn;
    private Button[] botones;
    private static GoogleMap map;
    boolean continente=true;
    private static LatLng location ;
    LocationClient locationClient;
    /*Service */
    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Boolean status = false;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SvtService.MSG_SET_INT_VALUE:
                    System.out.println("Int Message: " + msg.arg1);
                    break;
                case SvtService.MSG_SET_STRING_VALUE:
                    String str1 = msg.getData().getString("str1");
//                    textStrValue.setText("Str Message: " + str1);
                    System.out.println("Str  Message: " + msg.arg1);
                    break;
                case SvtService.MSG_SET_COORDS:
                    Double latitud = msg.getData().getDouble("latitud");
                    Double longitud = msg.getData().getDouble("logitud");
                    System.out.println("Str  Message recibed: " + latitud+"  "+longitud);
                    map.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title("Pos"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            System.out.println("Attached.");
            try {
                Message msg = Message.obtain(null, SvtService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            System.out.println("Disconnected.");
        }
    };

    /*Google services*/
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /*Fin google services*/


    /*file*/
    String filename = "nthData";
    String contenido = "";
    FileOutputStream outputStream;

    public NthMapFragment() {
        // Empty constructor required for fragment subclasses
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.map_layout, container, false);
        botones = new Button[3];
        botones[0]=(Button) view.findViewById(R.id.btnGalapagos);
        botones[1]= (Button) view.findViewById(R.id.btnLocate);
        botones[2]= (Button) view.findViewById(R.id.btnService);
        // chooseBtn = (Button) view.findViewById(R.id.btnGalapagos);
        //chooseBtn.setOnClickListener(this);
        for(int i=0;i<botones.length;i++){
            botones[i].setOnClickListener(this);
        }
        // map=((MapFragment) getFragmentManager().findFragmentById(R.id.mapF)).getMap();
        //  map=new MapFragment().getMap();
        locationClient = new LocationClient(this.getActivity(),this, this);
        locationClient.connect();
        setUpMapIfNeeded();
        restoreMe(savedInstanceState);
        CheckIfServiceIsRunning();
        //System.out.println("rutas "+Ruta.count(this.getActivity()));
        //Ruta ruta = Ruta.get(this.getActivity(),1L);
        //List<Coordenada> coords = Coordenada.findAllByRuta(this.getActivity(),ruta);
        return view;

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (map != null)
            setUpMap();

        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapF)).getMap();
            // Check if we were successful in obtaining the map.
            if (map != null)
                setUpMap();
        }
    }
    @Override
    public void onClick(View v){
        if(v.getId()==botones[0].getId()){

            if(!continente) {
                location = new LatLng(-1.6477220517969353, -78.46435546875);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 7);
                map.animateCamera(update);
                botones[0].setText(R.string.map_galapagos_btn);
                continente = true;
            }else{
                location = new LatLng(-0.4614207935306084, -90.615234375);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 7);
                map.animateCamera(update);
                botones[0].setText(R.string.map_continente_btn);
                continente = false;
            }
        }
        if(v.getId()==botones[1].getId()){
            Location mCurrentLocation;
            mCurrentLocation = locationClient.getLastLocation();
            //Location myLocation  = map.getMyLocation();
            //System.out.println(" location "+myLocation+" map "+map);
            location=new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,19);
            map.animateCamera(update);
        }
        if(v.getId()==botones[2].getId()){
            if(servicesConnected()){
                if(!status) {
                    this.getActivity().startService(new Intent(this.getActivity(), SvtService.class));
                    doBindService();
                    Location mCurrentLocation;
                    mCurrentLocation = locationClient.getLastLocation();
                    location=new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,19);
                    map.animateCamera(update);
                    Ruta ruta= new Ruta(this.getActivity(),"");
                    ruta.save();
                    sendMessageToService((int)ruta.id);
                    status=true;
                }else {
                    doUnbindService();
                    this.getActivity().stopService(new Intent(this.getActivity(), SvtService.class));
                    status=false;
                }
            }

        }

    }

    /*Google services*/
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Get the error code
            int errorCode = 10;
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,this.getActivity(),CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(this.getFragmentManager(),"Location Updates");
            }
            return false;
        }
    }


    /*service*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        System.out.println("on saved entro");
        super.onSaveInstanceState(outState);
//    outState.putString("textStatus", textStatus.getText().toString());
//    outState.putString("textIntValue", textIntValue.getText().toString());
//    outState.putString("textStrValue", textStrValue.getText().toString());
    }
    private void restoreMe(Bundle state) {
        if (state!=null) {
//            textStatus.setText(state.getString("textStatus"));
//            textIntValue.setText(state.getString("textIntValue"));
//            textStrValue.setText(state.getString("textStrValue"));
        }
    }
    private void CheckIfServiceIsRunning() {
        //If the service is running when the activity starts, we want to automatically bind to it.
        if (SvtService.isRunning()) {
            doBindService();
        }
    }


    private void sendMessageToService(int intvaluetosend) {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, SvtService.MSG_SET_INT_VALUE, intvaluetosend, 0);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }



    void doBindService() {
        this.getActivity().bindService(new Intent(this.getActivity(), SvtService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        System.out.println("Binding.");
    }
    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, SvtService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            this.getActivity().unbindService(mConnection);
            mIsBound = false;
            System.out.println("UnBinding.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

    /***** Sets up the map if it is possible to do so *****/
    public  void setUpMapIfNeeded() {
        System.out.println("setUpMap if needed");
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapF)).getMap();
            // Check if we were successful in obtaining the map.
            if (map != null)
                setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p>
     * This should only be called once and when we are sure that
     * is not null.
     */
    private static void setUpMap() {
        location=new LatLng(-1.6477220517969353, -78.46435546875);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,6);
        map.setMyLocationEnabled(true);
        map.animateCamera(update);
        // For showing a move to my loction button
        //map.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        //map.addMarker(new MarkerOptions().position(location).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
    }



    /*location */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        // Toast.makeText(this, "Disconnected. Please re-connect.",
        //Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        System.out.println("error connection failed");
    }


}