package ch.hsr.mge.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Loan;
import ch.hsr.mge.gadgeothek.domain.Reservation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IHandleGadgetDetailFragment} interface
 * to handle interaction events.
 */
public class GadgetDetailFragment extends Fragment implements View.OnClickListener {

    private IHandleGadgetDetailFragment mListener;

    public enum DetailType {
        GADGET, RESERVATION, LOAN
    }

    TextView mInventoryNumber;
    TextView mNameView;
    TextView mManufacturerView;
    TextView mPrice;
    TextView mConditionView;

    TextView mLoanUntil;

    Button mAddReservation;
    Button mDeleteReservation;

    public GadgetDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gadget_detail, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInventoryNumber = (TextView) getView().findViewById(R.id.gadget_inventorynumber);
        mNameView = (TextView) getView().findViewById(R.id.gadget_name);
        mManufacturerView = (TextView) getView().findViewById(R.id.gadget_manufacturer);
        mPrice = (TextView) getView().findViewById(R.id.gadget_price);
        mConditionView = (TextView) getView().findViewById(R.id.gadget_condition);

        mLoanUntil = (TextView) getView().findViewById(R.id.loan_until);

        mAddReservation = (Button) getView().findViewById(R.id.btn_add_reservation);
        mDeleteReservation = (Button) getView().findViewById(R.id.btn_delete_reservation);

        mAddReservation.setOnClickListener(this);
        mDeleteReservation.setOnClickListener(this);

        //
        Gadget gadget = null;
        switch (mListener.getDetailType()){
            case GADGET:
                gadget = mListener.getDetailGadget();

                mLoanUntil.setVisibility(View.GONE);

                mAddReservation.setVisibility(View.VISIBLE);
                mDeleteReservation.setVisibility(View.GONE);
                break;
            case RESERVATION:
                gadget = mListener.getDetailReservation().getGadget();

                mLoanUntil.setVisibility(View.GONE);

                mAddReservation.setVisibility(View.GONE);
                mDeleteReservation.setVisibility(View.VISIBLE);
                break;
            case LOAN:
                gadget = mListener.getDetailLoan().getGadget();

                mLoanUntil.setText(mListener.getDetailLoan().getReturnDate().toString());
                mLoanUntil.setVisibility(View.VISIBLE);

                mAddReservation.setVisibility(View.GONE);
                mDeleteReservation.setVisibility(View.GONE);
                break;
        }


//        mListener.setTitle(gadget.getName());

        mInventoryNumber.setText(gadget.getInventoryNumber());
        mNameView.setText(gadget.getName());
        mManufacturerView.setText(gadget.getManufacturer());
        mPrice.setText(Double.toString(gadget.getPrice()));
        mConditionView.setText(gadget.getCondition().toString());

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

    /**
     * Code duplication for API Level 22 support
     */
    public void onAttach(Activity context) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_reservation: {
                mListener.onReserveGadget();
                break;
            }
            case R.id.btn_delete_reservation: {
                mListener.onDeleteReservation();
                break;
            }

        }
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
        Gadget getDetailGadget();
        Reservation getDetailReservation();
        Loan getDetailLoan();
        DetailType getDetailType();
//        void setTitle(String title);
        void onReserveGadget();
        void onDeleteReservation();
    }
}
