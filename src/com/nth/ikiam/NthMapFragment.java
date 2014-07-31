package com.nth.ikiam;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.*;
import android.database.Cursor;
import android.graphics.*;
import android.location.Location;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.DbHelper;
import com.nth.ikiam.db.Ruta;
import com.nth.ikiam.image.ImageItem;
import com.nth.ikiam.image.ImageTableObserver;
import com.nth.ikiam.image.ImageUtils;
import com.nth.ikiam.utils.ImageUploader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by DELL on 23/07/2014.
 */
public class NthMapFragment extends Fragment implements Button.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener {
    private Button chooseBtn;
    private Button[] botones;
    /*Mapa*/
    private static GoogleMap map;
    private Polyline polyLine;
    private PolylineOptions rectOptions = new PolylineOptions().color(Color.RED);
    private static LatLng location ;
    LocationClient locationClient;
    Marker lastPosition;
    /*Fin mapa*/
    boolean continente=true;

    /*Service */
    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Boolean status = false;
    Boolean attached = false;
    Ruta ruta;
    private ImageTableObserver camera;
    /*Images*/
    int lastestImageIndex = 0;
    ImageItem imageItem;
    List<Bitmap> imagenes;
    int lastIndex = -1;
    int lastSize = 0;
    private ExecutorService queue = Executors.newSingleThreadExecutor();
    /*Fin imagenes*/
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
//                    System.out.println("Str  Message recibed: " + latitud+"  "+longitud);
                    LatLng latlong = new LatLng(latitud, longitud);
                    if(lastPosition==null)
                        lastPosition = map.addMarker(new MarkerOptions().position(latlong).title("Última posición registrada"));
                    else
                        lastPosition.setPosition(latlong);
                    updatePolyLine(latlong);
                    if(lastestImageIndex!=0){
                        System.out.println("Tomo foto "+lastestImageIndex);
                        imageItem = getLatestItem();
                        getFoto();
                        if(imagenes.size()>lastSize) {
//                            map.addMarker(new MarkerOptions().position(latlong).title("Sydney").snippet("Population: 4,627,300").icon(BitmapDescriptorFactory.fromBitmap(imagenes.get(lastIndex))));
                            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                            Bitmap bmp = Bitmap.createBitmap(86,59, conf);
                            Canvas canvas1 = new Canvas(bmp);
                            Paint color = new Paint();
                            color.setTextSize(35);
                            color.setColor(Color.BLACK);//modify canvas
                            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.pin3), 0,0, color);
                            canvas1.drawBitmap(imagenes.get(lastIndex), 3,2, color);

                            map.addMarker(new MarkerOptions().position(latlong)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                    .anchor(0.5f, 1));
                            lastSize=imagenes.size();
                            //queue.execute(new ImageUploader(getActivity(), queue, imageItem, 0));
                        }
                    }
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
                attached=true;
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
            if(ruta!=null){
                try {
                    Message msg = Message.obtain(null, SvtService.MSG_SET_INT_VALUE);
                    msg.replyTo = mMessenger;
                    msg.arg1=(int)ruta.id;
                    mService.send(msg);
                    //System.out.println("Mando mensaje de ruta");
                    attached=true;
                } catch (RemoteException e) {
                    // In this case the service has crashed before we could even do anything with it
                }
            }

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
           // System.out.println("Disconnected.");
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


    DbHelper helper;

    public NthMapFragment() {
        // Empty constructor required for fragment subclasses
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.map_layout, container, false);
        setUpMapIfNeeded();
        locationClient = new LocationClient(this.getActivity(),this, this);
        locationClient.connect();
        botones = new Button[2];
        botones[0]=(Button) view.findViewById(R.id.btnGalapagos);
        botones[1]= (Button) view.findViewById(R.id.btnService);
        for(int i=0;i<botones.length;i++){
            botones[i].setOnClickListener(this);
        }
        restoreMe(savedInstanceState);
        CheckIfServiceIsRunning();
