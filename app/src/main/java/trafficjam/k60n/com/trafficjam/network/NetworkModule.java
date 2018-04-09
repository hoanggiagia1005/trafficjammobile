package trafficjam.k60n.com.trafficjam.network;

/**
 * Created by HP on 7/24/2017.
 */

public class NetworkModule {
    private NetworkModule() {
    }

    public static NetworkMapService getServiceMapAPI() {
        return RetrofitClient.getClient(BuildConfig.BASEURLMAP).create(NetworkMapService.class);
    }

    public static NetworkService getService() {
        return RetrofitClient.getClient(BuildConfig.BASEURL).create(NetworkService.class);
    }

}
