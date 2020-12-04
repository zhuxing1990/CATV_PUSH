package com.vunke.catv_push.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


/**
 * Created by zhuxi on 2020/8/13.
 */
class TopicSQLite(context:Context):SQLiteOpenHelper(context,TopicTable.DATABASES_NAME,null,TopicTable.DATABASES_VERSION) {
    val TAG = "TopocSQLite"
    override fun onCreate(db: SQLiteDatabase?) {
       try {
           var createTable = "create table if not exists ${TopicTable.TABLE_NAME} (${TopicTable.TOPIC_NAME} varchat,${TopicTable.QOS} integer )"
           Log.i(TAG,"onCreate :$createTable")
           db!!.execSQL(createTable)
       }catch (e:Exception){
        e.printStackTrace()
       }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            db!!.execSQL("drop table if exists ${TopicTable.TABLE_NAME}")
            onCreate(db)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}