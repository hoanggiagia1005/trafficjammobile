package trafficjam.k60n.com.trafficjam.network;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import trafficjam.k60n.com.trafficjam.network.model.TrafficModel;

public interface NetworkService {
    @POST("api/save")
    Observable<JsonObject> pushLocation(TrafficModel trafficModel);
}
