package com.ihc.readpal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MyBooks extends MainActivity implements MultipleChoiceDialogFragment.onMultiChoiceListener {

    public static final String CHOSEN_BOOK = "com.ihc.readpal.extra.CHOSEN_BOOK";

    private EditText EDIT;
    private TextView tvSelectedChoices;
    private List<Book> library;
    private TextView noBooksMessage;
    private ConstraintLayout myBooksContent;
    private String[] colors = {"Nome (A-Z)", "Autor (A-Z)", "Data adicão (Recente-Antigo)", "Data de lançamento (Recente-Antigo)","Ordem inversa"};
    private static boolean ativadoF1=false;

    private RecyclerView bookList;
    private CatalogAdapter bookListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books3);
        startToolbar(true);

        // library is a mutable copy of catalog
        library = new ArrayList<>();
        library.addAll(getCatalog());

        ImageButton search= findViewById(R.id.search_button);

        bookList = findViewById(R.id.my_books_catalog);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
           @Override
           public int getSpanSize(int position) {
               if (position == 0)
                   return 2;
               return 1;
           }
        });
        bookList.setLayoutManager(layoutManager);
        // Dummy book, because the first one is always ignored by the recycler view...
        library.add(0, new Book());
        bookListAdapter = new CatalogAdapter(library, this::openActivityReader, new CatalogAdapter.CommandsListener() {
            @Override
            public void filter(TextView filterDesc) {
                DialogFragment multiChoiceDialog = new MultipleChoiceDialogFragment();
                multiChoiceDialog.setCancelable(false);
                multiChoiceDialog.show(getSupportFragmentManager(), "Multichoice Dialog");

                tvSelectedChoices = filterDesc;
            }

            @Override
            public void reset() {
                library.clear();
                library.addAll(getCatalog());
                bookListAdapter.notifyDataSetChanged();
            }

            @Override
            public void imprt() {
                openActivityImport();
            }
        });
        bookList.setAdapter(bookListAdapter);

        tvSelectedChoices = findViewById(R.id.tvSelectedChoices);
        noBooksMessage = findViewById(R.id.my_books_no_content);
        ativadoF1 = false;

        checkLibraryStock();

        search.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String text = ((EditText) findViewById(R.id.search_text)).getText().toString();
                search(text);
            }


        });


    }
    public void openActivityImport() {
        Intent intent = new Intent(this,Import_main.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void openActivityReader(Book book) {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra(CHOSEN_BOOK, book);
        startActivity(intent);
    }

    /**
     * Check if there are imported books in storage.
     * If so, then the page is filled with the imported books.
     * If not, then a message is presented to the user.
     */
    public void checkLibraryStock() {
        if (library.isEmpty()) {
            noBooksMessage.setVisibility(View.VISIBLE);
            bookList.setVisibility(View.GONE);
        }
        else {
            noBooksMessage.setVisibility(View.GONE);
            bookList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] list, ArrayList<String> selectedItemList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Sorted by:");
            for (String str : selectedItemList) {

                stringBuilder.append(str).append(" ");
                builder.setTitle("OUT").setMessage("Are you sure you want to delete this entry?");
                if (str.equals("Data adicão (Recente-Antigo)")) {
                    builder.setTitle("ola").setMessage("Are you sure you want to delete this entry?");
                    break;
                } else if (str.equals("Nome (A-Z)")) {
                    builder.setTitle("ola").setMessage("Are you sure you want to delete this entry?");
                    stringBuilder.append("true");
                    break;
                }
            }

        tvSelectedChoices.setText(stringBuilder);

        for (String str : selectedItemList) {
            String[] strArr = getResources().getStringArray(R.array.filter_itens);
            int idx = Arrays.binarySearch(strArr, str);

            switch (idx) {
                case 0:
                    Collections.sort(library, (b1, b2) -> {
                        String b1t = b1.gettitulo().toLowerCase();
                        String b2t = b2.gettitulo().toLowerCase();
                        return b1t.compareTo(b2t);
                    });
                case 1:
                    Collections.sort(library, (b1, b2) -> {
                        String b1t = b1.getcdate().toLowerCase();
                        String b2t = b2.getcdate().toLowerCase();
                        return b1t.compareTo(b2t);
                    });
                case 2:
                    Collections.reverse(library);
            }
        }
        bookListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onNegativeButtonClicked() {
        tvSelectedChoices.setText(R.string.filter_dialog_cancel);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void search(String title) {
        List<Book> temp = getCatalog().stream().filter(book -> book.gettitulo().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toList());
        library.clear();
        library.addAll(temp);
        bookListAdapter.notifyDataSetChanged();
    }



}