package ch.hsr.mge.gadgeothek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Reservation;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GadgetsFragment.IHandleGadgetsFragment, ReservationsFragment.IHandleReservationsFragment, LoansFragment.IHandleLoansFragment, GadgetDetailFragment.IHandleGadgetDetailFragment {

    GadgetsFragment gadgetsFragment;
    ReservationsFragment reservationsFragment;
    LoansFragment loansFragment;
    GadgetDetailFragment gadgetDetailFragment;

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
        gadgetDetailFragment = new GadgetDetailFragment();
        //



        //
        setTitle(getString(R.string.nav_gadgets));
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
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case R.id.nav_gadgets:
                snackIt("Showing Gadgets");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, gadgetsFragment).commit();
                break;
            case R.id.nav_reservations:
                snackIt("Showing Reservations");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, reservationsFragment).commit();
                break;
            case R.id.nav_loans:
                snackIt("Showing Loans");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, loansFragment).commit();
                break;
            case R.id.nav_logout:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                getApplicationContext().startActivity(intent);
                break;
        }

        item.setChecked(true);
        setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onShowGadgetDetail(Gadget gadget) {
        snackIt("Show Details");
        // TODO: Detailview mit den entsprechenden Daten abfuellen
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, gadgetDetailFragment).commit();
    }

    @Override
    public void onReserveGadget(Gadget gadget) {
        snackIt("Reserve a Gadget...");
        LibraryService.reserveGadget(gadget, new Callback<Boolean>() {

            @Override
            public void onCompletion(Boolean input) {
                if (input) {
                    snackIt("Reservation successful");
                } else {
                    snackIt("Reservation NOT successful");
                }
            }

            @Override
            public void onError(String message) {
                snackIt("Error onReserveGadget: " + message);
            }
        });
    }

    @Override
    public void onDeleteReservation(Reservation reservation) {
        LibraryService.deleteReservation(reservation, new Callback<Boolean>() {

            @Override
            public void onCompletion(Boolean input) {
                if (input) {
                    snackIt("Reservation deleted");
                } else {
                    snackIt("Reservation NOT deleted");
                }
            }

            @Override
            public void onError(String message) {
                snackIt("Error onDeleteReservation: " + message);
            }
        });
    }

    @Override
    public void snackIt(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
