package co.gergely.vasalka.api;

import co.gergely.vasalka.model.Satisfaction;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ApiSatisfaction {


    @GET("/satisfaction/forperson/{personId}")
    Observable<List<Satisfaction>> getSatisfactionsFor(@Path("personId") Long personId);

    @GET("/satisfaction/byperson/{personId}")
    Observable<List<Satisfaction>> getSatisfactionsBy(@Path("personId") Long personId);

    @POST("/satisfaction/{personId}")
    Observable<Satisfaction> addSatisfaction(@Path("personId") Long personId, @Body Satisfaction satisfaction);

    @GET("/satisfaction/{satisfactionId}")
    Observable<Satisfaction> getSatisfaction(@Path("satisfactionId") Long personId);
}
