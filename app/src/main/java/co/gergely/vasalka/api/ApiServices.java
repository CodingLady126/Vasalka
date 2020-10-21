package co.gergely.vasalka.api;

import co.gergely.vasalka.model.Service;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface ApiServices {

    @GET("/service/")
    Observable<List<Service>> getServiceList();


    @GET("/service/{serviceId}")
    Observable<Service> getService(@Path("serviceId") Long serviceId);

}
