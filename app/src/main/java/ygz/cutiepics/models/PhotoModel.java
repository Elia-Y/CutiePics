package ygz.cutiepics.models;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by yuyuxiao on 2018-03-20.
 *
 * This class is used as helper for savedPhoto activity
 *  it can be reused to transfer image between activities
 *
 */

public class PhotoModel {
    private static Bitmap mPhoto;
    private static Uri mUri;
    private static String[] photos;

    private PhotoModel() {}

    public static Bitmap getmPhoto() {
        return mPhoto;
    }

    public static Uri getmUri() {return mUri;}

    public static String[] getPhotos() {return photos;}

    public static void setmPhoto(Bitmap mInsertPicture) {
        mPhoto = mInsertPicture;
    }

    public static void setmUri(Uri uri) {mUri = uri;}

    public static void setPhotos(String[] pts) {photos = pts;}
}
