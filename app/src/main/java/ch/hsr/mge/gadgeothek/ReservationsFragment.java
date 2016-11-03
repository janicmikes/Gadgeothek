package ch.hsr.mge.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Loan;
import ch.hsr.mge.gadgeothek.domain.Reservation;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReservationsFragment.IHandleReservationsFragment} interface
 * to handle interaction events.
 */
public class ReservationsFragment extends Fragment {

    private IHandleReservationsFragment mListener;

    private RecyclerView recyclerView;
    private TextView mEmptyList;
    private GadgetsAdapter mAdapter;

    public ReservationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.reservations_recycler_view);
        mEmptyList = (TextView) view.findViewById(R.id.empty_view);

        mAdapter = new GadgetsAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener((Context) mListener, recyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mListener.onShowReservationDetail(mAdapter.getReservationByPosition(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void loadReservations() {
        LibraryService.getReservationsForCustomer(new Callback<List<Reservation>>() {
            @Override
            public void onCompletion(List<Reservation> input) {
                if (!input.isEmpty()){
                    mAdapter.setReservationList(input);
                    mEmptyList.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {
                mListener.snackIt("Error loading Reservations: " + message);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _onAttach_API_independent(context);
    }

    /**
     * Code duplication for API Level 22 support
     */
    public void onAttach(Activity context) {
        super.onAttach(context);
        _onAttach_API_independent(context);
    }

    private void _onAttach_API_independent(Context context){
        if (context instanceof IHandleReservationsFragment) {
            mListener = (IHandleReservationsFragment) context;
            loadReservations();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IHandleReservationsFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface IHandleReservationsFragment {
        void onShowReservationDetail(Reservation reservation);
        void snackIt(String message);
    }
}
