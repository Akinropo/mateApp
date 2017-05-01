package com.akinropo.taiwo.coursemate.ApiClasses;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TAIWO on 11/26/2016.
 */
public class ApiRetrofit {
    public static String BASE_URL = EndPoints.BASE_URL;
    public static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;

    }


}
