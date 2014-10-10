package geocoding.reynolds.ee461l.utexas.edu.geocoding;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MapsActivity extends Activity {

    MapView mapView;
    String APIkey = "AIzaSyALzlkKlBlVv95h4zG7laoB1Gwqg71Smjw";
    String geocodeURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String geocodeURLTail = "&key=";

    EditText addressForm;
    ImageView searchButton;
    GoogleMap map;

    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // setUpMapIfNeeded();

        addressForm = (EditText) findViewById(R.id.editText_addressForm);
        searchButton = (ImageView) findViewById(R.id.imageView_search);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView_map))
                .getMap();
//        Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
//                .title("Hamburg"));
//        Marker kiel = map.addMarker(new MarkerOptions()
//                .position(KIEL)
//                .title("Kiel")
//                .snippet("Kiel is cool")
//                .icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.ic_launcher)));

//        // Move the camera instantly to hamburg with a zoom of 15.
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
//
//        // Zoom in, animating the camera.
//        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

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
