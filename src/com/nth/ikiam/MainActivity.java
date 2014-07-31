package com.nth.ikiam;

import android.os.Bundle;
import com.nth.ikiam.db.DbHelper;
import com.nth.ikiam.drawer.*;
import com.nth.ikiam.utils.Utils;


/**
 * Created by DELL on 30/07/2014.
 */
public class MainActivity extends AbstractNavDrawerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbHelper helper = new DbHelper(this);
        helper.getWritableDatabase();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        }
    }

    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
        return DrawersConfig.mainDrawerInit(this);
    }

    @Override
    protected void onNavItemSelected(int id) {
        Bundle args = new Bundle();
        args.putString("pathFolder", Utils.getFolder(this));
        DrawersConfig.mainDrawerNav(this, id, args);
    }
}