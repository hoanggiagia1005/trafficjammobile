package trafficjam.k60n.com.trafficjam.network;

/**
 * Created by HP on 8/1/2017.
 */
import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("status")
    public String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @SuppressWarnings({"unused", "used by Retrofit"})
    public Response() {
    }

    public Response(String status) {
        this.status = status;
    }
}
