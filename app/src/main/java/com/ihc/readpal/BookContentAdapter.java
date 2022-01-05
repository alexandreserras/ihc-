package com.ihc.readpal;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BookContentAdapter extends RecyclerView.Adapter<BookContentAdapter.ViewHolder> {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private final List<String> pages;
    private final CommunicatePage listener;
    private final EditText currentPageNumber;

    public BookContentAdapter(List<String> pages, CommunicatePage listener, EditText currentPageNumber) {
        this.pages = pages;
        this.listener = listener;
        this.currentPageNumber = currentPageNumber;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "Page created!");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_content_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // TODO: Not working properly
        //currentPageNumber.setText(String.valueOf(position+1));
        //Log.d(LOG_TAG, "Changing page to position " + (position+1));
        holder.setPage(pages.get(position));
        holder.bind(listener);
        holder.resetTextView();
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView page;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            page = itemView.findViewById(R.id.book_content_page);
            page.setTextIsSelectable(true);
        }

        public void bind(CommunicatePage listener) {
            page.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    listener.selected(page);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    listener.deselected();
                }
            });
        }

        public void setPage(String content) {
            page.setText(content);
        }

        public void resetTextView() {
            page.setTextIsSelectable(true);

        }
    }

    public interface CommunicatePage {
        void selected(TextView page);
        void deselected();
    }
}
