package com.linroid.sky31radio.data;

import com.google.gson.Gson;
import com.squareup.okhttp.internal.DiskLruCache;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by linroid on 1/21/15.
 */
@Singleton
public class DiskCacheManager {
    public static final String KEY_PROGRAM = "program";
    public static final String KEY_ALBUM = "album";
    public static final String KEY_ANCHOR = "anchor";
    Gson gson;
    DiskLruCache cache;

    @Inject
    public DiskCacheManager(DiskLruCache cache, Gson gson){
        this.cache = cache;
        this.gson = gson;
    }
    public synchronized boolean exits(String key){
        try {
            DiskLruCache.Snapshot snapshot = cache.get(key);
            if(snapshot!=null){
                return true;
            }
        } catch (IOException e) {
            Timber.e(e, "出错");
        }
        return false;
    }
    public synchronized <T> T get(String key, Type type){
        try {
            String data = cache.get(key).getString(0);
            T obj = gson.fromJson(data, type);
            return obj;
        }catch(Exception ignored){
            Timber.e(ignored, "load cached data failed");
        }
        return null;
    }
    public synchronized boolean put(String key, Object obj){
        Timber.d("put key: %s", key);
        String json = gson.toJson(obj);
        DiskLruCache.Editor editor = null;
        try {
            editor = cache.edit(key);
            editor.set(0, json);
            editor.commit();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
