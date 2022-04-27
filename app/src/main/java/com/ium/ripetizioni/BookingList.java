package com.ium.ripetizioni;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookingList extends AppCompatActivity {

    private final List<String> listviewTeacher = new ArrayList<>();
    private final List<String> listviewCourse = new ArrayList<>();
    private final List<String> idPren = new ArrayList<>();
    private final List<String> listviewDate = new ArrayList<>();
    private final List<String> listviewStatus = new ArrayList<>();
    private int nElements = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_layout);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadBookingList(preferences.getString("ID", ""));
        List<HashMap<String, String>> aList = new ArrayList<>();

        for (int i = 0; i < nElements; i++) {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("listview_title", listviewTeacher.get(i));
            hm.put("listview_course", listviewCourse.get(i));
            hm.put("listview_date", listviewDate.get(i));
            hm.put("listview_status", listviewStatus.get(i));
            hm.put("id_pren", idPren.get(i));
            aList.add(hm);
        }

        if (nElements == 0) {
            setEmptyLayout();
        }

        String[] from = {"listview_course", "listview_title", "listview_date", "listview_status"};
        int[] to = {R.id.listview_item_course, R.id.listview_item_title, R.id.listview_item_short_description, R.id.listview_item_status};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.activity_booking_list, from, to);
        ListView androidListView = findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {
                final HashMap<String, String> item = (HashMap<String, String>) adattatore.getItemAtPosition(pos);
                final String value = item.get("listview_title");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final String name = preferences.getString("ID", "");
                if (name.equalsIgnoreCase("")) {
                    Intent intent = new Intent(getApplicationContext(), LoginSignup.class);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(BookingList.this)
                            .setTitle("Prenotazione")
                            .setMessage("Vuoi eliminare la prenotazione?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    DBManagement db = new DBManagement(getApplicationContext());
                                    db.open();
                                    db.deleteBookingByLessonId(item.get("id_pren"));
                                    db.close();
                                    Intent intent = new Intent(getApplicationContext(), BookingList.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Prenotazione eliminata!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void loadBookingList(String userId) {
        DBManagement db = new DBManagement(this);
        db.open();
        Cursor c = db.getBookingsByUser(userId);
        nElements = c.getCount();

        int i = 0;
        if (c.moveToFirst()) {
            do {
                listviewTeacher.add(i, "Docente: " + c.getString(0));
                listviewDate.add(i, "Giorno: " + c.getString((1)) + "      Ora: " + c.getString((2)));
                listviewStatus.add(i, "Stato: " + getFullStatus(c.getString((3))));
                idPren.add(i, c.getString(4));
                listviewCourse.add(i, "Corso: " + c.getString(5));

                i++;
            } while (c.moveToNext());
        }
        db.close();
    }

    private String getFullStatus(String string) {
        switch (string) {
            case "P":
                return "Prenotata";
            case "C":
                return "Cancellata";
            case "E":
                return "Effettuata";
        }

        return "";
    }

    private void setEmptyLayout() {
        RelativeLayout mainLayout = findViewById(R.id.empty_layout);
        View content = getLayoutInflater()
                .inflate(R.layout.content_empty_booking, mainLayout, false);
        mainLayout.addView(content);
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
            intent.putExtra("provenienza", "prenotazioni");
            this.startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Email", "");
            editor.putString("ID", "");
            editor.apply();
            Toast.makeText(getApplicationContext(), "Logout effettuato!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Homepage.class);
            this.startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    public void gotoCourses(View view) {
        Intent intent = new Intent(this, CourseList.class);
        intent.putExtra("provenienza", "prenotazioni");
        this.startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Homepage.class);
        startActivity(intent);
        finish();

        super.onBackPressed();
    }
}
