package com.example.chen41283922.wordmemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by chen41283922 on 2016/1/9.
 */
public class MyDB {

    public SQLiteDatabase db=null;
    private final static String	DATABASE_NAME= "db1.db";
    private final static String	TABLE_NAME="table01";
    private final static String	_ID	= "_id";
    private final static String	WORD = "word";
    private final static String	DEF = "def";
    private final static String	TYPE = "type";


    private final static String	CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID
            + " INTEGER PRIMARY KEY," + WORD + " TEXT," + DEF + " TEXT," + TYPE + " TEXT );" ;

    private Context mCtx = null;
    public MyDB(Context ctx){
        this.mCtx = ctx;
    }

    public void open() throws SQLException {
        db = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        try	{
            db.execSQL(CREATE_TABLE);
        }catch (Exception e) {
        }
    }

    public void close() {
        db.close();
    }

    public Cursor getAll() throws SQLiteException{
        Cursor cursor = db.rawQuery("SELECT _id, word, def , type FROM table01",null);
        return cursor;
    }

    public Cursor getRandom() throws SQLiteException{
        Cursor cursor =db.rawQuery("SELECT * FROM table01 ORDER BY RANDOM() LIMIT 1", null);
        return cursor;
    }

//    public Cursor getAll() {
//        return db.query(TABLE_NAME,
//                new String[] {_ID, NAME, PRICE, DATE},
//                null, null, null, null, null,null);
//    }

    public Cursor get(long rowId) throws SQLException {
        Cursor mCursor = db.query(TABLE_NAME,
                new String[] {_ID, WORD, DEF , TYPE},
                _ID +"=" + rowId, null, null, null, null,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public long append(String word,String def , String type) {
        ContentValues args = new ContentValues();
        args.put(WORD, word);
        args.put(TYPE,type);
        args.put(DEF, def);
        return db.insert(TABLE_NAME, null, args);
    }

    public boolean delete(long rowId) {
        return db.delete(TABLE_NAME, _ID + "=" + rowId, null) > 0;
    }

    public boolean update(long rowId, String word ,String def , String type) {
        ContentValues args = new ContentValues();
        args.put(WORD, word);
        args.put(TYPE,type);
        args.put(DEF, def);
        return db.update(TABLE_NAME, args,_ID + "=" + rowId, null) > 0;
    }
}
