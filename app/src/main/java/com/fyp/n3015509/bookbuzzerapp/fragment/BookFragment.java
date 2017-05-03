package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.bookbuzzerapp.activity.MainActivity;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "bookId";

    // TODO: Rename and change types of parameters
    private int mBookId;
    private GoodreadsBook mBook;
    ImageView image;
    TextView title;
    private OnFragmentInteractionListener mListener;

    public BookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bookId Parameter 1.
     * @return A new instance of fragment BookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookFragment newInstance(int bookId) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBookId = getArguments().getInt(ARG_PARAM1);
        }
        mBook = DBUtil.getBookFromBuzzlist(getActivity(), mBookId);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        image = (ImageView) view.findViewById(R.id.book_cover);
        image.setImageBitmap(mBook.getImage());

        title = (TextView) view.findViewById(R.id.title);
        title.setText(mBook.getTitle());

        RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
        rating.setRating((float) mBook.getAverage_rating());

        String kindlePrice = "";
        String paperPrice = "";
        String hardPrice = "";
        if (mBook.getKindlePrice() != 0.0)
            kindlePrice = "£" + Double.toString(mBook.getKindlePrice());
        else
            kindlePrice = "Not available";
        if (mBook.getPaperbackPrice() != 0.0)
            paperPrice = "£" + Double.toString(mBook.getPaperbackPrice());
        else
            paperPrice = "Not available";
        if (mBook.getHardcoverPrice() != 0.0)
            hardPrice = "£" + Double.toString(mBook.getHardcoverPrice());
        else
            hardPrice = "Not available";


        Button amazon = (Button) view.findViewById(R.id.amazon_button);

        amazon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/dp/" + mBook.getIsbn()));
                startActivity(browserIntent);
            }
        });

        TextView kindle = (TextView) view.findViewById(R.id.kindle_price);
        kindle.setText(kindlePrice);

        TextView paper = (TextView) view.findViewById(R.id.paper_price);
        paper.setText(paperPrice);

        TextView hard = (TextView) view.findViewById(R.id.hard_price);
        hard.setText(hardPrice);
        TextView author = (TextView) view.findViewById(R.id.author);
        StringBuilder b = new StringBuilder();
        for (GoodreadsAuthor a : mBook.getAuthors()) {
            if (b.length() != 0) {
                b.append(", ");
            }
            b.append(a.getName());
        }
        author.setText(b);

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText(mBook.getDescription());

        TextView isbn = (TextView) view.findViewById(R.id.isbn);
        isbn.setText(mBook.getIsbn());

        TextView pages = (TextView) view.findViewById(R.id.pages);
        pages.setText(mBook.getNumPages() + "pages ");

        TextView edition = (TextView) view.findViewById(R.id.format);
        edition.setText("(" + mBook.getEditionInformation() + ")");
        TextView series = (TextView) view.findViewById(R.id.series);

        if (!mBook.getTitleWithoutSeries().contentEquals(mBook.getTitle())) {

            String[] seriesString = mBook.getTitle().split("\\(");
            String[] justSeries = seriesString[1].split("\\,");

            series.setText(justSeries[0]);
        }else{
        series.setText("Standalone");}

        TextView published = (TextView) view.findViewById(R.id.publishing_details);
        edition.setText(mBook.getPublisher() + ", " + mBook.getReleaseDate());

        TextView shelf = (TextView) view.findViewById(R.id.bookshelf);
        return view;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        DBUtil db = new DBUtil();
        boolean watched = db.checkIfWatched(getContext(), mBook.getId());
        try {
            if (watched == true) {
                menu.findItem(R.id.unstar_book).setVisible(true);
            } else {
                menu.findItem(R.id.star_book).setVisible(true);
            }

            menu.findItem(R.id.action_mark_all_read).setVisible(false);
            menu.findItem(R.id.action_clear_notifications).setVisible(false);
            menu.findItem(R.id.create_buzzlist).setVisible(false);
            menu.findItem(R.id.download_book).setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        MainActivity.bookIdFromBookFrag = mBook.getId();

        super.onPrepareOptionsMenu(menu);

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

    private class WatchBookTask extends AsyncTask<Void, Void, Boolean> {

        private JSONObject deletedBook = new JSONObject();
        private ProgressDialog progress;
        Context mContext;

        WatchBookTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Adding to watch list...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                BookBuzzerAPI api = new BookBuzzerAPI();
                Boolean dbSuccess = DBUtil.WatchBook(mContext, mBook.getId());
                String listName = DBUtil.findListForBook(mContext, mBook.getId());
                Boolean apiSuccess = api.WatchBook(mContext, mBook.getId(), listName);

                if (dbSuccess == false || apiSuccess == false) {
                    return false;
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            progress.dismiss();

            if (success) {
                //finish();
                Toast.makeText(mContext, "Book watched", Toast.LENGTH_SHORT).show();
            } else {
                //book wasnt deleted succesfully
                Toast.makeText(mContext, "Error with adding book to watch list, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
