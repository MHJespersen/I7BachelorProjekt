package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    double[] coords = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        coords = intent.getDoubleArrayExtra(Constants.EXTRA_COORDS);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Aarhus and move the camera + zoom
        LatLng latlng = new LatLng(coords[0], coords[1]);
        float zoomLevel = 10.0f; // max = 21
        mMap.addMarker(new MarkerOptions().position(latlng).title(getString(R.string.map_marker_text)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
