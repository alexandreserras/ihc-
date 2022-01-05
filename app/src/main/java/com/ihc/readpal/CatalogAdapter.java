package com.ihc.readpal;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private final List<Book> catalog;
    private final GoReadListener goReadListener;
    private final CommandsListener commandsListener;

    public CatalogAdapter(List<Book> catalog, GoReadListener goReadListener, CommandsListener commandsListener) {
        this.catalog = catalog;
        this.goReadListener = goReadListener;
        this.commandsListener = commandsListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @NotNull
    @Override
    public CatalogAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        // Top commands
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_top, parent, false);
            return new CatalogAdapter.ViewHolderTop(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
            return new CatalogAdapter.ViewHolderItem(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CatalogAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            ((ViewHolderTop) holder).bind(commandsListener);
        }
        else {
            ((ViewHolderItem) holder).setBook(catalog.get(position));
            ((ViewHolderItem) holder).bind(goReadListener);
        }

    }

    @Override
    public int getItemCount() {
        return catalog.size();
    }

    /**
    * Custom ViewHolder, representing each book in the catalog.
    */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolderItem extends ViewHolder {

        private final TextView bookTitle;
        private final ImageView bookCover;
        private Book book;

        public ViewHolderItem(@NonNull @NotNull View itemView) {
            super(itemView);

            bookTitle = itemView.findViewById(R.id.book_title);
            bookCover = itemView.findViewById(R.id.book_cover);
        }

        public void setBook(Book book) {
            bookTitle.setText(book.gettitulo());
            bookCover.setImageURI(book.getUri_book_image());
            this.book = book;
        }

        public void bind(GoReadListener goReadListener) {
            bookCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goReadListener.read(book);
                }
            });
        }
    }

    public static class ViewHolderTop extends ViewHolder {

        private final ImageButton btnImport;
        private final ImageButton btnFilter;
        private final ImageButton btnReset;
        private final TextView filterDesc;

        public ViewHolderTop(@NonNull @NotNull View itemView) {
            super(itemView);

            btnImport = itemView.findViewById(R.id.catalog_import);
            btnFilter = itemView.findViewById(R.id.filter_menu);
            btnReset = itemView.findViewById(R.id.reset_filter);
            filterDesc = itemView.findViewById(R.id.tvSelectedChoices);
        }

        public void bind(CommandsListener commandsListener) {
            btnImport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commandsListener.imprt();
                }
            });
            btnFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commandsListener.filter(filterDesc);
                }
            });
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commandsListener.reset();
                }
            });
        }
    }

    public interface GoReadListener {
        void read(Book book);
    }

    public interface CommandsListener {
        void filter(TextView filterDesc);
        void reset();
        void imprt();
    }
}
