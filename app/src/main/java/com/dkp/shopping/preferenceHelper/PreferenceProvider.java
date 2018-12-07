package com.dkp.shopping.preferenceHelper;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by dkp on 2017/4/5.
 */

public class PreferenceProvider extends ContentProvider {
    private static final String TAG = "PreferenceProvider";

    private static final String DB_NAME = "preferencehelper.db";
    private static final String DB_TABLE = PreferenceTable.TABLE;
    private static final int DB_VERSION = 1;

    private static final String DB_CREATE = "create table " + DB_TABLE +
            " (" + PreferenceTable.ID + " integer primary key autoincrement, " +
            PreferenceTable.FILE + " text not null, " +
            PreferenceTable.KEY + " text not null, " +
            PreferenceTable.VALUE + " text, " +
            PreferenceTable.TYPE + " text not null);";

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PreferenceTable.AUTHORITY, "item", PreferenceTable.ITEM);
        uriMatcher.addURI(PreferenceTable.AUTHORITY, "item/#", PreferenceTable.ITEM_ID);
    }

    private static final HashMap<String, String> projectionMap;

    static {
        projectionMap = new HashMap<String, String>();
        projectionMap.put(PreferenceTable.ID, PreferenceTable.ID);
        projectionMap.put(PreferenceTable.FILE, PreferenceTable.FILE);
        projectionMap.put(PreferenceTable.KEY, PreferenceTable.KEY);
        projectionMap.put(PreferenceTable.VALUE, PreferenceTable.VALUE);
        projectionMap.put(PreferenceTable.TYPE, PreferenceTable.TYPE);
    }

    private DBHelper dbHelper = null;
    private ContentResolver resolver = null;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        resolver = context.getContentResolver();
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PreferenceTable.ITEM:
                return PreferenceTable.CONTENT_TYPE;
            case PreferenceTable.ITEM_ID:
                return PreferenceTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != PreferenceTable.ITEM) {
            throw new IllegalArgumentException("Error Uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = db.insert(DB_TABLE, PreferenceTable.ID, values);
        if (id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }

        Uri newUri = ContentUris.withAppendedId(uri, id);
        resolver.notifyChange(newUri, null);

        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case PreferenceTable.ITEM: {
                count = db.update(DB_TABLE, values, selection, selectionArgs);
                break;
            }
            case PreferenceTable.ITEM_ID: {
                String id = uri.getPathSegments().get(1);
                count = db.update(DB_TABLE, values, PreferenceTable.ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " and (" + selection + ')' : ""), selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }

        resolver.notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case PreferenceTable.ITEM: {
                count = db.delete(DB_TABLE, selection, selectionArgs);
                break;
            }
            case PreferenceTable.ITEM_ID: {
                String id = uri.getPathSegments().get(1);
                count = db.delete(DB_TABLE, PreferenceTable.ID + "=" + id
                        + (!TextUtils.isEmpty(selection) ? " and (" + selection + ')' : ""), selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }

        resolver.notifyChange(uri, null);

        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        String limit = null;
        switch (uriMatcher.match(uri)) {
            case PreferenceTable.ITEM: {
                sqlBuilder.setTables(DB_TABLE);
                sqlBuilder.setProjectionMap(projectionMap);
                break;
            }
            case PreferenceTable.ITEM_ID: {
                String id = uri.getPathSegments().get(1);
                sqlBuilder.setTables(DB_TABLE);
                sqlBuilder.setProjectionMap(projectionMap);
                sqlBuilder.appendWhere(PreferenceTable.ID + "=" + id);
                break;
            }
            default:
                throw new IllegalArgumentException("Error Uri: " + uri);
        }

        Cursor cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, TextUtils.isEmpty(sortOrder) ? PreferenceTable.DEFAULT_SORT_ORDER : sortOrder, limit);
        cursor.setNotificationUri(resolver, uri);

        return cursor;
    }

    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    }
}
