package com.abhishek.aspmanager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class AppDetails extends AppCompatActivity implements Animation.AnimationListener{
    private static final int MY_PERMISSION = 10;
    ImageView imageView;
    ImageButton ib;
    TextView name,version;
    AppInfo appInfo;
    CardView open,extract,permission_card,uninstall,clearcache,cleardata;
    Animation bounce,blink;
    AppPreferences appPreferences;
    ActionBar actionBar;
    int UNINSTALL_REQUEST_CODE = 1;
    Context context;
    ProgressDialog pd;
    View myview,myview2;
    Boolean appFav=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_details);
        this.context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        imageView = (ImageView) findViewById(R.id.app_icon);
        ib= (ImageButton) findViewById(R.id.favourite);
        name = (TextView) findViewById(R.id.app_name);
        version =(TextView) findViewById(R.id.version);
        open =(CardView) findViewById(R.id.open);
        extract = (CardView) findViewById(R.id.extract);
        permission_card = (CardView) findViewById(R.id.permission_card);
        uninstall = (CardView) findViewById(R.id.uninstall);
        clearcache= (CardView) findViewById(R.id.clearcache);
        cleardata= (CardView) findViewById(R.id.cleardata);
        String appName = getIntent().getStringExtra("appName");
        String appApk = getIntent().getStringExtra("app_apk");
        String Version = getIntent().getStringExtra("Version");
        String appSource = getIntent().getStringExtra("app_source");
        String appData = getIntent().getStringExtra("app_data");
        Bitmap bitmap = getIntent().getParcelableExtra("app_icon");
        Drawable appIcon = new BitmapDrawable(getResources(), bitmap);
        Boolean appIsSystem = getIntent().getExtras().getBoolean("app_isSystem");
        appPreferences=new AppPreferences(context);
        appInfo = new AppInfo(appName, appApk, Version, appSource, appData, appIcon, appIsSystem);

        imageView.setImageDrawable(appIcon);
        name.setText(appName);
        version.setText(Version);

        bounce = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bounce);
        bounce.setAnimationListener(this);
        imageView.startAnimation(bounce);

        blink = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
        blink.setAnimationListener(this);
        final Set<String> appFavourites=appPreferences.getFavouriteApps();
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ib.startAnimation(blink);
                if (appFav)
                {
                    appFavourites.remove(appInfo.getAPK());
                    appPreferences.setFavouriteApps(appFavourites);
                    ib.setImageResource(R.drawable.favouriteicon);
                    appFav=false;
                    Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    appFavourites.add(appInfo.getAPK());
                    appPreferences.setFavouriteApps(appFavourites);
                    ib.setImageResource(R.drawable.hearticon);
                    appFav=true;
                    Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /* check app is favourite or not */
        if(appFavourites.contains(appInfo.getAPK()))
        {
            appFav=true;
            ib.setImageResource(R.drawable.hearticon);
        }
        else
        {
            appFav=false;
            ib.setImageResource(R.drawable.favouriteicon);
        }
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                open.startAnimation(blink);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(appInfo.getAPK());
                            startActivity(intent);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Snackbar.make(view, "Can't open this App", Snackbar.LENGTH_LONG)
                                    .setDuration(3000).setAction("Action", null).show();
                        }
                    }
                },200);
            }
        });
        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                extract.startAnimation(blink);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd = new ProgressDialog(AppDetails.this);
                        pd.setTitle("Extracting");
                        pd.setMessage("This may take a while");
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setCancelable(false);
                        myview = view;
                        checkPermission();
                    }
                },200);

            }
        });
        uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                myview2=view;
                uninstall.startAnimation(blink);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        intent.setData(Uri.parse("package:"+appInfo.getAPK()));

                        intent.putExtra(Intent.EXTRA_RETURN_RESULT,true);
                        startActivityForResult(intent,UNINSTALL_REQUEST_CODE);
                    }
                },200);
            }
        });
        permission_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permission_card.startAnimation(blink);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent obj = new Intent(getApplicationContext(),PermissionsActivity.class);
                        obj.putExtra("package",appInfo.getAPK());
                        startActivity(obj);
                    }
                },200);
            }
        });
        clearcache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearcache.startAnimation(blink);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Boolean root=isrooted();
                        if (root)
                        {
                            clearcache.startAnimation(blink);
                            try {
                                String directory=appInfo.getData() + "/cache/**";
                                String[] command = new String[]{"su", "-c", "rm -rf " + directory};
                                Process process = Runtime.getRuntime().exec(command);
                                process.waitFor();
                                Toast.makeText(context, "Cache Cleared", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Cannot Clear Cache", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },200);
            }
        });
        cleardata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleardata.startAnimation(blink);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Boolean root=isrooted();
                        if (root)
                        {
                            cleardata.startAnimation(blink);
                            try {String directory= appInfo.getData() + "/**";
                                String[] command = new String[]{"su", "-c", "rm -rf " + directory};
                                Process process = Runtime.getRuntime().exec(command);
                                process.waitFor();
                                Toast.makeText(context, "Data Cleared", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Cannot Clear Data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                },200);
            }
        });
    }
    public boolean isrooted()
    {
        Process p;
        try {
            // Preform su to get root privledges
            p = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                if (p.exitValue() != 255) {
                    // TODO Code to run on success
                    //Toast.makeText(context, "Root", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else {
                    // TODO Code to run on unsuccessful
                    Toast.makeText(context, "Your Phone is Not Rooted", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                Toast.makeText(context, "Your Phone is Not Rooted", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            Toast.makeText(context, "Your Phone is Not Rooted", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void checkPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION);
        }
        else
        {
            new Extract().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case  MY_PERMISSION :
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    new Extract().execute();
                }
                else
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))
                    {
                        new AlertDialog.Builder(this).setTitle("Storage").setMessage("You need to grant permission").show();
                    }
                    else
                    {
                        new AlertDialog.Builder(this).setTitle("Storage permission denied").setMessage("You need to grant permission in the setting").show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==UNINSTALL_REQUEST_CODE)
        {
            if (resultCode==RESULT_OK)
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
            else
            {
                Snackbar.make(myview2, "Can't Uninstall this App", Snackbar.LENGTH_LONG)
                        .setDuration(3000).setAction("Action", null).show();
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.share)
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(appInfo.getSource())));
            intent.setType("application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, String.format("Share %s using", appInfo.getName())));
        }
        else if(id == android.R.id.home)
        {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    class Extract extends AsyncTask<Void,String,Void> {
        String path1 = Environment.getExternalStorageDirectory().toString() + "/ExtractedApk";
        File f2;
        String filename;
        Extract()
        {
            pd.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            File f = new File(appInfo.getSource());
            filename = appInfo.getName();
            try {
                String info = Environment.getExternalStorageState();
                if (info.equals(Environment.MEDIA_MOUNTED)) {
                    f2 = new File(Environment.getExternalStorageDirectory().toString() + "/ExtractedApk");
                }
                else {
                    f2 = getCacheDir();
                }
                if (!f2.exists())
                    f2.mkdirs();
                f2 = new File(f2.getPath() + "/" + filename + ".apk");
                if (!f2.exists()) {
                    f2.createNewFile();
                    InputStream in = new FileInputStream(f);
                    OutputStream out = new FileOutputStream(f2);
                    byte[] bf = new byte[1024];
                    int len;
                    while ((len = in.read(bf)) > 0) {
                        out.write(bf, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
            catch (FileNotFoundException ex) {
                System.out
                        .println(ex.getMessage() + " in the specified directory.");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            super.onPostExecute(aVoid);
            Snackbar snackbar=Snackbar.make(myview, "Apk Saved in "+path1, Snackbar.LENGTH_LONG).setDuration(5000).setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean deleted = f2.delete();
                    if (deleted)
                    {
                        Toast.makeText(context, "deleted succesfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context, "deletion failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            snackbar.show();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AppDetails.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }
}
