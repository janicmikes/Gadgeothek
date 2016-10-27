package ch.hsr.mge.gadgeothek;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.service.Callback;
import ch.hsr.mge.gadgeothek.service.LibraryService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GadgetsFragment.IHandleGadgetsFragment} interface
 * to handle interaction events.
 */
public class GadgetsFragment extends Fragment {

    private IHandleGadgetsFragment mListener;

    private List<Gadget> gadgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GadgetsAdapter mAdapter;

    public GadgetsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gadgets, container, false);
        // TODO: Bind the list actions to the listener
        // onSelect: onItemSelected(Convert List item to Gadget);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.gadgets_recycler_view);

        mAdapter = new GadgetsAdapter(gadgetList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        loadGadgets();
    }

    private void loadGadgets() {
        LibraryService.getGadgets(new Callback<List<Gadget>>() {
            @Override
            public void onCompletion(List<Gadget> input) {
                gadgetList = input;
                mListener.snackIt("New Gadgets Loaded");
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                mListener.snackIt("Error loading Gadgets: " + message);
            }
        });
    }

    public void onItemSelected(Gadget gadget) {
        if (mListener != null) {
            mListener.onShowGadgetDetail(gadget);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IHandleGadgetsFragment) {
            mListener = (IHandleGadgetsFragment) context;

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
    public interface IHandleGadgetsFragment {
        // TODO: Update argument type and name
        void onShowGadgetDetail(Gadget gadget);
        void snackIt(String message);
    }
}
