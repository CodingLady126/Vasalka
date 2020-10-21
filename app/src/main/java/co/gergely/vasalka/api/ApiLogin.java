package co.gergely.vasalka.api;

import co.gergely.vasalka.model.Person;
import io.reactivex.Observable;
import retrofit2.http.GET;


public interface ApiLogin {

    @GET("/login/")
    Observable<Person> getPerson();

}
