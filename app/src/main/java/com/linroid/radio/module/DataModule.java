package com.linroid.radio.module;

import android.content.Context;

import com.google.gson.Gson;
import com.linroid.radio.data.ApiService;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */
@Module(
        library = true,
        complete = false
)
public class DataModule {
    @Provides
    @Singleton
    Gson provideGson(){
        return new Gson();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttp(Cache cache) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCache(cache);
        return okHttpClient;
    }
    @Provides
    @Singleton
    Cache provideHttpCache(Context ctx) {
        //100M;
        int cacheSize = 1024*1024*100;
        try {
            return new Cache(ctx.getExternalCacheDir(), cacheSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter(Gson gson, OkHttpClient okHttpClient){
        return new RestAdapter.Builder()
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        Timber.e(retrofitError, "请求出现错误:%s", retrofitError.getUrl());
                        return retrofitError;
                    }
                })
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setEndpoint("http://newradio.sky31.com/api")
                .build();
    }
    @Provides
    @Singleton
    ApiService provideApiService(RestAdapter restAdapter){
        return restAdapter.create(ApiService.class);
    }

}
