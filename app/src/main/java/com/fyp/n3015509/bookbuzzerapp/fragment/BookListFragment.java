package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.fyp.n3015509.Util.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.bookbuzzerapp.fragment.dummy.DummyContent;
import com.fyp.n3015509.bookbuzzerapp.fragment.dummy.DummyContent.DummyItem;
import com.fyp.n3015509.bookbuzzerapp.other.ArraySwipeAdapterSample;
import com.fyp.n3015509.bookbuzzerapp.other.ListViewAdapter;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.goodreadsDAO.GoodreadsShelf;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class BookListFragment extends ListFragment implements OnItemClickListener {
View rootView;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private BookListFragment.OnFragmentInteractionListener mListener;
    ArrayList<Buzzlist> buzzlist = new ArrayList<>();

    public BookListFragment() {
    }
    public static BookListFragment newInstance(String param1, String param2) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);
        final ListView listv = (ListView) rootView.findViewById(R.id.list);

        buzzlist = DBUtil.GetBuzzlist(getActivity());
        ArrayList<String> buzzlistNames = new ArrayList<String>();
        String[] values = new String[buzzlist.size()];
        for(Buzzlist buzz : buzzlist)
        {
            buzzlistNames.add(buzz.getName());
        }
        values = buzzlistNames.toArray(values);

        listv.setAdapter( new ArraySwipeAdapterSample<String>(getActivity(), R.layout.listview_item, R.id.position, values));

       // listv.getAdapter().setMode(Attributes.Mode.Single);
        listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // ((SwipeLayout)(listv.getChildAt(position - listv.getFirstVisiblePosition()))).open(true);
                Buzzlist buzz = buzzlist.get(position);

                Fragment fragment = new ListFragment();

                Bundle args = new Bundle();
                args.putInt("listId", buzz.getId());
                fragment.setArguments(args);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                //fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });
        listv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        listv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        listv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


       // listv.setOnItemClickListener(this);

return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Buzzlist buzz = buzzlist.get(position);

        Fragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putInt("listId", buzz.getId());
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        //fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();

       // Toast.makeText(getActivity(), "Item: " + buzz.getName(), Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
