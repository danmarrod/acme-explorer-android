package com.tecmov.acmeexplorer.resttypes;

public class WeatherConditions {
    private float temp;
    private float feels_like;
    private float temp_max;
    private float temp_min;
    private int humidity;

    public WeatherConditions(float temp, float feels_like, float temp_max, float temp_min, int humidity, int pressure) {
        this.temp = temp;
        this.feels_like = feels_like;
        this.temp_max = temp_max;
        this.temp_min = temp_min;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public WeatherConditions() {
    }


    public float getTemp() {
        return temp;
    }

    public float getFeels_like() {
        return feels_like;
    }

    public float getTemp_max() {
        return temp_max;
    }

    public float getTemp_min() {
        return temp_min;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }

    private int pressure;


}
