package com.example.hansen.sqlitetest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hansen on 8/8/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "sql7.db";
    public static final int DATABASE_VERSION = 7;

    public static final String TABLE_NAME = "coba";
    public static final String EXPENSE_COLUMN_ID = "_id";
    public static final String EXPENSE_COLUMN_TITLE = "title";
    public static final String EXPENSE_COLUMN_VALUE = "value";
    public static final String EXPENSE_COLUMN_DATE = "date";
    public static final String EXPENSE_COLUMN_CURR = "cur";
    public static final String EXPENSE_COLUMN_CAT = "cat";

    public static final String CURRENCY_TABLE_NAME = "cur";
    public static final String CURRENCY_COLUMN_ID = "_id";
    public static final String CURRENCY_COLUMN_TITLE = "title";
    public static final String CURRENCY_COLUMN_VALUE = "value";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME +
                        "(" + EXPENSE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        EXPENSE_COLUMN_TITLE + " TEXT, " +
                        EXPENSE_COLUMN_VALUE + " INTEGER, " +
                        EXPENSE_COLUMN_CURR + " TEXT, " +
                        EXPENSE_COLUMN_CAT + " TEXT, " +
                        EXPENSE_COLUMN_DATE + " TEXT)"
        );

        db.execSQL(
                "CREATE TABLE " + CURRENCY_TABLE_NAME +
                        "(" + CURRENCY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        CURRENCY_COLUMN_TITLE + " TEXT, " +
                        CURRENCY_COLUMN_VALUE + " INTEGER)"
        );

        //insertCurrencyDefault();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertExpense(String title, int value, String cur, String cat, String date){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(EXPENSE_COLUMN_TITLE, title);
        contentValues.put(EXPENSE_COLUMN_VALUE, value);
        contentValues.put(EXPENSE_COLUMN_CURR, cur);
        contentValues.put(EXPENSE_COLUMN_CAT, cat);
        contentValues.put(EXPENSE_COLUMN_DATE, date);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertCurrencyDefault(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CURRENCY_COLUMN_TITLE, "USD $");
        contentValues.put(CURRENCY_COLUMN_VALUE, 13000);

        sqLiteDatabase.insert(CURRENCY_TABLE_NAME, null, contentValues);

        contentValues.clear();
        contentValues.put(CURRENCY_COLUMN_TITLE, "IDR Rp");
        contentValues.put(CURRENCY_COLUMN_VALUE, 1);

        sqLiteDatabase.insert(CURRENCY_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateCurrency (int a, int b) {
        int id=1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CURRENCY_COLUMN_TITLE, "USD $");
        contentValues.put(CURRENCY_COLUMN_VALUE, a);

        sqLiteDatabase.update(CURRENCY_TABLE_NAME, contentValues, CURRENCY_COLUMN_ID + " = ? ",
                new String[] {Integer.toString(id)});

        id++;

        contentValues.clear();
        contentValues.put(CURRENCY_COLUMN_TITLE, "IDR Rp");
        contentValues.put(CURRENCY_COLUMN_VALUE, b);

        sqLiteDatabase.update(CURRENCY_TABLE_NAME, contentValues, CURRENCY_COLUMN_ID + " = ? ",
                new String[] {Integer.toString(id)});

        return true;
    }

    public int deleteExpense(int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME,
                EXPENSE_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteAllExpense(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public boolean updateExpense(int id, String title, int value, String cur, String cat, String date){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(EXPENSE_COLUMN_TITLE, title);
        contentValues.put(EXPENSE_COLUMN_VALUE, value);
        contentValues.put(EXPENSE_COLUMN_CURR, cur);
        contentValues.put(EXPENSE_COLUMN_CAT, cat);
        contentValues.put(EXPENSE_COLUMN_DATE, date);

        sqLiteDatabase.update(TABLE_NAME, contentValues,
                EXPENSE_COLUMN_ID + " = ? ",
                new String[] {Integer.toString(id)});
        return true;
    }

    public Cursor getExpense(int id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + EXPENSE_COLUMN_ID + "=?",
                new String[] {Integer.toString(id)});
    }

    public Cursor getCurrency(int id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM " + CURRENCY_TABLE_NAME + " WHERE " + CURRENCY_COLUMN_ID + "=?",
                new String[] {Integer.toString(id)});
    }

    public Cursor getAllExpense(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor getTotalExpense(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT " + EXPENSE_COLUMN_VALUE + " FROM " + TABLE_NAME, null);
    }
}
