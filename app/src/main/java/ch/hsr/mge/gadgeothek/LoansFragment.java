package ch.hsr.mge.gadgeothek;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.hsr.mge.gadgeothek.domain.Gadget;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoansFragment.IHandleLoansFragment} interface
 * to handle interaction events.
 */
public class LoansFragment extends Fragment {

    private IHandleLoansFragment mListener;

    public LoansFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loans, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Gadget gadget) {
        if (mListener != null) {
            mListener.onShowGadgetDetail(gadget);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IHandleLoansFragment) {
            mListener = (IHandleLoansFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IHandleLoansFragment");
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
    public interface IHandleLoansFragment {
        void onShowGadgetDetail(Gadget gadget);
    }
}
