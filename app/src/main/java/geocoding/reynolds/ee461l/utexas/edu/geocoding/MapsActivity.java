package geocoding.reynolds.ee461l.utexas.edu.geocoding;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity {

    com.google.android.gms.maps.MapView mapView;
    String APIkey = "AIzaSyCEjmzkWPOxEj8r_kDM2sX6w9qmY50WlSw";
    String geocodeURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String geocodeURLTail = "&key=";

    EditText addressForm;
    ImageView searchButton;
    GoogleMap map;

    // private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // setUpMapIfNeeded();

        addressForm = (EditText) findViewById(R.id.editText_addressForm);
        searchButton = (ImageView) findViewById(R.id.imageView_search);
        mapView = (MapView) findViewById(R.id.mapView_map);

        // Why?
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Map settings
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);

        try {
            MapsInitializer.initialize(this);
        }
        catch (Exception e) {
            Log.d("GOOGLE PLAY SERVICES", "NOT AVAILABLE");
        }

        mapView.onResume();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAddress();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setUpMapIfNeeded();
    }

//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (map == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            map = (MapView) findViewById(R.id.mapView_map);
//            // Check if we were successful in obtaining the map.
//            if (map != null) {
//                setUpMap();
//            }
//        }
//    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     */

    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    // TODO: test and add functionality
    public void searchAddress() {

        // Replaces all the spaces with +'s
        String addressString = addressForm.getText().toString();

        // Not sure if we need this or not
        addressString = addressString.replaceAll(" ", "+");

        String geocodeFullUrl = geocodeURL + addressString + geocodeURLTail + APIkey;

        try {
            JSONObject locationData = new SearchAddress().execute(geocodeFullUrl).get();

            /* if(locationData == null){
                Toast.makeText(this, "Null pointer", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, locationData.toString(), Toast.LENGTH_SHORT).show();
            } */

            JSONArray results = locationData.getJSONArray("results");

            JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");

            String latitude = location.getString("lat");
            String longitude = location.getString("lng");

            Toast.makeText(this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
        }
        catch(InterruptedException e){

        }
        catch(ExecutionException e){

        }
        catch(JSONException e){

        }


        /*
        List<Address> foundGeocode = null;
        try {
            foundGeocode = new Geocoder(this).getFromLocationName(addressString, 1);

            double latitude = foundGeocode.get(0).getLatitude();
            double longitude = foundGeocode.get(0).getLongitude();

            String test = new String("Latifude: " + latitude + " Longitude: " + longitude);

            Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
        }
        catch(IOException e){

        }
        */

//        Toast.makeText(this, addressString, Toast.LENGTH_SHORT).show();
    }
}
