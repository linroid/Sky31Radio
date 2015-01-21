package com.linroid.radio.module;

import com.google.gson.Gson;
import com.linroid.radio.BuildConfig;
import com.linroid.radio.data.ApiService;
import com.linroid.radio.data.DiskCacheManager;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.internal.DiskLruCache;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;
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
    Cache provideHttpCache(@Named("Http") File httpCacheDir) {
        //100M;
        int cacheSize = 1024*1024*100;
        try {
            return new Cache(httpCacheDir, cacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            Timber.e("install http cache false");
        }

        return null;
    }
    @Provides
    @Singleton
    DiskLruCache provideDataCache(@Named("Data") File cacheDir){
        DiskLruCache cache = null;
        try {
            //10M
            cache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, 1, 1024 * 1024 * 10);
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e(e, "install data cache failed");
        }
        return cache;
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
                .setEndpoint("http://radio.sky31.com/api")
                .build();
    }
    @Provides
    @Singleton
    ApiService provideApiService(RestAdapter restAdapter){
        return restAdapter.create(ApiService.class);
    }

}
