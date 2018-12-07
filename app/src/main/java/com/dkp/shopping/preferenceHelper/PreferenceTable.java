package com.dkp.shopping.preferenceHelper;

import android.net.Uri;

/**
 * Created by dkp on 2017/4/5.
 */

public class PreferenceTable {
    public static final String ID = "_id";
    public static final String FILE = "_file";
    public static final String KEY = "_key";
    public static final String VALUE = "_value";
    public static final String TYPE = "_type";

    public static final String TABLE = "Preferences";

    public static final String DEFAULT_SORT_ORDER = "_id asc";

    public static final int ITEM = 1;
    public static final int ITEM_ID = 2;

    public static final String AUTHORITY = "com.dkp.shopping.preferencehelper";

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.preference.provider";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.preference.provider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/item");

    public static class Item {
        private long id;
        private String file;
        private String key;
        private String value;
        private String type;

        public Item(long id, String file, String key, String value, String type) {
            this.id = id;
            this.file = file;
            this.key = key;
            this.value = value;
            this.type = type;
        }

        public Item() {
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return this.id;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "[id = " + id + ", file = " + file + ", key = " + key + ", value = " + value + ", type = " + type + "]";
        }
    }
}
