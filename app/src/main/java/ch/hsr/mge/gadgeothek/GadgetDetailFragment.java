package ch.hsr.mge.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Loan;
import ch.hsr.mge.gadgeothek.domain.Reservation;
import ch.hsr.mge.gadgeothek.service.IDecoratorService;
import ch.hsr.mge.gadgeothek.service.LocalDecoratorService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IHandleGadgetDetailFragment} interface
 * to handle interaction events.
 */
public class GadgetDetailFragment extends Fragment implements View.OnClickListener {

    private IHandleGadgetDetailFragment mListener;
    private IDecoratorService decorator;

    public enum DetailType {
        GADGET, RESERVATION, LOAN
    }

    TextView mInventoryNumber;
    TextView mNameView;
    TextView mManufacturerView;
    TextView mPrice;
    TextView mConditionView;
    ImageView mManufacturerLogoView;

    TextView mLoanSince;
    TextView mLoanSinceLabel;

    Button mAddReservation;
    Button mDeleteReservation;

    public GadgetDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        decorator = LocalDecoratorService.getDecoratorService();
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

        mLoanSince = (TextView) getView().findViewById(R.id.loan_since);
        mLoanSinceLabel = (TextView) getView().findViewById(R.id.labelLoanSince);

        mAddReservation = (Button) getView().findViewById(R.id.btn_add_reservation);
        mDeleteReservation = (Button) getView().findViewById(R.id.btn_delete_reservation);

        mManufacturerLogoView = (ImageView) getView().findViewById(R.id.gadget_manufacturer_logo);

        mAddReservation.setOnClickListener(this);
        mDeleteReservation.setOnClickListener(this);

        //
        Gadget gadget = null;
        switch (mListener.getDetailType()){
            case GADGET:
                gadget = mListener.getDetailGadget();

                mLoanSince.setVisibility(View.GONE);
                mLoanSinceLabel.setVisibility(View.GONE);

                mAddReservation.setVisibility(View.VISIBLE);
                mDeleteReservation.setVisibility(View.GONE);
                break;
            case RESERVATION:
                gadget = mListener.getDetailReservation().getGadget();

                mLoanSince.setVisibility(View.GONE);
                mLoanSinceLabel.setVisibility(View.GONE);

                mAddReservation.setVisibility(View.GONE);
                mDeleteReservation.setVisibility(View.VISIBLE);
                break;
            case LOAN:
                gadget = mListener.getDetailLoan().getGadget();

                mLoanSince.setText(mListener.getDetailLoan().getPickupDate().toString());
                mLoanSince.setVisibility(View.VISIBLE);
                mLoanSinceLabel.setVisibility(View.VISIBLE);

                mAddReservation.setVisibility(View.GONE);
                mDeleteReservation.setVisibility(View.GONE);
                break;
        }

        mInventoryNumber.setText(gadget.getInventoryNumber());
        mNameView.setText(gadget.getName());
        mManufacturerView.setText(gadget.getManufacturer());
        mPrice.setText(Double.toString(gadget.getPrice()));
        mConditionView.setText(gadget.getCondition().toString());
        mManufacturerLogoView.setImageResource(decorator.getDrawableIdForManufacturerName(gadget.getManufacturer()));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _onAttach_API_independent(context);
    }
    /**
     * Code duplication for API Level 22 support
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _onAttach_API_independent(activity);
    }

    private void _onAttach_API_independent(Context context){
        if (context instanceof IHandleGadgetDetailFragment) {
            this.mListener = (IHandleGadgetDetailFragment) context;
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
        void onReserveGadget();
        void onDeleteReservation();
    }
}
