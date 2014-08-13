package com.nth.ikiam;
/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.nth.ikiam.db.DbHelper;

import java.io.File;


public class MainActivity extends Activity {
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

    private String pathFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int option = getIntent().getIntExtra("selected",1);
        System.out.println("image "+ getIntent().getStringExtra("image"));

        setContentView(R.layout.activity_main);



        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            pathFolder = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name);
            System.out.println("1" + Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name));
        } else {
            /* save the folder in internal memory of phone */
            pathFolder = "/data/data/" + getPackageName() + File.separator + getString(R.string.app_name);
            System.out.println("2" + "/data/data/" + getPackageName() + File.separator + getString(R.string.app_name));
        }
        File folder = new File(pathFolder);
        folder.mkdirs();

        DbHelper helper = new DbHelper(this);
        helper.getWritableDatabase();

        mTitle = mDrawerTitle = getTitle();
        mOptionsArray = getResources().getStringArray(R.array.options_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mOptionsArray));
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
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
                getActionBar().setTitle(R.string.menu_title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(option);
        }
    }

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
//                // create intent to perform web search for this planet
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }

//                // aqui para la seleccion de la galeria
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    //to get image and videos, I used a */"
//                    intent.setType("*/*");
//                    startActivityForResult(intent, 1);
//                } else {
//                    Toast.makeText(this, R.string.gallery_app_not_available, Toast.LENGTH_LONG).show();
//                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            case MAP_POS:
                fragment=null;
                Intent intent = new Intent(this,MapActivity.class);
                startActivity(intent);
                break;
            case CAPTURA_POS:
                fragment = new CapturaCientificoFragment();
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
            default:
                fragment = new NthMapFragment();
        }

        if(fragment!=null){
            Bundle args = new Bundle();
            args.putString("pathFolder", pathFolder);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(mOptionsArray[position]);
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
}