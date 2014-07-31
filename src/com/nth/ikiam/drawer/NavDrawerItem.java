package com.nth.ikiam.drawer;

/**
 * Created by DELL on 30/07/2014.
 */
public interface NavDrawerItem {
    public int getId();

    public String getLabel();

    public int getType();

    public boolean isEnabled();

    public boolean updateActionBarTitle();
}
