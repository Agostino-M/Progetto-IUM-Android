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

public class LessonList extends AppCompatActivity {

    private final List<String> listviewTitle = new ArrayList<>();
    private final List<String> id_pren = new ArrayList<>();
    private final List<String> listviewDate = new ArrayList<>();
    private String courseName;
    private int courseId;
    private int nElements = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        courseName = intent.getExtras().getString("nome_corso");
        courseId = intent.getExtras().getInt("id_corso");
        setContentView(R.layout.activity_list_layout);
        loadLessonList(courseId);
        List<HashMap<String, String>> aList = new ArrayList<>();

        for (int i = 0; i < nElements; i++) {
            HashMap<String, String> hm = new HashMap<>();

            hm.put("listview_title", listviewTitle.get(i));
            hm.put("listview_date", listviewDate.get(i));
            hm.put("id_pren", id_pren.get(i));
            aList.add(hm);
        }

        String[] from = { "listview_image", "listview_title", "listview_date"};
        int[] to = { R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.activity_lesson_list, from, to);
        ListView androidListView = findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adattatore, final View componente, int pos, long id) {
                final HashMap<String, String> item = (HashMap<String, String>) adattatore.getItemAtPosition(pos);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final String email = preferences.getString("Email", "");
                if (email.equalsIgnoreCase("")) {
                    new AlertDialog.Builder(LessonList.this)
                            .setTitle("Prenotazione")
                            .setMessage("Devi prima effettuare il login! Procedere ora?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent(getApplicationContext(), LoginSignup.class);
                                    intent.putExtra("provenienza", "ripetizioni");
                                    intent.putExtra("nome_corso", courseName);
                                    intent.putExtra("id_corso", courseId);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    new AlertDialog.Builder(LessonList.this)
                            .setTitle("Prenotazione")
                            .setMessage("Vuoi confermare la prenotazione?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    DBManagement db = new DBManagement(getApplicationContext());
                                    db.open();
                                    db.insertBooking(email, item.get("id_pren"));
                                    db.close();
                                    Toast.makeText(getApplicationContext(), "Prenotazione effettuata!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(courseName);
    }

    public void loadLessonList(int courseId) {
        DBManagement db = new DBManagement(this);
        db.open();
        Cursor c;
        c = db.getAllLessonsByCourse(courseId);
        nElements = c.getCount();

        int i = 0;
        if (c.moveToFirst()) {
            do {
                listviewTitle.add(i, "Docente: " + c.getString(0));
                listviewDate.add(i, "Giorno: " + c.getString((1)) + "      Ora: " + c.getString((2)));
                id_pren.add(i, c.getString(3));
                i++;
            } while (c.moveToNext());
        } else {
            setEmptyLayout();
        }
        db.close();
    }

    private void setEmptyLayout() {
        RelativeLayout mainLayout = findViewById(R.id.empty_layout);
        View content = getLayoutInflater()
                .inflate(R.layout.content_empty_lessons, mainLayout, false);
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
            intent.putExtra("nome_corso", courseName);
            intent.putExtra("id_corso", courseId);
            intent.putExtra("provenienza", "ripetizioni");
            this.startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Email", "");
            editor.putString("ID", "");
            editor.apply();
            Toast.makeText(getApplicationContext(), "Logout effettuato!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}