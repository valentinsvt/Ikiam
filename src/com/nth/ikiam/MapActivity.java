package com.nth.ikiam;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
//import android.support.v4.app.ActionBarDrawerToggle;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.facebook.*;
import com.facebook.android.Util;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.nth.ikiam.db.*;
import com.nth.ikiam.image.AtraccionUi;
import com.nth.ikiam.image.ImageItem;
import com.nth.ikiam.image.ImageTableObserver;
import com.nth.ikiam.image.ImageUtils;
import com.nth.ikiam.listeners.FieldListener;
import com.nth.ikiam.utils.AchievementChecker;
import com.nth.ikiam.utils.AtraccionDownloader;
import com.nth.ikiam.utils.LogrosChecker;
import com.nth.ikiam.utils.Utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends Activity implements Button.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    /*DRAWER*/
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mOptionsArray;

    public final int MAP_POS = 0;
    public final int CAPTURA_POS = 1;
    public final int ENCYCLOPEDIA_POS = 2;
    public final int GALERIA_POS = 3;
    public final int RUTAS_POS = 4;
    public final int SETTINGS_POS = 5;
    public final int LOGIN_POS = 6;

    public final int ACHIEV_FOTOS = 1;
    public final int ACHIEV_DISTANCIA = 2;
    public final int ACHIEV_UPLOADS = 3;
    public final int ACHIEV_SHARE = 4;

    private final static String TAG_MAP = "TAG_MAP_FRAGMENT";
    private final static String TAG_CAPTURA_C = "TAG_CAPTURA_C_FRAGMENT";
    private final static String TAG_CAPTURA_T = "TAG_CAPTURA_T_FRAGMENT";
    private final static String TAG_ENCYCLOPEDIA = "TAG_ENCYCLOPEDIA_FRAGMENT";
    private final static String TAG_GALERIA = "TAG_GALERIA_FRAGMENT";
    private final static String TAG_RUTAS = "TAG_RUTAS_FRAGMENT";
    private final static String TAG_SETTINGS = "TAG_SETTINGS_FRAGMENT";
    private final static String TAG_LOGIN = "TAG_LOGIN_FRAGMENT";

    private static final int CAMERA_REQUEST = 1337;
    /*Interfaz*/
    private Button[] botones;
    boolean continente = true;
    Activity activity;
    /*Mapa*/
    private static GoogleMap map;
    private Polyline polyLine;
    private PolylineOptions rectOptions = new PolylineOptions().color(Color.RED);
    private static LatLng location;
    LocationClient locationClient;
    Marker lastPosition;
    HashMap<Marker, Foto> data;
    HashMap<Marker, AtraccionUi> atracciones;
    HashMap<Marker, Bitmap> fotosUsuario;
    Marker selected;

    /*Google services*/
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    /*Fin mapa*/

    /*Service */
    Messenger mService = null;
    boolean mIsBound;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Boolean status = false;
    Boolean attached = false;
    Ruta ruta;

    /*Images*/
    private ImageTableObserver camera;
    int lastestImageIndex = 0;
    ImageItem imageItem;
    List<Bitmap> imagenes;
    int lastIndex = -1;
    int lastSize = 0;
    List<Foto> fotos;
    /*Fin imagenes*/

    //    String imagePathUpload = "";
    Foto imageToUpload;
    AlertDialog dialog;
    View myView;
    public int screenHeight;
    public int screenWidth;
    public static final String PREFS_NAME = "IkiamSettings";
    public String userId;
    public String name;
    public String type;
    public String email;
    public String esCientifico;
    public String errorMessage;
    public String ruta_remote_id;
    public String old_id;
    private UiLifecycleHelper uiHelper;
    private static final int REAUTH_ACTIVITY_CODE = 100;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    List<FieldListener> listeners = new ArrayList<FieldListener>();

    public List<Entry> entriesBusqueda;
    public List<Especie> especiesBusqueda;
    public Marker markerSubir;
    public LatLng posicionSubir;

    public Foto fotoSinCoords;


    public void setRuta_remote_id(String id) {
        fireEvent("ruta_remote_id", id);
        old_id = ruta_remote_id;
        this.ruta_remote_id = id;
    }

    public void setUserId(String id) {
        fireEvent("userId", id);
        this.userId = id;
    }

    public void setType(String type) {
        fireEvent("type", type);
        this.type = type;
    }

    public void setErrorMessage(String msg) {
        // System.out.println("::: SET ERROR MESSAGE::: " + msg);
        fireEvent("errorMessage", msg);
        this.errorMessage = msg;
    }

    public void addListener(FieldListener l) {
        if (l != null) listeners.add(l);
    }

    public void fireEvent(String fieldName, String newValue) {
        for (FieldListener l : listeners) {
            l.fieldValueChanged(fieldName, newValue);
        }
    }

    public void showToast(final String msg) {
        final Activity a = this;
        if (a != null) {
            a.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(a, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            makeMeRequest(session);
        } else {


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DbHelper helper = new DbHelper(this);
        helper.getWritableDatabase();

        ExecutorService queue = Executors.newSingleThreadExecutor();
        queue.execute(new LogrosChecker(this));

        fotoSinCoords = null;
        imageToUpload = null;

        /*preferencias*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("user", "-1");
        name = settings.getString("name", "-1");
        type = settings.getString("type", "-1");
        email = settings.getString("email", "-1");
        esCientifico = settings.getString("esCientifico", "-1");
        //System.out.println("variables name "+userId+"  name "+name);
        setContentView(R.layout.activity_map);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Session session;
        if (type.equals("-1") || type.equals("facebook")) {
            session = Session.getActiveSession();
            System.out.println("get active " + session);
            makeMeRequest(session);
        }

        this.activity = this;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
        fotos = new ArrayList<Foto>();

        /*CORE*/
        locationClient = new LocationClient(this, this, this);
        locationClient.connect();
        setUpMapIfNeeded();
        data = new HashMap<Marker, Foto>();
        atracciones = new HashMap<Marker, AtraccionUi>();
        fotosUsuario = new HashMap<Marker, Bitmap>();

        botones = new Button[5];
        botones[0] = (Button) this.findViewById(R.id.btnGalapagos);
        botones[1] = (Button) this.findViewById(R.id.btnService);
        botones[2] = (Button) this.findViewById(R.id.btnAtraccion);
        botones[3] = (Button) this.findViewById(R.id.btnEspecies);
        botones[4] = (Button) this.findViewById(R.id.btnCamara);
        if (type.equals("Ikiam")) {
            if (esCientifico.equals("S"))
                botones[2].setVisibility(View.GONE);
        }
        for (int i = 0; i < botones.length; i++) {
            botones[i].setOnClickListener(this);
        }
        restoreMe(savedInstanceState);
        CheckIfServiceIsRunning();
        /*fin*/

        /*DRAWER*/
        mTitle = mDrawerTitle = getTitle();
        mOptionsArray = getResources().getStringArray(R.array.options_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout2);
        mDrawerList = (ListView) findViewById(R.id.left_drawer2);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mOptionsArray));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
                getActionBar().setTitle(R.string.menu_title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }
        /*FinDrawer*/


    }

    private void restoreMe(Bundle state) {
        if (state != null) {
                /*Implementar el restor*/
        }

    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    @Override
    public void onResume() {

        super.onStart();
        uiHelper.onResume();
//        // TODO Auto-generated method stub

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        if (ruta != null)
            savedInstanceState.putInt("ruta", (int) ruta.id);
        savedInstanceState.putInt("restore", 1);
        getFragmentManager().saveFragmentInstanceState(getFragmentManager().findFragmentById(R.id.mapF));
//        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);

        // Always call the superclass so it can save the view hierarchy state
        uiHelper.onSaveInstanceState(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);

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
        try {
            doUnbindService();
            this.stopService(new Intent(this, SvtService.class));
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == botones[0].getId()) {
            //Continente --> Galapagos

            if (!continente) {
                location = new LatLng(-1.6477220517969353, -78.46435546875);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 7);
                map.animateCamera(update);
                botones[0].setText(R.string.map_galapagos_btn);
                continente = true;
            } else {
                location = new LatLng(-0.4614207935306084, -90.615234375);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 7);
                map.animateCamera(update);
                botones[0].setText(R.string.map_continente_btn);
                continente = false;
            }
        }

        if (v.getId() == botones[1].getId()) {
            //service de rutas
            if (servicesConnected()) {
                if (!status) {
                    map.clear();
                    ruta = new Ruta(this, "Ruta");
                    ruta.save();
                    this.startService(new Intent(this, SvtService.class));
                    doBindService();
                    //  sendMessageToService((int)ruta.id);
                    Location mCurrentLocation;
                    mCurrentLocation = locationClient.getLastLocation();
                    location = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 19);
                    map.animateCamera(update);
                    polyLine = map.addPolyline(rectOptions);
                    botones[1].setText("Parar");
                    camera = new ImageTableObserver(new Handler(), this);
                    this.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, camera);
                    status = true;
                } else {
                    doUnbindService();
                    this.stopService(new Intent(this, SvtService.class));
                    botones[1].setText("Nueva ruta");
                    ruta = null;
                    status = false;
                }
            }

        }

        if (v.getId() == botones[2].getId()) {
            map.clear();
            Location mCurrentLocation;
            mCurrentLocation = locationClient.getLastLocation();
            atracciones.clear();
            selected = null;
            //System.out.println("Altura "+ mCurrentLocation.getAltitude());
            location = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 9);
            map.animateCamera(update);
            ExecutorService queue = Executors.newSingleThreadExecutor();
            queue.execute(new AtraccionDownloader(this, queue, 0));
        }

        if (v.getId() == botones[3].getId()) {
            map.clear();
            Location mCurrentLocation;
            mCurrentLocation = locationClient.getLastLocation();
            atracciones.clear();
            selected = null;
            //System.out.println("Altura "+ mCurrentLocation.getAltitude());
            location = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 9);
            map.animateCamera(update);
            ExecutorService queue = Executors.newSingleThreadExecutor();
            queue.execute(new AtraccionDownloader(this, queue, 0));
        }
        if (v.getId() == botones[4].getId()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }


    }


    /*Google services*/
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
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

            return false;
        }
    }

    /*location services*/
    @Override
    public void onConnected(Bundle dataBundle) {
        System.out.println("connected");
        Location mCurrentLocation;
        mCurrentLocation = locationClient.getLastLocation();
        map.getMyLocation();
    }

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
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }

    public void setPing(final String title, final int likes, final double latitud, final double longitud, final Bitmap foto, final Bitmap fotoDialog, final String url) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final LatLng pos = new LatLng(latitud, longitud);
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(86, 59, conf);
                Canvas canvas1 = new Canvas(bmp);
                Paint color = new Paint();
                color.setTextSize(35);
                color.setColor(Color.BLACK);//modify canvas
                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.pin3), 0, 0, color);
                canvas1.drawBitmap(foto, 3, 2, color);
                Marker marker = map.addMarker(new MarkerOptions().position(pos)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .anchor(0.5f, 1).title(title));
                AtraccionUi atraccion = new AtraccionUi(title, fotoDialog, likes, url);
                atracciones.put(marker, atraccion);
            }
        });

    }


    /*CLASES PARA COMUNICARSE CON SvtService*/
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
                    while (!attached) {

                    }
                    Message msg = Message.obtain(null, SvtService.MSG_SET_INT_VALUE, intvaluetosend, 0);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }

    void doBindService() {
        this.bindService(new Intent(this, SvtService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
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
            this.unbindService(mConnection);
            mIsBound = false;
        }
    }

    public void setUpMapIfNeeded() {
        //System.out.println("setUpMap if needed" +map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapF)).getMap();
        //System.out.println("setUpMap if needed despues" +map);
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapF)).getMap();
            // Check if we were successful in obtaining the map.
            if (map != null)
                setUpMap();
        } else {
            setUpMap();
        }
    }

    private void setUpMap() {
        //locationClient.getLastLocation();
        location = new LatLng(-1.6477220517969353, -78.46435546875);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 6);
        map.setMyLocationEnabled(true);
        map.animateCamera(update);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(latLng);
                // Setting the title for the marker.
                // This will be displayed on taping the marker
                if (fotoSinCoords == null) {
                    markerOptions.title(getString(R.string.map_subirFotoMarker));
                } else {
                    markerOptions.title(getString(R.string.map_ubicar_foto_marker));
                }
                // Clears the previously touched position
                //googleMap.clear();
                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                // Placing a marker on the touched position
                markerSubir = map.addMarker(markerOptions);
                markerSubir.showInfoWindow();
            }
        });
        final Context context = this;
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (markerSubir != null) {
                    markerSubir.hideInfoWindow();
                    if (marker.getId().equals(markerSubir.getId())) {
                        posicionSubir = marker.getPosition();
                        if (fotoSinCoords == null) {
                            selectItem(CAPTURA_POS);
                        } else {
                            updateFotoSinCoords(posicionSubir);
                        }
                    }
                }

                if (data.get(marker) != null) {
                    marker.showInfoWindow();
                    LayoutInflater inflater = activity.getLayoutInflater();
                    myView = inflater.inflate(R.layout.dialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.map_activity_nuevaFoto);
                    builder.setView(myView);
                    builder.setPositiveButton(R.string.dialog_btn_descargar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
//                            imagePathUpload = data.get(marker).path;
                            imageToUpload = data.get(marker);
//                            dialog.dismiss();
//                            System.out.println("***************** ANTES: " + imageToUpload.path);
//                            System.out.println("*************** MAPA*************** " + imageToUpload.getCoordenada(context).latitud);
                            selectItem(CAPTURA_POS, false);
                        }
                    });
                    builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }


                    });

                    builder.setNeutralButton(R.string.dialog_btn_borrar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            data.get(marker).delete();
                            data.remove(marker);
                            marker.remove();
                            dialog.dismiss();
                        }


                    });
                    dialog = builder.create();
                    ImageView img = (ImageView) myView.findViewById(R.id.image);

                    img.setImageBitmap(getFotoDialog(data.get(marker), screenWidth, 300));
                    dialog.show();

                    return true;
                }
                if (atracciones.get(marker) != null) {
                    marker.showInfoWindow();
                    //System.out.println("click "+marker+"  "+selected+"  "+(selected!=marker));
                    if (selected == null) {
                        selected = marker;
                    } else {
                        if (selected.getId().equals(marker.getId())) {
                            selected = null;
                            final AtraccionUi current = atracciones.get(marker);
                            LayoutInflater inflater = activity.getLayoutInflater();
                            myView = inflater.inflate(R.layout.dialog, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle(current.nombre);
                            builder.setView(myView);
                            builder.setPositiveButton(R.string.map_activity_dialog_mas, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String url = current.url;
                                    try {
                                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(myIntent);
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            builder.setNegativeButton(R.string.map_activity_dialog_cerrar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }


                            });
                            String label = getString(R.string.map_activity_dialog_like);
                            builder.setNeutralButton(label + " (" + current.likes + ")", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                           /*aqui implementar like*/
                                    current.likes++;
                                    dialog.dismiss();
                                }


                            });
                            dialog = builder.create();
                            ImageView img = (ImageView) myView.findViewById(R.id.image);

                            img.setImageBitmap(current.foto);
                            dialog.show();
                        } else {
                            selected = marker;
                        }
                    }
                    return true;
                }
                return true;
            }
        });

    }

    private void updateFotoSinCoords(LatLng pos) {
        Coordenada coord = new Coordenada(this);
        markerSubir.hideInfoWindow();
        coord.latitud = pos.latitude;
        coord.longitud = pos.longitude;
        coord.altitud = 0;
        coord.save();
        fotoSinCoords.setCoordenada(coord);
        fotoSinCoords.save();
        Toast.makeText(this, getString(R.string.map_foto_ubicada), Toast.LENGTH_LONG).show();
        if(markerSubir.isInfoWindowShown())
            markerSubir.hideInfoWindow();
        markerSubir.remove();
        markerSubir = null;
        fotoSinCoords = null;
    }

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
                    if (lastPosition == null)
                        lastPosition = map.addMarker(new MarkerOptions().position(latlong).title("Última posición registrada"));
                    else
                        lastPosition.setPosition(latlong);
                    updatePolyLine(latlong);
                    if (lastestImageIndex != 0) {
                        //System.out.println("Tomo foto " + lastestImageIndex);
                        imageItem = getLatestItem();
                        getFoto();
                        if (imagenes.size() > lastSize) {
//                            map.addMarker(new MarkerOptions().position(latlong).title("Sydney").snippet("Population: 4,627,300").icon(BitmapDescriptorFactory.fromBitmap(imagenes.get(lastIndex))));
                            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                            Foto foto = new Foto(activity);
                            Coordenada cord = new Coordenada(activity, latitud, longitud);
                            cord.save();
                            foto.setCoordenada(cord);
                            foto.setRuta(ruta);
                            foto.path = imageItem.imagePath;
                            foto.uploaded = 0;
                            foto.save();
                            fotos.add(foto);
                            Bitmap bmp = Bitmap.createBitmap(86, 59, conf);
                            Canvas canvas1 = new Canvas(bmp);
                            Paint color = new Paint();
                            color.setTextSize(35);
                            color.setColor(Color.BLACK);//modify canvas
                            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.pin3), 0, 0, color);
                            canvas1.drawBitmap(imagenes.get(lastIndex), 3, 2, color);

                            Marker marker = map.addMarker(new MarkerOptions().position(latlong)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                    .anchor(0.5f, 1).title("Nueva fotografía"));

                            data.put(marker, foto);

                            lastSize = imagenes.size();
                            //queue.execute(new ImageUploader(activity, queue, imageItem, 0));
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

            try {
                Message msg = Message.obtain(null, SvtService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
                attached = true;
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
            if (ruta != null) {
                try {
                    Message msg = Message.obtain(null, SvtService.MSG_SET_INT_VALUE);
                    msg.replyTo = mMessenger;
                    msg.arg1 = (int) ruta.id;
                    mService.send(msg);
                    //System.out.println("Mando mensaje de ruta");
                    attached = true;
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


    /*IMAGES*/
    public void setImageIndex(int index) {
        //System.out.println("set image index " + index);
        if (imagenes == null) {
            imagenes = new ArrayList<Bitmap>();
        }
        if (index >= lastestImageIndex) {
            this.lastestImageIndex = index;
        } else {
            /*Borro una foto*/
            this.lastestImageIndex = 0;
        }
        //System.out.println("set image index fin "+this.lastestImageIndex);
    }

    /*Fotos*/

    public void getFoto() {
        if (imageItem != null) {
            // System.out.println("path "+imageItem.imagePath);
            //System.out.println("images " + imagenes);
            Bitmap b = ImageUtils.decodeFile(imageItem.imagePath);
            //System.out.println("width " + b.getWidth() + "  " + b.getHeight());
            imagenes.add(b);
            lastIndex++;
            lastestImageIndex = 0;
        }

    }

    public Bitmap getFotoDialog(Foto image, int width, int heigth) {
        if (image != null) {
            // System.out.println("path "+imageItem.imagePath);
            //System.out.println("images " + image.imagePath+"  "+width+"  "+heigth);
            Bitmap b = ImageUtils.decodeFile(image.path, width, heigth);
            return b;

        }
        return null;

    }

    public ImageItem getLatestItem() {
        // set vars
        if (lastestImageIndex > 0) {
            ImageItem item = null;
            String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.MINI_THUMB_MAGIC};

            Uri image = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, lastestImageIndex);
            Cursor cursor = this.managedQuery(image, columns, null, null, null);

            // check if cursus has rows, if not break and exit loop
            if (cursor.moveToFirst()) {
                //System.out.println("tiene rows "+cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MINI_THUMB_MAGIC)));
                item = new ImageItem();
                item.prefs = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
                item.imageId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                item.imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                item.imageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                item.imageType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                item.imageSize = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            }
            //cursor.close();

            // System.out.println("salio");
            return item;
        }

        return null;

    }


    /*DRAWER*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.search_btn_label).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.search_btn_label:
                Fragment fragment = new BusquedaFragment();
                Bundle args = new Bundle();
                //args.putString("pathFolder", pathFolder);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
                mainLayout.setVisibility(LinearLayout.GONE);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.content_frame, fragment)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // System.out.println("entro?");
            selectItem(position);
        }
    }

    public void selectItem(int position) {
        selectItem(position, true);
    }

    public void selectItem(int position, boolean drawer) {
        // update the main content by replacing fragments
        //System.out.println("pos? "+position);
        Utils.hideSoftKeyboard(this);
        Fragment fragment;
        switch (position) {
            case MAP_POS:
                // fragment = new NthMapFragment();

                //System.out.println("map?");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(fragmentManager.findFragmentById(R.id.content_frame))
                        .addToBackStack("")
                        .commit();
                RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
                mainLayout.setVisibility(View.VISIBLE);

                fragment = null;
                break;
            case CAPTURA_POS:
//                System.out.println(":::::cientifico:::: " + esCientifico);
                if (esCientifico.trim().equals("-1")) {
                    fragment = new CapturaTuristaFragment();
                } else {
                    fragment = new CapturaCientificoFragment();
                }
                this.addListener((FieldListener) fragment);
                break;
            case ENCYCLOPEDIA_POS:
                fragment = new EncyclopediaFragment();
                break;
            case GALERIA_POS:
                fragment = new GaleriaFragment();
                break;
            case RUTAS_POS:
                fragment = new RutasFragment();
                break;
            case SETTINGS_POS:
                fragment = new SettingsFragment();
                this.addListener((FieldListener) fragment);
                break;
            case LOGIN_POS:
                fragment = new Loginfragment();
                this.addListener((FieldListener) fragment);
                break;
            default:
                fragment = null;
                break;
        }
        if (fragment != null) {
            // System.out.println("fragment "+fragment);
//            Bundle args = new Bundle();
            //args.putString("pathFolder", pathFolder);
//            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
            mainLayout.setVisibility(LinearLayout.GONE);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack("")
                    .commit();
//            RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
//            mainLayout.setVisibility(LinearLayout.GONE);
//            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.replace(R.id.content_frame, fragment);
//            transaction.addToBackStack(null);
//            transaction.commit();

            // fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer

        }
        setTitle(mOptionsArray[position]);
        if (drawer) {
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /*Funcion para ver rutas desde el list*/
    public void openRutaFragment(Ruta ruta) {
        this.ruta = ruta;
        this.old_id = null;
        Fragment fragment = new RutaFragment();
        if (fragment != null) {
            this.addListener((FieldListener) fragment);
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
            mainLayout.setVisibility(LinearLayout.GONE);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack("")
                    .commit();
        }
        setTitle(ruta.descripcion);
    }

    public void showRuta(List<Coordenada> cords, List<Foto> fotos) {
        map.clear();
        data.clear();
        location = new LatLng(cords.get(0).getLatitud(), cords.get(0).getLongitud());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 19);
        map.animateCamera(update);
        polyLine = map.addPolyline(rectOptions);
        for (int i = 0; i < fotos.size(); i++) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(86, 59, conf);
            Canvas canvas1 = new Canvas(bmp);
            Paint color = new Paint();
            color.setTextSize(35);
            color.setColor(Color.BLACK);//modify canvas
            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.pin3), 0, 0, color);
            Bitmap b = ImageUtils.decodeFile(fotos.get(i).path);
            canvas1.drawBitmap(b, 3, 2, color);
            Coordenada co = fotos.get(i).getCoordenada(activity);
            location = new LatLng(co.getLatitud(), co.getLongitud());
            Marker marker = map.addMarker(new MarkerOptions().position(location)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                    .anchor(0.5f, 1).title("Nueva fotografía"));
            data.put(marker, fotos.get(i));
        }
        for (int i = 0; i < cords.size(); i++) {
            updatePolyLine(new LatLng(cords.get(i).getLatitud(), cords.get(i).getLongitud()));
            if (i == cords.size() - 1) {
                map.addMarker(new MarkerOptions().position(new LatLng(cords.get(i).getLatitud(), cords.get(i).getLongitud())).title("Última posición registrada"));
            }
        }
        showMap();

    }

    public void showMap() {
        selectItem(MAP_POS);
    }

    private void makeMeRequest(final Session session) {
        System.out.println("get session map activity " + session + " ");
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            System.out.println("get session map  activity active " + session + " " + user);
                            if (user != null) {
                                System.out.println("username " + user.getUsername() + " name  " + user.getName());
                                SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("user", user.getId());
                                editor.putString("login", user.getUsername());
                                editor.putString("name", user.getName());
                                editor.putString("type", "facebook");
                                userId = user.getId();
                                name = user.getName();
                                type = "facebook";
                                editor.commit();

                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();
    }

    public void mostrarEspecie(Especie especie){
        map.clear();
        List<Entry> entry = Entry.findAllByEspecie(this,especie);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = (100);
        PolygonOptions poly = new PolygonOptions();
        int fillColor = Color.argb(150,68,134,246);
        poly.strokeColor(fillColor);
        poly.fillColor(fillColor);

        List<LatLng> puntos = new ArrayList<LatLng>();
        List<LatLng> puntosfinal  = new ArrayList<LatLng>();
        for(int i = 0;i<entry.size();i++){
            Entry current = entry.get(i);
            List<Foto> fotos = Foto.findAllByEntry(activity,current);
            for(int j=0;j<fotos.size();j++){
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(86, 59, conf);
                Canvas canvas1 = new Canvas(bmp);
                Paint color = new Paint();
                color.setTextSize(35);
                color.setColor(Color.BLACK);//modify canvas
                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.pin3), 0, 0, color);
                Bitmap b = ImageUtils.decodeFile(fotos.get(j).path);
                canvas1.drawBitmap(b, 3, 2, color);
                Coordenada co = fotos.get(j).getCoordenada(activity);
                location = new LatLng(co.getLatitud(), co.getLongitud());
                puntos.add(location);
                builder.include(location);
                Marker marker = map.addMarker(new MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .anchor(0.5f, 1).title(getString(R.string.map_activity_captura)+" "+fotos.get(j).fecha));
                // Marker marker = map.addMarker(new MarkerOptions().position(location)
                //       .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                //     .anchor(0.5f, 1).title(" "+location.latitude+" , "+location.longitude));
                //marker.showInfoWindow();
                data.put(marker, fotos.get(j));
            }
        }
        if(puntos.size()>2){
            LatLng masAlto=null;
            LatLng masBajo=null;
            LatLng masDer=null ;
            LatLng masIzq=null;
            double maxX=0;
            double minX=0;
            double minY=0;
            double maxY=0;
            for(int i = 0;i<puntos.size();i++){
                double x1 = (puntos.get(i).longitude + 180) * 360;
                double y1  = (puntos.get(i).latitude + 90) * 180;
                if(minX==0){
                    minX=x1;
                    masIzq=puntos.get(i);
                }
                if(minY==0){
                    minY=y1;
                    masBajo=puntos.get(i);
                }
                if(maxX==0){
                    maxX=x1;
                    masDer=puntos.get(i);
                }
                if(maxY==0){
                    maxY=y1;
                    masAlto=puntos.get(i);
                }
                if(x1>maxX) {
                    maxX = x1;
                    masDer=puntos.get(i);
                }
                if(y1>maxY) {
                    maxY = y1;
                    masAlto=puntos.get(i);
                }
                if(x1<minX) {
                    minX = x1;
                    masIzq=puntos.get(i);
                }
                if(y1<minY) {
                    minY =y1 ;
                    masBajo=puntos.get(i);
                }

            }
            System.out.println("mas alto "+masAlto.latitude+" , "+masDer.longitude);
            System.out.println("mas izq "+masIzq.latitude+" , "+masIzq.longitude);
            System.out.println("mas bajo "+masBajo.latitude+" , "+masDer.longitude);
            System.out.println("mas der "+masDer.latitude+" , "+masDer.longitude);

            puntosfinal.add(masAlto);
            puntosfinal.add(masIzq);
            puntosfinal.add(masBajo);
            puntosfinal.add(masDer);
            double maxDis = distancia(masAlto,masBajo);
            System.out.println("despues ----------- ");


            for(int i = 0;i<puntosfinal.size()-1;i++) {
                LatLng current = puntosfinal.get(i);
                poly.add(current);
                LatLng next = puntosfinal.get(i + 1);
                double x1 = (current.longitude + 180) * 360;
                double y1  = (current.latitude + 90) * 180;
                double x2 = (next.longitude + 180) * 360;
                double y2  = (next.latitude + 90) * 180;
                LatLng nuevo = null;
                double nuevoX=0;
                switch (i){
                    case 0:
                        nuevo = null;
                        nuevoX=0;
                        // System.out.println("current "+current.latitude+" ; "+current.longitude+"  --->  "+x1+" ; "+y1);
                        //System.out.println("next "+next.latitude+" ; "+next.longitude+"  --->  "+x2+" ; "+y2);
                        for (int j =0;j<puntos.size();j++){
                            if(puntos.get(j)!=masIzq){
                                double x3 = (puntos.get(j).longitude + 180) * 360;
                                double y3  = (puntos.get(j).latitude + 90) * 180;
                                // System.out.println("ietracion "+puntos.get(j).latitude+" ; "+puntos.get(j).longitude+"  --->  "+x3+" ; "+y3);
                                if(x3<x1 && y2<y3){
                                    //System.out.println("paso es mas!");
                                    if(nuevo==null) {
                                        nuevo = puntos.get(j);
                                        nuevoX=x3;
                                    }else{
                                        if(x3<nuevoX){
                                            nuevo = puntos.get(j);
                                            nuevoX=x3;
                                        }
                                    }
                                }
                            }

                        }
                        if(nuevo!=null)
                            poly.add(nuevo);
                        break;
                    case 1:
                        nuevo = null;
                        nuevoX=0;
                        // System.out.println("current "+current.latitude+" ; "+current.longitude+"  --->  "+x1+" ; "+y1);
                        //System.out.println("next "+next.latitude+" ; "+next.longitude+"  --->  "+x2+" ; "+y2);
                        for (int j =0;j<puntos.size();j++){
                            if(puntos.get(j)!=masBajo){
                                double x3 = (puntos.get(j).longitude + 180) * 360;
                                double y3  = (puntos.get(j).latitude + 90) * 180;
                                // System.out.println("ietracion "+puntos.get(j).latitude+" ; "+puntos.get(j).longitude+"  --->  "+x3+" ; "+y3);
                                if(x3<x2 && y3>y2){
                                    //System.out.println("paso es mas!");
                                    if(nuevo==null) {
                                        nuevo = puntos.get(j);
                                        nuevoX=x3;
                                    }else{
                                        if(x3<nuevoX){
                                            nuevo = puntos.get(j);
                                            nuevoX=x3;
                                        }
                                    }
                                }
                            }

                        }
                        if(nuevo!=null)
                            poly.add(nuevo);
                        break;
                    case 2:
                        nuevo = null;
                        nuevoX=0;
                        // System.out.println("current "+current.latitude+" ; "+current.longitude+"  --->  "+x1+" ; "+y1);
                        //System.out.println("next "+next.latitude+" ; "+next.longitude+"  --->  "+x2+" ; "+y2);
                        for (int j =0;j<puntos.size();j++){
                            if(puntos.get(j)!=masAlto){
                                double x3 = (puntos.get(j).longitude + 180) * 360;
                                double y3  = (puntos.get(j).latitude + 90) * 180;
                                // System.out.println("ietracion "+puntos.get(j).latitude+" ; "+puntos.get(j).longitude+"  --->  "+x3+" ; "+y3);
                                if(x3>x1 && y3>y1){
                                    //System.out.println("paso es mas!");
                                    if(nuevo==null) {
                                        nuevo = puntos.get(j);
                                        nuevoX=x3;
                                    }else{
                                        if(x3>nuevoX){
                                            nuevo = puntos.get(j);
                                            nuevoX=x3;
                                        }
                                    }
                                }
                            }

                        }
                        if(nuevo!=null)
                            poly.add(nuevo);
                        break;
                }

            }
            Polygon polygon = map.addPolygon(poly);
        }


        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
        showMap();
        setTitle(getString(R.string.captura_nombre_especie_label)+": "+especie.nombre);

    }

    public double  pendiente(LatLng p1,LatLng p2){
        double x1 = (p1.longitude + 180) * 360;
        double y1  = (p1.latitude + 90) * 180;
        double x2 = (p2.longitude + 180) * 360;
        double y2  = (p2.latitude + 90) * 180;
        double dX = x2 - x1;
        double dY = y2 - y1;
        return dY / dX;
    }
    public double  distancia(LatLng p1,LatLng p2){
        double x1 = (p1.longitude + 180) * 360;
        double y1  = (p1.latitude + 90) * 180;
        double x2 = (p2.longitude + 180) * 360;
        double y2  = (p2.latitude + 90) * 180;
        double dX = x2 - x1;
        double dY = y2 - y1;
        return Math.sqrt((dX*dX) + (dY*dY));
    }

    public int  pointSort(LatLng p1, LatLng p2,LatLng upper) {
        // Exclude the 'upper' point from the sort (which should come first).
        if(p1 == upper) return -1;
        if(p2 == upper) return 1;

        // Find the slopes of 'p1' and 'p2' when a line is
        // drawn from those points through the 'upper' point.
        double m1 = pendiente(p1,upper);
        double m2 = pendiente(p2,upper);

        // 'p1' and 'p2' are on the same line towards 'upper'.
        if(m1 == m2) {
            // The point closest to 'upper' will come first.
//            return p1.distance(upper) < p2.distance(upper) ? -1 : 1;
            return  (distancia(p1,upper) < distancia(p2,upper))?-1:1;
        }

        // If 'p1' is to the right of 'upper' and 'p2' is the the left.
        if(m1 <= 0 && m2 > 0) return -1;

        // If 'p1' is to the left of 'upper' and 'p2' is the the right.
        if(m1 > 0 && m2 <= 0) return 1;

        // It seems that both slopes are either positive, or negative.
        return m1 > m2 ? -1 : 1;
    }

    public float updateAchivement( int tipo) {
        float res = 0;
        String nombre = "";
        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);

        switch (tipo) {
            case ACHIEV_FOTOS:
                nombre = "fotos";
                res = (float) settings.getInt(nombre, 0);
                res = res + 1;
                break;
            case ACHIEV_DISTANCIA:
                nombre = "distancia";
                res = settings.getFloat(nombre, 0);
                res = res + 1;
                break;
            case ACHIEV_UPLOADS:
                nombre = "uploads";
                res = (float) settings.getInt(nombre, 0);
                res = res + 1;
                break;
            case ACHIEV_SHARE:
                nombre = "shares";
                res = settings.getInt(nombre, 0);
                res = res + 1;
                break;
            default:
                res=0;

        }
        SharedPreferences.Editor editor = settings.edit();
        if (!nombre.equals("distancia")) {
            editor.putInt(nombre, (int) res);
        } else {
            editor.putFloat(nombre, res);
        }
        editor.commit();
        checkAchiev(tipo,res);
        return res;

    }

    public void checkAchiev(int tipo, float cant) {
        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
        if(settings.getInt("logros", 0) == 1) {
            ExecutorService queue = Executors.newSingleThreadExecutor();
            queue.execute(new AchievementChecker(this, tipo, cant));
        }
    }
    public float getAchievement(int tipo){
        float res = 0;
        String nombre = "";
        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);

        switch (tipo) {
            case ACHIEV_FOTOS:
                nombre = "fotos";
                res = (float) settings.getInt(nombre, 0);

                break;
            case ACHIEV_DISTANCIA:
                nombre = "distancia";
                res = settings.getFloat(nombre, 0);

                break;
            case ACHIEV_UPLOADS:
                nombre = "uploads";
                res = (float) settings.getInt(nombre, 0);

                break;
            case ACHIEV_SHARE:
                nombre = "shares";
                res = settings.getInt(nombre, 0);

                break;
            default:
                return 0;
        }
        return res;
    }

//    @Override
//    public void onBackPressed() {
////        System.out.println("on back pressed");
//        final Fragment fragment = getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
//
//        if (fragment.allowBackPressed()) { // and then you define a method allowBackPressed with the logic to allow back pressed or not
//            super.onBackPressed();
//        }
//    }
}