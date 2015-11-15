package com.fcu.photocollage;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.fcu.photocollage.cloudalbum.CloudAlbumFragment;
import com.fcu.photocollage.collage.CollageActivity;
import com.fcu.photocollage.member.LoginFragment;
import com.fcu.photocollage.member.SQLiteHandler;
import com.fcu.photocollage.member.SessionManager;
import com.fcu.photocollage.menu.MenuFragment;
import com.fcu.photocollage.menu.MyFragment;
import com.fcu.photocollage.movie.PhotoMovieMain;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements MyFragment {
    private String name = "";
    private String email = "";
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private SQLiteHandler db;
    private SessionManager session;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // SqLite database handler
        db = new SQLiteHandler(this.getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            HashMap<String, String> user = db.getUserDetails();
            name = user.get("name");
            email = user.get("email");
        }
        //設置 Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        //全螢幕模式
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                switchMenu(menuItem.getItemId());
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                if (session.isLoggedIn()) {
                    // Fetching user details from sqlite
                    HashMap<String, String> user = db.getUserDetails();
                    name = user.get("name");
                    email = user.get("email");
                }
                else{
                    name = "";
                    email = "";
                }
                ((TextView)drawerView.findViewById(R.id.name)).setText(name);
                ((TextView)drawerView.findViewById(R.id.email)).setText(email);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
//            selectItem(0);
        }
        fragment = new MenuFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
//        case R.id.action_websearch:
//            // create intent to perform web search for this planet
//            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
//            // catch event that there's no activity to handle intent
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//            }
//            return true;
            case R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean checkLogin(){
        if (!session.isLoggedIn()) {
            new AlertDialog.Builder(this).setTitle("要先登入哦！")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return false;
        }
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("MENU", "onActivityResult");
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntent().putExtras(intent);
    }

    @Override
    public void switchMenu(int itemId){
        MenuItem menuItem = navigationView.getMenu().findItem(itemId);
        menuItem.setChecked(true);
        switch(menuItem.getItemId()){
            case R.id.drawer_home:
                fragment = new MenuFragment();
                break;
            case R.id.drawer_member:
                fragment = new LoginFragment();
                break;
            case R.id.drawer_movie:
                if(!checkLogin()){
                    navigationView.setCheckedItem(R.id.drawer_member);
                    fragment = new LoginFragment();
                }
                else{
                    fragment = new PhotoMovieMain();
                }
                break;
            case R.id.drawer_collage:
                    fragment = new CollageActivity();
                break;
            case R.id.drawer_album:
                if(!checkLogin()){
                    navigationView.setCheckedItem(R.id.drawer_member);
                    fragment = new LoginFragment();
                }
                else {
                    fragment = new CloudAlbumFragment();
                }
                break;
            default:
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        setTitle(menuItem.getTitle());
    }

    @Override
    public void downloadManager(String urlStr, String path, String fileName) {
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlStr));
        request.setDestinationInExternalPublicDir(path, fileName);
        // request.setTitle("PhotoCollage");
        // request.setDescription("MeiLiShuo desc");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //顯示下載完是否通知，默認為下載中通知
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        // request.setMimeType("application/cn.trinea.download.file");
        long downloadId = downloadManager.enqueue(request);
    }
}
