package com.zwh.quickstart.quickstart;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/3/11.
 */
public class DatabaseTable {
    private static final String TAG = "DictionaryDatabase";

    //The columns we'll include in the dictionary table
    public static final String COL_LABEL = "LABEL";
    public static final String COL_NAME = "NAME";
    public static final String COL_PACKAGE_NAME = "PACKAGE_NAME";

    private static final String DATABASE_NAME = "LAUNCHER";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;

    private final DatabaseOpenHelper mDatabaseOpenHelper;

    public DatabaseTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    public Cursor getWordMatches(CharSequence query, String[] columns) {
        String selection;
        String[] selectionArgs;

        if( null == query || query.length()<1){
            selection = null;
            selectionArgs = null;
        }
        else{
            selection = COL_LABEL + " MATCH ?";
            selectionArgs = new String[] {query+"*"};
        }
        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);


        /*if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }*/
        return cursor;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        COL_LABEL + ", " +
                        COL_NAME + ", " +
                        COL_PACKAGE_NAME + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            loadDictionary();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

        private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                        loadWords();
                }
            }).start();
        }

        private void loadWords() {
            final PackageManager packageManager = mHelperContext.getPackageManager();
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);

            String packageName, name,label;

            for (ResolveInfo resolveInfo : apps) {
                label = resolveInfo.activityInfo.loadLabel(packageManager).toString();
                name = resolveInfo.activityInfo.name;
                packageName = resolveInfo.activityInfo.packageName;

                if( LangUtils.isChinese(label) ){
                    Log.e("zhengwenhui",label+"-------isChinese");
                    label = PinYin.getPinYin(label);
                    Log.e("zhengwenhui",label);
                }
                addWord(label, name, packageName);
            }
        }

        public long addWord(String label, String name, String packageName) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_LABEL, label);
            initialValues.put(COL_NAME, name);
            initialValues.put(COL_PACKAGE_NAME, packageName);

            Log.e("addWord", label+","+name+","+packageName);

            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
        }
    }
}
