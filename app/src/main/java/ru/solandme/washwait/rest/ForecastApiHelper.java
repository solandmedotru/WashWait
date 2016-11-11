package ru.solandme.washwait.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastApiHelper {
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static final long CONNECTION_TIMEOUT = 20;
    private static Retrofit retrofit = null;


    public static Retrofit requestForecast(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient(final Context context) {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();

        File baseDir = context.getCacheDir();
        if (baseDir != null) {
            File cacheDir = new File(baseDir, "HttpResponseCache");
            okClientBuilder.cache(new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));
        }

        okClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!isNetworkAvailable(context)) {
                    request = new Request.Builder()
                            .cacheControl(new CacheControl.Builder()
                                    .maxAge(1, TimeUnit.DAYS)
                                    .maxStale(1, TimeUnit.DAYS)
                                    .build())
                            .url(request.url())
                            .build();
                    return chain.proceed(request);
                } else {
                    request = new Request.Builder()
                            .cacheControl(new CacheControl.Builder()
                                    .maxStale(1, TimeUnit.HOURS)
                                    .build())
                            .url(request.url())
                            .build();
                    return chain.proceed(request);
                }
            }
        });

        okClientBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        okClientBuilder.readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);

        return okClientBuilder.build();
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();

        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
}
