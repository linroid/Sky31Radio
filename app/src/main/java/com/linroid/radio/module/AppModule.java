package com.linroid.radio.module;

import android.content.Context;
import android.net.Uri;

import com.linroid.radio.App;
import com.linroid.radio.service.RadioPlaybackService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Listener;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module(
        injects = {
                App.class,
                RadioPlaybackService.class
        },
        includes = {
                DataModule.class
        },
        library = true
)
public class AppModule {
    Context ctx;

    public AppModule(Context ctx) {
        this.ctx = ctx;
    }

    @Provides
    Context provideContext() {
        return this.ctx;
    }


    @Provides
    @Singleton
    Picasso providePicasso(OkHttpClient okHttpClient) {
        Picasso.Builder builder = new Picasso.Builder(this.ctx);
        builder.downloader(new OkHttpDownloader(okHttpClient))
                .listener(new Listener() {
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Timber.i(exception, "Picasso load image failed: " + uri.toString());
                    }
                })
                .loggingEnabled(false);
//                .loggingEnabled(BuildConfig.DEBUG);
        return builder.build();
    }

}