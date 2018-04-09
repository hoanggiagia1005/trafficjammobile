package trafficjam.k60n.com.trafficjam.network.model;

public class TrafficModel {
    public String device_id;
    public float lat;
    public float lng;
    public float speed;
    public String route;

    public TrafficModel(String device_id, float lat, float lng, float speed, String route) {
        this.device_id = device_id;
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
        this.route = route;
    }

    public TrafficModel() {
    }
}
