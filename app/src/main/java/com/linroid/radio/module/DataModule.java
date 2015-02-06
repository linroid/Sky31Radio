package com.linroid.radio.module;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.linroid.radio.BuildConfig;
import com.linroid.radio.R;
import com.linroid.radio.data.ApiService;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.internal.DiskLruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    Picasso providePicasso(OkHttpClient okHttpClient, Context ctx) {
        Picasso.Builder builder = new Picasso.Builder(ctx);
        builder.downloader(new OkHttpDownloader(okHttpClient))
                .listener(new Picasso.Listener() {
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Timber.e(exception, "Picasso load image failed: " + uri.toString());
                    }
                })
                .indicatorsEnabled(false)
                .loggingEnabled(false);
        return builder.build();
    }
    @Provides
    @Singleton
    OkHttpClient provideOkHttp(Cache cache) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCache(cache);
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
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
    RestAdapter provideRestAdapter(Gson gson, OkHttpClient okHttpClient, final Context ctx){
        return new RestAdapter.Builder()
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        Timber.e(retrofitError, "请求出现错误:%s", retrofitError.getUrl());
                        RetrofitError.Kind kind = retrofitError.getKind();
                        String message;
                        if(RetrofitError.Kind.NETWORK.equals(kind)){
                            message = ctx.getString(R.string.network_error);
                        }else if(RetrofitError.Kind.HTTP.equals(kind)){
                            message = ctx.getString(R.string.http_error);
                        }else if(RetrofitError.Kind.CONVERSION.equals(kind)){
                            message = ctx.getString(R.string.conversion_error);
                        }else{
                            message = ctx.getString(R.string.unexpected_error);
                        }
                        return new Exception(message);
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
