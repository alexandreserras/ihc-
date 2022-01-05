package com.ihc.readpal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private final String LOG_TAG = this.getClass().getSimpleName();

    public static final String EXTRA_MESSAGE = "com.ihc.readpal.extra.Message";

    private static final Uri.Builder resourcePath = new Uri.Builder();

    public static final List<Book> catalog = new ArrayList<>();

    // Used for filtered results
    public final List<Integer> catalogIndices = new ArrayList<>();

    private ImageButton toolbarButtonMenu;
    private ImageButton toolbarButtonSearch;
    private EditText toolbarEditTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();
        Resources resources = context.getResources();

        // Start library, which is the list of books.
        // Prevent the default books from being generated every time.
        if (catalog.size() < 4) {
            catalog.add(new Book(
                    new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(resources.getResourcePackageName(R.string.book_content_lusiadas))
                            .appendPath(resources.getResourceTypeName(R.string.book_content_lusiadas))
                            .appendPath(resources.getResourceEntryName(R.string.book_content_lusiadas))
                            .build(),
                    new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(resources.getResourcePackageName(R.drawable.lusiadas))
                            .appendPath(resources.getResourceTypeName(R.drawable.lusiadas))
                            .appendPath(resources.getResourceEntryName(R.drawable.lusiadas))
                            .build(),
                    "Os Lusíadas",
                    "Luís Vaz de Camões",
                    "25/05/2021"));
            catalog.add(new Book(
                    new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(resources.getResourcePackageName(R.string.book_content_memorial))
                            .appendPath(resources.getResourceTypeName(R.string.book_content_memorial))
                            .appendPath(resources.getResourceEntryName(R.string.book_content_memorial))
                            .build(),
                    new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(resources.getResourcePackageName(R.drawable.memorial))
                            .appendPath(resources.getResourceTypeName(R.drawable.memorial))
                            .appendPath(resources.getResourceEntryName(R.drawable.memorial))
                            .build(),
                    "Memorial do Convento",
                    "José Saramago",
                    "28/05/2021"));
            catalog.add(new Book(
                    new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(resources.getResourcePackageName(R.string.book_content_maias))
                            .appendPath(resources.getResourceTypeName(R.string.book_content_maias))
                            .appendPath(resources.getResourceEntryName(R.string.book_content_maias))
                            .build(),
                    new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(resources.getResourcePackageName(R.drawable.maias))
                            .appendPath(resources.getResourceTypeName(R.drawable.maias))
                            .appendPath(resources.getResourceEntryName(R.drawable.maias))
                            .build(),
                    "Os Maias",
                    "Eça de Queiroz",
                    "01/06/2021"));
            catalog.add(new Book(
                    new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(resources.getResourcePackageName(R.string.book_content_sermao))
                            .appendPath(resources.getResourceTypeName(R.string.book_content_sermao))
                            .appendPath(resources.getResourceEntryName(R.string.book_content_sermao))
                            .build(),
                    new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                            .authority(resources.getResourcePackageName(R.drawable.sermao))
                            .appendPath(resources.getResourceTypeName(R.drawable.sermao))
                            .appendPath(resources.getResourceEntryName(R.drawable.sermao))
                            .build(),
                    "Sermão de Santo António aos Peixes",
                    "António Vieira",
                    "30/05/2021"));
        }


    }

    public void startToolbar(boolean search) {
        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get toolbar items
        Log.d(LOG_TAG, "Getting the toolbar items");
        toolbarButtonMenu = findViewById(R.id.toolbar_menu);
        toolbarButtonMenu.setOnClickListener(this::showPopup);
        toolbarButtonSearch = findViewById(R.id.search_button);
        toolbarEditTextSearch = findViewById(R.id.search_text);
        if (search) {
            toolbarButtonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openActivitySearchPage();
                }
            });
        }
        else {
            toolbarEditTextSearch.setVisibility(View.INVISIBLE);
            toolbarButtonSearch.setVisibility(View.INVISIBLE);
        }
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this,v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(LOG_TAG, "Clicked on the menu!");
        switch (item.getItemId()){
            case R.id.item2:
                Toast.makeText(this, "My Books Clicked", Toast.LENGTH_SHORT).show();
                openActivityMyBooks();
                return true;
            case R.id.item3:
                Toast.makeText(this, "About Clicked", Toast.LENGTH_SHORT).show();
                openActivityAbout();
                return true;
            case R.id.item4:
                Toast.makeText(this, "Continue Reading Clicked", Toast.LENGTH_SHORT).show();
                openContinue();
            default:
                return false;

        }



    }

    private void openActivityImport() {
        Intent intent = new Intent(this,Import_main.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void openActivityAbout() {
        Intent intent = new Intent(this,About.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void openActivityMyBooks() {
        Intent intent = new Intent(this,MyBooks.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void openContinue() {
        Intent intent = new Intent(this,Continue_Reading.class);
        startActivity(intent);
        overridePendingTransition  (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void openActivitySearchPage() {
        String msg = toolbarEditTextSearch.getText().toString();
        Intent intent = new Intent(this, SearchPage.class);
        intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
    }

    public List<Book> getCatalog() {
        return catalog;
    }
}
