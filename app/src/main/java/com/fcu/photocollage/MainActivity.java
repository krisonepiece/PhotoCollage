package com.fcu.photocollage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fcu.photocollage.cloudalbum.CloudAlbumFragment;
import com.fcu.photocollage.member.LoginFragment;
import com.fcu.photocollage.member.SQLiteHandler;
import com.fcu.photocollage.member.SessionManager;
import com.fcu.photocollage.menu.MenuFragment;
import com.fcu.photocollage.menu.MyAdapter;
import com.fcu.photocollage.movie.PhotoMovieMain;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    private String name = "";
    private String email = "";
    String TITLES[] = {"主頁","會員","相片電影","協作相簿"};
    int ICONS[] = {R.mipmap.ic_home_white_24dp,
            R.mipmap.ic_person_white_24dp,
            R.mipmap.ic_movie_creation_white_24dp,
            R.mipmap.ic_cloud_queue_white_24dp};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    int PROFILE = R.drawable.unnamed;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private DrawerLayout mDrawerLayout;
    //private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private SQLiteHandler db;
    private SessionManager session;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuTitles;
    private int[] mMenuIcon = {
            R.mipmap.ic_home_white_24dp,
            R.mipmap.ic_person_white_24dp,
            R.mipmap.ic_movie_creation_white_24dp,
            R.mipmap.ic_cloud_queue_white_24dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // SqLite database handler
        db = new SQLiteHandler(this.getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();
            name = user.get("name");
            email = user.get("email");
        }
        //設置 Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS,name,email,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
/***/
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                int idx = recyclerView.getChildPosition(child);
                Fragment fragment = null;
                if(idx == 1){
                    fragment = new MenuFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    setTitle(mMenuTitles[idx-1]);
                }
                else if(idx == 2){
                    fragment = new LoginFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    setTitle(mMenuTitles[idx-1]);
                }
                else if(idx == 3){
                    fragment = new PhotoMovieMain();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    setTitle(mMenuTitles[idx-1]);
                }
                else if(idx == 4){
                    //checkLogin();
                    fragment = new CloudAlbumFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    setTitle(mMenuTitles[idx - 1]);
                }
                else{

                }


                //mDrawerList.setItemChecked(position, true);

                mDrawerLayout.closeDrawers();
                //mDrawerLayout.closeDrawer(mDrawerList);



//                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
//                    mDrawerLayout.closeDrawers();
//                    return true;
//                }
                return true;
            }
            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
/***/
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        mTitle = mDrawerTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuTitles));
        List<HashMap<String,String>> lstData = new ArrayList<HashMap<String,String>>();
        for (int i = 0; i < 4; i++) {
            HashMap<String, String> mapValue = new HashMap<String, String>();
            mapValue.put("icon", Integer.toString(mMenuIcon[i]));
            mapValue.put("title", mMenuTitles[i]);
            lstData.add(mapValue);
        }
//        SimpleAdapter adapter = new SimpleAdapter(this, lstData, R.layout.drawer_list_item, new String[]{"icon", "title"}, new int[]{R.id.drawerIcon, R.id.txtItem});
//        mDrawerList.setAdapter(adapter);
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    /* The click listner for ListView in the navigation drawer */
//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            selectItem(position);
//        }
//    }

//    private void selectItem(int position) {
//        Fragment fragment = null;
//        if(position == 0){
//            fragment = new MenuFragment();
//        }
//        else if(position == 1){
//            fragment = new LoginFragment();
//        }
//        else if(position == 2){
//            fragment = new PhotoMovieMain();
//        }
//        else if(position == 3){
//            checkLogin();
//            fragment = new CloudAlbumFragment();
//        }
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//        mDrawerList.setItemChecked(position, true);
//        setTitle(mMenuTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
//    }
    public void checkLogin(){
        if (!session.isLoggedIn()) {
            new AlertDialog.Builder(this).setTitle("要先登入哦！")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            selectItem(1);	//跳轉登入頁
                        }
                    })
                    .show();
        }
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
}
