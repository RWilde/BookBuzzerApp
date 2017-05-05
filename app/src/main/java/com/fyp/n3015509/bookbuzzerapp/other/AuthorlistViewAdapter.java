package com.fyp.n3015509.bookbuzzerapp.other;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.bookbuzzerapp.fragment.AuthorFragment;
import com.fyp.n3015509.bookbuzzerapp.fragment.ListFragment;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.db.dao.Buzzlist;

import java.util.ArrayList;

/**
 * Created by tomha on 05-May-17.
 */

public class AuthorlistViewAdapter  extends BaseSwipeAdapter implements Filterable {
    private ArrayList<GoodreadsAuthor> authors;
    private ArrayList<GoodreadsAuthor> filteredAuthor = new ArrayList();
    private FragmentActivity mContext;

    private TextView text;
    private ImageView image;
    private TextView currentTime;

    public AuthorlistViewAdapter(FragmentActivity activity, ArrayList<GoodreadsAuthor> buzz) {
        this.mContext = activity;
        this.authors = buzz;
        this.filteredAuthor = buzz;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.author_view_item, null);

        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_2));

        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodreadsAuthor buzz = getItem(position);
                Fragment fragment = new AuthorFragment();

                Bundle args = new Bundle();
                args.putInt("authorId", buzz.getColumnId());
                fragment.setArguments(args);

                FragmentTransaction fragmentTransaction = mContext.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment);
                //fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        text = (TextView) convertView.findViewById(R.id.author);
        text.setText(getItem(position).getName());
    }

    @Override
    public int getCount() {
        return filteredAuthor.size();
    }

    @Override
    public GoodreadsAuthor getItem(int position) {
        return filteredAuthor.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                String s = charSequence.toString().toLowerCase();
                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = authors;
                    results.count = authors.size();
                } else {
                    ArrayList<GoodreadsAuthor> filterResultsData = new ArrayList<>();
                    try {
                        for (GoodreadsAuthor data : authors) {
                            if (data.getName() != null) {
                                if (data.getName().toLowerCase().startsWith(s)) {
                                    filterResultsData.add(data);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.values == null) {
                    filteredAuthor = new ArrayList<GoodreadsAuthor>();
                } else {
                    filteredAuthor = (ArrayList<GoodreadsAuthor>) filterResults.values;
                }
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}