package com.abhishek.aspmanager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class PermissionsActivity extends AppCompatActivity {
    String[] requestedPermissions;
    ListView lv;
    ArrayAdapter<String> ad;
    String test="No Permission needed";
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Permissions");
        actionBar.setDisplayHomeAsUpEnabled(true);
        lv = (ListView) findViewById(R.id.mylist);
        ad = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
        String Package = getIntent().getStringExtra("package");
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            int x = Package.compareTo(applicationInfo.packageName);
            if(x==0) {
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                    //Get Permissions
                    requestedPermissions = packageInfo.requestedPermissions;
                    if(requestedPermissions != null) {
                        for (int i = 0; i < requestedPermissions.length; i++) {
                            Log.d("test", requestedPermissions[i]);
                            ad.add(requestedPermissions[i]);
                        }
                        //lv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        lv.setAdapter(ad);
                    }
                    else
                    {
                        Log.d("test","No need of permission");
                        ad.add(test);
                        lv.setAdapter(ad);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int x = item.getItemId();
        if(x==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
