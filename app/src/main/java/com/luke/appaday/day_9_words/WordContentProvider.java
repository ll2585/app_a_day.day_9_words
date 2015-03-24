package com.luke.appaday.day_9_words;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.widget.Toast;

import java.util.HashMap;

public class WordContentProvider extends ContentProvider {

    public static String PROVIDER_NAME = "com.luke.appaday.day_9_words.WordContentProvider";

    public static final String URL = "content://" + PROVIDER_NAME + "/words";

    public static final Uri CONTENT_URL = Uri.parse(URL);
    public static final String id = "id";
    public static final String word = "word";
    public static final String definition = "definition";
    public static final int uriCode = 1;

    private static HashMap<String, String> values;

    public static final UriMatcher uriMatcher;


    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "words", uriCode);
    }

    private SQLiteDatabase sqlDB;
    public static final String DATABASE_NAME = "Words";
    public static final String TABLE_NAME = "WordTable";
    public static final int DATABASE_VERSION = 1;

    public static final String CREATE_DB_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "(id integer primary key, word VARCHAR, definition VARCHAR);";

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        if (sqlDB != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Used to create a SQL query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set table to query
        queryBuilder.setTables(TABLE_NAME);

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case uriCode:

                // A projection map maps from passed column names to database column names
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Cursor provides read and write access to the database
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null,
                null, sortOrder);

        // Register to watch for URI changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {

            // vnd.android.cursor.dir/cpcontacts states that we expect multiple pieces of data
            case uriCode:
                return "vnd.android.cursor.dir/cpcontacts";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // Gets the row id after inserting a map with the keys representing the the column
        // names and their values. The second attribute is used when you try to insert
        // an empty row
        long rowID = sqlDB.insert(TABLE_NAME, null, values);

        // Verify a row has been added
        if (rowID > 0) {

            // Append the given id to the path and return a Builder used to manipulate URI
            // references
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL, rowID);

            // getContentResolver provides access to the content model
            // notifyChange notifies all observers that a row was updated
            getContext().getContentResolver().notifyChange(_uri, null);

            // Return the Builder used to manipulate the URI
            return _uri;
        }
        Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case uriCode:
                rowsDeleted = sqlDB.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // getContentResolver provides access to the content model
        // notifyChange notifies all observers that a row was updated
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;

        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            case uriCode:

                // Update the row or rows of data
                rowsUpdated = sqlDB.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // getContentResolver provides access to the content model
        // notifyChange notifies all observers that a row was updated
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_DB_TABLE);
        }

        // Recreates the table when the database needs to be upgraded
        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int oldVersion, int newVersion) {
            sqlDB.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqlDB);
        }
    }
}
