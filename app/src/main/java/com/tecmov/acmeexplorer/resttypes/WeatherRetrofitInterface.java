package com.tecmov.acmeexplorer.resttypes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRetrofitInterface {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrentWeather(@Query("lat") float lat,
                                            @Query("lon") float lon,
                                            @Query("appid") String appId,
                                            @Query("units") String units,
                                            @Query("lang") String lang);
    // Units of measurement. standard (Kelvin), metric (Celsius) and imperial (Fahrenheit) units are available. If you do not use the units parameter, standard units will be applied by default.
    // Lang, you can use this parameter to get the output in your language. Ex: esp, es for Spanish
}
