package com.example.homework03;

import java.io.Serializable;
import java.util.Date;

public class City implements Serializable {

    private String cityName;
    private String country;
    private String citykey;
    private String weatherText;
    private String weatherIcon;
    private String metricValue;
    private Date LocalObservationTime;
    private String administrativeArea;
    private String MetricUnit;
    private boolean favoutite;

    public boolean isFavoutite() {
        return favoutite;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityName='" + cityName + '\'' +
                ", country='" + country + '\'' +
                ", citykey='" + citykey + '\'' +
                ", weatherText='" + weatherText + '\'' +
                ", weatherIcon='" + weatherIcon + '\'' +
                ", metricValue='" + metricValue + '\'' +
                ", LocalObservationTime=" + LocalObservationTime +
                ", administrativeArea='" + administrativeArea + '\'' +
                ", MetricUnit='" + MetricUnit + '\'' +
                ", favoutite=" + favoutite +
                '}';
    }

    public void setFavoutite(boolean favoutite) {
        this.favoutite = favoutite;
    }

    public String getMetricUnit() {
        return MetricUnit;
    }

    public void setMetricUnit(String metricUnit) {
        MetricUnit = metricUnit;
    }

    public String getAdministrativeArea() {
        return administrativeArea;
    }

    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    public Date getLocalObservationTime() {
        return LocalObservationTime;
    }

    public void setLocalObservationTime(Date localObservationTime) {
        LocalObservationTime = localObservationTime;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCitykey() {
        return citykey;
    }

    public void setCitykey(String citykey) {
        this.citykey = citykey;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(String metricValue) {
        this.metricValue = metricValue;
    }

    @Override
    public boolean equals (Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            City city = (City) object;
            if (this.citykey.equals(city.getCitykey())) {
                result = true;
            }
        }
        return result;
    }
}
