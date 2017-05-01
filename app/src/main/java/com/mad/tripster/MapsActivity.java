package com.mad.tripster;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<PlaceObject> placeObjectArrayList;
    Polyline polyline;
    PolylineOptions polylineOptions;
    ArrayList<LatLng> listOfPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        placeObjectArrayList = (ArrayList<PlaceObject>) getIntent().getSerializableExtra("Place_ArrayList");
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
        listOfPoints = new ArrayList<>();
        polylineOptions = new PolylineOptions();
        polyline = mMap.addPolyline(polylineOptions);
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        LatLng paris = new LatLng(40, 100);
        mMap.addMarker(new MarkerOptions().position(paris).title("Marker in Paris"));
        LatLng mumbai = new LatLng(19.2, 72.5);
        mMap.addMarker(new MarkerOptions().position(mumbai).title("Marker in Mumbai"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mumbai));*/
        LatLng temp;
        if(placeObjectArrayList!=null) {
            for (int i = 0; i < placeObjectArrayList.size(); i++) {
                temp = new LatLng(placeObjectArrayList.get(i).getPlace_lat(), placeObjectArrayList.get(i).getPlace_lng());
                mMap.addMarker(new MarkerOptions().position(temp).title(placeObjectArrayList.get(i).getPlace_name()));
                listOfPoints.add(temp);
                polyline.setPoints(listOfPoints);

            }

            StringBuilder sb = new StringBuilder();
            sb.append("origin=" + placeObjectArrayList.get(0).getPlace_lat() + "," + placeObjectArrayList.get(0).getPlace_lng());
            String origin = sb.toString();
            sb = new StringBuilder();
        }

        //https://maps.googleapis.com/maps/api/directions/json?origin=&destination=Concord,MA&waypoints=Charlestown,MA|Lexington,MA&key=AIzaSyAyH3NemtU1p2qYr9n0CTAKPSFKkMyrg7M
    }
}
