package com.example.stevennguyen.countdownmobile;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.widget.Toast;

import java.util.Date;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cdList0.db";
    private static final String TABLE_NAME = "cdList_table";
    private static final String col_2 = "DESCRIPTION";
    private static final String col_3 = "COUNT";
    private static final String col_4 = "DATE";

    private int tDay, tMonth, tYear;
    private int[] daysInMonths = {31,28,31,30,31,30,31,31,30,31,30,31};



    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPTION TEXT, COUNT INTEGER, DATE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean insertData(String description, int count, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(col_2, description);
        contentValues.put(col_3, count);
        contentValues.put(col_4, date);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void checkForUpdate() {
        Cursor cursor = getAllData();
        String last_updated = "";
        while(cursor.moveToNext()) {
            last_updated = cursor.getString(3);
        }
        cursor.close();

        // get today's date
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        String[] dateArray = dateString.split("/");

        tMonth = Integer.valueOf(dateArray[1]);
        tDay = Integer.valueOf(dateArray[2]);
        tYear = Integer.valueOf(dateArray[0]);

        if(tYear % 4 == 0){
            daysInMonths[1] = 29;
        }

        String[] updated = last_updated.split("/");
        int m = Integer.parseInt(updated[0]);
        int d = Integer.parseInt(updated[1]);
        int y = Integer.parseInt(updated[2]);

        int t = tDay - d;

        if(m == tMonth && d <= tDay && y == tYear) {
            t += 0;
        }else if(m < tMonth && y == tYear) {   // chosen day is months of next years
            t = daysInMonths[m-1] - d;
            for(int i = m; i < tMonth-1; i++) {
                t += daysInMonths[i];
            }
            t += tDay;
        }else if(y < tYear){
            t = daysInMonths[m-1] - d;
            for(int i = m; i < 12; i++) {
                t += daysInMonths[i];
            }
            for(int i = 0; i < tMonth-1; i++) {
                t += daysInMonths[i];
            }
            t += tDay;
        }

        decrementDays(t);
    }

    private void decrementDays(int times) {
        SQLiteDatabase db = this.getWritableDatabase();
        String newDate = "\"" + tMonth+"/"+tDay+"/"+tYear + "\"";
        Cursor cursor = db.rawQuery("UPDATE cdList_table SET COUNT =  COUNT - " + times +
                ", DATE = " + newDate, null);
        cursor.moveToFirst();
        cursor.close();
    }

}
