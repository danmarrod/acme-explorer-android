package com.tecmov.acmeexplorer.resttypes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRetrofitInterface {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrentWeather(@Query("lat") float lat,
                                            @Query("lon") float lon,
                                            @Query("appid") String appId,
                                            @Query("units") String units);

}
