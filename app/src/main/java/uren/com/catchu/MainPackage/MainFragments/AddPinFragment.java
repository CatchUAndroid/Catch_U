package uren.com.catchu.MainPackage.MainFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import uren.com.catchu.Constants.PermissionConstant;
import uren.com.catchu.R;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AddPinFragment extends BaseFragment
        implements OnMapReadyCallback,
        View.OnClickListener,
        LocationListener{

    private View mView;
    private GoogleMap mMap;

    //List of Permissions
    boolean isPermissionGranted;

    //Location
    Location location;
    private LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; // 20 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 0; // 0 minute


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_add_pin, container, false);
            MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_view_map);
            mapFragment.getMapAsync(this);
        }

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mMap != null) {
            checkLocationPermissions();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(true);

        checkLocationPermissions();

    }

    private void checkLocationPermissions() {

        // ACCESS_FINE_LOCATION - (GPS)
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously*
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PermissionConstant.PERMISSION_REQUEST_ACCESS_FINE_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PermissionConstant.PERMISSION_REQUEST_ACCESS_FINE_LOCATION);

            }

        } else {
            isPermissionGranted = true;
            initMap();
        }
    }

    private void initMap() {

        location = getLocation();

        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                    LatLng(location.getLatitude(),
                    location.getLongitude()), 14));

        } else {
            LatLng sydney = new LatLng(-33.852, 151.211); //sydney
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
        }

        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionConstant.PERMISSION_REQUEST_ACCESS_FINE_LOCATION : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), " ACCESS_FINE_LOCATION - Permission granted", Toast.LENGTH_SHORT).show();
                    isPermissionGranted = true;
                    initMap();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnDropMarker:
                dropMarkerClicked();
                break;
        }


    }

    private void dropMarkerClicked() {
        getLocation();
    }

    public Location getLocation() {

        Location loc = null;

        if (!isPermissionGranted) {
            return null;
        }

        try {

            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); // GPS durumunu(true/false) elde ettik
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // Network durumunu(true/false) elde ettik


            if (!isGPSEnabled && !isNetworkEnabled) {
                //Network olmadığında bu koşula girer
            } else {
                this.canGetLocation = true;

                //Network Provider'dan ilk lokasyonu aldık
                if (isNetworkEnabled) {

                    if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }

                    }

                }

                //Eğer GPS etkin ise GPS Services kullanarak  latitude/longitude değerlerini alıyoruz
                if (isGPSEnabled) {
                    if (loc == null) {

                        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);


                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loc;

    }


}
