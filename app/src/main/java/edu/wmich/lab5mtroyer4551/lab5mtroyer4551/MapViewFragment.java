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
* This handles the map interactions
* and the main screen of the app in
* general. The user can tap the map
* to add a pin, tap a pin to change
* the information, and get a list
* of all of the restaurants they
* have saved.
*************************************
*/

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapViewFragment extends SupportMapFragment {

    private final int ACTIVITY_RESULT_SELECTION = 1;

    private GoogleApiClient client;
    private GoogleMap map;
    private SQLiteDatabase database;
    private Location currentLocation;
    private LatLng tappedLocation;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //menu handling won't be a thing
        //if it ain't got this swing
        setHasOptionsMenu(true);

        //create a new database
        //getContext().deleteDatabase("restaurants.db");
        database = new RestaurantHelper(getContext())
                .getWritableDatabase();

        //get the google api
        client = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {
                        //reload the options and the ui when connected
                        getActivity().invalidateOptionsMenu();
                        updateUI();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }

                })
                .build();

        //set up the google map and it's options
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                //when clicked, update the tapped location and the ui
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        currentLocation = null;
                        tappedLocation = latLng;

                        updateUI();
                    }
                });

                //when a marker is clicked
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        //Log.d("Marker", marker.toString() + " TITLE: " + marker.getTitle() + " ID: " + marker.getId());

                        //start the screen to fill in the restaurants name
                        Intent intent = new Intent(getActivity(), RestaurantInfoActivity.class);
                        intent.putExtra("EXTRA_NAME", marker.getTitle());
                        intent.putExtra("EXTRA_POSITION", marker.getPosition());

                        startActivity(intent);

                        return false;
                    }
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        //require a reload on the options menu
        getActivity().invalidateOptionsMenu();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    //create the menu and add items to the bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);

        //don't enable location finding if the map is not connected yet
        MenuItem find_location = menu.findItem(R.id.action_find_location);
        find_location.setEnabled(client.isConnected());
    }

    @Override //when the user touches the menu
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_find_location:
                //get the location on the button press
                getLocation();
                return true;
            case R.id.action_show_list:
                //show the list of restaurants, and wait for a result
                Intent intent = new Intent(getActivity(), LocationListActivity.class);
                getActivity().startActivityForResult(intent, ACTIVITY_RESULT_SELECTION);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //get the user's location
    private void getLocation(){
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(client, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        //Log.i("Location", "Got a fix: " + location);
                        currentLocation = location;
                        updateUI();
                    }
                });

    }

    //update the pins on the map
    private void updateUI() {
        if (map == null) {
            return;
        }

        LatLng currentPoint;

        //using the user's location
        if(currentLocation != null){
            currentPoint = new LatLng(
                    currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            //using the location tapped by the user
            currentPoint = tappedLocation;
        }

        //if the point exists and the map is not just reloading, add the marker
        if(currentPoint != null) {
            MarkerOptions myMarker = new MarkerOptions()
                    .position(currentPoint);
            map.clear();
            map.addMarker(myMarker);
        }

        //get all of the restaurants to turn them into markers
        List<Restaurant> list = new ArrayList<>();
        RestaurantCursorWrapper cursor = queryRestaurants(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getRestaurant());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        for(Restaurant r : list){
            //Log.d("Rest", r.getName() + " " + r.getLocation().toString());

            //place a marker for each of the restaurants
            Location location = r.getLocation();
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(r.getName());
            map.addMarker(marker);
        }

        //zoom to location if using current location
        if(currentLocation != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentPoint, 17.0f);
            map.animateCamera(update);
        }

    }

    //query to get all of the restaurants
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

    @Override //action when the list activity returns
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_RESULT_SELECTION) {
            if(resultCode == getActivity().RESULT_OK){

                //get the location of the point
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);

                LatLng point;

                point = new LatLng(latitude, longitude);

                //go to the selected point
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point, 17.0f);
                map.animateCamera(update);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
