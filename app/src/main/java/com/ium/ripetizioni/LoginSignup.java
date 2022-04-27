package com.ium.ripetizioni;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.ium.ripetizioni.fragment.LoginFragment;
import com.ium.ripetizioni.fragment.SignUpFragment;

public class LoginSignup extends AppCompatActivity implements View.OnClickListener {

    private String provenienza;
    private String nomeCorso;
    private int idCorso;
    private TextView loginText, signUpBtn;
    private Button sign_button;
    private EditText nomeEditText;
    private EditText cognomeEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            provenienza = extras.getString("provenienza");
            nomeCorso = extras.getString("nome_corso");
            idCorso = extras.getInt("id_corso");
        }
        setContentView(R.layout.activity_login);

        loginText = findViewById(R.id.login);
        signUpBtn = findViewById(R.id.signup);
        sign_button = findViewById(R.id.sign_button);
        sign_button.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        loginText.setOnClickListener(this);
        sign_button.setText(R.string.login);
        sign_button.setTag(R.string.login);


        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoginFragment());
        transaction.commit();
    }

    private void setFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onClick(View v) {
        if (v == loginText) {
            sign_button.setText(R.string.login);
            sign_button.setTag(R.string.login);
            setFragment(new LoginFragment());
        }

        if (v == signUpBtn) {
            sign_button.setText(R.string.signup);
            sign_button.setTag(R.string.signup);
            setFragment(new SignUpFragment());
        }

        if (v == sign_button) {
            Object tag = v.getTag();
            if (tag.equals(R.string.login)) {
                DBManagement db = new DBManagement(this);
                db.open();
                emailEditText = findViewById(R.id.login_email);
                passwordEditText = findViewById(R.id.login_password);

                if (!checkValidFieldsLogin())
                    return;

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                Cursor c = db.getAllUsers();
                boolean found = false;
                if (c.moveToFirst()) {
                    do {
                        if (c.getString(3).equals(email) && c.getString(4).equals(password)) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("Email", c.getString(3));
                            editor.putString("ID", c.getString(0));
                            editor.apply();
                            Toast.makeText(getApplicationContext(), "Login effettuato!", Toast.LENGTH_SHORT).show();
                            found = true;
                            db.close();
                            goToActivity();
                        }
                    } while (c.moveToNext());
                    if (!found) {
                        Toast.makeText(getApplicationContext(), "L'utente non esiste", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Impossibile effettuare il login", Toast.LENGTH_SHORT).show();
                }
                db.close();
            } else if (tag.equals(R.string.signup)) {
                DBManagement db = new DBManagement(this);
                db.open();
                nomeEditText = findViewById(R.id.nome);
                cognomeEditText = findViewById(R.id.cognome);
                emailEditText = findViewById(R.id.email);
                passwordEditText = findViewById(R.id.password);

                if (!checkValidFieldsSignup())
                    return;

                String nome = nomeEditText.getText().toString();
                String cognome = cognomeEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                long id = db.insertUser(nome, cognome, email, password);
                if (id != -1) {
                    Toast.makeText(getApplicationContext(), "Registrazione effettuata!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Impossibile registrarsi!", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                Cursor c = db.getUser(email);
                editor.putString("Email", email);
                editor.putString("ID", c.getString(0));
                editor.apply();
                db.close();
                goToActivity();
            }
        }
    }

    private void goToActivity() {
        Intent intent = new Intent(this, Homepage.class);
        if (provenienza != null && provenienza.equals("prenotazioni")) {
            intent = new Intent(this, BookingList.class);
        } else if (provenienza != null && provenienza.equals("ripetizioni")) {
            intent = new Intent(this, LessonList.class);
            intent.putExtra("nome_corso", nomeCorso);
            intent.putExtra("id_corso", idCorso);
        } else if (provenienza != null && provenienza.equals("corsi")) {
            intent = new Intent(this, CourseList.class);
        }
        finish();
        this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (provenienza != null && provenienza.equals("ripetizioni")) {
            Intent intent = new Intent(getApplicationContext(), LessonList.class);
            intent.putExtra("nome_corso", nomeCorso);
            intent.putExtra("id_corso", idCorso);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), Homepage.class);
            startActivity(intent);
        }
        finish();
        super.onBackPressed();
    }

    private boolean checkValidFieldsLogin() {
        boolean result = true;
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.length() <= 0) {
            emailEditText.setError(getString(R.string.mandatory));
            result = false;
        }
        if (password.length() <= 0) {
            passwordEditText.setError(getString(R.string.mandatory));
            result = false;
        }

        return result;
    }

    private boolean checkValidFieldsSignup() {
        boolean result = true;
        String nome = nomeEditText.getText().toString();
        String cognome = cognomeEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (nome.length() <= 0) {
            nomeEditText.setError(getString(R.string.mandatory));
            result = false;
        }
        if (cognome.length() <= 0) {
            cognomeEditText.setError(getString(R.string.mandatory));
            result = false;
        }
        if (email.length() <= 0) {
            emailEditText.setError(getString(R.string.mandatory));
            result = false;
        }
        if (password.length() <= 0) {
            passwordEditText.setError(getString(R.string.mandatory));
            result = false;
        }

        return result;
    }

}
