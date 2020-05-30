package com.gprs.uttarpradesh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1011;
    private UiSettings mUiSettings;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    ImageView imageButton;
    HashMap<String,UserLocationHelper> helperHashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        imageButton=findViewById(R.id.mapmenu);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu rolemenu = new PopupMenu(getApplicationContext(), imageButton);
                rolemenu.getMenuInflater().inflate(R.menu.role, rolemenu.getMenu());
                rolemenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {

                        mMap.clear();
                        FirebaseDatabase.getInstance().getReference().child("Location")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            final UserLocationHelper userLocationHelper = snapshot.getValue(UserLocationHelper.class);

                                            if(userLocationHelper.role.equals(item.toString())) {
                                                LatLng latLng = new LatLng(userLocationHelper.lat, userLocationHelper.lon);
                                              Marker marker = mMap.addMarker(new MarkerOptions()
                                                        .position(latLng)
                                                        .title(userLocationHelper.email)
                                                        .snippet(userLocationHelper.fname + '\n' + userLocationHelper.role)
                                                        .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_place_black_24dp)));
                                                marker.showInfoWindow();
                                                helperHashMap.put(marker.getSnippet(),userLocationHelper);                                                CustomInfoWindow customInfoWindow = new CustomInfoWindow(MapsActivity.this);
                                                mMap.setInfoWindowAdapter(customInfoWindow);
                                                marker.showInfoWindow();



                                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                    @Override
                                                    public void onInfoWindowClick(Marker marker) {
                                                        view(marker.getSnippet());
                                                    }
                                                });
                                            }
                                        }

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                        return true;
                    }
                });
                rolemenu.show();
            }
        });



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Add a marker in Sydney and move the camera
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        }
                    }
                });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);


            helperHashMap=new HashMap<>();
        FirebaseDatabase.getInstance().getReference().child("Location")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final UserLocationHelper userLocationHelper = snapshot.getValue(UserLocationHelper.class);



                            LatLng latLng = new LatLng(userLocationHelper.lat, userLocationHelper.lon);
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(userLocationHelper.fname)
                                    .snippet(userLocationHelper.phone+'\n'+userLocationHelper.role)
                                    .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_place_black_24dp)));
                            marker.showInfoWindow();
                            helperHashMap.put(marker.getSnippet(),userLocationHelper);
                            CustomInfoWindow customInfoWindow = new CustomInfoWindow(MapsActivity.this);
                            mMap.setInfoWindowAdapter(customInfoWindow);
                            marker.showInfoWindow();

                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    view(marker.getSnippet());
                                }
                            });
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void view(String id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(helperHashMap.get(id).fname);


        LayoutInflater inflater=getLayoutInflater();
       View view = inflater.inflate(R.layout.mapview, null, true);


        TextView email = view.findViewById(R.id.mapemail);
        TextView phone = view.findViewById(R.id.mapphone);
        TextView role = view.findViewById(R.id.maprole);

        email.setText(helperHashMap.get(id).email);
        phone.setText(helperHashMap.get(id).phone);
        role.setText(helperHashMap.get(id).role);

        AlertDialog alert = builder.create();
        alert.setView(view);
        alert.show();

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
