package com.abhishek.aspmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    private MyAdapter appAdapter;
    private MyAdapter sysAdapter;
    private MyAdapter favAdapter;
    private List<AppInfo> instapp;
    private List<AppInfo> sysapp;
    private List<AppInfo> favapp;
    private ActionBar actionBar;
    SwipeRefreshLayout SwipeRefreshLayout;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    NavigationView navigationView;
    ProgressBar progressBar;
    Context context;
    TextView tv1, tv2, tv3;
    private Boolean doubleBackToExitPressedOnce = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        SwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        tv1 = (TextView) findViewById(R.id.installedAppsCount);
        tv2 = (TextView) findViewById(R.id.systemAppsCount);
        tv3 = (TextView) findViewById(R.id.favoritesAppsCount);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        context = this;
        new getInstalledApps().execute();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int i) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int checknavigation = checkNavigation();
                        Intent obj = new Intent(MainActivity.this, AppDetails.class);
                        if (checknavigation == 0) {
                            String appName = instapp.get(i).getName();
                            String Version = instapp.get(i).getVersion();
                            String app_apk = instapp.get(i).getAPK();
                            String app_source = instapp.get(i).getSource();
                            String app_data = instapp.get(i).getData();
                            Boolean appIsSystem = instapp.get(i).isSystem();
                            Bitmap bitmap = ((BitmapDrawable) instapp.get(i).getIcon()).getBitmap();
                            obj.putExtra("app_icon", bitmap);
                            obj.putExtra("appName", appName);
                            obj.putExtra("Version", Version);
                            obj.putExtra("app_apk", app_apk);
                            obj.putExtra("app_source", app_source);
                            obj.putExtra("app_data", app_data);
                            obj.putExtra("appIsSystem", appIsSystem);
                            startActivity(obj);
                        } else if (checknavigation == 1) {
                            String appName = sysapp.get(i).getName();
                            String Version = sysapp.get(i).getVersion();
                            String app_apk = sysapp.get(i).getAPK();
                            String app_source = sysapp.get(i).getSource();
                            String app_data = sysapp.get(i).getData();
                            Boolean appIsSystem = sysapp.get(i).isSystem();
                            Bitmap bitmap = ((BitmapDrawable) sysapp.get(i).getIcon()).getBitmap();
                            obj.putExtra("app_icon", bitmap);
                            obj.putExtra("appName", appName);
                            obj.putExtra("Version", Version);
                            obj.putExtra("app_apk", app_apk);
                            obj.putExtra("app_source", app_source);
                            obj.putExtra("app_data", app_data);
                            obj.putExtra("appIsSystem", appIsSystem);
                            startActivity(obj);
                        } else if (checknavigation == 2) {
                            String appName = favapp.get(i).getName();
                            String Version = favapp.get(i).getVersion();
                            String app_apk = favapp.get(i).getAPK();
                            String app_source = favapp.get(i).getSource();
                            String app_data = favapp.get(i).getData();
                            Boolean appIsSystem = favapp.get(i).isSystem();
                            Bitmap bitmap = ((BitmapDrawable) favapp.get(i).getIcon()).getBitmap();
                            obj.putExtra("app_icon", bitmap);
                            obj.putExtra("appName", appName);
                            obj.putExtra("Version", Version);
                            obj.putExtra("app_apk", app_apk);
                            obj.putExtra("app_source", app_source);
                            obj.putExtra("app_data", app_data);
                            obj.putExtra("appIsSystem", appIsSystem);
                            startActivity(obj);
                        }
                    }
                }, 200);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
   SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
       @Override
       public void onRefresh() {
           new getInstalledApps().execute();
           SwipeRefreshLayout.setRefreshing(false);
       }
   });
    }
    public int sortapps() {
        sp = getApplicationContext().getSharedPreferences("myfile", 0);
        ed = sp.edit();
        String sort = sp.getString("sort", null);
        int returnvar;
        if (sort == null) {
            return 0;
        } else {
            returnvar = Integer.parseInt(sort);
            return returnvar;
        }
    }

    public int checkNavigation() {
        String checkNavi = sp.getString("navi", null);
        int temp;
        if (checkNavi == null) {
            return 0;
        } else {
            temp = Integer.parseInt(checkNavi);
            return temp;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tap Again To Close The App", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.installed_apps) {
            ed.putString("navi","0");
            recyclerView.setAdapter(appAdapter);
            actionBar.setTitle("Installed Apps");
            ed.commit();
        } else if (id == R.id.system_apps) {
            ed.putString("navi","1");
            recyclerView.setAdapter(sysAdapter);
            actionBar.setTitle("System Apps");
            ed.commit();
        } else if (id == R.id.favourite_apps) {
            ed.putString("navi","2");
            recyclerView.setAdapter(favAdapter);
            actionBar.setTitle("Favourite Apps");
            ed.commit();
        } else if (id == R.id.send) {
            ApplicationInfo app = getApplicationContext().getApplicationInfo();
            String filePath = app.sourceDir;
            String appName = "AndroApp Manager";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.setType("application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, String.format("Send %s using", appName)));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class getInstalledApps extends AsyncTask<Void, String, Void> {
        public getInstalledApps() {
            instapp = new ArrayList<>();
            sysapp = new ArrayList<>();
            favapp = new ArrayList<>();
            actionBar = getSupportActionBar();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final PackageManager packageManager = getPackageManager();
            List<PackageInfo> packages;
            packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            int checksort = sortapps();
            switch (checksort) {
                default:
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return packageManager.getApplicationLabel(p1.applicationInfo).toString().toLowerCase().compareTo(packageManager.getApplicationLabel(p2.applicationInfo).toString().toLowerCase());
                        }
                    });
                    break;
                case 1:
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            Long size1 = new File(p1.applicationInfo.sourceDir).length();
                            Long size2 = new File(p2.applicationInfo.sourceDir).length();
                            return size2.compareTo(size1);
                        }
                    });
                    break;
                case 2:
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return Long.toString(p2.firstInstallTime).compareTo(Long.toString(p1.firstInstallTime));
                        }
                    });
                    break;
                case 3:
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return Long.toString(p2.lastUpdateTime).compareTo(Long.toString(p1.lastUpdateTime));
                        }
                    });
                    break;

            }
            for (PackageInfo packageInfo : packages) {
                if (!(packageManager.getApplicationLabel(packageInfo.applicationInfo).equals("") || packageInfo.packageName.equals(""))) {
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        try {
                            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), false);
                            instapp.add(tempApp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            AppInfo SystemApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), false);
                            sysapp.add(SystemApp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appAdapter = new MyAdapter(instapp);
            sysAdapter = new MyAdapter(sysapp);
            favAdapter = new MyAdapter(getFavouriteList(instapp,sysapp));
            int checknavigation = checkNavigation();
            if (checknavigation==0)
            {
                navigationView.setCheckedItem(R.id.installed_apps);
                recyclerView.setAdapter(appAdapter);
                actionBar.setTitle("Installed Apps");
            }
            else if (checknavigation==1)
            {
                navigationView.setCheckedItem(R.id.system_apps);
                recyclerView.setAdapter(sysAdapter);
                actionBar.setTitle("System Apps");
            }
            else if (checknavigation==2)
            {
                navigationView.setCheckedItem(R.id.favourite_apps);
                recyclerView.setAdapter(favAdapter);
                actionBar.setTitle("Favourite Apps");
            }
            tv1.setText(appAdapter.getItemCount()+" ");
            tv2.setText(sysAdapter.getItemCount()+" ");
            tv3.setText(favAdapter.getItemCount()+" ");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private List<AppInfo> getFavouriteList(List<AppInfo> appList, List<AppInfo> appSystemList) {
        AppPreferences appPreferences = new AppPreferences(context);
        Set<String> favapps = appPreferences.getFavouriteApps();
        for (AppInfo app: appList)
        {
            if (favapps.contains(app.getAPK()))
            {
                favapp.add(app);
            }
        }
        for (AppInfo app : appSystemList) {
            if (favapps.contains(app.getAPK())){
                favapp.add(app);
            }
        }
        return favapp;
    }
}
