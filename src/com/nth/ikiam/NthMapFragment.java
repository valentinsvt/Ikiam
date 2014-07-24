package com.nth.ikiam;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by DELL on 23/07/2014.
 */
public class NthMapFragment extends Fragment implements Button.OnClickListener {
    private Button chooseBtn;
    private static GoogleMap map;
    private static LatLng location ;
    public NthMapFragment() {
        // Empty constructor required for fragment subclasses
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_layout, container, false);
        chooseBtn = (Button) view.findViewById(R.id.btnLocate);
        chooseBtn.setOnClickListener(this);
        // map=((MapFragment) getFragmentManager().findFragmentById(R.id.mapF)).getMap();
        //  map=new MapFragment().getMap();
        setUpMapIfNeeded();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (map != null) {
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.mapF)).commit();
            // MainActivity.fragmentManager.beginTransaction()
            //       .remove(MainActivity.fragmentManager.findFragmentById(R.id.mapF)).commit();
            map = null;
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

        System.out.println("setUpMap");
        location=new LatLng(-1.6477220517969353, -78.46435546875);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,6);

        System.out.println("map "+map);
        map.animateCamera(update);
        // For showing a move to my loction button
        //map.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        //map.addMarker(new MarkerOptions().position(location).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));
    }

    @Override
    public void onClick(View v){

        location=new LatLng( -0.4614207935306084, -90.615234375);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(location,7);


        map.animateCamera(update);
    }


}