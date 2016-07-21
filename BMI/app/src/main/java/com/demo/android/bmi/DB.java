package com.demo.android.bmi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by peter.chiu on 2016/1/21.
 */
public class DB {
    private Context mContext = null;
    private DatabaseHelper dbHelper ;
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "history.db";
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_TABLE = "history";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ITEM = "item";
    public static final String KEY_CREATED = "created";

    /** Constructor */
    public DB(Context context) {
        this.mContext = context;
    }

    public DB open () throws SQLException {
        dbHelper = new DatabaseHelper(mContext);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {

        dbHelper.close();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE =
                "CREATE TABLE " + DATABASE_TABLE + "("
                        + KEY_ROWID + " INTEGER PRIMARY KEY,"
                        + KEY_ITEM + " TEXT NOT NULL,"
                        + KEY_CREATED + " TIMESTAMP"
                        +");";

        //		public DatabaseHelper(Context context, String name,
//				CursorFactory factory, int version) {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }

    }

    //CRUD
    //DATABASE_TABLE, which table to select; strCols, which columns to return; null, where clause; null, where arguments;
    //null, group by clause; null, having clause; DESC, order by clause
    public Cursor getAll() {
//        return db.rawQuery("SELECT * FROM "+ DATABASE_TABLE + " ORDER BY "+ KEY_CREATED +" DESC", null);
//        return db.query(DATABASE_TABLE, strCols, null, null, null, null, KEY_CREATED + " DESC");
        return db.query(DATABASE_TABLE, //Which table to Select
//	         strCols,// Which columns to return
                new String[] {KEY_ROWID, KEY_ITEM, KEY_CREATED},
                null, // WHERE clause
                null, // WHERE arguments
                null, // GROUP BY clause
                null, // HAVING clause
                KEY_CREATED + " DESC" //Order-by clause
        );
    }

//    String[] strCols = new String[]{
//            KEY_ROWID, KEY_ITEM, KEY_CREATED
//    };
     // add an entry //
    public long create(String record){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
        Date now = new Date();
        ContentValues args = new ContentValues();
//        args.put(KEY_ROWID,);
        args.put(KEY_ITEM, record);
        args.put(KEY_CREATED, df.format(now.getTime()));
        return db.insert(DATABASE_TABLE, null, args);
    }
}
