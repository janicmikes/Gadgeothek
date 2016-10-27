package ch.hsr.mge.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Reservation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReservationsFragment.IHandleReservationsFragment} interface
 * to handle interaction events.
 */
public class ReservationsFragment extends Fragment {

    private IHandleReservationsFragment mListener;

    public ReservationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Reservation reservation) {
        if (mListener != null) {
            mListener.onDeleteReservation(reservation);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IHandleReservationsFragment) {
            mListener = (IHandleReservationsFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IHandleReservationsFragment");
        }
    }

    /**
     * Code duplication for API Level 22 support
     */
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof IHandleReservationsFragment) {
            mListener = (IHandleReservationsFragment) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface IHandleReservationsFragment {
        void onShowGadgetDetail(Gadget gadget);
        void onDeleteReservation(Reservation reservation);
    }
}
