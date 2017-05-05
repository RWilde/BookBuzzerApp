package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.fyp.n3015509.APIs.GoodreadsShelves;
import com.fyp.n3015509.Util.AppUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.db.dao.Buzzlist;

import java.util.ArrayList;
import java.util.List;

public class BuzzlistShelfFrag extends Fragment {

    GoodreadsShelves util = new GoodreadsShelves();
    private OnFragmentInteractionListener mListener;

    public BuzzlistShelfFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment ShelfImportFrag.
     */
    public static BuzzlistShelfFrag newInstance() {
        BuzzlistShelfFrag frag = new BuzzlistShelfFrag();
        Bundle args = new Bundle();
        return frag;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private Handler handler_ = new Handler(){
        @Override
        public void handleMessage(Message msg){

        }

    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.buzzlist_shelf_selector, container, false);

        DBUtil util = new DBUtil();
        ArrayList<Buzzlist> buzz   = util.GetBuzzlist(getContext());
        String[] buzzlistNames = new String[buzz.size()];
        ArrayList<String> buzzListNameStrings = new ArrayList<>();
        for(Buzzlist b : buzz)
        {
            buzzListNameStrings.add(b.getName());
        }

        buzzlistNames = buzzListNameStrings.toArray(buzzlistNames);

        final ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, buzzlistNames);

        final Spinner sp = new Spinner(getActivity());
        sp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sp.setAdapter(adp);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(sp);
        builder.create().show();

        return view;
    }


//    private class UserShelves extends AsyncTask<Void, Void, ArrayList<GoodreadsShelf>> {
//
//        UserShelves() {
//
//        }
//
//        @Override
//        protected ArrayList<GoodreadsShelf> doInBackground(Void... params) {
//            return util.getShelves(getContext());
//        }
//
//        protected void onPostExecute(final ArrayList<GoodreadsShelf> shelves) {
//            //showProgress(false);
//
//
//        }
//    }
}

