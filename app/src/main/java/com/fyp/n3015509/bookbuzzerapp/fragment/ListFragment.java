package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.fyp.n3015509.Util.AppUtil;
import com.fyp.n3015509.apppreferences.OnScrollObserver;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.other.ListViewAdapter;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
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
    private String mListName;
    EditText inputSearch;

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

            AppUtil app = new AppUtil();

            mListName = DBUtil.getBuzzlistName(getActivity(), listId);
            booklist = DBUtil.getBooksFromBuzzlist(getActivity(), listId);

            mAdapter = app.setAdapter(booklist, listId, mListName, getActivity());
            inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
            inputSearch.setVisibility(View.GONE);

            listv.setAdapter(mAdapter);
            mAdapter.setMode(Attributes.Mode.Single);
            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //((SwipeLayout) (listv.getChildAt(position - listv.getFirstVisiblePosition()))).open(true);
                    ((SwipeLayout) (listv.getChildAt(Integer.parseInt((String)parent.getAdapter().getItem(position)) + 1))).open(true);

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
//            listv.setOnScrollListener(new OnScrollObserver() {
//                @Override
//                public void onScrollUp() {
//                    String search = inputSearch.getText().toString();
//                    if (search.matches("")){
//                    inputSearch.setVisibility(View.GONE);}
//                }
//
//                @Override
//                public void onScrollDown() {
//                    inputSearch.setVisibility(View.VISIBLE);
//                }
//            });

            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    mAdapter.getFilter().filter(cs);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }
            });
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GoodreadsBook buzz = booklist.get(position);

        Bundle args = new Bundle();
        args.putString("book", new Gson().toJson(buzz));

        Fragment fragment = new BookFragment();
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
