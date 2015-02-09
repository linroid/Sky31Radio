package com.linroid.sky31radio.module;

import android.content.Context;

import com.linroid.sky31radio.App;
import com.linroid.sky31radio.BuildConfig;
import com.linroid.sky31radio.service.RadioPlaybackService;

import java.io.File;

import javax.inject.Named;
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
    Timber.Tree provideTimberTree(){
        return BuildConfig.DEBUG ?
                new Timber.DebugTree() :
                new Timber.HollowTree();
    }


    @Provides @Singleton @Named("Root")
    File provideCacheDir(Context ctx){
        return ctx.getCacheDir();
    }
    @Provides @Singleton @Named("Http")
    File provideHttpCacheDir(@Named("Root") File root){
        return new File(root, "http");
    }
    @Provides @Singleton @Named("Data")
    File provideDataCacheDir(@Named("Root") File data){
        return new File(data, "data");
    }

}