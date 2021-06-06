package com.example.navi_ver2;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET("api")
    Call<Data> getData();

}
