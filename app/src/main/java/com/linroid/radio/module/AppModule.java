package com.linroid.radio.module;

import android.content.Context;

import com.linroid.radio.App;
import com.linroid.radio.service.RadioPlaybackService;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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


    @Provides @Singleton @Named("Root")
    File provideCacheDir(Context ctx){
        return ctx.getExternalCacheDir();
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