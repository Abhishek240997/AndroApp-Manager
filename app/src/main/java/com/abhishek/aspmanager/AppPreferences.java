package com.abhishek.aspmanager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ABHISHEK on 14-08-2017.
 */

public class AppPreferences {
    private SharedPreferences shared;
    private SharedPreferences.Editor edit;
    private Context context;
    public static final String KeyFavouriteApps = "prefFavouriteApps";
    public AppPreferences(Context context)
    {
        this.shared=context.getSharedPreferences("appPreferences",0);
        this.edit=shared.edit();
        this.context=context;
    }
    public Set<String> getFavouriteApps()
    {
        return shared.getStringSet(KeyFavouriteApps,new HashSet<String>());
    }
    public void setFavouriteApps(Set<String> favouriteApps){
        edit.remove(KeyFavouriteApps);
        edit.commit();
        edit.putStringSet(KeyFavouriteApps, favouriteApps);
        edit.commit();
    }
}
