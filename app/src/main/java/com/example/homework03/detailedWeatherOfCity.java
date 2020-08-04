package com.example.homework03;

import java.util.Date;

public class detailedWeatherOfCity {
    private String headLine;
    private String date;
    private String TempUnit;
    private String minTempValue;
    private String maxTempValue;
    private String dayIconID;
    private String nightIconID;
    private String dayText;
    private String nightText;
    private String mobileLinkurl;

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTempUnit() {
        return TempUnit;
    }

    public void setTempUnit(String tempUnit) {
        TempUnit = tempUnit;
    }

    public String getMinTempValue() {
        return minTempValue;
    }

    public void setMinTempValue(String minTempValue) {
        this.minTempValue = minTempValue;
    }

    public String getMaxTempValue() {
        return maxTempValue;
    }

    public void setMaxTempValue(String maxTempValue) {
        this.maxTempValue = maxTempValue;
    }

    public String getDayIconID() {
        return dayIconID;
    }

    public void setDayIconID(String dayIconID) {
        this.dayIconID = dayIconID;
    }

    public String getNightIconID() {
        return nightIconID;
    }

    public void setNightIconID(String nightIconID) {
        this.nightIconID = nightIconID;
    }

    public String getDayText() {
        return dayText;
    }

    public void setDayText(String dayText) {
        this.dayText = dayText;
    }

    public String getNightText() {
        return nightText;
    }

    public void setNightText(String nightText) {
        this.nightText = nightText;
    }

    public String getMobileLinkurl() {
        return mobileLinkurl;
    }

    public void setMobileLinkurl(String mobileLinkurl) {
        this.mobileLinkurl = mobileLinkurl;
    }
}
