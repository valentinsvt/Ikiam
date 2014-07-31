package com.nth.ikiam.drawer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.nth.ikiam.*;

/**
 * Created by DELL on 30/07/2014.
 */
public class DrawersConfig {

    static final int MAP_POS = 0;
    static final int CAPTURA_POS = 1;
    static final int ENCYCLOPEDIA_POS = 2;
    static final int HERRAMIENTAS_POS = 3;
    static final int SETTINGS_POS = 4;

    static final String variation = "_light";

    public static NavDrawerActivityConfiguration mainDrawerInit(Context context) {
        NavDrawerItem[] menu = new NavDrawerItem[]{
                NavMenuItem.create(MAP_POS, context.getString(R.string.map_title), "ic_drawer_map" + variation, false, context),
                NavMenuItem.create(CAPTURA_POS, context.getString(R.string.captura_title), "ic_drawer_captura" + variation, false, context),
                NavMenuItem.create(ENCYCLOPEDIA_POS, context.getString(R.string.encyclopedia_title), "ic_drawer_encyclopedia" + variation, false, context),
                NavMenuItem.create(HERRAMIENTAS_POS, context.getString(R.string.tools_title), "ic_drawer_tools" + variation, false, context),
                NavMenuItem.create(SETTINGS_POS, context.getString(R.string.settings_title), "ic_drawer_settings" + variation, false, context),
        };

        NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
        navDrawerActivityConfiguration.setMainLayout(R.layout.main_layout);
        navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
        navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);
        navDrawerActivityConfiguration.setNavItems(menu);
        navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);
        navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
        navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.drawer_close);
        navDrawerActivityConfiguration.setBaseAdapter(
                new NavDrawerAdapter(context, R.layout.navdrawer_item, menu));

        return navDrawerActivityConfiguration;
    }

    public static void mainDrawerNav(FragmentActivity fa, int id, Bundle args) {
        Fragment fragment = new MainFragment();
        switch (id) {
            case MAP_POS:
                Intent intent=new Intent(fa,MapActivity.class);
                fa.startActivity(intent);
                break;
            case CAPTURA_POS:
                fragment = new CapturaFragment();
                break;
            case ENCYCLOPEDIA_POS:
                fragment = new EncyclopediaFragment();
                break;
            case HERRAMIENTAS_POS:
                break;
            case SETTINGS_POS:
                fragment = new SettingsFragment();
                break;
        }
        if (args != null) {
            fragment.setArguments(args);
        }

        fa.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public static void mainDrawerNav(FragmentActivity fa, int id) {
        mainDrawerNav(fa, id, null);
    }

}
