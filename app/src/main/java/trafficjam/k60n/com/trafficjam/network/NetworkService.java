package trafficjam.k60n.com.trafficjam.network;

import com.akexorcist.googledirection.model.Direction;
import com.google.gson.JsonObject;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import trafficjam.k60n.com.trafficjam.network.model.Respone;
import trafficjam.k60n.com.trafficjam.network.model.TrafficModel;

public interface NetworkService {
    @POST("api/save")
    Observable<JsonObject> pushLocation(@Body TrafficModel trafficModel);

    @POST("api/result")
    Observable<Respone> getResult(@Body Direction jsonObject);
}
