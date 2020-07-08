package com.rahtech.chineseappsremover;

import android.graphics.drawable.Drawable;

class item {

    private String appname;
    private String[] similar;
    private Drawable appIcon;
    private String packageName;

    item(String appname, String packageName, String[] similar, Drawable appIcon) {
        this.appname = appname;
        this.similar = similar;
        this.appIcon = appIcon;
        this.packageName=packageName;
    }


    String[] getSimilar() {
        return similar;
    }


    String getAppname() {
        return appname;
    }

    String getPackageName() {
        return packageName;
    }


    Drawable getAppIcon() {
        return appIcon;
    }
}
