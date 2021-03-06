package com.example.hila.myfirstapplication.controller;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hila.myfirstapplication.R;
import com.example.hila.myfirstapplication.model.backend.FactoryDataBase;
import com.example.hila.myfirstapplication.model.backend.IDataBase;
import com.example.hila.myfirstapplication.model.entities.Driver;

/**
 * this class represent the activity of driver profile that include navigation driver 4 option and 3 fragments
 */
public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Driver driver;//the user
    IDataBase fb;
    String email;//mail to find driver from shared preferences
    static ComponentName service = null;// service to see notification

    /**
     * this func create the activity
     *
     * @param savedInstanceState represent the jump of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (service == null) {//service does'nt start
            Intent intent = new Intent(getBaseContext(), NotificationService.class);//create new notification service intent
            service = startService(intent);// start the intent
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);//connect to view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//set toolbar

        //set the drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //set navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set the open fragment that show in activity
        Fragment fragment = new RandomDrive();// fragment of counter in random drive
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()//upp the  fragment
                .replace(R.id.frame_container, fragment).commit();

        //get the details of the driver that enter in login activity
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);//get reference
        email = sharedpreferences.getString("email", "");//get mail key
        fb = FactoryDataBase.getDataBase();
        driver = fb.getDriver(email);//get drive by mail
    }

    /***
     * this function close the drawer when we back from activity
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /***
     *this function check if items to the action bar present
     * @param menu menu of navigation drawer
     * @return true if items to the action bar present
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    /***
     *this function check if items to the action bar connected
     * @param item utem in menu
     * @return true if items to the action connected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /***
     * this function say what to do when every item in menu selected
     * @param item item in menu
     * @return true after item select
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_see_all_drives) {//click on available drive
            fragment = new AvailableDrivesFragment(driver);//set new fragment of available drive
        } else if (id == R.id.nav_see_my_drives) {//click on my drive
            fragment = new MyDrivesFragment(driver);//set new fragment of my drive
        } else if (id == R.id.go_web) {//click on visit in veb
            Intent broIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gett.com/il/about/"));//create new intent of web
            startActivity(broIntent);//start intent
        } else if (id == R.id.nav_exit) {//click on exit
            AlertDialog.Builder builder;//create new alert dialog
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//check the version of emulator
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("EXIT?")//set title of dialog
                    .setMessage("Are you sure you want to EXIT this app?")//set massage of dialog
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {//if click yes
                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);//open new intent
                            homeIntent.addCategory(Intent.CATEGORY_HOME);//go home screen
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//turn the flag to clear

                            //go to phones home screen
                            startActivity(homeIntent);
                            finish();//finish the active
                            System.exit(0);//exit from system
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {//wen press no
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if (fragment != null) {//click on whan of navigation item fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()//upp fragment
                    .replace(R.id.frame_container, fragment).addToBackStack(null).commit();
        }
        //find drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
