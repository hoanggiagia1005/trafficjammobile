package trafficjam.k60n.com.trafficjam.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Leg {


    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("sumDevice")
    @Expose
    private Integer sumDevice;
    @SerializedName("avgSpeed")
    @Expose
    private Double avgSpeed;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Integer getSumDevice() {
        return sumDevice;
    }

    public void setSumDevice(Integer sumDevice) {
        this.sumDevice = sumDevice;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}