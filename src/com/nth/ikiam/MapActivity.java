package com.nth.ikiam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.*;
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
import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;
import com.nth.ikiam.db.Coordenada;
import com.nth.ikiam.db.DbHelper;
import com.nth.ikiam.db.Foto;
import com.nth.ikiam.db.Ruta;
import com.nth.ikiam.image.ImageItem;
import com.nth.ikiam.image.ImageTableObserver;
import com.nth.ikiam.image.ImageUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends Activity  implements Button.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{
    /*DRAWER*/
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mOptionsArray;
    private final int MAP_POS = 0;
    private final int CAPTURA_POS = 1;
    private final int ENCYCLOPEDIA_POS = 2;
    private final int SETTINGS_POS = 3;
    private final int LOGIN_POS = 4;

    /*Interfaz*/
    private Button[] botones;
    boolean continente=true;
    Activity activity;
    /*Mapa*/
    private static GoogleMap map;
    private Polyline polyLine;
    private PolylineOptions rectOptions = new PolylineOptions().color(Color.RED);
    private static LatLng location ;
    LocationClient locationClient;
    Marker lastPosition;
    HashMap<Marker,Foto> data;

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
    private ExecutorService queue = Executors.newSingleThreadExecutor();
    List<Foto> fotos;
    /*Fin imagenes*/

    boolean first = true;
    String imagePathUpload="";
    AlertDialog dialog;
    View myView;
    public int screenHeight;
    public int screenWidth;
    public static final String PREFS_NAME = "IkiamSettings";
    public String userId;
    public String name;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        first = true;

        /*preferencias*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId=settings.getString("user","-1");
        name = settings.getString("name","-1");
        //System.out.println("variables name "+userId+"  name "+name);
        setContentView(R.layout.activity_map);
        DbHelper helper = new DbHelper(this);
        helper.getWritableDatabase();

        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {

        }else{
            SharedPreferences.Editor editor = settings.edit();
            if(!settings.getString("user","-1").equals("-1") && !settings.getString("type","-1").equals("ikiam")) {
                editor.putString("user", "-1");
                editor.putString("login", "-1");
                editor.putString("name", "-1");
                editor.putString("type", "-1");
                editor.commit();
            }
        }

        this.activity = this;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
        fotos = new ArrayList<Foto>();

        /*CORE*/
        setUpMapIfNeeded();
        data = new HashMap<Marker, Foto>();
        locationClient = new LocationClient(this, this, this);
        locationClient.connect();
        botones = new Button[2];
        botones[0] = (Button) this.findViewById(R.id.btnGalapagos);
        botones[1] = (Button) this.findViewById(R.id.btnService);
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
        if (state!=null) {
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

//        // TODO Auto-generated method stub

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        if(ruta!=null)
            savedInstanceState.putInt("ruta", (int)ruta.id);
        savedInstanceState.putInt("restore", 1);
        getFragmentManager().saveFragmentInstanceState(getFragmentManager().findFragmentById(R.id.mapF));
//        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
            this.stopService(new Intent(this, SvtService.class));
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
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
            if(servicesConnected()){
                if(!status) {
                    map.clear();
                    ruta= new Ruta(this,"Ruta");
                    ruta.save();
                    this.startService(new Intent(this, SvtService.class));
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
                    this.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, camera);
                    status=true;
                }else {
                    doUnbindService();
                    this.stopService(new Intent(this, SvtService.class));
                    botones[1].setText("Nueva ruta");
                    ruta=null;
                    status=false;
                }
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
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        map.animateCamera(cu);
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
                    while (!attached){

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

    public  void setUpMapIfNeeded() {
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
        }else{
            setUpMap();
        }
    }
    private  void setUpMap() {
        location=new LatLng(-1.6477220517969353, -78.46435546875);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,6);
        map.setMyLocationEnabled(true);
        map.animateCamera(update);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker){
                if(data.get(marker)!=null){
                    marker.showInfoWindow();
                    LayoutInflater inflater = activity.getLayoutInflater();
                    myView= inflater.inflate(R.layout.dialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.map_activity_nuevaFoto);
                    builder.setView(myView);
                    builder.setPositiveButton(R.string.dialog_btn_descargar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            imagePathUpload = data.get(marker).path;
                            selectItem(1);
                            dialog.dismiss();
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
                    ImageView img= (ImageView) myView.findViewById(R.id.image);

                    img.setImageBitmap(getFotoDialog(data.get(marker), screenWidth, 300));
                    dialog.show();

                    return true;
                }
                return false;
            }
        });

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
                            Foto foto = new Foto(activity);
                            Coordenada cord=new Coordenada(activity,latitud,longitud);
                            cord.save();
                            foto.setCoordenada(cord);
                            foto.setRuta(ruta);
                            foto.path=imageItem.imagePath;
                            foto.uploaded=0;
                            foto.save();
                            fotos.add(foto);
                            Bitmap bmp = Bitmap.createBitmap(86,59, conf);
                            Canvas canvas1 = new Canvas(bmp);
                            Paint color = new Paint();
                            color.setTextSize(35);
                            color.setColor(Color.BLACK);//modify canvas
                            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                                    R.drawable.pin3), 0,0, color);
                            canvas1.drawBitmap(imagenes.get(lastIndex), 3,2, color);

                            Marker marker = map.addMarker(new MarkerOptions().position(latlong)
                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                                    .anchor(0.5f, 1).title("Nueva fotografía"));

                            data.put(marker,foto);

                            lastSize=imagenes.size();
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


    /*IMAGES*/
    public void setImageIndex(int index){
        //System.out.println("set image index " + index);
        if(imagenes==null){
            imagenes=  new ArrayList<Bitmap>();
        }
        if(index>=lastestImageIndex) {
            this.lastestImageIndex = index;
        }else {
            /*Borro una foto*/
            this.lastestImageIndex=0;
        }
        //System.out.println("set image index fin "+this.lastestImageIndex);
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
    public Bitmap getFotoDialog(Foto image,int width,int heigth){
        if(image!=null) {
            // System.out.println("path "+imageItem.imagePath);
            //System.out.println("images " + image.imagePath+"  "+width+"  "+heigth);
            Bitmap b = ImageUtils.decodeFile(image.path,width,heigth);
            return b;

        }
        return null;

    }

    public ImageItem getLatestItem()
    {
        // set vars
        if(lastestImageIndex>0){
            ImageItem item  = null;
            String columns[] = new String[]{ MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.MINI_THUMB_MAGIC };

            Uri image     = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, lastestImageIndex);
            Cursor cursor = this.managedQuery(image, columns, null, null, null);

            // check if cursus has rows, if not break and exit loop
            if (cursor.moveToFirst()) {
                //System.out.println("tiene rows "+cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MINI_THUMB_MAGIC)));
                item           = new ImageItem();
                item.prefs     = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
                item.imageId   = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
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

    private void selectItem(int position) {
        // update the main content by replacing fragments
        //System.out.println("pos? "+position);
        Fragment fragment;
        switch (position) {
            case MAP_POS:
                // fragment = new NthMapFragment();


                //System.out.println("map?");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(fragmentManager.findFragmentById(R.id.content_frame))
                        .commit();
                RelativeLayout mainLayout=(RelativeLayout)this.findViewById(R.id.rl2);
                mainLayout.setVisibility(View.VISIBLE);


                fragment=null;

                break;
            case CAPTURA_POS:
                fragment = new CapturaFragment();
                break;
            //            case CAMERA_POS:
//                fragment = new CameraFragment();
//                break;
//            case GALLERY_POS:
//                fragment = new GalleryFragment();
//                break;
            case ENCYCLOPEDIA_POS:
                fragment = new EncyclopediaFragment();
                break;
            case SETTINGS_POS:
                fragment = new SettingsFragment();
                break;
            case LOGIN_POS:
                fragment = new Loginfragment();
                break;
            default:
                fragment=null;
                break;


        }
        if(fragment!=null){
            // System.out.println("fragment "+fragment);
            Bundle args = new Bundle();
            //args.putString("pathFolder", pathFolder);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            RelativeLayout mainLayout=(RelativeLayout)this.findViewById(R.id.rl2);
            mainLayout.setVisibility(LinearLayout.GONE);
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.content_frame, fragment)
                    .commit();
            // fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer

        }
        mDrawerList.setItemChecked(position, true);
        setTitle(mOptionsArray[position]);
        mDrawerLayout.closeDrawer(mDrawerList);



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
}