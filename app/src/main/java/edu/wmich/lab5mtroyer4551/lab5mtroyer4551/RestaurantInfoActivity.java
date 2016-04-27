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
* Put in a new Restaurant Info Fragment
*************************************
*/

import android.support.v4.app.Fragment;

public class RestaurantInfoActivity extends SingleFragmentActivity {

    protected Fragment createFragment(){
        return new RestaurantInfoFragment();
    }
}
