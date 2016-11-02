package ch.hsr.mge.gadgeothek;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Loan;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoansFragment.IHandleLoansFragment} interface
 * to handle interaction events.
 */
public class LoansFragment extends Fragment {

    private IHandleLoansFragment mListener;

    private RecyclerView recyclerView;
    private GadgetsAdapter mAdapter;


    public LoansFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loans, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.loans_recycler_view);

        mAdapter = new GadgetsAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener((Context) mListener, recyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mListener.onShowLoanDetail(mAdapter.getLoanByPosition(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void loadLoans() {
        LibraryService.getLoansForCustomer(new Callback<List<Loan>>() {
            @Override
            public void onCompletion(List<Loan> input) {
                mAdapter.setLoanList(input);
            }

            @Override
            public void onError(String message) {
                mListener.snackIt("Error loading Loans: " + message);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IHandleLoansFragment) {
            mListener = (IHandleLoansFragment) context;
            loadLoans();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IHandleLoansFragment");
        }
    }

    /**
     * Code duplication to enable API Level 22 support
     */
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof IHandleLoansFragment) {
            mListener = (LoansFragment.IHandleLoansFragment) context;
            loadLoans();
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

    public interface IHandleLoansFragment {
        void onShowLoanDetail(Loan loan);
        void snackIt(String message);
    }
}
