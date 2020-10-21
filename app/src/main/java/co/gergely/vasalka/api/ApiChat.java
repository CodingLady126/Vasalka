package co.gergely.vasalka.api;

import co.gergely.vasalka.model.ChatRoom;
import co.gergely.vasalka.model.Image;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiChat {

    @POST("/chat/{personId}")
    Observable<ChatRoom> createChat(@Path("personId") Long personId, @Body ChatRoom chatRoom);

    @GET("/chat/{personId}")
    Observable<List<ChatRoom>> getList(@Path("personId") Long personId);

    @GET("/chat.php")
    Observable<ChatRoom> getRoomByPartner(@Query("person_id") Long personId, @Query("partner_id") Long partnerId);

    @Multipart
    @POST("/chat.php")
    Call<Image> addImage(@Part() MultipartBody.Part file, @Query("img") boolean isImage, @Query("person_id") Long personId, @Query("partner_id") Long partnerId);

    @DELETE("/chat.php")
    Call<Void> deleteImage(@Query("img") boolean isImage, @Query("person_id") Long personId, @Query("partner_id") Long partnerId, @Query("file") String fileName);

    @PUT("/chat/{personId}")
    Observable<ChatRoom> updateChat(@Path("personId") Long personId, @Body ChatRoom chatRoom);
}
