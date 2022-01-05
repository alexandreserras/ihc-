package com.ihc.readpal;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ColorPaletteFragment extends Fragment {

    public static final String RESULT_KEY = "ColorPaletteFragment.color";

    private static final String LOG_CAT = ColorPaletteFragment.class.getSimpleName();
    private static final int[] arrColorResources = {
            R.id.color_blue,
            R.id.color_green,
            R.id.color_orange,
            R.id.color_purple,
            R.id.color_red,
            R.id.color_yellow,
            R.id.color_none
    };
    private static final int[] arrColors = {
            R.color.colorMarkerBlue,
            R.color.colorMarkerGreen,
            R.color.colorMarkerOrange,
            R.color.colorMarkerPurple,
            R.color.colorMarkerRed,
            R.color.colorMarkerYellow,
            R.color.colorMarkerNone
    };

    // Bundle to send to activity/fragment using the color palette
    private Bundle result;

    public ColorPaletteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_color_palette, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize a chosen color
        Bundle arguments = getArguments();
        if (arguments != null) {
            int chosenColor = arguments.getInt("color");
            boolean hasNoneColor = arguments.getBoolean("none");

            // Since we are trading explicit color values, we need to check which button contains that color.
            int chosenColorResource;
            int chosenColorResourceIndex = findColorRes(chosenColor);
            if (chosenColorResourceIndex != -1) {
                chosenColorResource = arrColorResources[chosenColorResourceIndex];

                RadioButton checkedColor = view.findViewById(chosenColorResource);
                if (checkedColor != null)
                    checkedColor.setChecked(true);
            }

            if (!hasNoneColor)
                view.findViewById(R.id.color_none).setVisibility(View.GONE);
        }

        result = new Bundle();

        // Radio buttons click listeners
        for (int id : arrColorResources)
            view.findViewById(id).setOnClickListener(this::setColor);
    }

    public void setColor(View view) {

        FragmentActivity parent = getActivity();
        if (parent == null) return;

        boolean checked = ((RadioButton) view).isChecked();

        String colorName = "colorMarkerNone";
        boolean marking = true;
        switch (view.getId()) {
            case R.id.color_blue:
                if (checked) colorName = "colorMarkerBlue";
                break;
            case R.id.color_green:
                if (checked) colorName = "colorMarkerGreen";
                break;
            case R.id.color_orange:
                if (checked) colorName = "colorMarkerOrange";
                break;
            case R.id.color_purple:
                if (checked) colorName = "colorMarkerPurple";
                break;
            case R.id.color_red:
                if (checked) colorName = "colorMarkerRed";
                break;
            case R.id.color_yellow:
                if (checked) colorName = "colorMarkerYellow";
                break;
            default:
                marking = false;
        }

        int colorResourceName = getResources().getIdentifier(colorName,
                "color", parent.getApplicationContext().getPackageName());
        int chosenColor = ContextCompat.getColor(parent, colorResourceName);

        // Send color to parent
        result.putInt("color", chosenColor);
        result.putBoolean("marking", marking);
        getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
    }

    private int findColorRes(int color) {
        FragmentActivity parent = getActivity();
        if (parent != null)
            for (int idx = 0; idx < arrColors.length; idx++)
                if (color == ContextCompat.getColor(parent, arrColors[idx]))
                    return idx;
        return -1;
    }
}