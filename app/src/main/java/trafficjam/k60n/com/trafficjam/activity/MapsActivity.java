package trafficjam.k60n.com.trafficjam.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnTouch;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trafficjam.k60n.com.trafficjam.R;
import trafficjam.k60n.com.trafficjam.network.NetworkModule;
import trafficjam.k60n.com.trafficjam.util.Utils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    View mMapView;
    @BindView(R.id.edt_start)
    EditText edtStart;
    @BindView(R.id.edt_end)
    EditText edtEnd;


    LatLng startLocation, endLocation;
    Marker startMarker, endMarker;
    private LocationManager locationManager;

    @OnTouch({R.id.edt_start, R.id.edt_end})
    boolean focus(View v) {
        switch (v.getId()) {
            case R.id.edt_start:
                mMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));
                break;
            case R.id.edt_end:
                if(endLocation!=null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(endLocation));
                }
                break;
            default:
                break;
        }
        return false;
    }

    @OnClick({R.id.edt_start, R.id.edt_end})
    void click(View view) {
        switch (view.getId()) {
            case R.id.edt_start: {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, Utils.PLACE_AUTOCOMPLETE_START);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;

            }
            case R.id.edt_end: {
                try {
                    Intent i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(MapsActivity.this);
                    startActivityForResult(i, Utils.PLACE_AUTOCOMPLETE_END);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;

            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.PLACE_AUTOCOMPLETE_START) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                edtStart.setText(place.getName());

                LatLng placeLocation = place.getLatLng();
                startLocation = placeLocation;
                this.startLocation = placeLocation;

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                if (mMap != null) {
                    //gMap.clear();
                    startMarker = mMap.addMarker(new MarkerOptions().position(placeLocation).title("" + place.getAddress()));
                    mMap.moveCamera(cu);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(MapsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (requestCode == RESULT_CANCELED) {

            }
        } else if (requestCode == Utils.PLACE_AUTOCOMPLETE_END) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                edtEnd.setText(place.getName());
                LatLng placeLocation = place.getLatLng();
                endLocation = placeLocation;
                this.endLocation = placeLocation;

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                if (mMap != null) {
                    //gMap.clear();
                    endMarker = mMap.addMarker(new MarkerOptions().position(placeLocation).title("" + place.getAddress()));
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(MapsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (requestCode == RESULT_CANCELED) {

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // lay vi tri chinh giua man hinh

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                String addressStr = "";
                Geocoder geoCoder;
                List<Address> addresses;
                geoCoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                LatLng center = mMap.getCameraPosition().target;
                Log.d("Tag", center + "");
                try {
                    addresses = geoCoder.getFromLocation(center.latitude, center.longitude, 1);
                    Log.d("Tag", addresses + "");
                    if (addresses.size() != 0) {
                        if (addresses.get(0).getFeatureName().equals("Unnamed Road")) {
                            addressStr = addresses.get(0).getLocality() + ", " + addresses.get(0).getSubAdminArea() + "";
                        } else {
                            addressStr = addresses.get(0).getFeatureName() + " " + addresses.get(0).getThoroughfare() + "";
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (edtStart.isFocused()) {
                    edtStart.setText(addressStr + "");
                    startLocation = new LatLng(center.latitude, center.longitude);
                } else {
                    edtEnd.setText(addressStr + "");
                    endLocation = new LatLng(center.latitude, center.longitude);
                }

            }
        });


        //check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //cai dat lai vi tri cho button now location

        mMap.setMyLocationEnabled(true);

        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButtonNow = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButtonNow.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }


    }

    private void getDirections() {
        String startLoc = startLocation.latitude + "," + startLocation.longitude;
        String endLoc = endLocation.latitude + "," + endLocation.longitude;
        NetworkModule.getServiceMapAPI().getDirections(startLoc, endLoc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {

                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();

            String addressStr = "";
            if (address.get(0).getFeatureName().equals("Unnamed Road")) {
                addressStr = address.get(0).getLocality() + ", " + address.get(0).getSubAdminArea() + "";
            } else {
                addressStr = address.get(0).getFeatureName() + " " + address.get(0).getThoroughfare() + "";
            }
            builder.append(addressStr);
            builder.append("");


            String nowAddress = builder.toString(); //This is the complete address.
            edtStart.setText(nowAddress + "");
            startLocation = new LatLng(lat, lng);
        } catch (IOException e) {
        } catch (NullPointerException e) {
        }

//        Log.i("ABC", "onLocationChanged: "+location.getSpeed()* 3.6);
//        TrafficModel model = new TrafficModel("12eassd",(float) location.getLatitude(),(float)location.getLongitude(),location.getSpeed()*3.6f,"Test");
//        NetworkModule.getService().pushLocation(model)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<JsonObject>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(JsonObject jsonObject) {
//
//                    }
//                });
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}

