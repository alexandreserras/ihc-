package com.ihc.readpal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoteFragment extends Fragment {

    public static final String NOTE_CONFIRM_KEY = "noteConfirm";
    public static final String NOTE_REMOVE_KEY = "noteRemove";
    public static final String NOTE_COLOR_KEY = "noteColor";
    public static final String NOTE_DESCRIPTION = "noteDescription";
    public static final String NOTE_CONTENT = "noteContent";

    private static final String LOG_TAG = NoteFragment.class.getSimpleName();

    private boolean choosingColor;
    private TextView description;
    private FragmentManager fragmentManager;
    private Fragment colorPalette;
    private Note note;

    public NoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        description = view.findViewById(R.id.note_text_content);
        choosingColor = false;
        colorPalette = null;

        Bundle arguments = getArguments();
        if (arguments != null)
            note = arguments.getParcelable("note");

        description.setText(note.getDescription());

        fragmentManager = getChildFragmentManager();

        // Confirm button
        view.findViewById(R.id.note_button_confirm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Confirm button clicked");
                note.setDescription(description.getText().toString());
                Bundle result = new Bundle();
                result.putParcelable(NOTE_CONTENT, note);
                getParentFragmentManager().setFragmentResult(NOTE_CONFIRM_KEY, result);
            }
        });
        // Remove all button
        view.findViewById(R.id.note_button_remove).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Remove button clicked");
                getParentFragmentManager().setFragmentResult(NOTE_REMOVE_KEY, new Bundle());
            }
        });
        // Color change button
        view.findViewById(R.id.note_button_colorize).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (choosingColor) {
                    closeColorPalette();
                }
                else {
                    openColorPalette();
                }
                choosingColor = !choosingColor;
            }
        });

        // Get the chosen color from the color palette
        fragmentManager.setFragmentResultListener(ColorPaletteFragment.RESULT_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                note.setColor(result.getInt("color"));
                sendColorToParent();
            }
        });
    }

    private void openColorPalette() {
        Log.d(LOG_TAG, "Opening the color palette");
        Bundle pass = new Bundle();
        pass.putInt("color", note.getColor());
        pass.putBoolean("none", false);

        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.note_color_palette, ColorPaletteFragment.class, pass)
                .commitNow();

        colorPalette = fragmentManager.findFragmentById(R.id.note_color_palette);
    }

    private void closeColorPalette() {
        Log.d(LOG_TAG, "Closing the color palette");
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .remove(colorPalette)
                .commitNow();

        colorPalette = null;
    }

    // Pass color to the reader, in order for it to update the highlight's color
    private void sendColorToParent() {
        Log.d(LOG_TAG, "Sending the color to my parent!");
        Bundle result = new Bundle();
        result.putInt("color", note.getColor());
        getParentFragmentManager().setFragmentResult(NOTE_COLOR_KEY, result);
    }
}