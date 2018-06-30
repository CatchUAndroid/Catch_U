package uren.com.catchu.MainPackage.MainFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import butterknife.ButterKnife;
import uren.com.catchu.GetFriendOrGroupPackage.SelectedItemPackage.SelectedFriendList;
import uren.com.catchu.MainPackage.LocationTrackerAdapter;
import uren.com.catchu.MainPackage.MainActivity;
import uren.com.catchu.MainPackage.UgurDenemePackage.UgurDenemeActivity;
import uren.com.catchu.R;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static uren.com.catchu.Constants.PermissionConstant.*;

public class AddPinFragment extends BaseFragment implements
        OnMapReadyCallback ,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnCameraMoveListener,
        View.OnClickListener{

    private View mView;
    private MapView mapView;
    private Context context;
    private LocationTrackerAdapter locationTrackObj;
    public GoogleMap mMap;
    private LatLng latLng;
    private MapRipple mapRipple;
    LocationManager locationManager;

    private Button selectFriendBtn;
    private static SelectedFriendList selectedFriendListInstance;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_add_pin, container, false);
        ButterKnife.bind(this, mView);

        //( (MainActivity)getActivity()).updateToolbarTitle("Add Pin");

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) mView.findViewById(R.id.map);

        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        context = getActivity();

        locationTrackObj = new LocationTrackerAdapter((MainActivity)getActivity());

        if (!locationTrackObj.canGetLocation()) {
            locationTrackObj.showSettingsAlert();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
        }

        initViews();
    }

    private void initViews() {

        selectFriendBtn = mView.findViewById(R.id.selectFriendBtn);
        selectFriendBtn.setOnClickListener(this);
    }

    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }

            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        //mMap.setOnCameraChangeListener(this);
        mMap.setOnCameraMoveListener(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap);
            }
        } else {
            initializeMap(mMap);
        }
    }

    private void initializeMap(GoogleMap mMap) {
        if (mMap != null) {
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);

            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
            Location location = getLastKnownLocation();

            if (location == null)
                location = locationTrackObj.getLocation();

            try {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(location.getLatitude(),
                        location.getLongitude()), 14));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (location != null)
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            else {
                latLng = new LatLng(0.0, 0.0);
            }

            mapRipple = new MapRipple(mMap, latLng, context);
        }
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {

        Log.i("Info", "getLastKnownLocation starts");

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {

            Location location = null;

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                location = locationManager.getLastKnownLocation(provider);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    @SuppressLint("MissingPermission")
    public void centerMapOnLocation() {

        Log.i("Info", "centerMapOnLocation============");

        //if (!mLocationPermissionGranted) {
        if(ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED){

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0, (android.location.LocationListener) context);
        }

        Location currentLocation = getLastKnownLocation();

        if (mMap != null) {
            mMap.clear();
            mapRipple = new MapRipple(mMap, latLng, context);

            float zoom = mMap.getCameraPosition().zoom;

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                    LatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude()), zoom), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    //checkMarkerAndCreatePopup();
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {
            switch (requestCode) {
                case PERMISSION_REQUEST_ACCESS_FINE_LOCATION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //mLocationPermissionGranted = true;
                        initializeMap(mMap);
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            Log.i("Info", "     >>onRequestPermissionsResult Error:" + e.toString());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mapRipple.isAnimationRunning()) {
                mapRipple.stopRippleMapAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        switch (i) {
            case R.id.selectFriendBtn:
                //startActivity(new Intent(getActivity(), SelectFriendOrGroupActivity.class));
                startActivity(new Intent(getActivity(), UgurDenemeActivity.class));
                break;

            default:
                break;
        }
    }
}
