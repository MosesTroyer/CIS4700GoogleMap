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
* This wrapper builds restaurant
* objects when given the relevant
* cursor
*************************************
*/

import android.database.Cursor;
import android.database.CursorWrapper;
import android.location.Location;

public class RestaurantCursorWrapper extends CursorWrapper {

    public RestaurantCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Restaurant getRestaurant() {
        //build a new restaurant
        String name = getString(getColumnIndex(RestaurantDBSchema.RestaurantTable.Cols.NAME));
        Location location = new Location(name);
        location.setLatitude(getDouble(getColumnIndex(RestaurantDBSchema.RestaurantTable.Cols.LATITUDE)));
        location.setLongitude(getDouble(getColumnIndex(RestaurantDBSchema.RestaurantTable.Cols.LONGITUDE)));

        Restaurant restaurant = new Restaurant(name, location);

        //return it
        return restaurant;
    }

}


