package com.linroid.radio.data;

import com.google.gson.reflect.TypeToken;
import com.linroid.radio.model.Album;
import com.linroid.radio.model.Pagination;
import com.linroid.radio.model.Program;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import timber.log.Timber;


/**
 * Created by linroid on 1/21/15.
 */
public class ApiDatabase {
    ApiService apiService;
    DiskCacheManager cacheManager;

    PublishSubject<List<Album>> albumRequest;
    PublishSubject<Pagination<Program>> programRequest;

    public ApiDatabase(DiskCacheManager manager, ApiService apiService) {
        this.cacheManager = manager;
        this.apiService = apiService;
        programRequest = PublishSubject.create();
        albumRequest = PublishSubject.create();
    }

    public Subscription listProgram(int page, final Map<String, String> params, Observer<Pagination<Program>> observer){

        Subscription subscription = programRequest.subscribe(observer);
        if(page == 1 && params.size()==0){
            Observable.create(new Observable.OnSubscribe<Pagination<Program>>() {
                @Override
                public void call(Subscriber<? super Pagination<Program>> subscriber) {
                    Type type = new TypeToken<Pagination<Program>>(){}.getType();
                    if(cacheManager.exits(DiskCacheManager.KEY_PROGRAM)){
                        Pagination<Program> cachedData = cacheManager.get(DiskCacheManager.KEY_PROGRAM, type);
                        Timber.d("load data from cached file successful!");
                        subscriber.onNext(cachedData);
                    }
                    subscriber.onCompleted();
                }
            }).subscribe(programRequest);
        }
        Observable.create(new Observable.OnSubscribe<Pagination<Program>>() {
            @Override
            public void call(Subscriber<? super Pagination<Program>> subscriber) {

            }
        }).subscribe(programRequest);
        apiService.listPrograms(page, params)
                .map(new Func1<Pagination<Program>, Pagination<Program>>() {
                    @Override
                    public Pagination<Program> call(Pagination<Program> programPagination) {
                        if(programPagination.getCurrentPage() == 1  && params.size()==0){
                            cacheManager.put(DiskCacheManager.KEY_PROGRAM, programPagination);
                        }
                        return programPagination;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(programRequest);
        return subscription;
    }
    public Subscription listAlbums(Observer<List<Album>> observer){
        Subscription subscription = albumRequest.subscribe(observer);

        Observable.create(new Observable.OnSubscribe<List<Album>>() {
            @Override
            public void call(Subscriber<? super List<Album>> subscriber) {
                if(cacheManager.exits(DiskCacheManager.KEY_ALBUM)){
                    List<Album> albums = cacheManager.get(DiskCacheManager.KEY_ALBUM, new TypeToken<List<Album>>(){}.getType());
                    subscriber.onNext(albums);
                }
                subscriber.onCompleted();
            }
        }).subscribe(albumRequest);
        apiService.listAlbums()
                .map(new Func1<List<Album>, List<Album>>() {
                    @Override
                    public List<Album> call(List<Album> albums) {
                        cacheManager.put(DiskCacheManager.KEY_ALBUM, albums);
                        return albums;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        return subscription;
    }
}
