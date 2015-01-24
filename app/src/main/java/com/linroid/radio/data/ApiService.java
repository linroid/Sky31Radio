package com.linroid.radio.data;

import com.linroid.radio.model.Album;
import com.linroid.radio.model.Anchor;
import com.linroid.radio.model.Pagination;
import com.linroid.radio.model.Program;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by linroid on 1/14/15.
 */
public interface ApiService {
    @GET("/program")
    Observable<Pagination<Program>> listPrograms(@Query("page") int page, @QueryMap Map<String, String> params);
    @GET("/album")
    Observable<List<Album>> listAlbums();

    @GET("/anchor")
    Observable<List<Anchor>> listAnchor();

    @GET("/program/{id}")
    Observable<Program> programDetail(@Path("id") int programId);
}
