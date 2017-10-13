package com.abhishek.aspmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    ListView lv;
    ArrayAdapter<String> ad;
    ActionBar actionBar;
    String settingdata[]={"Sort Apps by","Delete all extracted files","Remove all Favourite Apps"};
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    static int setvar;
    static Context mcontext;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mcontext = this;
        this.context=this;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);
        lv = (ListView) findViewById(R.id.mylist);
        ad = new ArrayAdapter<String>(SettingsActivity.this,android.R.layout.simple_list_item_1,settingdata);
        lv.setAdapter(ad);
        sp = getApplicationContext().getSharedPreferences("myfile",0);  //0 represent for private mode
        ed=sp.edit();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                    LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();

                    final View dialogView = inflater.inflate(R.layout.app_sort, null);
                    final RadioButton rb1 = (RadioButton) dialogView.findViewById(R.id.rb1);
                    final RadioButton rb2 = (RadioButton) dialogView.findViewById(R.id.rb2);
                    final RadioButton rb3 = (RadioButton) dialogView.findViewById(R.id.rb3);
                    final RadioButton rb4 = (RadioButton) dialogView.findViewById(R.id.rb4);
                    String sort = sp.getString("sort",null);
                    if(sort == null)
                    {
                        setvar = 0;
                    }
                    else
                    {
                        setvar = Integer.parseInt(sort);
                    }
                    if (setvar == 0)
                    {
                        rb1.setChecked(true);
                    }
                    if (setvar == 1)
                    {
                        rb2.setChecked(true);
                    }
                    if (setvar == 2)
                    {
                        rb3.setChecked(true);
                    }
                    if (setvar == 3)
                    {
                        rb4.setChecked(true);
                    }
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(rb1.isChecked())
                            {
                                ed.putString("sort","0");
                            }
                            if(rb2.isChecked())
                            {
                                ed.putString("sort","1");
                            }
                            if (rb3.isChecked())
                            {
                                ed.putString("sort","2");
                            }
                            if(rb4.isChecked())
                            {
                                ed.putString("sort","3");
                            }
                            ed.commit();
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.setTitle("Sort Apps");
                    alertDialog.show();
                }
                if(i==1)
                {
                    AlertDialog.Builder ab=new AlertDialog.Builder(SettingsActivity.this);
                    ab.setTitle("Delete All Extracted APKs");
                    ab.setMessage("Are you sure you want to delete all extracted APKs?");
                    ab.setCancelable(true);
                    ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Boolean res = deleteAppFiles();
                                    if(res)
                                    {
                                        Toast.makeText(SettingsActivity.this,"All files are deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } );
                    ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                        }
                    });
                    ab.show();
                }
                if (i==2)
                {
                    AlertDialog.Builder ab=new AlertDialog.Builder(SettingsActivity.this);
                    ab.setTitle("Remove all Favourite Apps");
                    ab.setMessage("Are you sure you want to remove all favourite Apps?");
                    ab.setCancelable(true);
                    ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                         AppPreferences appPreferences=new AppPreferences(context);
                            final Set<String> appFavourites=appPreferences.getFavouriteApps();
                            appFavourites.clear();
                        }
                    });
                    ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ab.show();
                }
            }
        });
    }

    public static Boolean deleteAppFiles() {
        Boolean res = false;
        File f = new File(Environment.getExternalStorageDirectory().toString() + "/ExtractedApk");
        try {
            if (f.exists() && f.isDirectory()) {
                File[] files = f.listFiles();
                for (File file : files) {
                    file.delete();
                }
                if (f.listFiles().length == 0) {
                    res = true;
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(mcontext,"Please give the Storage Permission from the settings", Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
        return;
    }
}
