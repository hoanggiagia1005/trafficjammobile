package trafficjam.k60n.com.trafficjam.network;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Part;
import rx.Observable;

public interface NetworkMapService {
    @GET("directions/json?origin={originLoc}&destination={destLoc}&sensor=true&alternatives=true")
    Observable<JsonObject> getDirections(@Part("originLoc") String originLoc, @Part("destLoc") String destLoc);

    @GET("geocode/json?latlng={lat},{lng}")
    Observable<JsonObject> getGeoCode(@Part("lat") float lat,@Part("lng") float lng);
}
