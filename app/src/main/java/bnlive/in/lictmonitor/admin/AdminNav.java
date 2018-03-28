package bnlive.in.lictmonitor.admin;

import android.app.ActionBar;
import android.support.design.widget.TabLayout;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;

import bnlive.in.lictmonitor.MainActivity;
import bnlive.in.lictmonitor.R;

import bnlive.in.lictmonitor.RegistrationLogin;
import bnlive.in.lictmonitor.common.FragmentLogin;
import bnlive.in.lictmonitor.common.FragmentRegistration;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

public class AdminNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
private TextView headerName;
private TextView headerEmail;
private TextView headerRole;
private NavigationView navigationView;
private FragmentManager fragmentManager;
private DashboardFragment dashboardFragment;
    View headerView;
Context context;
    private final String TAG="adminnav";
    SharedPreferences sharedPreferences;
    private String MyPreferences="monitordb";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context=getBaseContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        fragmentManager=getSupportFragmentManager();
        dashboardFragment=new DashboardFragment();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView=findViewById(R.id.nav_view);

        headerView=navigationView.getHeaderView(0);
        headerName=headerView.findViewById(R.id.headerName);
        headerEmail=headerView.findViewById(R.id.headerEmail);
        headerRole=headerView.findViewById(R.id.headerRole);

        sharedPreferences=context.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        headerName.setText(""+sharedPreferences.getString("name",null));
        headerEmail.setText(""+sharedPreferences.getString("email",null));
        headerRole.setText(""+sharedPreferences.getString("role",null));

//        fragmentManager.beginTransaction().replace(R.id.change_content_admin,dashboardFragment).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
        //start the service for status modification
        Intent intent=new Intent(this, DocChangeService.class);
        context.startService(intent);
        LayoutInflater inflater=LayoutInflater.from(getBaseContext());
        View view=inflater.inflate(R.layout.app_bar_admin_nav,null);
        // Set up the ViewPager with the sections adapter.
       getSupportFragmentManager();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
       mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }
long currenttime;
    @Override
    public void onBackPressed() {
        if(currenttime>System.currentTimeMillis()){
          //  onDestroy();
            System.exit(0);
        }
        else{
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
        }
        }
        currenttime=System.currentTimeMillis()+2000;
        Toast.makeText(getBaseContext(),"Press once again to exit the app",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trainer_nav, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static AdminNav.PlaceholderFragment newInstance(int sectionNumber) {
            AdminNav.PlaceholderFragment fragment = new AdminNav.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.layout_dshboardfragment, container, false);

            return rootView;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_logout) {
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                case 0:
                   DashboardFragment dashboardFragment=new DashboardFragment();
                    return dashboardFragment;
                case 1:
                    AdminMapFragment mapFragment=new AdminMapFragment();
                    return mapFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
