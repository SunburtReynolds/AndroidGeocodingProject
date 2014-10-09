package geocoding.reynolds.ee461l.utexas.edu.geocoding;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    com.google.android.gms.maps.MapView mapView;
    String APIkey = "AIzaSyCEjmzkWPOxEj8r_kDM2sX6w9qmY50WlSw";
    EditText addressForm;
    GoogleMap map;

    // private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // setUpMapIfNeeded();

        addressForm = (EditText) findViewById(R.id.editText_addressForm);
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

        String addressString = addressForm.getText().toString();
        addressString.replace(" ", "+");

        Toast.makeText(this, "addressString", Toast.LENGTH_SHORT).show();
    }
}
