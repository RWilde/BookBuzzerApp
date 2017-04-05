package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.fyp.n3015509.Util.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.other.ListViewAdapter;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    ArrayList<GoodreadsBook> booklist = new ArrayList<>();
    private ListFragment.OnFragmentInteractionListener mListener;
    View rootView;
    private ListViewAdapter mAdapter;

    public ListFragment() {
    }

    public static ListFragment newInstance(String param1, String param2) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        final ListView listv = (ListView) rootView.findViewById(R.id.list);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int listId = bundle.getInt("listId");
            booklist = DBUtil.getBooksFromBuzzlist(getActivity(), listId);
            ArrayList<String> buzzlistNames = new ArrayList<>();
            ArrayList<Bitmap> buzzlistImages = new ArrayList<>();
            ArrayList<String> buzzlistAuthors = new ArrayList<>();
            Bitmap[] images = new Bitmap[booklist.size()];
            String[] values = new String[booklist.size()];
            String[] authors = new String[booklist.size()];

            for (GoodreadsBook buzz : booklist) {
                buzzlistNames.add(buzz.getTitle());
                buzzlistImages.add(buzz.getSmallImage());
                String authorList = "";
                int count = 0;
                for (GoodreadsAuthor auth : buzz.getAuthors())
                {
                    authorList += auth.getName();
                    if (count >=1 )
                    {
                        authorList += ", ";
                    }
                    count++;
                }
                buzzlistAuthors.add(authorList);
            }
            values = buzzlistNames.toArray(values);
            images = buzzlistImages.toArray(images);
            authors = buzzlistAuthors.toArray(authors);

            mAdapter = new ListViewAdapter(getActivity(), values, images, authors, listId);

            listv.setAdapter(mAdapter);
            mAdapter.setMode(Attributes.Mode.Single);
            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((SwipeLayout) (listv.getChildAt(position - listv.getFirstVisiblePosition()))).open(true);
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

            // ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
            //         android.R.layout.simple_list_item_1, values);
            // listv.setAdapter(adapter);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoodreadsBook buzz = booklist.get(position);

        Fragment fragment = new BookFragment();

        Bundle args = new Bundle();
        args.putString("book", new Gson().toJson(buzz));
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
