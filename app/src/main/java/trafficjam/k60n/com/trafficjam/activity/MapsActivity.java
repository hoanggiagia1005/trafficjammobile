package trafficjam.k60n.com.trafficjam.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trafficjam.k60n.com.trafficjam.R;
import trafficjam.k60n.com.trafficjam.network.NetworkModule;
import trafficjam.k60n.com.trafficjam.network.model.TrafficModel;
import trafficjam.k60n.com.trafficjam.util.Utils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    View mMapView;
    @BindView(R.id.edt_start)
    EditText edtStart;
    @BindView(R.id.edt_end)
    EditText edtEnd;
    @BindView(R.id.btn_optimize)
    Button btnOptimize;

    LatLng startLocation, endLocation;
    Marker startMarker, endMarker;
    private LocationManager locationManager;

    @OnClick({R.id.edt_end, R.id.edt_start, R.id.btn_optimize})
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
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, Utils.PLACE_AUTOCOMPLETE_END);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;

            }

            case R.id.btn_optimize: {
                if (edtStart.getText().toString().isEmpty() || edtEnd.getText().toString().isEmpty()) {
                    Toast.makeText(MapsActivity.this, "Nhập đủ thông tin đi", Toast.LENGTH_SHORT).show();
                } else {
                    getDirections();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.PLACE_AUTOCOMPLETE_START) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                edtStart.setText(place.getAddress());
                LatLng placeLocation = place.getLatLng();

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

                edtEnd.setText(place.getAddress());
                LatLng placeLocation = place.getLatLng();

                this.endLocation = placeLocation;

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                if (mMap != null) {
                    //gMap.clear();
                    endMarker = mMap.addMarker(new MarkerOptions().position(placeLocation).title("" + place.getAddress()));
                    mMap.moveCamera(cu);
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
        // Add a marker in Sydney and move the camera
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
        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;

            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);


            rlp.setMargins(0, 50, 0, 0);
        }

    }

    private void getDirections(){
        String startLoc = startLocation.latitude+","+startLocation.longitude;
        String endLoc = endLocation.latitude+","+endLocation.longitude;
        NetworkModule.getServiceMapAPI().getDirections(startLoc,endLoc)
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
        Log.i("ABC", "onLocationChanged: "+location.getSpeed()* 3.6);
        TrafficModel model = new TrafficModel("12eassd",(float) location.getLatitude(),(float)location.getLongitude(),location.getSpeed()*3.6f,"Test");
        NetworkModule.getService().pushLocation(model)
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
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
