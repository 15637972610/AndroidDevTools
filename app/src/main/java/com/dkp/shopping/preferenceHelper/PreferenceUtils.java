package com.dkp.shopping.preferenceHelper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by dkp on 2017/4/5.
 */

public class PreferenceUtils {
    private static final String TAG = "PreferenceUtils";

    private static long insert(ContentResolver cr, PreferenceTable.Item item) {
        if (item != null) {
            return insert(cr, item.getFile(), item.getKey(), item.getValue(), item.getType());
        }
        return -1;
    }

    private static long insert(ContentResolver cr, String file, String key, String value, String type) {
        ContentValues values = new ContentValues();
        values.put(PreferenceTable.FILE, file);
        values.put(PreferenceTable.KEY, key);
        values.put(PreferenceTable.VALUE, value);
        values.put(PreferenceTable.TYPE, type);

        Uri uri = cr.insert(PreferenceTable.CONTENT_URI, values);
        String itemId = uri.getPathSegments().get(1);
        return Integer.valueOf(itemId).longValue();
    }

    private static boolean update(ContentResolver cr, long id, String value) {
        Uri uri = ContentUris.withAppendedId(PreferenceTable.CONTENT_URI, id);

        ContentValues values = new ContentValues();
        values.put(PreferenceTable.VALUE, value);
        int count = cr.update(uri, values, null, null);

        return count > 0;
    }

    public static boolean remove(ContentResolver cr, int id) {
        Uri uri = ContentUris.withAppendedId(PreferenceTable.CONTENT_URI, id);
        int count = cr.delete(uri, null, null);
        return count > 0;
    }

    private static long getId(ContentResolver cr, String file, String key, String type) {
        PreferenceTable.Item item = getItem(cr, file, key, type);
        if (item != null) {
            return item.getId();
        }
        return -1;
    }

    public static void setItem(ContentResolver cr, PreferenceTable.Item item) {
        String file = item.getFile();
        String key = item.getKey();
        String value = item.getValue();
        String type = item.getType();
        if (TextUtils.isEmpty(file) || TextUtils.isEmpty(key) || TextUtils.isEmpty(type)) {
            Log.i(TAG, "setItem error " + item);
            return;
        }
        Log.d(TAG, "setItem " + item);
        long id = getId(cr, file, key, type);
        if (id >= 0) {
            update(cr, id, value);
        } else {
            insert(cr, item);
        }
    }

    public static PreferenceTable.Item getItem(ContentResolver cr, PreferenceTable.Item item) {
        String file = item.getFile();
        String key = item.getKey();
        String value = item.getValue();
        String type = item.getType();
        if (TextUtils.isEmpty(file) || TextUtils.isEmpty(key) || TextUtils.isEmpty(type)) {
            Log.i(TAG, "getItem error " + item);
            return null;
        }
        return getItem(cr, file, key, type);
    }

    private static PreferenceTable.Item getItem(ContentResolver cr, String file, String key, String type) {
        String selection = PreferenceTable.FILE + " = ? AND " +
                PreferenceTable.KEY + " = ? AND " +
                PreferenceTable.TYPE + " = ? ";
        String[] selectionArgs = new String[]{file, key, type};
        String[] projection = new String[]{
                PreferenceTable.ID,
                PreferenceTable.FILE,
                PreferenceTable.KEY,
                PreferenceTable.VALUE,
                PreferenceTable.TYPE
        };

        Cursor cursor = null;
        PreferenceTable.Item result = null;
        try {
            cursor = cr.query(PreferenceTable.CONTENT_URI, projection, selection, selectionArgs, PreferenceTable.DEFAULT_SORT_ORDER);
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            long itemId = cursor.getLong(0);
            String itemFile = cursor.getString(1);
            String itemKey = cursor.getString(2);
            String itemValue = cursor.getString(3);
            String itemType = cursor.getString(4);
            result = new PreferenceTable.Item(itemId, itemFile, itemKey, itemValue, itemType);
            Log.d(TAG, "getItem result: " + result);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
                cursor = null;
            }
        }
        return result;


    }
}
