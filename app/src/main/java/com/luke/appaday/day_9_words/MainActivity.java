package com.luke.appaday.day_9_words;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends ActionBarActivity {
    private String DB_NAME = "Words";
    private String TABLE_NAME = "WordTable";
    private SQLiteDatabase wordDB = null;
    private TextView wordsFromDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordsFromDB = (TextView) findViewById(R.id.words_from_db);
        wordsFromDB.setMovementMethod(new ScrollingMovementMethod());
        if(!noSQLDatabaseOrTable()){
            wordDB = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        }
        setViews();
    }

    private void setViews() {
        findViewById(R.id.no_db_warning_textview).setVisibility(noSQLDatabaseOrTable() ? View.VISIBLE : View.GONE);
        ((Button) findViewById(R.id.create_delete_db_text_view)).setText(noSQLDatabaseOrTable() ? "CREATE TABLE/DB" : "DELETE TABLE/DB");
        wordsFromDB.setText(noSQLDatabaseOrTable() ? "No Table/DB; create it first" : noWordsInDB() ? "No words in DB, enter some" : getWordsFromDB());
    }

    private String getWordsFromDB() {
        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = wordDB.rawQuery("SELECT id, word, definition FROM " + TABLE_NAME + ";", null);

        String words = "";

        // Cycle through and display every row of data
        if(cursor.moveToFirst()){

            do{

                String id = cursor.getString(cursor.getColumnIndex("id"));
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String definition = cursor.getString(cursor.getColumnIndex("definition"));

                words = words + "(" + id + ") : " + word + ": " + definition + "\n";

            }while (cursor.moveToNext());

        }

       return words;
    }

    private boolean noWordsInDB() {
        Cursor cursor = wordDB.rawQuery("SELECT count(*) FROM " + TABLE_NAME + ";", null);
        if(cursor!=null) {
            Log.d("DB CHECKER", cursor.toString());
            if(cursor.getCount()>0) {
                cursor.close();
                Log.d("DB CHECKER", "WORDS YES");
                return false;
            }
            cursor.close();
        }
        Log.d("DB CHECKER", "WORDS NO");
        return true;
    }

    private boolean noSQLDatabaseOrTable() {
        SQLiteDatabase checkDB = null;
        File dbFile = getApplicationContext().getDatabasePath(DB_NAME);

        if(!dbFile.exists()){
            Log.d("DB CHECKER", "NO DB 1");
            return true;
        }
        Log.d("DB CHECKER", "DB YES");
        checkDB = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        Cursor cursor = checkDB.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+TABLE_NAME+"'", null);
        if(cursor!=null) {
            Log.d("DB CHECKER", cursor.toString());
            if(cursor.getCount()>0) {
                cursor.close();
                Log.d("DB CHECKER", "TBL YES");
                return false;
            }
            cursor.close();
        }
        Log.d("DB CHECKER", "TBL NO");
        return true;
    }

    public void createOrDeleteDB(View view) {
        // makeDB();

        if(noSQLDatabaseOrTable()){

            //create

            wordDB = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            Log.d("DB CHECKER", String.valueOf(wordDB));
            // Execute an SQL statement that isn't select
            wordDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                    "(id integer primary key, word VARCHAR, definition VARCHAR);");

            // The database on the file system
            File database = getApplicationContext().getDatabasePath(DB_NAME);

            // Check if the database exists
            if (database.exists()) {
                Toast.makeText(this, "Database Created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Database Missing", Toast.LENGTH_SHORT).show();
            }

        }else{
            Log.d("DB CHECKER", "DEL");
            this.deleteDatabase(DB_NAME);
            //delete
        }
        setViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addItem(View view) {
        if(noSQLDatabaseOrTable()) {
            makeToast("Create the DB first.");
            return;
        }

        EditText wordEditText = (EditText) findViewById(R.id.the_word);
        EditText definitionEditText = (EditText) findViewById(R.id.the_definition);

        String theWord = wordEditText.getText().toString();
        String theDefinition = definitionEditText.getText().toString();
        if(theWord.equals("")){
            makeToast("Enter a word");
            return;
        }
        if(theDefinition.equals("")){
            makeToast("Enter a definition");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(WordContentProvider.word, theWord);
        values.put(WordContentProvider.definition, theDefinition);
        Uri uri = getContentResolver().insert(WordContentProvider.CONTENT_URL, values);

        makeToast("added " + theWord);
        setViews();
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
