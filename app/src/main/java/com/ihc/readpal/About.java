package com.ihc.readpal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

public class About extends MainActivity implements PopupMenu.OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        startToolbar(false);
    }
    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this,v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
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

}
