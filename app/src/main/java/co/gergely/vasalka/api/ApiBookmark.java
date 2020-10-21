package co.gergely.vasalka.api;

import co.gergely.vasalka.model.Bookmark;
import co.gergely.vasalka.model.BookmarkForSend;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiBookmark {

    @GET("/bookmark/{personId}")
    Observable<List<Bookmark>> getBookmarkList(@Path("personId") Long personId);

    @PUT("/bookmark/{personId}")
    Observable<Bookmark> updateBookmark(@Path("personId") Long id, @Body BookmarkForSend bookmark);

    @DELETE("/bookmark/{bookmarkId}")
    Call<Void> deleteBookmark(@Path("bookmarkId") Long bookmarkId);

    @POST("/bookmark/{personId}")
    Observable<Bookmark> createBookmark(@Path("personId") Long personId, @Body BookmarkForSend bookmark);

}
