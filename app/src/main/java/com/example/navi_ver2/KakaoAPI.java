package com.example.navi_ver2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface KakaoAPI {
    @GET("/v2/local/search/keyword.json")
    Call<ResultSearchKeyword> getSearchKeyword(@Header("Authorization") String key, @Query("query") String query);

    @GET("/v2/local/search/keyword.json")
    Call<ResultSearchKeyword> getSearchCategory(@Header("Authorization") String key,
                                                @Query("query") String query,
                                                @Query("category_group_code") String category_group_code);

    //카테고리로 검색
    @GET("v2/local/search/category.json")
    Call<ResultSearchKeyword> getSearchLocationDetail(
            @Header("Authorization") String token,
            @Query("x") String x,
            @Query("y") String y,
            @Query("category_group_code") String category_group_code,
            @Query("sort") String sort
    );
}
