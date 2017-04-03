package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fyp.n3015509.Util.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.fragment.dummy.DummyContent;
import com.fyp.n3015509.bookbuzzerapp.fragment.dummy.DummyContent.DummyItem;
import com.fyp.n3015509.db.dao.Buzzlist;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class BookListFragmentFragment extends ListFragment implements AdapterView.OnItemClickListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    public BookListFragmentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booklistfragment_list, container, false);



        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<Buzzlist> buzzlist = DBUtil.GetBuzzlist(getActivity());
        ArrayList<String> buzzlistNames = new ArrayList<String>();
        String[] buzzlistForView = new String[buzzlist.size()];
        for(Buzzlist buzz : buzzlist)
        {
            buzzlistNames.add(buzz.getName());
        }
        buzzlistForView = buzzlistNames.toArray(buzzlistForView);
        ListView list = (ListView) getActivity().findViewById(R.id.list);
        list.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),
                buzzlistForView.length , buzzlistForView));


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
    }
}
