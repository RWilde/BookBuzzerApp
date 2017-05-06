package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsShelves;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.bookbuzzerapp.other.BuzzlistViewAdapter;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.other.ArraySwipeAdapterSample;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.google.gson.Gson;

import java.util.ArrayList;

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
        DBUtil util = new DBUtil();
        buzzlist = util.GetBuzzlist(getActivity());
        if (buzzlist.size() > 0 && !buzzlist.isEmpty()) {
            final ListView listv = (ListView) rootView.findViewById(R.id.book_list);
            final BuzzlistViewAdapter adapter = new BuzzlistViewAdapter(getActivity(), buzzlist);
            listv.setAdapter(adapter);

            inputSearch = (EditText) rootView.findViewById(R.id.inputSearch);
            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    adapter.getFilter().filter(cs);

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class BuzzlistCreate extends AsyncTask<Void, Void, Boolean> {
        private final String name;
        GoodreadsShelves util = new GoodreadsShelves();
        private final Context mContext;

        BuzzlistCreate(Context context, String buzzName) {
            this.mContext = context;
            this.name = buzzName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                DBUtil.SaveBuzzlist(mContext, name);
                BookBuzzerAPI.SaveBuzzlist(mContext, name);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private ProgressDialog pdia;

        protected void onPostExecute(final Boolean success) {
            //showProgress(false);

        }
    }
}
