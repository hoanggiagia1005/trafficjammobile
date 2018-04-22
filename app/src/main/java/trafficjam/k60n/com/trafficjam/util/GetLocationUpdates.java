package trafficjam.k60n.com.trafficjam.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GetLocationUpdates implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context mContext;

    public int UPDATE_INTERVAL = 4000; // 10 sec
    public int FATEST_INTERVAL = 2000; // 5 sec
    private int DISPLACEMENT = 8; // 10 meters
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LocationUpdates locationsUpdates;

    boolean isPermissionDialogShown = false;

    private LastLocationListner LastLocationListner;

    Location mLastLocation;

    boolean isApiConnected = false;

    public GetLocationUpdates(Context context, int displacement) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.DISPLACEMENT = displacement;
        buildGoogleApiClient();
        createLocationRequest();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    /**
     * Creating location request object
     */
    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Starting the location updates
     */
    public void startLocationUpdates() {


        try

        {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (Exception e) {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }


    }

    /**
     * Stopping location updates
     */
    public void stopLocationUpdates() {
//        Utils.printLog("Loc stop update","called");
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        Utils.printLog("Location changed", "changed");
        if (locationsUpdates != null) {
            Utils.printLog("locationsUpdates", "not null");
            locationsUpdates.onLocationUpdate(location);
        }

        this.mLastLocation = location;
    }

    public Location getLocation() {
        return this.mLastLocation;
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    public void StopUpdates() {
        stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub

        isApiConnected = true;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (locationsUpdates != null) {
                locationsUpdates.onLocationUpdate(mLastLocation);
            } else if (LastLocationListner != null) {
                if (mLastLocation != null) {
                    LastLocationListner.handleLastLocationListnerCallback(mLastLocation);
                } else if (mLastLocation == null) {
                    LastLocationListner.handleLastLocationListnerNOVALUECallback(0);
                }
            }

        startLocationUpdates();
    }

    public boolean isApiConnected() {
        return this.isApiConnected;
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public interface LocationUpdates {
        void onLocationUpdate(Location location);
    }

    public void setLocationUpdatesListener(LocationUpdates locationsUpdates) {
        this.locationsUpdates = locationsUpdates;
    }

    public interface LastLocationListner {
        void handleLastLocationListnerCallback(Location mLastLocation);

        void handleLastLocationListnerNOVALUECallback(int id);
    }

    public void setLastLocationListener(LastLocationListner LastLocationListner) {
        this.LastLocationListner = LastLocationListner;
    }
}
