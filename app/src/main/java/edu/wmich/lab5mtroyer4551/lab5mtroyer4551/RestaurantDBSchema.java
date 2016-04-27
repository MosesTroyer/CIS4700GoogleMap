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
* This is the schema for the
* restaurants table. Holds relevant
* spaces for the name and location
*************************************
*/

public class RestaurantDBSchema {

    public static final class RestaurantTable {
        public static final String NAME = "restaurants";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
        }

    }

}
