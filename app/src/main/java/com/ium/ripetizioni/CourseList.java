package com.ium.ripetizioni;

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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CourseList extends AppCompatActivity {

    private final List<String> listviewTitle = new ArrayList<>();
    private final List<String> listviewShortDescription = new ArrayList<>();
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            source = extras.getString("provenienza");
        }

        loadCourseList();
        List<HashMap<String, String>> adapterList = new ArrayList<>();

        for (int i = 0; i < getNCourses(); i++) {
            HashMap<String, String> hm = new HashMap<>();
            String title = listviewTitle.get(i);
            hm.put("listview_title", title);
            hm.put("listview_description", listviewShortDescription.get(i));
            title = title.toLowerCase(Locale.ROOT).replace(" ", "");
            hm.put("listview_image", Integer.toString(getResources()
                    .getIdentifier(title, "drawable", "com.ium.ripetizioni")));

            adapterList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_description"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), adapterList, R.layout.activity_course_list, from, to);
        ListView androidListView = findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);
        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, final View component, int pos, long id) {
                HashMap<String, String> item = (HashMap<String, String>) adapter.getItemAtPosition(pos);
                String courseName = item.get("listview_title");
                Intent intent = new Intent(getApplicationContext(), LessonList.class);
                intent.putExtra("nome_corso", courseName);
                intent.putExtra("id_corso", (int) id + 1);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void loadCourseList() {
        DBManagement db = new DBManagement(this);
        db.open();
        Cursor c = db.getCourses();
        if (c.moveToFirst()) {
            do {
                listviewTitle.add(c.getString(1));
                listviewShortDescription.add(c.getString(2));
            } while (c.moveToNext());
        }

        db.close();
    }

    private int getNCourses() {
        DBManagement db = new DBManagement(this);
        db.open();
        Cursor c = db.getNCourses();
        int result = 0;
        if (c.moveToFirst()) {
            result = c.getInt(0);
        }
        db.close();

        return result;
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
            intent.putExtra("provenienza", "corsi");
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

    @Override
    public void onBackPressed() {
        if (source != null && source.equals("homepage")) {
            Intent intent = new Intent(getApplicationContext(), Homepage.class);
            startActivity(intent);
        } else if (source != null && source.equals("prenotazioni")) {
            Intent intent = new Intent(getApplicationContext(), BookingList.class);
            startActivity(intent);
        }
        finish();
        super.onBackPressed();
    }
}