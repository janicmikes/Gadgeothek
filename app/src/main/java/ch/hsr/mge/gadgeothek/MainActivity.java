package ch.hsr.mge.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GadgetsFragment gadgetsFragment;
    ReservationsFragment reservationsFragment;
    LoansFragment loansFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gadgetsFragment = new GadgetsFragment();
        reservationsFragment = new ReservationsFragment();
        loansFragment = new LoansFragment();

        Fragment startFragment = null;
        startFragment = new LoginFragment();
        setTitle(getString(R.string.title_activity_login));
        //

        // add the starting fragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, gadgetsFragment).commit();

        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        Fragment fragment = null;

        switch (item.getItemId()){
            case R.id.action_settings:
//                fragment = new SettingsActivityFragment();
                break;

        }

        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass = null;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()){
            case R.id.nav_gadgets:
                //fragmentClass = GadgetsFragment.class;
                break;
            case R.id.nav_reservations:
                //fragmentClass = ReservationsFragment.class;
                break;
            case R.id.nav_loans:
                //fragmentClass = LoansFragment.class;
                break;
            case R.id.nav_logout:
                fragmentClass = LoginFragment.class;
                break;
            default:
                //fragmentClass = GadgetsFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();

            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } catch (Exception e){
            e.printStackTrace();
        }


        item.setChecked(true);
        setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
