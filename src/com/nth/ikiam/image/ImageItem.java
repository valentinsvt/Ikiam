package com.nth.ikiam.image;

import android.content.SharedPreferences;

/**
 * Holds image details, this class is used as queue item
 *
 * @author Jan Peter Hooiveld
 */
public class ImageItem
{
    /**
     * User preferences
     */
    public SharedPreferences prefs;

    /**
     * Google authentication string
     */
    public String imageAuth;

    /**
     * Image id
     */
    public Integer imageId;

    /**
     * Location where image is stored
     */
    public String imagePath;

    /**
     * Filename of the image
     */
    public String imageName;

    /**
     * Image mime-type
     */
    public String imageType;

    /**
     * Image size
     */
    public int imageSize;
}