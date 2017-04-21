package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.NotificationTypes;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    ArrayList<BuzzNotification> notifications = new ArrayList<>();
    NotificationsViewAdapter mAdapter;
    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        final ListView listv = (ListView) rootView.findViewById(R.id.not_list);
        notifications = DBUtil.GetWatchNotifications(getActivity());

        ArrayList<String> bookName = new ArrayList<>();
        ArrayList<Boolean> notified = new ArrayList<>();
        ArrayList<NotificationTypes> notTypes = new ArrayList<>();
        ArrayList<Integer> bookIds = new ArrayList<>();
        ArrayList<Bitmap> bookImage = new ArrayList<>();

        String[] bookNameArray = new String[notifications.size()];
        Boolean[] notifiedArray = new Boolean[notifications.size()];
        NotificationTypes[] notTypesArray = new NotificationTypes[notifications.size()];
        Integer[] bookIdsArray = new Integer[notifications.size()];
        Bitmap[] bookImageArray = new Bitmap[notifications.size()];

        for (BuzzNotification buzz : notifications) {
            bookName.add(buzz.getBookName());
            notified.add(buzz.getNotified());
            bookIds.add(buzz.getBookId());
            notTypes.add(buzz.getType());
            bookImage.add(buzz.getImage());
        }
        bookNameArray = bookName.toArray(bookNameArray);
        notifiedArray = notified.toArray(notifiedArray);
        notTypesArray = notTypes.toArray(notTypesArray);
        bookIdsArray = bookIds.toArray(bookIdsArray);
        bookImageArray = bookImage.toArray(bookImageArray);

        mAdapter = new NotificationsViewAdapter(getActivity(), bookNameArray, notifiedArray, notTypesArray, bookIdsArray, bookImageArray);

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
