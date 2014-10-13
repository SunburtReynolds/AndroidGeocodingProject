package geocoding.reynolds.ee461l.utexas.edu.geocoding;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends Activity {

    private Context context;

    MapView mapView;
    String APIkey = "AIzaSyCqPD0WxZJe4A6Rqy3aYBgXTDKNsJRG5Pg";
    String geocodeURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String geocodeURLTail = "&key=";

    String searchesFile = "searches";
    List<String> recentSearches = new ArrayList<String>();

    EditText addressForm;
    ImageView searchButton;
    GoogleMap map;
    Marker previousMarker;
    TextView vLongitude;
    TextView vLatitude;

    FileOutputStream searchesOutput = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // setUpMapIfNeeded();

        addressForm = (EditText) findViewById(R.id.editText_addressForm);
        searchButton = (ImageView) findViewById(R.id.imageView_search);
        vLatitude = (TextView) findViewById(R.id.textView_latitude);
        vLongitude = (TextView) findViewById(R.id.textView_longitude);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView_map))
                .getMap();

        //Enable GPS
        map.setMyLocationEnabled(true);

        // Get past searches
        FileInputStream input = null;
        BufferedReader reader = null;

        // If an input file exists, load the arraylist with recent searches
        if(fileExistence(searchesFile)) {
            try {
                input = openFileInput(searchesFile);
                reader = new BufferedReader(new InputStreamReader(input));

                String search = reader.readLine();
                while (search != null) {
                    recentSearches.add(search);
                    search = reader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Get the output stream to write the searches to a file
        try{
            searchesOutput = openFileOutput(searchesFile, Context.MODE_PRIVATE);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = addressForm.getText().toString();

                if (!(address == null)) {
                    recentSearches.add(address);

                    try {
                        searchesOutput.write((address + "\n").getBytes());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    searchAddress();
                }
                else {
                    Toast.makeText(context, "You must enter an address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy(){
        try {
            searchesOutput.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_mapType:
                openAlertMapType();
                return true;
            case R.id.menu_recentSearches:
                openAlertRecentSearches();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

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
            String formattedAddress = results.getJSONObject(0).getString("formatted_address");
            JSONObject location = geometry.getJSONObject("location");

            Double latitude = location.getDouble("lat");
            Double longitude = location.getDouble("lng");
            LatLng coordinates = new LatLng(latitude, longitude);

            vLatitude.setText(location.getString("lat"));
            vLongitude.setText(location.getString("lng"));

            Marker newMarker = map.addMarker(new MarkerOptions().position(coordinates)
                .title(formattedAddress));

            // Move the camera instantly to hamburg with a zoom of 15.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));

            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

            if (previousMarker != null) {
                previousMarker.remove();
            }
            previousMarker = newMarker;
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

    public void openAlertMapType() {
        AlertDialog mapTypeDialog;

        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] mapTypes = {"Normal","Satellite","Hybrid","Terrain"};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your map type:");
        builder.setSingleChoiceItems(mapTypes, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {


                switch (item) {
                    case 0:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case 3:
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    default:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                }
                dialog.dismiss();
            }
        });
        mapTypeDialog = builder.create();
        mapTypeDialog.show();
    }

    public void openAlertRecentSearches() {
        AlertDialog recentSearchesDialog;

        // Required to have a char sequence; convert array list
        CharSequence[] recentSearchesCS = recentSearches.toArray(new CharSequence[recentSearches.size()]);

        if (recentSearchesCS.length > 0) {

            // Creating and Building the Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Recent searches:");
            builder.setItems(recentSearchesCS, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // populate the edit text with the selected search
                    String selectedItem = recentSearches.get(item);
                    addressForm.setText(selectedItem);

                    dialog.dismiss();
                }
            });
            recentSearchesDialog = builder.create();
            recentSearchesDialog.show();

        }
        else {

            Toast.makeText(this, "No previous searches.", Toast.LENGTH_SHORT).show();

        }
    }

    public boolean fileExistence(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

}
