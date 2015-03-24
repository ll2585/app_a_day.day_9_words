package com.luke.appaday.day_9_words;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.HashMap;

public class WordContentProvider extends ContentProvider {

    public static String PROVIDER_NAME = "com.luke.appaday.day_9_words.WordContentProvider";

    public static final String URL = "content://" + PROVIDER_NAME + "/words";

    public static final Uri CONTENT_URL = Uri.parse(URL);
    public static final String id = "id";
    public static final String name = "name";
    public static final int uriCode = 1;

    private static HashMap<String, String> values;

    public static final UriMatcher uriMatcher;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "words", uriCode);
    }

    private SQLiteDatabase sqlDB;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
