package com.vunke.catv_push.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Created by zhuxi on 2020/8/5.
 */
class PushSQLite(context: Context): SQLiteOpenHelper(context, PushTable.DATABASE_NAME,null,PushTable.DATABASE_VERSION) {
    val TAG = "PushSQLite"
    override fun onCreate(db: SQLiteDatabase?) {
        try {
            Log.i(TAG,"onCreate")
            var createTable = "create table if not exists ${PushTable.TABLE_NAME}" +
                    " (" +
                    "${PushTable.GROUP_ID} varchat primary key, "+
                    "${PushTable.PUSH_ID} varchat, " +
                    "${PushTable.PUSH_NAME} varchat, " +
                    "${PushTable.PUSH_TITLE} varchat, " +
                    "${PushTable.CREATE_TIMES} integer, " +
                    "${PushTable.EXPIRE_TIME} integer, " +
                    "${PushTable.SHOW_START_TIME} integer, " +
                    "${PushTable.SHOW_END_TIME} integer, " +
                    "${PushTable.SHOW_TIMES} integer, " +
                    "${PushTable.PUSH_STATUS} varchat, " +
                    "${PushTable.PUSH_TYPE} varchat, " +
                    "${PushTable.PUSH_LINK} varchat, " +
                    "${PushTable.PUSH_UTL} varchat, " +
                    "${PushTable.PUSH_MESSAGE} varchat, " +
                    "${PushTable.WIDTH} integer, " +
                    "${PushTable.HEIGHT} integer, " +
                    "${PushTable.MARGIN_LEFT} integer, " +
                    "${PushTable.MARGIN_TOP}  integer, "+
                    "${PushTable.ACC_TIME} integer, " +
                    "${PushTable.TASK_ID} varchat , " +
                    "${PushTable.TOPIC_TYPE} varchat, " +
                    "${PushTable.DURATION} integer, "+
                    "${PushTable.INTERVAL_TIME} integer, "+
                    "${PushTable.SHOW_RULES} integer ,"+
                    "${PushTable.SYSTEM_ID} integer ,"+
                    "${PushTable.START_TIME} integer ,"+
                    "${PushTable.END_TIME} integer "+
                    ")"
            Log.i(TAG,"sql:$createTable")
            db!!.execSQL(createTable)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            Log.i(TAG,"onUpgrade")
            db!!.execSQL("drop table if exists ${PushTable.TABLE_NAME}")
            onCreate(db)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}