package com.ium.ripetizioni;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManagement {

    private static final String TAG = "DBManagement";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "nome";
    private static final String KEY_SURNAME = "cognome";
    private static final String KEY_DESCRIPTION = "descrizione";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String USER_TABLE = "utente";
    private static final String LESSON_TABLE = "ripetizione";
    private static final String TEACHING_TABLE = "docenza";
    private static final String COURSE_TABLE = "corso";
    private static final String DATABASE_NAME = "TestDB";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_USER_TABLE = "CREATE TABLE utente " +
            "(id integer primary key autoincrement, " +
            "nome text not null, " +
            "cognome text not null, " +
            "email text not null unique, " +
            "password text not null);";
    private static final String CREATE_COURSES_TABLE = "CREATE TABLE corso " +
            "(id integer primary key autoincrement, " +
            "nome text not null, " +
            "descrizione text not null);";
    private static final String CREATE_LESSON_TABLE = "CREATE TABLE ripetizione " +
            "(id integer primary key autoincrement, " +
            "id_docenza integer not null, " +
            "giorno text not null," +
            "ora text not null);";
    private static final String CREATE_TEACHING_TABLE = "CREATE TABLE docenza " +
            "(id integer primary key autoincrement, " +
            "id_corso integer not null, " +
            "nominativo text not null);";
    private static final String CREATE_BOOKING_TABLE = "CREATE TABLE prenotazione " +
            "(id integer primary key autoincrement, " +
            "stato text, " +
            "id_utente integer, " +
            "id_ripetizione integer);";

    private static final String INSERT_USER = "INSERT INTO utente (nome, cognome, email, password) VALUES " +
            "('agostino', 'messsina', 'agostino', 'agostino');";
    private static final String INSERT_LESSON = "INSERT INTO ripetizione (id_docenza, giorno, ora) VALUES" +
            "(1, '22/05/2022', '15:00')," +
            "(1, '23/05/2022', '16:00')," +
            "(2, '22/05/2022', '15:00')," +
            "(2, '23/05/2022', '16:00')," +
            "(2, '23/05/2022', '18:00')," +
            "(3, '22/05/2022', '15:00')," +
            "(3, '23/05/2022', '16:00')," +
            "(4, '22/05/2022', '17:00')," +
            "(4, '23/05/2022', '18:00')," +
            "(5, '22/05/2022', '15:00')," +
            "(6, '22/05/2022', '16:00')," +
            "(6, '23/05/2022', '15:00')," +
            "(7, '23/05/2022', '15:00')," +
            "(8, '23/05/2022', '16:00')," +
            "(8, '24/05/2022', '16:00')," +
            "(9, '23/05/2022', '17:00')," +
            "(9, '24/05/2022', '17:00')," +
            "(10, '23/05/2022', '15:00')," +
            "(10, '24/05/2022', '15:00')," +
            "(11, '25/05/2022', '15:00')," +
            "(12, '24/05/2022', '17:00')," +
            "(13, '22/05/2022', '17:00')," +
            "(14, '23/05/2022', '16:00');";

    private static final String INSERT_TEACHING = "INSERT INTO docenza (id_corso, nominativo) VALUES " +
            "(1, 'Cristina Baroglio'), " +
            "(2, 'Francesco Bergadano'), " +
            "(7, 'Viviana Barutello'), " +
            "(3, 'Gianluca Pozzato'), " +
            "(6, 'Igor Pesando'), " +
            "(1, 'Claudio Schifanella'), " +
            "(3, 'Claudio Schifanella'), " +
            "(4, 'Marco Botta'), " +
            "(6, 'Ruggero Pensa'), " +
            "(4, 'Liliana Ardissono'), " +
            "(5, 'Roberto Aringhieri')," +
            "(3, 'Ugo DeLiguoro')," +
            "(1, 'Enrico Bini')," +
            "(5, 'Marino Segnan');";
    private static final String INSERT_BOOKING = "INSERT INTO prenotazione (stato, id_utente, id_ripetizione) VALUES " +
            "('E', 1, 1)," +
            "('E', 1, 2)," +
            "('E', 1, 3)," +
            "('E', 1, 4)," +
            "('C', 1, 5);";
    private static final String INSERT_COURSES = "INSERT INTO corso (nome, descrizione) VALUES" +
            "('Sistemi Operativi', \"L’insegnamento fornisce una conoscenza di base dell'architettura interna e del funzionamento dei moderni sistemi operativi.\"), " +
            "('Sicurezza', 'Il corso si propone di fornire agli studenti gli strumenti crittografici e tecnici utilizzati per garantire la sicurezza di reti e calcolatori.'), " +
            "('Algoritmi', 'L’insegnamento ha lo scopo di introdurre i concetti e le tecniche fondamentali per l’analisi e la progettazione di algoritmi.'), " +
            "('Tecnologie Web', 'Obiettivi: Imparare diversi linguaggi e tecnologie per lo sviluppo Web client-side, quali HTML5, CSS, JavaScript, JQuery.'), " +
            "('Ricerca Operativa', 'Il corso si propone di fornire agli studenti nozioni generali di calcolo matriciale, algebra e geometria.'), " +
            "('Database', \"L'insegnamento è un'introduzione alle basi di dati e ai sistemi di gestione delle medesime (SGBD).\"), " +
            "('Analisi', \"L'insegnamento ha lo scopo di presentare le nozioni di base su funzioni, grafici e loro trasformazioni.\");";

    private final DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBManagement(Context ctx) {
        DBHelper = new DatabaseHelper(ctx);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TEACHING_TABLE);
                db.execSQL(CREATE_USER_TABLE);
                db.execSQL(CREATE_COURSES_TABLE);
                db.execSQL(CREATE_LESSON_TABLE);
                db.execSQL(CREATE_BOOKING_TABLE);

                //Inserting sample data
                db.execSQL(INSERT_USER);
                db.execSQL(INSERT_TEACHING);
                db.execSQL(INSERT_LESSON);
                db.execSQL(INSERT_BOOKING);
                db.execSQL(INSERT_COURSES);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DatabaseHelper.class.getName(), "Aggiornamento database dalla versione " + oldVersion + " alla "
                    + newVersion + ". I dati esistenti verranno eliminati.");
            onCreate(db);
        }
    }

    public DBManagement open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long insertUser(String name, String surname, String email, String password) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_SURNAME, surname);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_PASSWORD, password);
        return db.insert(USER_TABLE, null, initialValues);
    }

    public Cursor getAllUsers() {
        return db.query(USER_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_SURNAME, KEY_EMAIL, KEY_PASSWORD}, null, null, null, null, null);
    }

    public Cursor getUser(String email) {
        String sql = "SELECT id FROM utente WHERE email='" + email + "'";
        Cursor c = db.rawQuery(sql, null);
        c.moveToNext();
        return c;
    }

    public Cursor getAllLessonsByCourse(int courseId) {
        String sql = "SELECT docenza.nominativo, ripetizione.giorno, ripetizione.ora, ripetizione.id " +
                "FROM docenza INNER JOIN ripetizione ON docenza.id = ripetizione.id_docenza " +
                "WHERE docenza.id_corso ='" + courseId + "' " +
                "AND ripetizione.id NOT IN ( " +
                "SELECT id_ripetizione " +
                "FROM prenotazione " +
                "WHERE stato = 'P' OR stato = 'E') " +
                "ORDER BY giorno";

        Log.i(TAG, "getAllLessonsByCourse: courseId=" + courseId + ", query=" + sql);
        return db.rawQuery(sql, null);
    }


    public Cursor getBookingsByUser(String userId) {
        String sql = "SELECT docenza.nominativo, ripetizione.giorno, ripetizione.ora, prenotazione.stato, ripetizione.id, corso.nome " +
                "FROM docenza JOIN ripetizione ON docenza.id = ripetizione.id_docenza " +
                "JOIN prenotazione ON ripetizione.id = prenotazione.id_ripetizione " +
                "JOIN utente ON prenotazione.id_utente = utente.id " +
                "JOIN corso ON docenza.id_corso = corso.id " +
                "WHERE utente.id = " + userId + "";

        Log.i(TAG, "getBookingsByUser: userId=" + userId + ", query=" + sql);
        return db.rawQuery(sql, null);
    }

    public void insertBooking(String userId, String lessonId) {
        Log.i(TAG, "insertBooking: userId=" + userId + ", lessonId=" + lessonId);
        ContentValues initialValues = new ContentValues();
        initialValues.put("id_utente", userId);
        initialValues.put("id_ripetizione", lessonId);
        initialValues.put("stato", "P");
        db.insert("prenotazione", null, initialValues);
    }

    public void deleteBookingByLessonId(String lessonId) {
        String sql = "UPDATE prenotazione SET stato = 'C' WHERE id_ripetizione=" + lessonId;

        Log.i(TAG, "deleteBookingById: id=" + lessonId + ", sql=" + sql);
        db.execSQL(sql);
    }

    public Cursor getCourses() {
        Log.i(TAG, "getCourses");
        return db.query(COURSE_TABLE, new String[]{KEY_ID, KEY_NAME, KEY_DESCRIPTION}, null, null, KEY_NAME, null, null);
    }

    public Cursor getNCourses() {
        Log.i(TAG, "getNCourses");
        String sql = "SELECT COUNT(*) FROM corso ";
        return db.rawQuery(sql, null);
    }
}