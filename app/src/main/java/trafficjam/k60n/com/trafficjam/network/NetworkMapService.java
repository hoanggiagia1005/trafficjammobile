package trafficjam.k60n.com.trafficjam.network;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

public interface NetworkMapService {
    @GET("directions/json?sensor=true&alternatives=true")
    Observable<JsonObject> getDirections(@Query("origin") String originLoc, @Query("destination") String destLoc);

    @GET("geocode/json?latlng={lat},{lng}")
    Observable<JsonObject> getGeoCode(@Query("lat") float lat,@Query("lng") float lng);
}
