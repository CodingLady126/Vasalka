package co.gergely.vasalka.api;

import co.gergely.vasalka.model.City;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface ApiCity {

    @GET("/city/")
    Observable<List<City>> getCities();

    @GET("/city/{cityId}/")
    Observable<City> getCity(@Path("cityId") String cityId);

}
