package ch.hsr.mge.gadgeothek;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Stack;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Loan;
import ch.hsr.mge.gadgeothek.domain.Reservation;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GadgetsFragment.IHandleGadgetsFragment, ReservationsFragment.IHandleReservationsFragment, LoansFragment.IHandleLoansFragment, GadgetDetailFragment.IHandleGadgetDetailFragment {

    public static String ARG_LOGIN_EMAIL = "email";

    private Gadget detailGadget;
    private Reservation detailReservation;
    private Loan detailLoan;

    private Stack<Fragment> history = new Stack<>();
    private GadgetDetailFragment.DetailType detailType;

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

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
        history.push(gadgetsFragment);
        drawer.closeDrawer(GravityCompat.START);
        //

        String email = getIntent().getStringExtra(ARG_LOGIN_EMAIL);
        TextView email_menu = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_menu);
        email_menu.setText(email);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (history.size() > 1) {
                history.pop();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, history.peek()).commit();
            } else {
                // initial fragment reached
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case R.id.nav_gadgets:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, gadgetsFragment).commit();
                if(history.peek() != gadgetsFragment) {
                    history.push(gadgetsFragment);
                }
                break;
            case R.id.nav_reservations:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, reservationsFragment).commit();
                if(history.peek() != reservationsFragment) {
                    history.push(reservationsFragment);
                }
                break;
            case R.id.nav_loans:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, loansFragment).commit();
                if(history.peek() != loansFragment) {
                    history.push(loansFragment);
                }
                break;
            case R.id.nav_logout:
                history.clear();
                onLogout();
                break;
        }

        item.setChecked(true);
        setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onShowGadgetDetail(Gadget gadget) {
        this.detailGadget = gadget;
        detailType = GadgetDetailFragment.DetailType.GADGET;

        showDetailView();

    }

//    @Override
//    public void setTitle(String title){
//        setTitle(title);
//    }

    private void showDetailView() {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, gadgetDetailFragment).commit();
        history.push(gadgetDetailFragment);
    }

    @Override
    public Gadget getDetailGadget() {
        return detailGadget;
    }

    @Override
    public Reservation getDetailReservation() {
        return this.detailReservation;
    }

    @Override
    public Loan getDetailLoan() {
        return this.detailLoan;
    }

    public GadgetDetailFragment.DetailType getDetailType(){
        return this.detailType;
    }

    public void onLogout(){
        LibraryService.logout(new Callback<Boolean>() {

            @Override
            public void onCompletion(Boolean input) {
                if (input) {
                    finish();
                } else {
                    snackIt("Logout Failed");
                }
            }

            @Override
            public void onError(String message) {
                snackIt("Error onLogout: " + message);
            }
        });
    }

    @Override
    public void onReserveGadget() {

        LibraryService.reserveGadget(detailGadget, new Callback<Boolean>() {

            @Override
            public void onCompletion(Boolean input) {
                if (input) {
                    gadgetDetailFragment.mAddReservation.setVisibility(View.GONE);
                    snackIt("Reservation successful");
                } else {
                    snackIt("Reservation failed");
                }
            }

            @Override
            public void onError(String message) {
                snackIt("Error onReserveGadget: " + message);
            }
        });
    }

    @Override
    public void onShowReservationDetail(Reservation reservation) {
        this.detailReservation = reservation;
        this.detailType = GadgetDetailFragment.DetailType.RESERVATION;

        showDetailView();
    }

    @Override
    public void onDeleteReservation() {
        LibraryService.deleteReservation(detailReservation, new Callback<Boolean>() {

            @Override
            public void onCompletion(Boolean input) {
                if (input) {
                    gadgetDetailFragment.mDeleteReservation.setVisibility(View.GONE);
                    snackIt("Reservation deleted");
                } else {
                    snackIt("Reservation could not be deleted");
                }
            }

            @Override
            public void onError(String message) {
                snackIt("Error onDeleteReservation: " + message);
            }
        });
    }

    @Override
    public void onShowLoanDetail(Loan loan) {
        this.detailLoan = loan;
        this.detailType = GadgetDetailFragment.DetailType.LOAN;

        showDetailView();
    }

    @Override
    public void snackIt(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
