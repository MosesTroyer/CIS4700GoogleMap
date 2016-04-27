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
* This handles the interactions when
* changing the information for a
* restaurant, or adding a new one
*************************************
*/

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class RestaurantInfoFragment extends Fragment {

    private Bundle extras;
    private SQLiteDatabase database;
    private String name;
    private EditText restaurantName;
    private LatLng position;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        extras = getActivity().getIntent().getExtras();

        //get the database
        database = new RestaurantHelper(getContext())
                .getWritableDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        //get the passed name and location
        name = extras.getString("EXTRA_NAME");
        position = (LatLng) extras.get("EXTRA_POSITION");

        //if(name != null)
        //    Log.d("name", name);

        //set the text of the restaurant
        restaurantName = (EditText) view.findViewById(R.id.editTextRestaurantName);
        restaurantName.setText(name);

        Button buttonSave = (Button) view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d("rest name", restaurantName.getText().toString());

                //require the user to actually enter a name
                if(restaurantName.getText().length() == 0){
                    Toast toast = Toast.makeText(getContext(), R.string.toast_enter_name, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                name = restaurantName.getText().toString();

                if(existsInDB(database, position.latitude, position.longitude)){
                    //update the field
                    //Log.d("updating", "updating");
                    update(database, name, position.latitude, position.longitude);
                } else {
                    //create a new one
                    //Log.d("creating", "creating");
                    ContentValues values = new ContentValues();
                    values.put(RestaurantDBSchema.RestaurantTable.Cols.NAME, name);
                    values.put(RestaurantDBSchema.RestaurantTable.Cols.LATITUDE, position.latitude);
                    values.put(RestaurantDBSchema.RestaurantTable.Cols.LONGITUDE, position.longitude);
                    database.insert(RestaurantDBSchema.RestaurantTable.NAME, null, values);
                }

                getActivity().finish();
            }
        });

        return view;
    }

    //check if the record exists
    public boolean existsInDB(SQLiteDatabase database, double latitude, double longitude) {
        String Query = "Select * from " + RestaurantDBSchema.RestaurantTable.NAME
                + " where "
                + RestaurantDBSchema.RestaurantTable.Cols.LATITUDE + " = " + latitude
                + " and " + RestaurantDBSchema.RestaurantTable.Cols.LONGITUDE + " = " + longitude;
        Cursor cursor = database.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    //update the existing record
    public void update(SQLiteDatabase database, String name, double latitude, double longitude) {
        String Query = "update " + RestaurantDBSchema.RestaurantTable.NAME
                + " set " + RestaurantDBSchema.RestaurantTable.Cols.NAME + " = '" + name + "'"
                + " where " + RestaurantDBSchema.RestaurantTable.Cols.LATITUDE + " = " + latitude
                + " AND " + RestaurantDBSchema.RestaurantTable.Cols.LONGITUDE + " = " + longitude;
        database.execSQL(Query);
    }

}
