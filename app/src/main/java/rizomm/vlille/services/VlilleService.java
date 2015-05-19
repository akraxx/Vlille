package rizomm.vlille.services;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import rizomm.vlille.model.StationDetails;
import rizomm.vlille.model.Vlille;

/**
 * Created by Maximilien on 19/02/2015.
 */
public interface VlilleService {

    @GET("/stations/xml-stations.aspx")
    void stations(Callback<Vlille> vlilleCallback);

    @GET("/stations/xml-station.aspx")
    void station(@Query("borne") Integer borne, Callback<StationDetails> vlilleCallback);
}
