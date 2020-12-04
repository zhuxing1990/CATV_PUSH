package com.vunke.catv_push.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vunke.catv_push.db.TopicSQLite;
import com.vunke.catv_push.db.TopicTable;



/**
 * Created by zhuxi on 2020/8/5.
 */

public class TopicProvider extends ContentProvider {
    private static final String TAG = "TopicProvider";

    private static String AUTHORITH= "com.vunke.catv_push.topic";
    private static String PATH ="/topic_info";
    private static String PATHS ="/topic_info/#";

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CODE_DIR = 1;
    private static final int CODE_ITEM = 2;
    static {
        uriMatcher.addURI(AUTHORITH, PATH, CODE_DIR);
        uriMatcher.addURI(AUTHORITH, PATHS, CODE_ITEM);
    }
    private TopicSQLite sqLite;
    private SQLiteDatabase db;
    @Override
    public boolean onCreate() {
        sqLite = new TopicSQLite(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        db = sqLite.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TopicTable.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CODE_DIR:
                return "vnd.android.cursor.dir/" + TopicTable.TABLE_NAME;
            case CODE_ITEM:
                return "vnd.android.cursor.item/" + TopicTable.TABLE_NAME;
            default:
                throw new IllegalArgumentException("异常参数:Unknown URI:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        db = sqLite.getWritableDatabase();
        long count=-1;
        try {
            count = db.insert(TopicTable.TABLE_NAME,null, values);
            getContext().getContentResolver().notifyChange(uri,null);
            return  ContentUris.withAppendedId(uri, count);
        }catch (Exception e){
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        db = sqLite.getWritableDatabase();
        int number = -1;
        try {
            number = db.delete(TopicTable.TABLE_NAME, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int number = -1;
        db = sqLite.getWritableDatabase();
        try{
            number = db.update(TopicTable.TABLE_NAME, values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }
}
