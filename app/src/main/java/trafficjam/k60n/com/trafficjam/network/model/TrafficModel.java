package trafficjam.k60n.com.trafficjam.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrafficModel {
    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("pathName")
    @Expose
    private String pathName;
    @SerializedName("speed")
    @Expose
    private Double speed;

    public TrafficModel(String deviceId, Double latitude, Double longitude, String pathName, Double speed) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pathName = pathName;
        this.speed = speed;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
