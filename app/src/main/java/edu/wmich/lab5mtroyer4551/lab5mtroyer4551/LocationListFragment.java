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
* The code responsible for the list
* of restaurants. When an item is
* clicked, it will return to the main
* map and zoom to that location.
*************************************
*/

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LocationListFragment extends Fragment {

    private RecyclerView locationRecyclerView;
    private LocationAdapter adapter;
    private List restaurants;
    private SQLiteDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the database
        database = new RestaurantHelper(getContext())
                .getWritableDatabase();

        //start the growing list of restaurants
        restaurants = new ArrayList<>();

        //query DB
        RestaurantCursorWrapper cursor = queryRestaurants(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //add to list
                restaurants.add(cursor.getRestaurant());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location_list, container, false);

        //create a recycler view for the list of restaurants and return it
        locationRecyclerView = (RecyclerView) view
                .findViewById(R.id.location_recycler_view);
        //required or else will crash
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    public void updateUI() {

        //if there are no restaurants saved display it
        if(restaurants.size() == 0){
            Restaurant noRest = new Restaurant();
            noRest.setName("No restaurants saved.");

            restaurants.add(noRest);
        }


        if(adapter == null) {
            adapter = new LocationAdapter(restaurants);
            locationRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    //this viewholder will store references to the displayed variables
    private class LocationHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private Restaurant restaurant;

        private TextView restaurantTextView;

        public LocationHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            //set the text in the fragment
            restaurantTextView = (TextView) itemView.findViewById(R.id.list_item_text);
        }


        public void bindRestaurant(Restaurant restaurant){
            this.restaurant = restaurant;
            restaurantTextView.setText(this.restaurant.getName());
        }

        @Override //return the information needed so the map can zoon to the selected restaurant
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("latitude", restaurant.getLocation().getLatitude());
            intent.putExtra("longitude", restaurant.getLocation().getLongitude());
            getActivity().setResult(getActivity().RESULT_OK, intent);
            getActivity().finish();
        }
    }

    //recyclerview will talk to this when a viewholder needs to be created or connected
    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder>{
        private List<Restaurant> restaurants;

        public LocationAdapter(List<Restaurant> restaurants){
            this.restaurants = restaurants;
        }

        @Override //inflate a new view
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_restaurant, parent, false);
            return new LocationHolder(view);
        }

        @Override //set the text
        public void onBindViewHolder(LocationHolder holder, int position) {
            Restaurant restaurant = restaurants.get(position);
            holder.bindRestaurant(restaurant);
        }

        @Override //get restaurant count
        public int getItemCount() {
            return restaurants.size();
        }
    }

    //select all of the available restaurants
    private RestaurantCursorWrapper queryRestaurants(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                RestaurantDBSchema.RestaurantTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        return new RestaurantCursorWrapper(cursor);
    }

}
