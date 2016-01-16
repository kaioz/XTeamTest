package com.cocosw.xteam.data;


import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Coco studio
 * <p/>
 * Created by kai on 1/12/2015.
 */
public interface WarehouseService {

    @GET("/api/search")
    Observable<List<Emotion>> fetch(
            @Query("limit") int limit,
            @Query("skip") long skip,
            @Query("q") String searchFromTags,
            @Query("onlyInStock") int inStock
    );

}
