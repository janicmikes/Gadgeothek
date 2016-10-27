package ch.hsr.mge.gadgeothek;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
public class GadgetsFragment extends Fragment implements RecyclerView.OnItemTouchListener {

    private IHandleGadgetsFragment mListener;

//    private List<Gadget> gadgetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GadgetsAdapter mAdapter;
    private GestureDetector gestureDetector;

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

        mAdapter = new GadgetsAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

//        gestureDetector = new GestureDetector(mListener, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
////                if (child != null && clickListener != null) {
////                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
////                }
//            }
//        });
    }

    private void loadGadgets() {
        LibraryService.getGadgets(new Callback<List<Gadget>>() {
            @Override
            public void onCompletion(List<Gadget> input) {
                mAdapter.setGadgetList(input);
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
            loadGadgets();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IHandleGadgetDetailFragment");
        }
    }

    /**
     * Code duplication to enable API Level 22 support
     */
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof IHandleGadgetsFragment) {
            mListener = (IHandleGadgetsFragment) context;
            loadGadgets();
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
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && mListener != null && gestureDetector.onTouchEvent(e)) {
//            clickListener.onClick(child, rv.getChildPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

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
