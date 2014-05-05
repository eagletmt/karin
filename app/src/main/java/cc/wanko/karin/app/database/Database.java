package cc.wanko.karin.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eagletmt on 14/05/05.
 */
public class Database {

    private static final String TOP_IDS_TABLE = "top_ids";
    private static final String KEY_COLUMN = "key";
    private static final String TOP_ID_COLUMN = "top_id";

    private static class Helper extends SQLiteOpenHelper {
        private static final String DB_NAME = "karin.db";
        private static final int DB_VERSION = 1;

        public Helper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TOP_IDS_TABLE
                    + " (" + KEY_COLUMN + " varchar(128) PRIMARY KEY,"
                    + TOP_ID_COLUMN + " integer NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + TOP_IDS_TABLE);
            onCreate(db);
        }
    }

    private SQLiteDatabase db;

    public Database(Context context) {
        db = new Helper(context).getWritableDatabase();
    }

    public void storeTopId(String key, long topId) {
        ContentValues values = new ContentValues();
        values.put(KEY_COLUMN, key);
        values.put(TOP_ID_COLUMN, topId);
        db.insertWithOnConflict(TOP_IDS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
