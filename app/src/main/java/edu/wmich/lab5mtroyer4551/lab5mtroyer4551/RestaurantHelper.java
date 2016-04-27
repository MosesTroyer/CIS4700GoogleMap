package edu.wmich.lab5mtroyer4551.lab5mtroyer4551;

/*
*************************************
* Programmer: Moses Troyer
* Class ID: mtroyer4551
* Lab 5
* CIS 4700: Mobile Commerce Development
* Spring 2016
* Due date: 4/24/16
* Date completed: 4/24/16
*************************************
* Database helper to create a new DB
* when needed, with the restaurants
* table
*************************************
*/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "restaurants.db";

    public RestaurantHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + RestaurantDBSchema.RestaurantTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                RestaurantDBSchema.RestaurantTable.Cols.NAME + ", " +
                RestaurantDBSchema.RestaurantTable.Cols.LATITUDE + ", " +
                RestaurantDBSchema.RestaurantTable.Cols.LONGITUDE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    }

}
