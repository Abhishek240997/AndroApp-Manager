package com.abhishek.aspmanager;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by ABHISHEK on 07-08-2017.
 */

public class AppInfo implements Serializable{
    private String name;
    private String apk;
    private String version;
    private String source;
    private String data;
    private Drawable icon;
    private Boolean system;

    public AppInfo(String name, String apk, String version, String source, String data, Drawable icon, Boolean isSystem) {
        this.name = name;
        this.apk = apk;
        this.version = version;
        this.source = source;
        this.data = data;
        this.icon = icon;
        this.system = isSystem;
    }

    public String getName() {
        return name;
    }

    public String getAPK() {
        return apk;
    }

    public String getVersion() {
        return version;
    }

    public String getSource() {
        return source;
    }

    public String getData() {
        return data;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Boolean isSystem() {
        return system;
    }

    public String toString() {
        return getName() + "##" + getAPK() + "##" + getVersion() + "##" + getSource() + "##" + getData() + "##" + isSystem();
    }
}

