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
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.nth.ikiam.db.*;
import com.nth.ikiam.image.*;
import com.nth.ikiam.listeners.FieldListener;
import com.nth.ikiam.utils.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.moodstocks.android.Scanner;
import com.moodstocks.android.MoodstocksError;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends Activity implements Button.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, Scanner.SyncListener {
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
    public final int GALERIA_POS = -1;
    public final int RUTAS_POS = 3;
    public final int IKIAM_WEB_POS = 4;
    public final int NOTEPAD_POS = 5;
    public final int NOTA_POS = 6;
    public final int SETTINGS_POS = 7;
    public final int LOGIN_POS = 8;

    public final int MAP_POS_T = 0;
    public final int CAPTURA_POS_T = 1;
    public final int ENCYCLOPEDIA_POS_T = 2;
    public final int GALERIA_POS_T = 3;
    public final int RUTAS_POS_T = 4;
    public final int IKIAM_WEB_POS_T = 5;
    public final int NOTEPAD_POS_T = 6;
    public final int NOTA_POS_T = 7;
    public final int SETTINGS_POS_T = 8;
    public final int LOGIN_POS_T = 9;
    public final int SCANER_POS = 10;

    public final int TOOLS_POS = 17;
    public final int BUSQUEDA_POS = 18;

    public int activeFragment = 0;

    public final int ACHIEV_FOTOS = 1;
    public final int ACHIEV_DISTANCIA = 2;
    public final int ACHIEV_UPLOADS = 3;
    public final int ACHIEV_SHARE = 4;

    public final int INTENT_LOGRO = 1;

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
    HashMap<Marker, Foto> dataUsuario;
    HashMap<Marker, AtraccionUi> atracciones;
    HashMap<Marker, EspecieUi> especies;
    HashMap<Marker, SocialUi> social;
    HashMap<Marker, Bitmap> fotosUsuario;
    Marker selected;
    int tipoMapa = 0;
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
    public String titulo;
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
    private boolean pendingPublishReauthorization = false;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    List<FieldListener> listeners = new ArrayList<FieldListener>();

    public List<Entry> entriesBusqueda;
    public List<Especie> especiesBusqueda;
    public List<Entry> entriesBusquedaNuevo;
    public Marker markerSubir;
    public LatLng posicionSubir;
    public String strEspeciesList;
    public AtraccionUi atraccion;

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

    public void showDownloadedEspecies(final String msg, final ProgressDialog progressDialog) {
        final Activity a = this;
        final MapActivity mapAct = (MapActivity) a;
        if (a != null) {
            a.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressDialog.hide();
                    Fragment fragment = new BusquedaDownloadResultsFragment();
                    Utils.openFragment(mapAct, fragment, getString(R.string.descarga_busqueda_download_title));
//                    FragmentManager fragmentManager = getFragmentManager();
//                    RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.rl2);
//                    mainLayout.setVisibility(LinearLayout.GONE);
//                    fragmentManager.beginTransaction()
//                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                            .replace(R.id.content_frame, fragment)
//                            .addToBackStack("")
//                            .commit();
                }
            });
        }
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            makeMeRequest(session);
            if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                pendingPublishReauthorization = false;
                //System.out.println("run del publish");
                publishStory();
            }
        } else {


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
            System.out.println("paso?");
            //AQUI ACHIEVEMENT
            updateAchievement(this.ACHIEV_SHARE);
            Bundle postParams = new Bundle();
            postParams.putString("name", getString(R.string.share_name));
            postParams.putString("caption", getString(R.string.share_caption));
            postParams.putString("description", getString(R.string.share_description));
            postParams.putString("link", this.atraccion.url);
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
                        Toast.makeText(getApplicationContext(),
                                error.getErrorMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Atraccion compartida exitosamente",
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

    /*moodstock*/
    private static final String API_KEY = "mfpiet0szwumqvp5bati";
    private static final String API_SECRET = "soulgMN1H2Epn0zB";

    private boolean compatible = false;
    private Scanner scanner;

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
        titulo = settings.getString("titulo", "-1");
        //System.out.println("variables name "+userId+"  name "+name);
        setContentView(R.layout.activity_map);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Session session;
        if (type.equals("-1") || type.equalsIgnoreCase("facebook")) {
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
        dataUsuario = new HashMap<Marker, Foto>();
        atracciones = new HashMap<Marker, AtraccionUi>();
        especies = new HashMap<Marker, EspecieUi>();
        social = new HashMap<Marker, SocialUi>();
        fotosUsuario = new HashMap<Marker, Bitmap>();

        botones = new Button[9];
        botones[0] = (Button) this.findViewById(R.id.btnGalapagos);
        botones[1] = (Button) this.findViewById(R.id.btnService);
        botones[2] = (Button) this.findViewById(R.id.btnAtraccion);
        botones[3] = (Button) this.findViewById(R.id.btnEspecies);
        botones[4] = (Button) this.findViewById(R.id.btnCamara);
        botones[5] = (Button) this.findViewById(R.id.btnLimpiar);
        botones[6] = (Button) this.findViewById(R.id.btnTools);
        botones[7] = (Button) this.findViewById(R.id.btnTipo);
        botones[8] = (Button) this.findViewById(R.id.btnSocial);
//        System.out.println("TYPE::::: " + type + "    " + type.equalsIgnoreCase("Ikiam"));
//        System.out.println("ES CIENTIFICO::::: >" + esCientifico.trim() + "<     >" + esCientifico.equalsIgnoreCase("S") + "<");
        if (type.equalsIgnoreCase("Ikiam")) {
            if (esCientifico.trim().equalsIgnoreCase("S")) {
//                System.out.println("TYPE::::: " + type);
//                System.out.println("ES CIENTIFICO::::: " + esCientifico);
                botones[2].setVisibility(View.GONE);
                botones[3].setVisibility(View.GONE);
                botones[8].setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < botones.length; i++) {
            botones[i].setOnClickListener(this);
        }
        restoreMe(savedInstanceState);
        CheckIfServiceIsRunning();
        /*fin*/

        /*DRAWER*/
        mTitle = mDrawerTitle = getTitle();
        if (!esCientifico.trim().equals("S")) {
            mOptionsArray = getResources().getStringArray(R.array.options_array_turista);
        } else {
            mOptionsArray = getResources().getStringArray(R.array.options_array_cientifico);

        }

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

        /* INTENT */
        Intent intent = getIntent();
        try {
            String action = intent.getAction();
//            System.out.println("8888888888888888888888 " + action);
            if (action != null) {
                try {
                    int actionId = Integer.parseInt(action);
                    switch (actionId) {
                        case INTENT_LOGRO:
                            Fragment fragment = new LogrosFragment();
                            Utils.openFragment(this, fragment, getString(R.string.logro_title));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                selectItem(Integer.parseInt(action), false);
//                if (action.equalsIgnoreCase(getResources().getString(R.string.notification_action_friend))) {
//                    goFrag(getResources().getInteger(R.integer.FRAG_A_INT));
//                }
//                if (action.equalsIgnoreCase(getResources().getString(R.string.notification_action_article))) {
//                    goFrag(getResources().getInteger(R.integer.FRAG_B_INT));
//                }
//                if (action.equalsIgnoreCase(getResources().getString(R.string.notification_action_points))) {
//                    goFrag(getResources().getInteger(R.integer.FRAG_C_INT));
//                }
//                if (action.equalsIgnoreCase(getResources().getString(R.string.notification_action_redeemable))) {
//                    goFrag(getResources().getInteger(R.integer.FRAG_D_INT));
//                }
//                if (action.equalsIgnoreCase(getResources().getString(R.string.notification_action_dance))) {
//                    goFrag(getResources().getInteger(R.integer.FRAG_E_INT));
//                }
            } else {
                Log.d("ERROR", "Intent was null");
            }
        } catch (Exception e) {
            Log.e("ERROR", "Problem consuming action from intent", e);
        }
        /*moodstock*/
        compatible = Scanner.isCompatible();
        System.out.println("compatible " + compatible);
        if (compatible) {
            try {
                scanner = Scanner.get();
                String path = Scanner.pathFromFilesDir(this, "scanner.db");
                scanner.open(path, API_KEY, API_SECRET);
                scanner.setSyncListener(this);
                scanner.sync();
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }


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
        if (compatible) {
            try {
                scanner.close();
                scanner.destroy();
                scanner = null;
            } catch (MoodstocksError e) {
                e.printStackTrace();
            }
        }
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
                    //ACHIEVEMENT CHEcK
                    double dist = UtilsDistancia.calculaDistancia(activity, ruta);
                    updateAchievement(ACHIEV_DISTANCIA, (float) dist);
                    this.stopService(new Intent(this, SvtService.class));
                    botones[1].setText("Nueva ruta");
                    ruta = null;
                    status = false;
                }
            }

        }

        if (v.getId() == botones[2].getId()) {
            map.clear();
            //Location mCurrentLocation;
            // mCurrentLocation = locationClient.getLastLocation();
            atracciones.clear();
            especies.clear();
            social.clear();
            selected = null;
            //System.out.println("Altura "+ mCurrentLocation.getAltitude());
            location = new LatLng(-1.6477220517969353, -78.46435546875);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 7);
            map.animateCamera(update);
            ExecutorService queue = Executors.newSingleThreadExecutor();
            queue.execute(new AtraccionDownloader(this, queue, 0));
        }

        if (v.getId() == botones[3].getId()) {
            map.clear();

            atracciones.clear();
            especies.clear();
            social.clear();
            selected = null;
            //System.out.println("Altura "+ mCurrentLocation.getAltitude());
            location = new LatLng(-1.6477220517969353, -78.46435546875);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 7);
            map.animateCamera(update);
            ExecutorService queue = Executors.newSingleThreadExecutor();
            queue.execute(new EspeciesDownloader(this, queue, 0));
        }
        if (v.getId() == botones[4].getId()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
        if (v.getId() == botones[5].getId()) {
            map.clear();
        }
        if (v.getId() == botones[6].getId()) {
            Fragment fragment = new ToolsFragment();
            Utils.openFragment(this, fragment, getString(R.string.map_tools));
//            FragmentManager fragmentManager = getFragmentManager();
//            RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
//            mainLayout.setVisibility(LinearLayout.GONE);
//            fragmentManager.beginTransaction()
//                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                    .replace(R.id.content_frame, fragment)
//                    .addToBackStack("")
//                    .commit();
//            setTitle(getString(R.string.map_tools));
            activeFragment = TOOLS_POS;
        }
        if (v.getId() == botones[7].getId()) {
            tipoMapa++;
            switch (tipoMapa) {
                case 0:
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;
                case 1:
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case 2:
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
                case 3:
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                default:
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    tipoMapa = 0;
                    break;
            }
        }
        if (v.getId() == botones[8].getId()) {
            map.clear();
            Location mCurrentLocation;
            mCurrentLocation = locationClient.getLastLocation();
            atracciones.clear();
            especies.clear();
            social.clear();
            selected = null;
            //System.out.println("Altura "+ mCurrentLocation.getAltitude());
            location = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 9);
            map.animateCamera(update);
            ExecutorService queue = Executors.newSingleThreadExecutor();
            /*aqui cambiar por el buen downloader*/
            queue.execute(new SocialDownloader(this, queue, 0));
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

    public void setPing(final String title, final int likes, final double latitud, final double longitud, final Bitmap foto, final Bitmap fotoDialog, final String url, final String descripcion) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final LatLng pos = new LatLng(latitud, longitud);
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(170, 126, conf);
                Canvas canvas1 = new Canvas(bmp);
                Paint color = new Paint();
                color.setTextSize(35);
                color.setColor(Color.BLACK);//modify canvas
                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.pin3), 0, 0, color);
                canvas1.drawBitmap(foto, 5, 4, color);
                Marker marker = map.addMarker(new MarkerOptions().position(pos)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .anchor(0.5f, 1).title(title));
                AtraccionUi atraccion = new AtraccionUi(title, fotoDialog, likes, url, descripcion);
                atracciones.put(marker, atraccion);
            }
        });

    }

    public void setPingEspecie(final String title, final int likes, final double latitud, final double longitud, final Bitmap foto, final Bitmap fotoDialog, final String desc, final String nombreEspecie) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final LatLng pos = new LatLng(latitud, longitud);
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(170, 126, conf);
                Canvas canvas1 = new Canvas(bmp);
                Paint color = new Paint();
                color.setTextSize(10);
                color.setColor(Color.BLACK);//modify canvas
                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.pin3), 0, 0, color);
                canvas1.drawBitmap(foto, 5, 4, color);
                Marker marker = map.addMarker(new MarkerOptions().position(pos)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .title(title));
                EspecieUi especieUi = new EspecieUi(title, nombreEspecie, fotoDialog, likes, desc);
                especies.put(marker, especieUi);
            }
        });

    }

    public void setPingSocial(final String id, final int likes, final double latitud, final double longitud, final Bitmap foto, final Bitmap fotoDialog, final String comentario, final String usuario) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final LatLng pos = new LatLng(latitud, longitud);
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(170, 126, conf);
                Canvas canvas1 = new Canvas(bmp);
                Paint color = new Paint();
                color.setTextSize(35);
                color.setColor(Color.BLACK);//modify canvas
                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.pin3), 0, 0, color);
                canvas1.drawBitmap(foto, 5, 4, color);
                Marker marker = map.addMarker(new MarkerOptions().position(pos)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .anchor(0.5f, 1).title(""));
                SocialUi socialUi = new SocialUi(id, comentario, usuario, fotoDialog, likes);
                social.put(marker, socialUi);
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
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 7);
        map.setMyLocationEnabled(true);
        map.animateCamera(update);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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

                if (dataUsuario.get(marker) != null) {
                    System.out.println("click data usuario");
                    marker.showInfoWindow();
                    LayoutInflater inflater = activity.getLayoutInflater();
                    myView = inflater.inflate(R.layout.dialog_usuario, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.map_activity_nuevaFoto);
                    builder.setView(myView);
                    if (dataUsuario.get(marker).uploaded != 1) {
                        builder.setPositiveButton(R.string.dialog_btn_descargar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
//                            imagePathUpload = data.get(marker).path;
                                imageToUpload = dataUsuario.get(marker);
//                            dialog.dismiss();
//                            System.out.println("***************** ANTES: " + imageToUpload.path);
//                            System.out.println("*************** MAPA*************** " + imageToUpload.getCoordenada(context).latitud);
                                selectItem(CAPTURA_POS, false);
                            }
                        });
                    }

                    builder.setNegativeButton(R.string.dialog_btn_cerrar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }


                    });

                    builder.setNeutralButton(R.string.dialog_btn_borrar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dataUsuario.get(marker).delete();
                            dataUsuario.remove(marker);
                            marker.remove();
                            dialog.dismiss();
                        }


                    });
                    dialog = builder.create();
                    ImageView img = (ImageView) myView.findViewById(R.id.image_usuairo);
                    if (titulo != null) {
                        if (!titulo.equals("") && !titulo.equals("-1")) {
                            ((TextView) myView.findViewById(R.id.usuario_lbl)).setText(getString(R.string.foto_por) + " " + name + ", " + titulo);
                        } else {
                            ((TextView) myView.findViewById(R.id.usuario_lbl)).setText(getString(R.string.foto_por) + " " + name);
                        }
                    } else {
                        ((TextView) myView.findViewById(R.id.usuario_lbl)).setText(getString(R.string.foto_por) + " " + name);
                    }


                    img.setImageBitmap(getFotoDialog(dataUsuario.get(marker), screenWidth, 300));
                    dialog.show();

                    return true;
                }

                if (data.get(marker) != null) {
                    System.out.println("click data");
                    marker.showInfoWindow();
                    LayoutInflater inflater = activity.getLayoutInflater();
                    myView = inflater.inflate(R.layout.dialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.map_activity_nuevaFoto);
                    builder.setView(myView);
                    if (data.get(marker).uploaded != 1) {
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
                    }

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
//                    System.out.println("click  atracc " + marker + "  " + selected + "  " + (selected != marker));
                    if (selected == null) {
                        selected = marker;
                    } else {
                        if (selected.getId().equals(marker.getId())) {
                            selected = null;
                            final AtraccionUi current = atracciones.get(marker);
                            atraccion = current;
                            LayoutInflater inflater = activity.getLayoutInflater();
                            myView = inflater.inflate(R.layout.especie_map_dialog, null);
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

                            if (type.equalsIgnoreCase("facebook")) {
                                String label = getString(R.string.ruta_subir);
                                builder.setNeutralButton(label, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        publishStory();
                                        dialog.dismiss();
                                    }


                                });

                            }

                            dialog = builder.create();
                            ImageView img = (ImageView) myView.findViewById(R.id.especie_info_dialog_image);
                            img.setImageBitmap(current.foto);
                            TextView txt = (TextView) myView.findViewById(R.id.especie_info_dialog_comentarios);
                            txt.setText(current.descripcion);
                            dialog.show();
                            if (type.equalsIgnoreCase("facebook")) {
                                Button bq = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                                bq.setBackgroundColor(Color.BLUE);
                                bq.setTextColor(Color.WHITE);
                            }
                        } else {
                            selected = marker;
                        }
                    }
                    return true;
                }
                if (especies.get(marker) != null) {
                    marker.showInfoWindow();
                    //System.out.println("especie " + marker + "  " + selected + "  " + (selected != marker));
                    if (selected == null) {
                        selected = marker;
                    } else {
                        if (selected.getId().equals(marker.getId())) {
                            selected = null;
                            final EspecieUi current = especies.get(marker);
                            LayoutInflater inflater = activity.getLayoutInflater();
                            myView = inflater.inflate(R.layout.especie_map_dialog, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle(current.nombre);
                            builder.setView(myView);
                            builder.setPositiveButton(R.string.map_activity_dialog_mas, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String url = UtilsUploaders.getIp() + "especie/show?nombre=" + especies.get(marker).nombreEspecie;
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
                            ImageView img = (ImageView) myView.findViewById(R.id.especie_info_dialog_image);
                            img.setImageBitmap(current.foto);
                            TextView txt = (TextView) myView.findViewById(R.id.especie_info_dialog_comentarios);
                            txt.setText(current.desc);
                            dialog.show();
                        } else {
                            selected = marker;
                        }
                    }
                    return true;
                }
                if (social.get(marker) != null) {
                    marker.showInfoWindow();
                    if (marker.getId().equals(marker.getId())) {
                        //selected = null;
                        final SocialUi current = social.get(marker);
                        LayoutInflater inflater = activity.getLayoutInflater();
                        myView = inflater.inflate(R.layout.dialog_usuario, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("");
                        builder.setView(myView);
                        builder.setPositiveButton(R.string.map_comentar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String url = UtilsUploaders.getIp() + "entry/comment/" + social.get(marker).id + "?usuario=" + userId;
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
                        builder.setNeutralButton(R.string.map_reportar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                           /*aqui implementar like*/

                                String url = UtilsUploaders.getIp() + "entry/comment/" + social.get(marker).id + "?usuario=" + userId;
                                try {
                                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(myIntent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }


                        });
                        dialog = builder.create();
                        ImageView img = (ImageView) myView.findViewById(R.id.image_usuairo);
                        img.setImageBitmap(current.foto);
                        TextView txt = (TextView) myView.findViewById(R.id.usuario_lbl);
                        txt.setText(getString(R.string.foto_por) + " " + current.usuario);
                        //System.out.println("comentario "+current.comentario);
                        ((TextView) myView.findViewById(R.id.comentario_usuario)).setText(current.comentario);
                        dialog.show();

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
        if (markerSubir.isInfoWindowShown())
            markerSubir.hideInfoWindow();
        markerSubir.remove();
        markerSubir = null;
        fotoSinCoords = null;
    }

    @Override
    public void onSyncStart() {
        System.out.println("Moodstocks SDK Sync will start.");
    }

    @Override
    public void onSyncComplete() {
        try {
            System.out.println("Moodstocks SDK Sync succeeded (" + scanner.count() + " images)");
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSyncFailed(MoodstocksError e) {
        System.out.println("Moodstocks SDK Sync error #" + e.getErrorCode() + ": " + e.getMessage());
    }

    @Override
    public void onSyncProgress(int total, int current) {
        int percent = (int) ((float) current / (float) total * 100);
        System.out.println("Moodstocks SDK Sync progressing: " + percent + "%");
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
                            Bitmap bmp = Bitmap.createBitmap(170, 126, conf);
                            ;
                            Canvas canvas1 = new Canvas(bmp);
                            Paint color = new Paint();
                            color.setTextSize(35);
                            color.setColor(Color.BLACK);//modify canvas
                            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.pin3), 0, 0, color);
                            canvas1.drawBitmap(imagenes.get(lastIndex), 5, 4, color);

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
            Bitmap b = ImageUtils.decodeBitmapPath(imageItem.imagePath, 160, 90);
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
                Utils.openFragment(this, fragment, getString(R.string.busqueda_title), args);
                //args.putString("pathFolder", pathFolder);
//                fragment.setArguments(args);
//
//                FragmentManager fragmentManager = getFragmentManager();
//                RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
//                mainLayout.setVisibility(LinearLayout.GONE);
//                fragmentManager.beginTransaction()
//                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                        .replace(R.id.content_frame, fragment)
//                        .commit();
                activeFragment = BUSQUEDA_POS;
                return true;
            case R.id.ver_fotos_usuario:
                verFotosUsuario();
                return true;
            case R.id.menu_help:
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.help_layout, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(v)
                        .setTitle(getString(R.string.help_help));
                builder.setPositiveButton(R.string.dialog_btn_cerrar, null);

                final AlertDialog d = builder.create();
                final TextView txt = (TextView) v.findViewById(R.id.help_container);
                if (!esCientifico.trim().equals("S")) {
                    switch (activeFragment) {
                        case MAP_POS_T:
                            txt.setText(getString(R.string.help_map));
                            break;
                        case CAPTURA_POS_T:
                            txt.setText(getString(R.string.help_captura));
                            break;
                        case ENCYCLOPEDIA_POS_T:
                            txt.setText(getString(R.string.help_enciclopedia));
                            break;
                        case GALERIA_POS_T:
                            txt.setText(getString(R.string.help_galeria));
                            break;
                        case RUTAS_POS_T:
                            txt.setText(getString(R.string.help_rutas));
                            break;
                        case IKIAM_WEB_POS_T:
                            txt.setText(getString(R.string.help_ikiam_web));
                            break;
                        case NOTEPAD_POS_T:
                        case NOTA_POS_T:
                            txt.setText(getString(R.string.help_notepad));
                            break;
                        case SETTINGS_POS_T:
                            txt.setText(getString(R.string.help_configuracion));
                            break;
                        case LOGIN_POS_T:
                            txt.setText(getString(R.string.help_login));
                            break;
                        case BUSQUEDA_POS:
                            txt.setText(getString(R.string.help_busqueda));
                            break;
                        case TOOLS_POS:
                            txt.setText(getString(R.string.help_tools));
                            break;
                    }
                } else {
                    switch (activeFragment) {
                        case MAP_POS:
                            txt.setText(getString(R.string.help_map));
                            break;
                        case CAPTURA_POS:
                            txt.setText(getString(R.string.help_captura));
                            break;
                        case ENCYCLOPEDIA_POS:
                            txt.setText(getString(R.string.help_enciclopedia));
                            break;
                        case GALERIA_POS:
                            txt.setText(getString(R.string.help_galeria));
                            break;
                        case RUTAS_POS:
                            txt.setText(getString(R.string.help_rutas));
                            break;
                        case IKIAM_WEB_POS:
                            txt.setText(getString(R.string.help_ikiam_web));
                            break;
                        case NOTEPAD_POS:
                        case NOTA_POS:
                            txt.setText(getString(R.string.help_notepad));
                            break;
                        case SETTINGS_POS:
                            txt.setText(getString(R.string.help_configuracion));
                            break;
                        case LOGIN_POS:
                            txt.setText(getString(R.string.help_login));
                            break;
                        case BUSQUEDA_POS:
                            txt.setText(getString(R.string.help_busqueda));
                            break;
                        case TOOLS_POS:
                            txt.setText(getString(R.string.help_tools));
                            break;
                        case SCANER_POS:
                            txt.setText(getString(R.string.help_tools));
                            break;

                    }
                }

                d.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button cerrar = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        cerrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                d.dismiss();
                            }
                        });

                    }
                });
                d.show();
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
        String title = "";
        Utils.hideSoftKeyboard(this);
        Fragment fragment = null;
        Bundle args = null;
        if (!esCientifico.trim().equals("S")) {
            switch (position) {
                case MAP_POS_T:
                    // fragment = new NthMapFragment();
                    // System.out.println("map?");
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                            .hide(fragmentManager.findFragmentById(R.id.content_frame))
//                            .addToBackStack("")
//                            .commit();
//                    RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
//                    mainLayout.setVisibility(View.VISIBLE);
                    title = getString(R.string.map_title);
                    fragment = null;
                    activeFragment = MAP_POS;
                    break;
                case CAPTURA_POS_T:
//                System.out.println(":::::cientifico:::: " + esCientifico);
                    if (!esCientifico.trim().equals("S")) {
                        fragment = new CapturaTuristaFragment();
                    } else {
                        fragment = new CapturaCientificoFragment();
                    }
                    this.addListener((FieldListener) fragment);
                    title = getString(R.string.captura_title);
                    activeFragment = CAPTURA_POS;
                    break;
                case ENCYCLOPEDIA_POS_T:
                    fragment = new EncyclopediaFragment();
                    title = getString(R.string.encyclopedia_title);
                    activeFragment = ENCYCLOPEDIA_POS;
                    break;
                case GALERIA_POS_T:
                    fragment = new GaleriaFragment();
                    title = getString(R.string.gallery_title);
                    activeFragment = GALERIA_POS;
                    break;
                case RUTAS_POS_T:
                    fragment = new RutasFragment();
                    title = getString(R.string.rutas_title);
                    activeFragment = RUTAS_POS;
                    break;
                case IKIAM_WEB_POS_T:
                    fragment = new DescargaBusquedaFragment();
                    title = getString(R.string.ikiam_web_title);
                    activeFragment = IKIAM_WEB_POS;
                    break;
                case NOTEPAD_POS_T:
                    fragment = new NotepadFragment();
                    title = getString(R.string.notepad_title);
                    activeFragment = NOTEPAD_POS;
                    break;
                case NOTA_POS_T:
                    fragment = new NotaCreateFrgment();
                    args = new Bundle();
                    args.putLong("nota", -1);
                    title = getString(R.string.nota_create_title);
                    activeFragment = NOTA_POS;
                    break;
                case SETTINGS_POS_T:
                    fragment = new SettingsFragment();
                    this.addListener((FieldListener) fragment);
                    title = getString(R.string.settings_title);
                    activeFragment = SETTINGS_POS;
                    break;
                case LOGIN_POS_T:
                    fragment = new Loginfragment();
                    this.addListener((FieldListener) fragment);
                    title = getString(R.string.login_title);
                    activeFragment = LOGIN_POS;
                    break;
                case SCANER_POS:
                    if (compatible) {
                        startActivity(new Intent(this, ScanActivity.class));
                        return;
                    }
                    break;
                default:
                    fragment = null;
                    break;
            }
        } else {
            switch (position) {
                case MAP_POS:
                    // fragment = new NthMapFragment();

                    //System.out.println("map?");
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                            .hide(fragmentManager.findFragmentById(R.id.content_frame))
//                            .addToBackStack("")
//                            .commit();
//                    RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
//                    mainLayout.setVisibility(View.VISIBLE);

                    fragment = null;
                    title = getString(R.string.map_title);
                    activeFragment = MAP_POS;
                    break;
                case CAPTURA_POS:
//                System.out.println(":::::cientifico:::: " + esCientifico);
                    if (!esCientifico.trim().equals("S")) {
                        fragment = new CapturaTuristaFragment();
                    } else {
                        fragment = new CapturaCientificoFragment();
                    }
                    this.addListener((FieldListener) fragment);
                    title = getString(R.string.captura_title);
                    activeFragment = CAPTURA_POS;
                    break;
                case ENCYCLOPEDIA_POS:
                    fragment = new EncyclopediaFragment();
                    title = getString(R.string.encyclopedia_title);
                    activeFragment = ENCYCLOPEDIA_POS;
                    break;
                case GALERIA_POS:
                    fragment = new GaleriaFragment();
                    title = getString(R.string.gallery_title);
                    activeFragment = GALERIA_POS;
                    break;
                case RUTAS_POS:
                    fragment = new RutasFragment();
                    title = getString(R.string.rutas_title);
                    activeFragment = RUTAS_POS;
                    break;
                case IKIAM_WEB_POS:
                    fragment = new DescargaBusquedaFragment();
                    title = getString(R.string.ikiam_web_title);
                    activeFragment = IKIAM_WEB_POS;
                    break;
                case NOTEPAD_POS:
                    fragment = new NotepadFragment();
                    title = getString(R.string.notepad_title);
                    activeFragment = NOTEPAD_POS;
                    break;
                case NOTA_POS:
                    fragment = new NotaCreateFrgment();
                    args = new Bundle();
                    args.putLong("nota", -1);
                    title = getString(R.string.nota_create_title);
                    activeFragment = NOTA_POS;
                    break;
                case SETTINGS_POS:
                    fragment = new SettingsFragment();
                    this.addListener((FieldListener) fragment);
                    title = getString(R.string.settings_title);
                    activeFragment = SETTINGS_POS;
                    break;
                case LOGIN_POS:
                    fragment = new Loginfragment();
                    this.addListener((FieldListener) fragment);
                    title = getString(R.string.login_title);
                    activeFragment = LOGIN_POS;
                    break;
                default:
                    fragment = null;
                    break;
            }
        }

