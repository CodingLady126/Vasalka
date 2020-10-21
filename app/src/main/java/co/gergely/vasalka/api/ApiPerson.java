package co.gergely.vasalka.api;

import co.gergely.vasalka.model.Person;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiPerson {

    @GET("/person")
    Call<List<Person>> getPersonList();

    @GET("/person/{personId}")
    Observable<Person> getPerson(@Path("personId") long personId);

    @Multipart
    @POST("/personimage.php")
    Call<Void> uploadImage(@Part() MultipartBody.Part file,
                           @Query("id") long personId);
    @PUT("/person/{personId}")
    Observable<Person> updatePerson(@Path("personId") long personId, @Body Person person);

    @GET("/person.php")
    Observable<List<Person>> searchByName(@Query("id") Long personId, @Query("str") String searchString);

    @DELETE("/person/{personId}")
    Call<Void> delete(@Path("personId") long personId);

}
