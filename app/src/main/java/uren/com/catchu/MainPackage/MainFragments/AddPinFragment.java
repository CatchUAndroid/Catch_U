package uren.com.catchu.MainPackage.MainFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import uren.com.catchu.MainPackage.LocationUtils.PermissionUtils;
import uren.com.catchu.R;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AddPinFragment extends BaseFragment
        implements OnMapReadyCallback,
        View.OnClickListener,
        LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private View mView;
    private GoogleMap mMap;

    //List of Permissions
    boolean isPermissionGranted;
    ArrayList<String> permissions;

    //Location
    private LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; // 20 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 0; // 0 minute
    float MapZoom = 8f;
    private MapRipple mapRipple;
    private LatLng latLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_add_pin, container, false);
            createMap();
        }

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(mMap != null){
            initPermissions();
            if(isPermissionGranted){
                centerMapToCurrentLocation();
            }
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(true);

        initPermissions();
        if(isPermissionGranted){
            centerMapToCurrentLocation();
        }


    }

    private void createMap() {
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_view_map);
        mapFragment.getMapAsync(this);
    }

    private void initPermissions() {

        permissions = new ArrayList<String>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        PermissionUtils.getInstance().checkPermissions(permissions, "Need GPS permission for getting your location", 1, getActivity());
        isPermissionGranted = PermissionUtils.getInstance().isPermissionGranted;
    }

    private void centerMapToCurrentLocation() {

        Location loc = getLocation();
        LatLng mFirstLocation;
        if(loc == null){
            LatLng sydney = new LatLng(-33.852, 151.211);
            mFirstLocation = new LatLng(sydney.latitude,sydney.longitude);
        }else{
            mFirstLocation = new LatLng(loc.getLatitude(),loc.getLongitude());
        }

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(mFirstLocation,16);
        mMap.moveCamera(cameraPosition);
        mMap.animateCamera(cameraPosition);

        //Get Camera Zoom when Camera changes
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                MapZoom =  mMap.getCameraPosition().zoom;
            }
        });

        latLng = mFirstLocation ;
        mapRipple = new MapRipple(mMap, latLng, getContext());



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

        Location location = null;

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
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }

                    }

                }

                //Eğer GPS etkin ise GPS Services kullanarak  latitude/longitude değerlerini alıyoruz
                if (isGPSEnabled) {
                    if (location == null) {

                        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);


                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;

    }




}
