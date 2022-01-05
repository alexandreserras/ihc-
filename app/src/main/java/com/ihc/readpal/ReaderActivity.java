package com.ihc.readpal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReaderActivity extends FragmentActivity {

    private static final String LOG_TAG = ReaderActivity.class.getSimpleName();

    //TODO: save and reuse notes
    private static int noteId = 0;
    private final Map<Integer, Note> notes = new HashMap<>();
    private final Map<Integer, TextView> notesPage = new HashMap<>();

    private int chosenColor;
    private int defaultMarkerColor;
    private int defaultHighlightColor;
    private int chosenNoteId;
    // Togglers
    private boolean choosingColor;
    private boolean pickingBookmark;
    private boolean marking;

    private final List<Bookmark> bookmarks = new ArrayList<>();

    private TextView highlightedPage;
    private EditText currentPageNumber;
    private FragmentManager fragmentManager;
    private Fragment noteDialog;
    private Fragment colorPalette;
    private RecyclerView bookmarksView;
    private RecyclerView pagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        // Get book content
        Intent intent = getIntent();
        int currentPage = intent.getIntExtra(BookPage.PAGE_NUMBER, 0);
        Book book = intent.getParcelableExtra(MyBooks.CHOSEN_BOOK);
        String bookContentString = getBookContent(book);

        List<String> pages = Arrays.asList(bookContentString.split("\\|!PAGE!\\|"));

        //TODO: obtain bookmark list. For now it's simulated
        //TODO: make the code foolproof, because it will crash if there are less than 3 chapters
        bookmarks.add(new Bookmark("Chapter 1", 0));
        bookmarks.add(new Bookmark("Chapter 2", 1));
        bookmarks.add(new Bookmark("Chapter 3", 5));

        Log.d(LOG_TAG, "Intent extra name: " + BookPage.BOOK_CONTENT);
        Log.d(LOG_TAG, "Book content: " + bookContentString.substring(0, 10));

        // Get the maximum number of pages
        ((TextView) findViewById(R.id.page_number_max)).setText(
                String.format(getString(R.string.page_number_max), pages.size()));

        // Store view references in the private variables, and fill them accordingly
        pagesView = findViewById(R.id.reader_content);
        currentPageNumber = findViewById(R.id.page_number_current);
        pagesView.setItemViewCacheSize(10);
        pagesView.setAdapter(new BookContentAdapter(pages, new BookContentAdapter.CommunicatePage() {
            @Override
            public void selected(TextView page) {
                Log.d(LOG_TAG, "Text selection created");
                if (marking) {
                    Note tempNote = new Note(chosenColor, "");
                    // Pop the note editing window up
                    openNoteDialog(tempNote);
                    closeColorPalette();
                }
                page.setHighlightColor(chosenColor);
                highlightedPage = page;
            }

            @Override
            public void deselected() {

            }
        }, currentPageNumber));
        pagesView.setLayoutManager(new LinearLayoutManager(this));
        // Change page and edit text listener
        currentPageNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(LOG_TAG, "Specified a page number! Event: " + event + " | actionId: " + actionId);
                String txt = v.getText().toString();
                if (!txt.isEmpty()) {
                    int pnumber = Integer.parseInt(txt);
                    changePage(pnumber-1);
                }
                return true;
            }
        });
        changePage(currentPage);

        //TODO: dumb af
        defaultHighlightColor = (new TextView(this)).getHighlightColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            defaultMarkerColor = getColor(R.color.colorMarkerNone);
        }
        else {
            defaultMarkerColor = defaultHighlightColor;
        }

        // Private variable initialization, representing this activity's state
        chosenColor = defaultHighlightColor;
        choosingColor = false;
        pickingBookmark = false;
        marking = false;
        chosenNoteId = -1;

        /*
         * Toolbar
         */
        // Add bookmarks to the RecyclerView that displays the list of bookmarks
        bookmarksView = findViewById(R.id.reader_toolbar_bookmark_list);
        bookmarksView.setAdapter(new BookmarkItemAdapter(bookmarks, new BookmarkItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pageNumber) {
                changePage(pageNumber);
            }
        }));
        bookmarksView.setLayoutManager(new LinearLayoutManager(this));
        // Back button behaviour
        ImageButton buttonBack = findViewById(R.id.reader_toolbar_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: send profile data back (notes)
                finish();
            }
        });
        // Bookmarks button behaviour
        ImageButton buttonBookmarks = findViewById(R.id.reader_toolbar_bookmarks);
        buttonBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickingBookmark)
                    bookmarksView.setVisibility(View.GONE);
                else
                    bookmarksView.setVisibility(View.VISIBLE);
                pickingBookmark = !pickingBookmark;
            }
        });

        // Define text selection listener
        noteDialog = null;
        colorPalette = null;

        // Fragment manager
        fragmentManager = getSupportFragmentManager();
        // Color palette
        fragmentManager.setFragmentResultListener(ColorPaletteFragment.RESULT_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d(LOG_TAG, "Obtained color from the color palette!");
                int colorTemp = result.getInt("color");
                marking = result.getBoolean("marking");
                if (!marking)
                    markerClean();
                else
                    markerColor(colorTemp);
            }
        });
        // Note dialog confirm
        fragmentManager.setFragmentResultListener(NoteFragment.NOTE_CONFIRM_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d(LOG_TAG, "Note dialog confirmed!");
                if (highlightedPage != null) {
                    Note tempNote = result.getParcelable(NoteFragment.NOTE_CONTENT);
                    // Add note and its highlight. addNote() should be done after highlightText()
                    // because noteId is incremented in addNote().
                    if (chosenNoteId == -1) {
                        highlightText(
                                Selection.getSelectionStart(highlightedPage.getText()),
                                Selection.getSelectionEnd(highlightedPage.getText()),
                                tempNote);

                        addNote(tempNote);
                    }
                    else {
                        notes.put(chosenNoteId, tempNote);
                        notesPage.put(chosenNoteId, highlightedPage);
                    }

                    closeNoteDialog();
                    markerClean();
                    hideKeyboard(ReaderActivity.this);
                }
                else
                    Log.e(LOG_TAG, "The highlighted page is null! The ordering of events may be wrong...");
            }
        });
        // Note dialog remove all
        fragmentManager.setFragmentResultListener(NoteFragment.NOTE_REMOVE_KEY, this, new FragmentResultListener() {

            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d(LOG_TAG, "Note dialog removed the note!");

                if (chosenNoteId != -1)
                    removeNote(chosenNoteId);



                closeNoteDialog();
                terminateSelection();
                markerClean();
            }
        });
        // Note dialog change color
        fragmentManager.setFragmentResultListener(NoteFragment.NOTE_COLOR_KEY, this, new FragmentResultListener() {

            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d(LOG_TAG, "Note dialog changed color!");
                markerColor(result.getInt("color"));
            }
        });

        // Access the book's data
        ImageButton btnBookData = findViewById(R.id.reader_toolbar_data);
        btnBookData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_data = new Intent(getApplicationContext(), BookPage.class);
                intent_data.putExtra(MyBooks.CHOSEN_BOOK, book);
                startActivity(intent_data);
            }
        });

        // I have to do this for some reason, or the icons won't show
        buttonBookmarks.setBackgroundResource(R.drawable.ic_bookmark);
        buttonBack.setBackgroundResource(R.drawable.ic_back_arrow);
        btnBookData.setBackgroundResource(R.drawable.ic_book_data);
    }

    // Initiate the color palette
    public void chooseColor(View view) {
        if (choosingColor) {
            closeColorPalette();
        }
        else {
            openColorPalette();
        }
    }

    private void openNoteDialog(Note note) {
        Log.d(LOG_TAG, "Opening note dialog");

        Bundle pass = new Bundle();
        if (note != null) {
            pass.putParcelable("note", note);
        }

        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.note_dialog, NoteFragment.class, pass)
                .commitNow();

        noteDialog = fragmentManager.findFragmentById(R.id.note_dialog);
    }

    private void closeNoteDialog() {
        Log.d(LOG_TAG, "Closing note dialog");
        if (noteDialog != null) {
            fragmentManager.beginTransaction()
                    .remove(noteDialog)
                    .commitNow();

            noteDialog = null;
        }
        chosenNoteId = -1;
    }

    private void openColorPalette() {
        Log.d(LOG_TAG, "Opening color palette");
        if (colorPalette == null) {
            Bundle pass = new Bundle();
            pass.putInt("color", chosenColor);
            pass.putBoolean("none", true);

            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.marker_color_palette, ColorPaletteFragment.class, pass)
                    .commitNow();

            colorPalette = fragmentManager.findFragmentById(R.id.marker_color_palette);

            choosingColor = true;
        }
    }

    private void closeColorPalette() {
        Log.d(LOG_TAG, "Closing color palette");
        // The user might mark text and so the color palette should be closed.
        // However, the palette might not even be opened at the time of closing, so we should check.
        if (colorPalette != null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(colorPalette)
                    .commitNow();

            colorPalette = null;

            choosingColor = false;
        }
    }

    private void markerColor(int color) {
        chosenColor = color;
        
        // Change the marker icon color
        if (color == defaultHighlightColor)
            color = defaultMarkerColor;
        FloatingActionButton marker = findViewById(R.id.marker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            marker.setImageTintList(ColorStateList.valueOf(color));
        }
        
        if (highlightedPage != null)
            highlightedPage.setHighlightColor(chosenColor);
    }
    
    private void markerClean() {
        markerColor(defaultHighlightColor);
        marking = false;
    }

    private void terminateSelection() {
        Selection.removeSelection((SpannableString) highlightedPage.getText());
    }

    private void addNote(Note note) {
        notes.put(noteId, note);
        notesPage.put(noteId, highlightedPage);
        noteId++;
    }

    private void removeNote(int id) {
        notes.remove(id);

        Objects.requireNonNull(notesPage.get(id)).setText(Objects.requireNonNull(notesPage.get(id)).getText().toString());
        notesPage.remove(id);

    }

    /**
     * Create the note object on the text, which is represented by an highlighted section of the text.
     *
     * @param start beginning index of the highlight in the text
     * @param end ending index of the highlight in the text
     */
    // TODO: finish this
    private void highlightText(int start, int end, Note note) {
        // Create note
        Spannable span = new SpannableString(highlightedPage.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            private final int id = noteId;

            @Override
            public void onClick(@NonNull View view) {
                chosenNoteId = id;
                openNoteDialog(notes.get(id));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setStyle(Paint.Style.FILL);
                ds.setColor(note.getColor());
                ds.setUnderlineText(true);
            }
        };
        span.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        highlightedPage.setText(span, TextView.BufferType.SPANNABLE);
        highlightedPage.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void changePage(int pageNumber) {
        Log.d(LOG_TAG, "Changing page to position " + (pageNumber+1));
        currentPageNumber.setText(String.valueOf(pageNumber+1));
        pagesView.scrollToPosition(pageNumber);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String getBookContent(Book book) {
        String bookContent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                bookContent = Files.readAllLines(Paths.get(book.getUri_book_text().getPath())).toString();
            }
            catch (IOException e) {
                //Toast.makeText(getApplicationContext(), "Error: Could not get book content, showing Lusíadas.", Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "IOException while getting the book content!");
                bookContent = getString(R.string.book_content_lusiadas);
            }
        }
        else { // TODO: bookContent reading for API < 26
            bookContent = getString(R.string.book_content_lusiadas);
        }
        return bookContent;
    }
}