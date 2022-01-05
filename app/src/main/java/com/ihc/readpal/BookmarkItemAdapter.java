package com.ihc.readpal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookmarkItemAdapter extends RecyclerView.Adapter<BookmarkItemAdapter.ViewHolder> {

    private final List<Bookmark> bookmarks;
    private final OnItemClickListener listener;

    /**
     * Initialize the bookmark list.
     *
     * @param bookmarks List containing the bookmarks to populate views to be used
     * by RecyclerView.
     */
    public BookmarkItemAdapter(List<Bookmark> bookmarks, OnItemClickListener listener) {
        this.bookmarks = bookmarks;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public BookmarkItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_list_item, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull BookmarkItemAdapter.ViewHolder holder, int position) {
        // Get element from the bookmark list at this position
        // and replace the contents of the view with that bookmark
        holder.setBookmark(bookmarks.get(position));
        holder.bind(listener);
    }

    // Return the size of the bookmark list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    /**
     * Custom ViewHolder, representing each item in the list holding a bookmark.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView bookmarkName;
        private int bookmarkPageNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmarkName = itemView.findViewById(R.id.bookmark_item_text);
        }

        public void bind(OnItemClickListener listener) {
            bookmarkName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(bookmarkPageNumber);
                }
            });
        }

        public void setBookmark(Bookmark bookmark) {
            bookmarkName.setText(bookmark.getName());
            bookmarkPageNumber = bookmark.getPage();
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int pageNumber);
    }
}
