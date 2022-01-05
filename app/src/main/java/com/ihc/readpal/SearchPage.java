package com.ihc.readpal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SearchPage extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_paga);
        startToolbar(true);

        Intent intent=getIntent();
        String Message="Results: "+intent.getStringExtra(MyBooks.EXTRA_MESSAGE);
        TextView texto=findViewById(R.id.results_search);
        texto.setText(Message);
        ImageButton reverse=findViewById(R.id.filter_menu);
        ImageButton button2= findViewById(R.id.lusiadas_res);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openActivityBookPage();
            }
        });
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.activity_my_books3);

            }
        });

    }

    private void openActivityBookPage() {
        Intent intent = new Intent(this,BookPage.class);

        startActivity(intent);

    }
}