//        System.out.println("rutas "+Ruta.count(this.getActivity()));
//        Ruta ruta = Ruta.get(this.getActivity(),11L);
//
//        List<Coordenada> coords = Coordenada.findAllByRuta(this.getActivity(),ruta);
//        for (Coordenada cord : coords) {
//            System.out.println("coord--> "+cord.latitud+"  "+cord.longitud+"  "+cord.ruta.id);
//        }
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
//        if(v.getId()==botones[1].getId()){
//            Location mCurrentLocation;
//            mCurrentLocation = locationClient.getLastLocation();
//            //Location myLocation  = map.getMyLocation();
//            //System.out.println(" location "+myLocation+" map "+map);
//            location=new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//            CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,19);
//            map.animateCamera(update);
//
//        }
        if(v.getId()==botones[1].getId()){
            if(servicesConnected()){
                if(!status) {
                    map.clear();
                    ruta= new Ruta(this.getActivity(),"Ruta");
                    ruta.save();
                    this.getActivity().startService(new Intent(this.getActivity(), SvtService.class));
                    doBindService();
                    //  sendMessageToService((int)ruta.id);
                    Location mCurrentLocation;
                    mCurrentLocation = locationClient.getLastLocation();
                    location=new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,19);
                    map.animateCamera(update);
                    polyLine = map.addPolyline(rectOptions);
                    botones[1].setText("Parar");
                    camera = new ImageTableObserver(new Handler(),this);
                    this.getActivity().getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, camera);
                    status=true;
                }else {
                    doUnbindService();
                    this.getActivity().stopService(new Intent(this.getActivity(), SvtService.class));
                    botones[1].setText("Nueva ruta");
                    ruta=null;
                    status=false;
                }
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        if(ruta!=null)
            savedInstanceState.putInt("ruta", (int)ruta.id);
//        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        //System.out.println("restore bundle");
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey("ruta")){
                //System.out.println("contiene "+savedInstanceState.getLong("ruta"));
                ruta = Ruta.get(this.getActivity(),savedInstanceState.getLong("ruta"));
                List<Coordenada>   coords = Coordenada.findAllByRuta(this.getActivity(),ruta);

                setUpMapIfNeeded();
                boolean band = false;
                for (Coordenada cord : coords) {
                    if(!band){
                        location = new LatLng(cord.latitud, cord.longitud);
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 16);
                        map.animateCamera(update);
                    }
                    map.addMarker(new MarkerOptions().position(new LatLng(cord.latitud, cord.longitud)).title("Pos"));
                    band=true;
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
                    while (!attached){

                    }
                    Message msg = Message.obtain(null, SvtService.MSG_SET_INT_VALUE, intvaluetosend, 0);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                    System.out.println("mando mensaje "+intvaluetosend);
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
    void doBindServiceAndSend(int id) {
        this.getActivity().bindService(new Intent(this.getActivity(), SvtService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        System.out.println("Binding.");
        System.out.println("mando id de ruta "+id);
        sendMessageToService(id);
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
            this.getActivity().stopService(new Intent(this.getActivity(), SvtService.class));
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

    /***** Sets up the map if it is possible to do so *****/
    public  void setUpMapIfNeeded() {
        //System.out.println("setUpMap if needed");
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



    /*location services*/
    @Override
    public void onConnected(Bundle dataBundle) {
        Location mCurrentLocation;
        mCurrentLocation = locationClient.getLastLocation();
        map.getMyLocation();
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


    /*MAPS*/
    /**
     * Add the marker to the polyline.
     */
    private void updatePolyLine(LatLng latLng) {
        List<LatLng> points = polyLine.getPoints();
        points.add(latLng);
        polyLine.setPoints(points);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng l : points) {
            builder.include(l);
        }
        LatLngBounds bounds = builder.build();
        int padding = (100);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        map.animateCamera(cu);
    }

    public void setImageIndex(int index){
        //System.out.println("set image index " + index);
        if(imagenes==null){
            imagenes=  new ArrayList<Bitmap>();;
        }
        if(index>=lastestImageIndex) {
            this.lastestImageIndex = index;
        }else {
            /*Borro una foto*/
            this.lastestImageIndex=0;
        }
        System.out.println("set image index fin "+this.lastestImageIndex);
    }

    /*Fotos*/

    public void getFoto(){
        if(imageItem!=null) {
           // System.out.println("path "+imageItem.imagePath);
            //System.out.println("images " + imagenes);
            Bitmap b = ImageUtils.decodeFile(imageItem.imagePath);
            System.out.println("width "+b.getWidth()+"  "+b.getHeight());
            imagenes.add(b);
            lastIndex++;
            lastestImageIndex = 0;
        }

    }

    public ImageItem getLatestItem()
    {
        // set vars
        if(lastestImageIndex>0){
            ImageItem item  = null;
            String columns[] = new String[]{ MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.MINI_THUMB_MAGIC };

            // loop until break
            System.out.println("getLatestItem");

            // get latest image from table
            Uri image     = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, lastestImageIndex);
            Cursor cursor = this.getActivity().managedQuery(image, columns, null, null, null);

            // check if cursus has rows, if not break and exit loop
            if (cursor.moveToFirst()) {
                System.out.println("tiene rows "+cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MINI_THUMB_MAGIC)));
                item           = new ImageItem();
                item.prefs     = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getBaseContext());
                item.imageId   = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                item.imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                item.imageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                item.imageType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                item.imageSize = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            }
            //cursor.close();

            System.out.println("salio");
            return item;
        }

        return null;

    }




}