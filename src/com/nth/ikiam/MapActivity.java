package com.nth.ikiam;

import android.os.Bundle;
import android.view.View;
import com.nth.ikiam.drawer.AbstractNavDrawerActivity;
import com.nth.ikiam.drawer.DrawersConfig;
import com.nth.ikiam.drawer.NavDrawerActivityConfiguration;
import com.nth.ikiam.utils.Utils;

/**
 * Created by Svt on 7/31/2014.
 */
public class MapActivity extends AbstractNavDrawerActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        }


    }

    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
        return DrawersConfig.mainDrawerInit(this);
    }

    @Override
    protected void onNavItemSelected(int id) {
        DrawersConfig.mainDrawerNav(this, id);
    }
}