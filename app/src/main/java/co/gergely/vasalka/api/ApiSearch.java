package co.gergely.vasalka.api;

import co.gergely.vasalka.model.Search;
import co.gergely.vasalka.model.SearchPerson;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ApiSearch {

    @POST("/search/")
    Observable<List<SearchPerson>> doSearch(@Body Search search);

    @GET("/search/{personId}")
    Observable<List<SearchPerson>> getListFor(@Path("personId") Long personId);

}
