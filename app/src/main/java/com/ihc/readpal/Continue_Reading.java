package com.ihc.readpal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Continue_Reading extends MainActivity implements MultipleChoiceDialogFragment.onMultiChoiceListener {

    public static final String CHOSEN_BOOK = "com.ihc.readpal.extra.CHOSEN_BOOK";

    private EditText EDIT;
    private TextView tvSelectedChoices;
    private List<Book> library;
    private TextView noBooksMessage;
    private ConstraintLayout myBooksContent;
    private String[] colors = {"Nome (A-Z)", "Autor (A-Z)", "Data adicão (Recente-Antigo)", "Data de lançamento (Recente-Antigo)","Ordem inversa"};
    private static boolean ativado=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_reading);
        startToolbar(false);


        ImageButton button4=findViewById(R.id.sermao);
        ImageButton button6=findViewById(R.id.memorial);


        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivityBookPage(3);
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityBookPage(1);

            }
        });





    }

    private void openActivityBookPage(int id) {
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.putExtra(CHOSEN_BOOK, catalog.get(id));
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
            myBooksContent.setVisibility(View.GONE);
        }
        else {
            noBooksMessage.setVisibility(View.GONE);
            myBooksContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] list, ArrayList<String> selectedItemList) {


    }



    @Override
    public void onNegativeButtonClicked() {
        tvSelectedChoices.setText("Dialog Cancel");
    }


}