package co.gergely.vasalka.api;

import android.util.Log;
import co.gergely.vasalka.BuildConfig;
import co.gergely.vasalka.VasalkaApp;
import co.gergely.vasalka.common.Constants;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NetworkClient {

    public static Retrofit retrofit;
    private final static boolean debug = true;

    public void NetworkClient(){
    }

    public static Retrofit getRetrofit(final String token){

        OkHttpClient.Builder okBuilder = null;
        if (token == null) {
            okBuilder = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Interceptor.Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", "MYFBS " + VasalkaApp.getSession().getJwtToken())
                                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                                    .build();
                            return chain.proceed(newRequest);
                        }

                    });
        } else {
            okBuilder = new OkHttpClient.Builder()
                    // .cookieJar(new SessionCookieJar())
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Interceptor.Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", "MYFBS " + token)
                                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                                    .build();
                            return chain.proceed(newRequest);
                        }

                    });
        }

        if (BuildConfig.DEBUG && debug) {
            okBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }

        okBuilder.readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient client = okBuilder.build();

        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    private static class SessionCookieJar implements CookieJar {

        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            for (Cookie cookie : cookies) {
                Log.d("COOKIE", cookie.name() + "=" + cookie.value());

            }
            this.cookies = new ArrayList<>(cookies);
        }


        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            if (cookies != null) {
                return cookies;
            }
            return Collections.emptyList();
        }
    }


}