//        if (fragment != null) {
        // System.out.println("fragment "+fragment);
//            Bundle args = new Bundle();
        //args.putString("pathFolder", pathFolder);
//            fragment.setArguments(args);
        Utils.openFragment(this, fragment, title, args);
//            FragmentManager fragmentManager = getFragmentManager();
//            RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
//            mainLayout.setVisibility(LinearLayout.GONE);
//
//            fragmentManager.beginTransaction()
//                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                    .replace(R.id.content_frame, fragment)
//                    .addToBackStack("")
//                    .commit();
//            RelativeLayout mainLayout = (RelativeLayout) this.findViewById(R.id.rl2);
//            mainLayout.setVisibility(LinearLayout.GONE);
//            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.replace(R.id.content_frame, fragment);
//            transaction.addToBackStack(null);
//            transaction.commit();

        // fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer

//        }
//        setTitle(mOptionsArray[position]);
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
        dataUsuario.clear();
        location = new LatLng(cords.get(0).getLatitud(), cords.get(0).getLongitud());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 19);
        map.animateCamera(update);
        polyLine = map.addPolyline(rectOptions);
        for (int i = 0; i < fotos.size(); i++) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(170, 126, conf);
            ;
            Canvas canvas1 = new Canvas(bmp);
            Paint color = new Paint();
            color.setTextSize(35);
            color.setColor(Color.BLACK);//modify canvas
            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.pin3), 0, 0, color);
            Bitmap b = ImageUtils.decodeBitmapPath(fotos.get(i).path, 160, 90);
            ;
            canvas1.drawBitmap(b, 5, 4, color);
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
        // System.out.println("get session map activity " + session + " ");
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
                                //System.out.println("username " + user.getUsername() + " name  " + user.getName());
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

    private void verFotosUsuario() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<Entry> entrys = new ArrayList<Entry>();

        if (!esCientifico.trim().equals("S")) {
            entrys = Entry.findAllByEspecieIsNull(this);
        } else {
            entrys = Entry.list(this);
        }
        int padding = (100);
        for (Entry entry : entrys) {
            List<Foto> fotos = Foto.findAllByEntry(activity, entry);
            for (int j = 0; j < fotos.size(); j++) {
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(170, 126, conf);
                ;
                Canvas canvas1 = new Canvas(bmp);
                Paint color = new Paint();
                color.setTextSize(35);
                color.setColor(Color.BLACK);//modify canvas
                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.pin3), 0, 0, color);
                Bitmap b = ImageUtils.decodeBitmapPath(fotos.get(j).path, 160, 90);
                canvas1.drawBitmap(b, 5, 4, color);
                Coordenada co = fotos.get(j).getCoordenada(activity);
                if (co != null) {
                    location = new LatLng(co.getLatitud(), co.getLongitud());
                    Marker marker = map.addMarker(new MarkerOptions().position(location)
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                            .anchor(0.5f, 1).title(getString(R.string.map_activity_captura) + " " + fotos.get(j).fecha));
                    builder.include(location);
                    dataUsuario.put(marker, fotos.get(j));
                }

            }
        }
        if (entrys.size() > 0) {
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            map.animateCamera(cu);
        }
        if (activeFragment != MAP_POS)
            showMap();
        setTitle(getString(R.string.menu_fotos_usuario));


    }

    public void mostrarEspecie(Especie especie) {
        map.clear();
        List<Entry> entry = Entry.findAllByEspecie(this, especie);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = (100);
        PolygonOptions poly = new PolygonOptions();
        int fillColor = Color.argb(150, 68, 134, 246);
        poly.strokeColor(fillColor);
        poly.fillColor(fillColor);

        List<LatLng> puntos = new ArrayList<LatLng>();
        List<LatLng> puntosfinal = new ArrayList<LatLng>();
        for (int i = 0; i < entry.size(); i++) {
            Entry current = entry.get(i);
            List<Foto> fotos = Foto.findAllByEntry(activity, current);
            for (int j = 0; j < fotos.size(); j++) {
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(170, 126, conf);
                ;
                Canvas canvas1 = new Canvas(bmp);
                Paint color = new Paint();
                color.setTextSize(35);
                color.setColor(Color.BLACK);//modify canvas
                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                        R.drawable.pin3), 0, 0, color);
                Bitmap b = ImageUtils.decodeFile(fotos.get(j).path);
                canvas1.drawBitmap(b, 5, 4, color);
                Coordenada co = fotos.get(j).getCoordenada(activity);
                location = new LatLng(co.getLatitud(), co.getLongitud());
                puntos.add(location);
                builder.include(location);
                Marker marker = map.addMarker(new MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .anchor(0.5f, 1).title(getString(R.string.map_activity_captura) + " " + fotos.get(j).fecha));
                // Marker marker = map.addMarker(new MarkerOptions().position(location)
                //       .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                //     .anchor(0.5f, 1).title(" "+location.latitude+" , "+location.longitude));
                //marker.showInfoWindow();
                data.put(marker, fotos.get(j));
            }
        }
        if (puntos.size() > 2) {
            LatLng masAlto = null;
            LatLng masBajo = null;
            LatLng masDer = null;
            LatLng masIzq = null;
            double maxX = 0;
            double minX = 0;
            double minY = 0;
            double maxY = 0;
            for (int i = 0; i < puntos.size(); i++) {
                double x1 = (puntos.get(i).longitude + 180) * 360;
                double y1 = (puntos.get(i).latitude + 90) * 180;
                if (minX == 0) {
                    minX = x1;
                    masIzq = puntos.get(i);
                }
                if (minY == 0) {
                    minY = y1;
                    masBajo = puntos.get(i);
                }
                if (maxX == 0) {
                    maxX = x1;
                    masDer = puntos.get(i);
                }
                if (maxY == 0) {
                    maxY = y1;
                    masAlto = puntos.get(i);
                }
                if (x1 > maxX) {
                    maxX = x1;
                    masDer = puntos.get(i);
                }
                if (y1 > maxY) {
                    maxY = y1;
                    masAlto = puntos.get(i);
                }
                if (x1 < minX) {
                    minX = x1;
                    masIzq = puntos.get(i);
                }
                if (y1 < minY) {
                    minY = y1;
                    masBajo = puntos.get(i);
                }

            }
            // System.out.println("mas alto "+masAlto.latitude+" , "+masDer.longitude);
            // System.out.println("mas izq "+masIzq.latitude+" , "+masIzq.longitude);
            // System.out.println("mas bajo "+masBajo.latitude+" , "+masDer.longitude);
            // System.out.println("mas der "+masDer.latitude+" , "+masDer.longitude);

            puntosfinal.add(masAlto);
            puntosfinal.add(masIzq);
            puntosfinal.add(masBajo);
            puntosfinal.add(masDer);
            double maxDis = distancia(masAlto, masBajo);

            for (int i = 0; i < puntosfinal.size() - 1; i++) {
                LatLng current = puntosfinal.get(i);
                poly.add(current);
                LatLng next = puntosfinal.get(i + 1);
                double x1 = (current.longitude + 180) * 360;
                double y1 = (current.latitude + 90) * 180;
                double x2 = (next.longitude + 180) * 360;
                double y2 = (next.latitude + 90) * 180;
                LatLng nuevo = null;
                double nuevoX = 0;
                switch (i) {
                    case 0:
                        nuevo = null;
                        nuevoX = 0;
                        // System.out.println("current "+current.latitude+" ; "+current.longitude+"  --->  "+x1+" ; "+y1);
                        //System.out.println("next "+next.latitude+" ; "+next.longitude+"  --->  "+x2+" ; "+y2);
                        for (int j = 0; j < puntos.size(); j++) {
                            if (puntos.get(j) != masIzq) {
                                double x3 = (puntos.get(j).longitude + 180) * 360;
                                double y3 = (puntos.get(j).latitude + 90) * 180;
                                // System.out.println("ietracion "+puntos.get(j).latitude+" ; "+puntos.get(j).longitude+"  --->  "+x3+" ; "+y3);
                                if (x3 < x1 && y2 < y3) {
                                    //System.out.println("paso es mas!");
                                    if (nuevo == null) {
                                        nuevo = puntos.get(j);
                                        nuevoX = x3;
                                    } else {
                                        if (x3 < nuevoX) {
                                            nuevo = puntos.get(j);
                                            nuevoX = x3;
                                        }
                                    }
                                }
                            }

                        }
                        if (nuevo != null)
                            poly.add(nuevo);
                        break;
                    case 1:
                        nuevo = null;
                        nuevoX = 0;
                        // System.out.println("current "+current.latitude+" ; "+current.longitude+"  --->  "+x1+" ; "+y1);
                        //System.out.println("next "+next.latitude+" ; "+next.longitude+"  --->  "+x2+" ; "+y2);
                        for (int j = 0; j < puntos.size(); j++) {
                            if (puntos.get(j) != masBajo) {
                                double x3 = (puntos.get(j).longitude + 180) * 360;
                                double y3 = (puntos.get(j).latitude + 90) * 180;
                                // System.out.println("ietracion "+puntos.get(j).latitude+" ; "+puntos.get(j).longitude+"  --->  "+x3+" ; "+y3);
                                if (x3 < x2 && y3 > y2) {
                                    //System.out.println("paso es mas!");
                                    if (nuevo == null) {
                                        nuevo = puntos.get(j);
                                        nuevoX = x3;
                                    } else {
                                        if (x3 < nuevoX) {
                                            nuevo = puntos.get(j);
                                            nuevoX = x3;
                                        }
                                    }
                                }
                            }

                        }
                        if (nuevo != null)
                            poly.add(nuevo);
                        break;
                    case 2:
                        nuevo = null;
                        nuevoX = 0;
                        // System.out.println("current "+current.latitude+" ; "+current.longitude+"  --->  "+x1+" ; "+y1);
                        //System.out.println("next "+next.latitude+" ; "+next.longitude+"  --->  "+x2+" ; "+y2);
                        for (int j = 0; j < puntos.size(); j++) {
                            if (puntos.get(j) != masAlto) {
                                double x3 = (puntos.get(j).longitude + 180) * 360;
                                double y3 = (puntos.get(j).latitude + 90) * 180;
                                // System.out.println("ietracion "+puntos.get(j).latitude+" ; "+puntos.get(j).longitude+"  --->  "+x3+" ; "+y3);
                                if (x3 > x1 && y3 > y1) {
                                    //System.out.println("paso es mas!");
                                    if (nuevo == null) {
                                        nuevo = puntos.get(j);
                                        nuevoX = x3;
                                    } else {
                                        if (x3 > nuevoX) {
                                            nuevo = puntos.get(j);
                                            nuevoX = x3;
                                        }
                                    }
                                }
                            }

                        }
                        if (nuevo != null)
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
        setTitle(getString(R.string.captura_nombre_especie_label) + ": " + especie.nombre);

    }

    public double pendiente(LatLng p1, LatLng p2) {
        double x1 = (p1.longitude + 180) * 360;
        double y1 = (p1.latitude + 90) * 180;
        double x2 = (p2.longitude + 180) * 360;
        double y2 = (p2.latitude + 90) * 180;
        double dX = x2 - x1;
        double dY = y2 - y1;
        return dY / dX;
    }

    public double distancia(LatLng p1, LatLng p2) {
        double x1 = (p1.longitude + 180) * 360;
        double y1 = (p1.latitude + 90) * 180;
        double x2 = (p2.longitude + 180) * 360;
        double y2 = (p2.latitude + 90) * 180;
        double dX = x2 - x1;
        double dY = y2 - y1;
        return Math.sqrt((dX * dX) + (dY * dY));
    }

    public int pointSort(LatLng p1, LatLng p2, LatLng upper) {
        // Exclude the 'upper' point from the sort (which should come first).
        if (p1 == upper) return -1;
        if (p2 == upper) return 1;

        // Find the slopes of 'p1' and 'p2' when a line is
        // drawn from those points through the 'upper' point.
        double m1 = pendiente(p1, upper);
        double m2 = pendiente(p2, upper);

        // 'p1' and 'p2' are on the same line towards 'upper'.
        if (m1 == m2) {
            // The point closest to 'upper' will come first.
//            return p1.distance(upper) < p2.distance(upper) ? -1 : 1;
            return (distancia(p1, upper) < distancia(p2, upper)) ? -1 : 1;
        }

        // If 'p1' is to the right of 'upper' and 'p2' is the the left.
        if (m1 <= 0 && m2 > 0) return -1;

        // If 'p1' is to the left of 'upper' and 'p2' is the the right.
        if (m1 > 0 && m2 <= 0) return 1;

        // It seems that both slopes are either positive, or negative.
        return m1 > m2 ? -1 : 1;
    }

    public void setAchivement(int tipo, float valor) {
        String nombre = "";
        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);

        switch (tipo) {
            case ACHIEV_FOTOS:
                nombre = "fotos";
                break;
            case ACHIEV_DISTANCIA:
                nombre = "distancia";
                break;
            case ACHIEV_UPLOADS:
                nombre = "uploads";
                break;
            case ACHIEV_SHARE:
                nombre = "shares";
                break;

        }
        SharedPreferences.Editor editor = settings.edit();
        if (!nombre.equals("distancia")) {
            editor.putInt(nombre, (int) valor);
        } else {
            editor.putFloat(nombre, valor);
        }
        editor.commit();
    }

    public float updateAchievement(int tipo) {
        return updateAchievement(tipo, 0);
    }

    public float updateAchievement(int tipo, float distancia) {
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
                res = res + distancia;
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
                res = 0;

        }
        SharedPreferences.Editor editor = settings.edit();
        if (!nombre.equals("distancia")) {
            editor.putInt(nombre, (int) res);
        } else {
            editor.putFloat(nombre, res);
        }
        editor.commit();
        checkAchiev(tipo, res);
        return res;
    }

    public void checkAchiev(int tipo, float cant) {
        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
        if (settings.getInt("logros", 0) == 1) {
            ExecutorService queue = Executors.newSingleThreadExecutor();
            queue.execute(new AchievementChecker(this, tipo, cant));
        }
    }

    public float getAchievement(int tipo) {
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