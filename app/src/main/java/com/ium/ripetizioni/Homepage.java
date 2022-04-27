package com.ium.ripetizioni;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Homepage extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Button courseButton = findViewById(R.id.viewCoursesButton);
        courseButton.setOnClickListener(this);
        Button bookingButton = findViewById(R.id.viewBookingsButton);
        bookingButton.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.viewCoursesButton) {
            Intent intent = new Intent(this, CourseList.class);
            intent.putExtra("provenienza", "homepage");
            this.startActivity(intent);
            finish();

        } else if (id == R.id.viewBookingsButton) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String name = preferences.getString("Email", "");
            if (name.equalsIgnoreCase("")) {
                new AlertDialog.Builder(Homepage.this)
                        .setTitle("Prenotazioni effettuate")
                        .setMessage("Devi prima effettuare il login.\nProcedere ora?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent2 = new Intent(getApplicationContext(), LoginSignup.class);
                                intent2.putExtra("provenienza", "prenotazioni");
                                startActivity(intent2);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            } else {
                Intent intent2 = new Intent(this, BookingList.class);
                startActivity(intent2);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("Email", "");

        if (name.equalsIgnoreCase("")) {
            Intent intent = new Intent(this, LoginSignup.class);
            this.startActivity(intent);
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Email", "");
            editor.putInt("ID", 0);
            editor.apply();
            Toast.makeText(getApplicationContext(), "Logout effettuato!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
