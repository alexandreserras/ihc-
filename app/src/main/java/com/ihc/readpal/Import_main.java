package com.ihc.readpal;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.javatuples.*;


public class Import_main extends MainActivity implements PopupMenu.OnMenuItemClickListener{


    public Uri Bookfile_uri;
    public Uri Imagefile_uri=Uri.parse("android.resource://com.ihc.readpal/drawable/photo_icon");
    public EditText Booktitle;
    public EditText AuthorName;
    public TextView mtv;
    public Calendar c=Calendar.getInstance();

    public String date = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

    public DatePickerDialog dpd;


    public Book thisbook = new Book();

    public HashMap<String,Book> mybooks= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_main);
        startToolbar(false);

        mtv = (TextView) findViewById(R.id.textView4);
        mtv.setText("Open Calender");
    }


    ActivityResultLauncher<Intent> fileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("whatsup", "NOTMUCH");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    Context context = getApplicationContext();
                    Intent data = result.getData();

                    Uri uri = data.getData();
                    Bookfile_uri=uri;
                    Log.d("FILEURI", "File Uri: " + uri.getPath());

                    View c = findViewById(R.id.constraint_layout);
                    c.setVisibility(View.VISIBLE);

                    String filename = uri.getLastPathSegment();
                    filename = filename.substring(filename.lastIndexOf(":")+1);

                    Button button = (Button)findViewById(R.id.choose_file_btn);
                    button.setText(filename);
                }
            });

    ActivityResultLauncher<Intent> ImageresultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("whatsup", "NOTMUCH");
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    Context context = getApplicationContext();
                    Intent data = result.getData();

                    Uri uri = data.getData();
                    Imagefile_uri=uri;
                    Log.d("FILEURI", "File Uri: " + uri.getPath());


                    View ch = findViewById(R.id.closeimage);
                    ch.setVisibility(View.VISIBLE);


                    ImageView imgView=(ImageView)findViewById(R.id.imageView2);
                    imgView.getLayoutParams().height = 550;
                    imgView.getLayoutParams().width = 465;
                    imgView.setImageURI(Imagefile_uri);
                }
            });


    public void getFile(View v) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fileResultLauncher.launch(intent);
    }

    public void getImage(View v) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        ImageresultLauncher.launch(intent);
    }

    public void revertImage(View v) {

        Imagefile_uri=Uri.parse("android.resource://com.ihc.readpal/drawable/photo_icon");
        ImageView imgView=(ImageView)findViewById(R.id.imageView2);
        imgView.getLayoutParams().height = 550;
        imgView.getLayoutParams().width = 465;
        imgView.setImageResource(R.drawable.photo_icon);

        View ch = findViewById(R.id.closeimage);
        ch.setVisibility(View.GONE);

    }
    public void removedate(View v){
        mtv = (TextView) findViewById(R.id.textView4);
        mtv.setText("Open Calender");

        View ch = findViewById(R.id.closedate);
        ch.setVisibility(View.GONE);
        date="Not set";

    }



    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this,v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    public void confirm(){
        Booktitle = (EditText) findViewById(R.id.editText1);
        AuthorName = (EditText) findViewById(R.id.editText2);
        //date = (EditText) findViewById(R.id.editTextDate);

        if(!mybooks.isEmpty()) {
            List<String> list = new ArrayList<>(mybooks.keySet()); //all keys in hashmap

            for (int i = 0; i < list.size(); i++) {
                if (Bookfile_uri.getPath().equals(list.get(i))) {
                    Toast.makeText(this, "File already Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


        }
        //quintet= new Quintet<>(Bookfile_uri,Imagefile_uri,Booktitle.getText().toString(),AuthorName.getText().toString(),date.getText().toString());
        thisbook.setUri_book_text(Bookfile_uri);
        thisbook.setUri_book_image(Imagefile_uri);
        thisbook.setUri_default_book_image(Imagefile_uri);
        thisbook.setauthor(AuthorName.getText().toString());
        thisbook.settitulo(Booktitle.getText().toString());
        thisbook.setcdate(date);

        Sextet sixtet = thisbook.getall();



        Log.d("Sextet", "Sextet: " + sixtet.toString());

        mybooks.put(Bookfile_uri.getPath(),thisbook);

        Log.d("MYBOOKS", "mybooks  :  " + mybooks.toString());

        catalog.add(thisbook);
        openActivityBooks();
        Toast.makeText(this, "Success!!\nBook added", Toast.LENGTH_SHORT).show();

    }

    public void confirmbutton(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure?");
        // Add the buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                confirm();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }




    private void openActivityImport() {
        Intent intent = new Intent(this,Import_main.class);
        startActivity(intent);

        overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void openActivityBooks() {
        Intent intent = new Intent(this,MyBooks.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void openActivityAbout() {
        Intent intent = new Intent(this,About.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void pickdate(View view) {


        c=Calendar.getInstance();
        int day=c.get(Calendar.DAY_OF_MONTH);
        int month=c.get(Calendar.MONTH);
        int year=c.get(Calendar.YEAR);
        mtv = (TextView) findViewById(R.id.textView4);

        dpd = new DatePickerDialog(this, (view1, tyear, tmonth, tday) -> {
            date=tday+"/"+ (tmonth+1) +"/"+ tyear;
            mtv.setText(date);
            if (!date.equals("Open Calender") || !date.equals("Not set")){
                View ch = findViewById(R.id.closedate);
                ch.setVisibility(View.VISIBLE);

            }

        },year,month,day);
        dpd.show();
    }
}