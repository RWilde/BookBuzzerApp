package com.fyp.n3015509.bookbuzzerapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;

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

        String kindlePrice = "";
        String paperPrice="";
        String hardPrice="";
        if( mBook.getKindlePrice() != 0.0)
            kindlePrice = "£"+Double.toString(mBook.getKindlePrice());
        else
            kindlePrice = "Not available";
        if( mBook.getPaperbackPrice() != 0.0)
            paperPrice = "£"+Double.toString(mBook.getPaperbackPrice());
        else
            paperPrice = "Not available";
        if( mBook.getHardcoverPrice() != 0.0)
            hardPrice = "£"+Double.toString(mBook.getHardcoverPrice());
        else
            hardPrice = "Not available";


        TextView kindle = (TextView) view.findViewById(R.id.kindle_price);
        kindle.setText( kindlePrice);

        TextView paper = (TextView) view.findViewById(R.id.paper_price);
        paper.setText(paperPrice);

        TextView hard = (TextView) view.findViewById(R.id.hard_price);
        hard.setText(hardPrice);
        TextView author = (TextView) view.findViewById(R.id.author);
        StringBuilder b = new StringBuilder();
        for (GoodreadsAuthor a:mBook.getAuthors() )
        {
            if (b.length() != 0)
            {
                b.append(", ");
            }
            b.append(a.getName());
        }
        author.setText(b);

        TextView description = (TextView) view.findViewById(R.id.description);
        description.setText( mBook.getDescription());

        TextView isbn = (TextView) view.findViewById(R.id.isbn);
        isbn.setText( mBook.getIsbn());

        TextView pages = (TextView) view.findViewById(R.id.pages);
        pages.setText( mBook.getNumPages() + "pages ");

        TextView edition = (TextView) view.findViewById(R.id.format);
        edition.setText("("+mBook.getEditionInformation()+")");

        TextView series = (TextView) view.findViewById(R.id.series);
        String[] seriesString = mBook.getTitle().split("\\(");
        String[] justSeries = seriesString[1].split("\\,");

        series.setText(justSeries[0]);

        TextView published = (TextView) view.findViewById(R.id.publishing_details);
        edition.setText(mBook.getPublisher() +", "+ mBook.getReleaseDate());

        TextView shelf = (TextView) view.findViewById(R.id.bookshelf);
        edition.setText("shelF TODO");

        return view;
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
