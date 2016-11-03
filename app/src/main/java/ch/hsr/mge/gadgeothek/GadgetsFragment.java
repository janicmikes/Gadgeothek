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
import android.widget.TextView;

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

    private RecyclerView recyclerView;
    private TextView mEmptyList;
    private GadgetsAdapter mAdapter;

    public GadgetsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gadgets, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.gadgets_recycler_view);
        mEmptyList = (TextView) view.findViewById(R.id.empty_view);

        mAdapter = new GadgetsAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener((Context) mListener, recyclerView, new MainActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mListener.onShowGadgetDetail(mAdapter.getGadgetByPosition(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void loadGadgets() {
        LibraryService.getGadgets(new Callback<List<Gadget>>() {
            @Override
            public void onCompletion(List<Gadget> input) {
                if (!input.isEmpty()){
                    mAdapter.setGadgetList(input);
                    mEmptyList.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyList.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {
                mListener.snackIt("Error loading Gadgets: " + message);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _onAttach_API_independent(context);
    }
    /**
     * Code duplication to enable API Level 22 support
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        _onAttach_API_independent(activity);
    }

    private void _onAttach_API_independent(Context context){
        if (context instanceof IHandleGadgetsFragment) {
            mListener = (IHandleGadgetsFragment) context;
            loadGadgets();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IHandleGadgetsFragment");
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
        void onShowGadgetDetail(Gadget gadget);
        void snackIt(String message);
    }
}
