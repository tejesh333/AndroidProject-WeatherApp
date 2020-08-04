package com.example.homework03;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CityWeather extends AppCompatActivity implements myAdapter.InteractWithMainActivity {

    City selectedCity;
    ArrayList<detailedWeatherOfCity> fivedaysForecast = new ArrayList<>();
    String cityName,TAG="DEMO";

    TextView tv_cityName,tv_headlineWeatherText,tv_forecastDate,tv_temperature,tv_moreInformation,tv_dayWeatherText,tv_nightWeatherText;
    ImageView iv_day,iv_night;
    Button btn_saveCity,btn_setAsCurrent;
    RecyclerView rv_nextFivedaysWeather;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;
    ProgressBar pb_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);

        tv_cityName = findViewById(R.id.tv_cityName);
        tv_headlineWeatherText = findViewById(R.id.tv_headlineWeatherText);
        tv_forecastDate = findViewById(R.id.tv_forecastDate);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_moreInformation = findViewById(R.id.tv_moreInformation);
        tv_dayWeatherText = findViewById(R.id.tv_dayWeatherText);
        tv_nightWeatherText = findViewById(R.id.tv_nightWeatherText);
        iv_day = findViewById(R.id.iv_day);
        iv_night = findViewById(R.id.iv_night);
        btn_saveCity = findViewById(R.id.btn_saveCity);
        btn_setAsCurrent = findViewById(R.id.btn_setAsCurrent);
        rv_nextFivedaysWeather = findViewById(R.id.rv_nextFivedaysWeather);
        pb_loading = findViewById(R.id.pb_loading);

        setLoadingVisible();


        btn_saveCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new setCityDetailsFromSavedCities().execute();
            }
        });

        btn_setAsCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new setCityDetailsFromCurrentCity().execute();
            }
        });


        selectedCity = (City)getIntent().getExtras().get("SELECTEDCITY");
        String selectedCityKEY = selectedCity.getCitykey();
        cityName = selectedCity.getCityName()+", "+selectedCity.getCountry();
        String newUrl = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/"+selectedCityKEY+"?apikey="+getString(R.string.apiKey);

        new getCurrentCityConditions().execute(newUrl);
    }

    public void setLoadingVisible(){
        tv_cityName.setVisibility(TextView.INVISIBLE);
        tv_headlineWeatherText.setVisibility(TextView.INVISIBLE);
        tv_forecastDate.setVisibility(TextView.INVISIBLE);
        tv_temperature.setVisibility(TextView.INVISIBLE);
        tv_moreInformation.setVisibility(TextView.INVISIBLE);
        tv_moreInformation.setClickable(false);
        tv_dayWeatherText.setVisibility(TextView.INVISIBLE);
        tv_nightWeatherText.setVisibility(TextView.INVISIBLE);
        iv_day.setVisibility(ImageView.INVISIBLE);
        iv_night.setVisibility(ImageView.INVISIBLE);
        btn_saveCity.setVisibility(Button.INVISIBLE);
        btn_setAsCurrent.setVisibility(Button.INVISIBLE);
        rv_nextFivedaysWeather.setVisibility(RecyclerView.INVISIBLE);
        pb_loading.setVisibility(ProgressBar.VISIBLE);
    }

    public void setLoadingInVisible(){
        tv_cityName.setVisibility(TextView.VISIBLE);
        tv_headlineWeatherText.setVisibility(TextView.VISIBLE);
        tv_forecastDate.setVisibility(TextView.VISIBLE);
        tv_temperature.setVisibility(TextView.VISIBLE);
        tv_moreInformation.setVisibility(TextView.VISIBLE);
        tv_moreInformation.setClickable(true);
        tv_dayWeatherText.setVisibility(TextView.VISIBLE);
        tv_nightWeatherText.setVisibility(TextView.VISIBLE);
        iv_day.setVisibility(ImageView.VISIBLE);
        iv_night.setVisibility(ImageView.VISIBLE);
        btn_saveCity.setVisibility(Button.VISIBLE);
        btn_setAsCurrent.setVisibility(Button.VISIBLE);
        rv_nextFivedaysWeather.setVisibility(RecyclerView.VISIBLE);
        pb_loading.setVisibility(ProgressBar.INVISIBLE);

    }
    @Override
    public void selecteditem(int position) {
        setTexts(position);
    }

    public void setTexts(int index){
        tv_headlineWeatherText.setText(fivedaysForecast.get(index).getHeadLine());
        tv_forecastDate.setText("Forecat on "+fivedaysForecast.get(index).getDate());
        tv_temperature.setText("Temperature : "+fivedaysForecast.get(index).getMaxTempValue()+" / "+fivedaysForecast.get(index).getMinTempValue()+" "+fivedaysForecast.get(index).getTempUnit());
        tv_moreInformation.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='"+fivedaysForecast.get(index).getMobileLinkurl()+"'> CLick here for more details </a>";
        tv_moreInformation.setText(Html.fromHtml(text));
        tv_dayWeatherText.setText(fivedaysForecast.get(index).getDayText());
        tv_nightWeatherText.setText(fivedaysForecast.get(index).getNightText());

        Picasso.get().load("http://developer.accuweather.com/sites/default/files/"+fivedaysForecast.get(index).getDayIconID()+"-s.png").into(iv_day);
        Picasso.get().load("http://developer.accuweather.com/sites/default/files/"+fivedaysForecast.get(index).getNightIconID()+"-s.png").into(iv_night);

    }

    class getCurrentCityConditions extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            setLoadingVisible();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(strings[0]).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String json = response.body().string();

                JSONObject root = new JSONObject(json);
                JSONObject cityJson = root.getJSONObject("Headline");
                String headlineText = cityJson.getString("Text");
                JSONArray fiveTelecasts = root.getJSONArray("DailyForecasts");
                for(int i= 0;i<fiveTelecasts.length();i++){
                    JSONObject dayWeather = fiveTelecasts.getJSONObject(i);
                    detailedWeatherOfCity day = new detailedWeatherOfCity();
                    day.setHeadLine(headlineText);


                    Date last_date = new SimpleDateFormat("yyyy-MM-dd").parse(dayWeather.getString("Date").substring(0,10));
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                    String dateString = sdf.format(last_date);
                    day.setDate(dateString);

                    day.setTempUnit(dayWeather.getJSONObject("Temperature").getJSONObject("Minimum").getString("Unit"));
                    int dayIcon = dayWeather.getJSONObject("Day").getInt("Icon");
                    int nightIcon =dayWeather.getJSONObject("Night").getInt("Icon");
                    if (dayIcon<9) day.setDayIconID("0"+dayIcon);
                    else day.setDayIconID(String.valueOf(dayIcon));
                    if (nightIcon<9) day.setNightIconID("0"+nightIcon);
                    else day.setNightIconID(String.valueOf(nightIcon));
                    day.setDayText(dayWeather.getJSONObject("Day").getString("IconPhrase"));
                    day.setNightText(dayWeather.getJSONObject("Night").getString("IconPhrase"));
                    day.setMinTempValue(dayWeather.getJSONObject("Temperature").getJSONObject("Minimum").getString("Value"));
                    day.setMaxTempValue(dayWeather.getJSONObject("Temperature").getJSONObject("Maximum").getString("Value"));
                    day.setMobileLinkurl(dayWeather.getString("MobileLink"));

                    fivedaysForecast.add(day);
                }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                 return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            tv_cityName.setText(cityName);
            setTexts(0);
            rv_nextFivedaysWeather.setHasFixedSize(true);
            rv_layoutManager = new GridLayoutManager(CityWeather.this, 5);
            rv_nextFivedaysWeather.setLayoutManager(rv_layoutManager);
            rv_adapter = new myAdapter(fivedaysForecast, CityWeather.this);
            rv_nextFivedaysWeather.setAdapter(rv_adapter);
            setLoadingInVisible();
        }
    }

    class setCityDetailsFromCurrentCity extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            setLoadingVisible();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... strings) {
            OkHttpClient client = new OkHttpClient();
            String urltoSend = "http://dataservice.accuweather.com/currentconditions/v1/"+selectedCity.getCitykey()+"?apikey="+getString(R.string.apiKey);
            Request request = new Request.Builder().url(urltoSend).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String json = response.body().string();

                JSONArray CurrentCity = new JSONArray(json);
                JSONObject cityJson = CurrentCity.getJSONObject(0);
                int imageID = cityJson.getInt("WeatherIcon");
                String weatherIconID = "";
                if(imageID < 10){
                    weatherIconID="0"+imageID;
                }else{
                    weatherIconID= String.valueOf(imageID);
                }
                selectedCity.setWeatherIcon(weatherIconID);
                selectedCity.setMetricValue(cityJson.getJSONObject("Temperature").getJSONObject("Metric").getString("Value"));
                selectedCity.setWeatherText(cityJson.getString("WeatherText"));
                selectedCity.setMetricUnit(cityJson.getJSONObject("Temperature").getJSONObject("Metric").getString("Unit"));
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = simpleDateFormat.parse(cityJson.getString("LocalObservationDateTime").substring(0,19));
                selectedCity.setLocalObservationTime(date);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            Intent gotoMain = new Intent();
            gotoMain.putExtra("RESULTCITY", selectedCity);
            setResult(RESULT_CANCELED,gotoMain);
            finish();
        }
    }

    class setCityDetailsFromSavedCities extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            setLoadingVisible();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... strings) {
            OkHttpClient client = new OkHttpClient();
            String urltoSend = "http://dataservice.accuweather.com/currentconditions/v1/"+selectedCity.getCitykey()+"?apikey="+getString(R.string.apiKey);
            Request request = new Request.Builder().url(urltoSend).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String json = response.body().string();

                JSONArray CurrentCity = new JSONArray(json);
                JSONObject cityJson = CurrentCity.getJSONObject(0);
                int imageID = cityJson.getInt("WeatherIcon");
                String weatherIconID = "";
                if(imageID < 10){
                    weatherIconID="0"+imageID;
                }else{
                    weatherIconID= String.valueOf(imageID);
                }
                selectedCity.setWeatherIcon(weatherIconID);
                selectedCity.setMetricValue(cityJson.getJSONObject("Temperature").getJSONObject("Metric").getString("Value"));
                selectedCity.setWeatherText(cityJson.getString("WeatherText"));
                selectedCity.setMetricUnit(cityJson.getJSONObject("Temperature").getJSONObject("Metric").getString("Unit"));
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = simpleDateFormat.parse(cityJson.getString("LocalObservationDateTime").substring(0,19));
                selectedCity.setLocalObservationTime(date);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            Intent gotoMain = new Intent();
            gotoMain.putExtra("RESULTCITY", selectedCity);
            setResult(RESULT_OK,gotoMain);
            finish();
        }
    }
}
