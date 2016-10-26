package ch.hsr.mge.gadgeothek;

import android.content.Context;
import android.net.Uri;
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
 * {@link IHandleGadgetDetailFragment} interface
 * to handle interaction events.
 */
public class GadgetDetailFragment extends Fragment {

    private IHandleGadgetDetailFragment mListener;

    public GadgetDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gadget_detail, container, false);
    }

    public void onReserveGadget(Gadget gadget) {
        if (mListener != null) {
            mListener.onReserveGadget(gadget);
        }
    }

    public void onDeleteReservation(Reservation reservation) {
        if (mListener != null) {
            mListener.onDeleteReservation(reservation);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IHandleGadgetDetailFragment) {
            mListener = (IHandleGadgetDetailFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IHandleGadgetDetailFragment");
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
    public interface IHandleGadgetDetailFragment {
        void onReserveGadget(Gadget gadget);
        void onDeleteReservation(Reservation reservation);
    }
}
