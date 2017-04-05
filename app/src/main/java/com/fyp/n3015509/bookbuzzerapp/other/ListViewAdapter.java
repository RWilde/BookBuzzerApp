package com.fyp.n3015509.bookbuzzerapp.other;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.fyp.n3015509.Util.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;

public class ListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private TextView text;
    private ImageView image;
    private TextView authorText;
    String[] names;
    Bitmap[] images;
    String[] authors;
    int listId;

    public ListViewAdapter(Context mContext) {
        this.mContext = mContext;
    }


    public ListViewAdapter(FragmentActivity activity, String[] values, Bitmap[] images, String[] authors, int listId) {
        this.mContext = activity;
        this.names = values;
        this.images = images;
        this.authors = authors;
        this.listId = listId;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);

        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_2));

        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Click on surface", Toast.LENGTH_SHORT).show();
                Log.d(mContext.toString(), "click on surface");
            }
        });
        swipeLayout.findViewById(R.id.star2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Star", Toast.LENGTH_SHORT).show();
            }
        });

        swipeLayout.findViewById(R.id.trash2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean success = DBUtil.RemoveBookFromBuzzList(names[position], listId);
                if (success) Toast.makeText(mContext, "Book successfully removed", Toast.LENGTH_SHORT).show();
                else Toast.makeText(mContext, "Error with removing book from buzzlist, please try again", Toast.LENGTH_SHORT).show();
            }
        });

        swipeLayout.findViewById(R.id.magnifier2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Magnifier", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        image = (ImageView) convertView.findViewById(R.id.imageView);
        image.setImageBitmap(images[position]);

        text = (TextView)convertView.findViewById(R.id.book_name);
        text.setText(names[position]);

        authorText = (TextView)convertView.findViewById(R.id.book_author);
        authorText.setText(authors[position]);
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
