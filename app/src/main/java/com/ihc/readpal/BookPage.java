package com.ihc.readpal;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class BookPage extends MainActivity {

    private static final String LOG_TAG = BookPage.class.getSimpleName();

    public static final String BOOK_CONTENT = "com.ihc.readpal.extra.BOOK_CONTENT";
    public static final String PAGE_NUMBER = "com.ihc.readpal.extra.BOOK_CONTENT";

    boolean control=false;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

        // Get chosen book from the MyBooks/SearchPage
        Intent intent = getIntent();
        book = intent.getParcelableExtra(MyBooks.CHOSEN_BOOK);

        // Set page content
        TextView txtBookTitle = findViewById(R.id.Book_title);
        txtBookTitle.setText(String.format(
                getString(R.string.Book_title),
                book.gettitulo()
        ));
        TextView txtBookAuthor = findViewById(R.id.Book_author);
        txtBookAuthor.setText(String.format(
                getString(R.string.Book_author),
                book.getauthor()
        ));
        TextView txtImportDate = findViewById(R.id.Book_import_date);
        txtImportDate.setText(String.format(
                getString(R.string.Book_import_date),
                book.getcdate()
        ));
        ImageView imgCover = findViewById(R.id.Book_cover);
        imgCover.setImageURI(book.getUri_book_image());
        // TODO: missing release date definition

        ImageButton backButton = findViewById(R.id.bookpage_toolbar_back);
        backButton.setOnClickListener(v -> finish());
        backButton.setBackgroundResource(R.drawable.ic_back_arrow);
    }
}