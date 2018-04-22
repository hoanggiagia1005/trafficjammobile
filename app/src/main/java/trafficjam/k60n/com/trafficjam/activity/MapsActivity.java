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
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
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
    String TAG = "MapsActivity";

    private GoogleMap mMap;
    View mMapView;
//    @BindView(R.id.edt_start)
//    EditText edtStart;
//    @BindView(R.id.edt_end)
//    EditText edtEnd;

    @BindView(R.id.destarea)
    LinearLayout destarea;
    @BindView(R.id.sourceLocSelectTxt)
    TextView sourceLocSelectTxt;
    @BindView(R.id.sourceLocCardArea)
    CardView sourceLocCardArea;
    @BindView(R.id.pickUpLocHTxt)
    TextView pickUpLocHTxt;
    @BindView(R.id.pickUpLocTxt)
    TextView pickUpLocTxt;
    @BindView(R.id.destLocSelectTxt)
    TextView destLocSelectTxt;
    @BindView(R.id.destLocHTxt)
    TextView destLocHTxt;
    @BindView(R.id.destLocTxt)
    TextView destLocTxt;
    @BindView(R.id.area_source)
            RelativeLayout area_source;
    @BindView(R.id.area2)
            RelativeLayout area2;


    LatLng startLocation, endLocation;
    Marker startMarker, endMarker;
    ArrayList<Polyline> listPolyLine;
    private LocationManager locationManager;



    @OnClick({R.id.destarea, R.id.sourceLocSelectTxt,R.id.sourceLocCardArea,R.id.destLocSelectTxt})
    void click(View view) {
        switch (view.getId()) {
            case R.id.sourceLocCardArea: {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, Utils.PLACE_AUTOCOMPLETE_START);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;

            }
            case R.id.destarea: {
                try {
                    Intent i = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(MapsActivity.this);
                    startActivityForResult(i, Utils.PLACE_AUTOCOMPLETE_END);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;

            }
            case R.id.sourceLocSelectTxt:{
                area2.setVisibility(View.GONE);
                area_source.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.destLocSelectTxt:{
                area_source.setVisibility(View.GONE);
                area2.setVisibility(View.VISIBLE);
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

                pickUpLocTxt.setText(place.getName());
                sourceLocSelectTxt.setText(place.getName());


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

                destLocTxt.setText(place.getName());
                destLocSelectTxt.setText(place.getName());
                LatLng placeLocation = place.getLatLng();
                this.endLocation = placeLocation;

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                if (mMap != null) {
                    //gMap.clear();
                    endMarker = mMap.addMarker(new MarkerOptions().position(placeLocation).title("" + place.getAddress()));
                }

                if (!pickUpLocTxt.getText().toString().isEmpty()){
                    getDirections();
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

        listPolyLine = new ArrayList<>();
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
                        Log.i(TAG, "onNext: "+jsonObject);
                    }
                });
        String serverKey = "AIzaSyDt2fvDhvVmvluksCk1qNLS0oP2czcrtfk";
        GoogleDirection.withServerKey(serverKey)
                .from(startLocation)
                .to(endLocation)
                .alternativeRoute(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        Log.i(TAG, "onDirectionSuccess: "+direction.toString());
                        // Do something here
                        DrawRoute(direction,rawBody,startLocation,endLocation);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                        Log.i(TAG, "onDirectionFailure: "+t.toString());
                    }
                });
    }

    private void DrawRoute(Direction direction, String message,LatLng sourceLatlng,LatLng DestinationLng) {
        Polyline poly;
        for (Route route : direction.getRouteList()) {
            PolylineOptions polyoptions = new PolylineOptions();
            polyoptions.color(getResources().getColor(R.color.appThemeColor_1));
            polyoptions.width(5);
            polyoptions.addAll(route.getOverviewPolyline().getPointList());
            poly = mMap.addPolyline(polyoptions);
            poly.setClickable(true);

        }
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        if(sourceLatlng!=null)
            latLngBuilder.include(sourceLatlng);
        if(DestinationLng!=null)
            latLngBuilder.include(DestinationLng);

        try {
            LatLngBounds bounds = latLngBuilder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                    Utils.dpToPx(this, 135));
            mMap.animateCamera(cu);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//        mMap.animateCamera(cameraUpdate);
//        locationManager.removeUpdates(this);
//
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        Geocoder geoCoder = new Geocoder(this, Locale.getDefault()); //it is Geocoder
//        StringBuilder builder = new StringBuilder();
//        try {
//            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
//            int maxLines = address.get(0).getMaxAddressLineIndex();
//
//            String addressStr = "";
//            if (address.get(0).getFeatureName().equals("Unnamed Road")) {
//                addressStr = address.get(0).getLocality() + ", " + address.get(0).getSubAdminArea() + "";
//            } else {
//                addressStr = address.get(0).getFeatureName() + " " + address.get(0).getThoroughfare() + "";
//            }
//            builder.append(addressStr);
//            builder.append("");
//
//
//            String nowAddress = builder.toString(); //This is the complete address.
//            sourceLocSelectTxt.setText(nowAddress + "");
//            startLocation = new LatLng(lat, lng);
//        } catch (IOException e) {
//        } catch (NullPointerException e) {
//        }


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

