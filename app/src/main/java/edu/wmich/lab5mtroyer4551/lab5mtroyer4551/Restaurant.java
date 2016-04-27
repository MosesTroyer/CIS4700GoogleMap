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
* Restaurant object stores the
* information needed
*************************************
*/

import android.location.Location;

public class Restaurant {

    private String name;
    private Location location;

    public Restaurant(){}

    public Restaurant(String name, Location location){
        setName(name);
        setLocation(location);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Location getLocation(){
        return location;
    }

    public void setLocation(Location location){
        this.location = location;
    }



}
