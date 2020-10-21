package co.gergely.vasalka.api;

import co.gergely.vasalka.model.News;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface ApiNews {

    @GET("/news/{newsId}")
    Observable<News> get(@Path("newsId") Long newsId);

    @GET("/news/")
    Observable<List<News>> getList();

}
