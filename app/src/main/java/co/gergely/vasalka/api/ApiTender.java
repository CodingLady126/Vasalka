package co.gergely.vasalka.api;

import co.gergely.vasalka.model.SearchPerson;
import co.gergely.vasalka.model.Tender;
import co.gergely.vasalka.model.TenderAlertSum;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiTender {


    @POST("/tender/")
    Observable<Tender> createTender(@Body Tender tender);

    @Multipart
    @POST("/tenderimage.php")
    Call<Void> uploadTenderImages(@Part() MultipartBody.Part image, @Query("tender_id") Long tenderId, @Query("person_id") Long personId, @Query("number") int num);

    @PUT("/tender/{tender_id}")
    Observable<Tender> updateTender(@Path("tender_id") Long tenderId, @Body Tender tender);

    @PUT("/tender/apply/{tender_id}")
    Call<Void> applyForTender(@Path("tender_id") Long tenderId);

    @GET("/tender/person/{person_id}")
    Observable<List<Tender>> getPersonTenderList(@Path("person_id") Long personId);

    @GET("/tender/affected/{person_id}")
    Observable<List<Tender>> getAffectedTenderList(@Path("person_id") Long personId);

    @GET("/tender/{tender_id}")
    Observable<Tender> getTender(@Path("tender_id") Long tenderId);

    @GET("/tender/applied/{tender_id}")
    Observable<List<SearchPerson>> getAppliedPersonList(@Path("tender_id") Long tenderId);

    @GET("/tender/alert/{tender_id}")
    Observable<TenderAlertSum> getAlertSummary(@Path("tender_id") Long tenderId);

    @DELETE("/tender/{tender_id}")
    Call<Void> deleteTender(@Path("tender_id") Long tenderId);

    @DELETE("/tender/person/{person_id}")
    Call<Void> deleteAllTendersFor(@Path("person_id") Long personId);
}
