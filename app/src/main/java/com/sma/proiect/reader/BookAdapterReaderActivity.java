package com.sma.proiect.reader;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.sma.proiect.AppState;
import com.sma.proiect.Book;
import com.sma.proiect.BookType;
import com.sma.proiect.R;
import com.sma.proiect.user.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookAdapterReaderActivity extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> books;
    private int layoutResID;
    private boolean isCurrentBookBorrowed = false;

    public BookAdapterReaderActivity(Context context, int layoutResourceID, List<Book> books) {
        super(context, layoutResourceID, books);
        this.context = context;
        this.books = books;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookAdapterReaderActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new BookAdapterReaderActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.lHeader = view.findViewById(R.id.lHeader);
            itemHolder.tTitle = view.findViewById(R.id.tTitle);
            itemHolder.tAuthor = view.findViewById(R.id.tAuthorValue);
            itemHolder.tPublisher = view.findViewById(R.id.tPublisherValue);
            itemHolder.tPublicationDate = view.findViewById(R.id.tPublicationDateValue);
            itemHolder.tGenre = view.findViewById(R.id.tGenreValue);
            itemHolder.tISBN10 = view.findViewById(R.id.tISBN10Value);
            itemHolder.tNumOfBooksValue = view.findViewById(R.id.tNumOfBooksValue);
            itemHolder.iAdd = view.findViewById(R.id.iAddReq);
            itemHolder.iDelete = view.findViewById(R.id.iDelete);

            itemHolder.iAdd.setVisibility(View.VISIBLE);
            itemHolder.iDelete.setVisibility(View.INVISIBLE);

            view.setTag(itemHolder);

        } else {
            itemHolder = (BookAdapterReaderActivity.ItemHolder) view.getTag();
        }

        // current values of book
        final Book bItem = books.get(position);

        String title = bItem.getTitle();
        String author = bItem.getAuthor();
        String publisher = bItem.getPublisher();
        String publicationDate = bItem.getPublicationDate();
        String genre = bItem.getGenre();
        String ISBN10 = bItem.getISBN10();
        String tNumOfBooksValue = bItem.getNumOfBooks();


        itemHolder.lHeader.setBackgroundColor(BookType.getColorFromBookGenre(bItem.getGenre()));
        itemHolder.tTitle.setText(title);
        itemHolder.tAuthor.setText(author);
        itemHolder.tPublisher.setText(publisher);
        itemHolder.tPublicationDate.setText(publicationDate);
        itemHolder.tGenre.setText(genre);
        itemHolder.tISBN10.setText(ISBN10);
        itemHolder.tNumOfBooksValue.setText(tNumOfBooksValue);

        User currentUser = AppState.get().getCurrentUser();

        itemHolder.iAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ... request book at "position"
                isCurrentBookBorrowed = false;
                checkIfCurrentBookIsBorrowed(ISBN10, itemHolder);
                if (!isCurrentBookBorrowed) {
                    requestBook(currentUser, ISBN10, title);
                }
                isCurrentBookBorrowed = false;
            }
        });

        itemHolder.iDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ... delete book request at "position"
                deleteBookRequest(currentUser, ISBN10);
                itemHolder.iAdd.setVisibility(View.VISIBLE);
                itemHolder.iDelete.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    public static class ItemHolder {
        TextView tTitle;
        TextView tAuthor;
        TextView tPublisher;
        TextView tPublicationDate;
        TextView tGenre;
        TextView tISBN10;
        TextView tNumOfBooksValue;
        RelativeLayout lHeader;
        ImageView iAdd, iDelete;
    }

    private void requestBook(User currentUser, String ISBN10, String title) {
        String currentUserUID = currentUser.getUserID();
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        Map<String, Object> map = new HashMap<>();
        map.put("Title", title);
        map.put("ISBN10", ISBN10);
        map.put("Submitted", 0);
        databaseReference.child("bookRequests").child(currentUserUID).child(ISBN10).updateChildren(map);
    }

    private void deleteBookRequest(User currentUser, String ISBN10) {
        String currentUserUID = currentUser.getUserID();
        AppState.get().getDatabaseReference().child("bookRequests").child(currentUserUID).child(ISBN10).removeValue();
    }

    /**
     * Function sets isCurrentBookBorrowed to 1 if the current book has been borrowed.
     * In the contrary, the function sets isCurrentBookBorrowed variable to 0.
     * @param ISBN10 - String which identifies in the database, whether the book has been borrowed
     *               by the current user
     */
    private void checkIfCurrentBookIsBorrowed(String ISBN10, BookAdapterReaderActivity.ItemHolder itemHolder) {
        DatabaseReference databaseReference = AppState.get().getDatabaseReference();
        String currentUID = AppState.get().getUserID();
        databaseReference.child("bookRequests").child(currentUID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(ISBN10)) {
                    isCurrentBookBorrowed = true;
                    itemHolder.iAdd.setVisibility(View.INVISIBLE);
                    itemHolder.iDelete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}