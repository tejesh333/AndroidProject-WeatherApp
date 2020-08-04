package com.example.homework03;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements savedCityAdapter.InteractWithMA {

    ArrayList<City> savedCities = new ArrayList<>();
    ArrayList<City> searchedCities = new ArrayList<>();
    City currentCity = new City();
    public static String TAG= "DEMO";
    int REQ_CODE = 001;

    TextView tv_currentCityNotSet,tv_CurrentCity,tv_CurrentCityTemperature,tv_CurrentCityWeather,tv_CurrentCityUpdated,tv_noCitySaved;
    Button btn_setCurrentCity,btn_searchCity;
    ImageView iv_CurrentCityWeatherIcon;
    ProgressBar pb_currentCity;
    EditText et_searchCity,et_searchCountry;
    RecyclerView rv_savedCitiesList;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Weather App");

        final String country="", city="",cityKey="", imageId="";

        tv_currentCityNotSet = findViewById(R.id.tv_currentCityNotSet);
        tv_CurrentCity = findViewById(R.id.tv_currentCity);
        tv_CurrentCityTemperature = findViewById(R.id.tv_currentCityTemperature);
        tv_CurrentCityWeather = findViewById(R.id.tv_currentCityWeataher);
        tv_CurrentCityUpdated = findViewById(R.id.tv_currentCityUpdated);
        tv_noCitySaved = findViewById(R.id.tv_noCitySaved);
        et_searchCity = findViewById(R.id.et_searchCity);
        et_searchCountry = findViewById(R.id.et_searchCountry);
        pb_currentCity = findViewById(R.id.pb_currentCity);
        rv_savedCitiesList = findViewById(R.id.rv_savedCities);

        btn_setCurrentCity=findViewById(R.id.btn_setCurrentCity);
        btn_searchCity = findViewById(R.id.btn_searchCity);

        iv_CurrentCityWeatherIcon=findViewById(R.id.iv_currentCItyWeatherIcon);
        tv_CurrentCity.setEnabled(false);
        tv_CurrentCity.setClickable(false);
        tv_CurrentCity.setVisibility(TextView.INVISIBLE);
        tv_CurrentCityWeather.setVisibility(TextView.INVISIBLE);
        pb_currentCity.setVisibility(ProgressBar.INVISIBLE);
        tv_CurrentCityTemperature.setVisibility(TextView.INVISIBLE);
        tv_CurrentCityUpdated.setVisibility(TextView.INVISIBLE);
        iv_CurrentCityWeatherIcon.setVisibility(ImageView.INVISIBLE);
        rv_savedCitiesList.setVisibility(RecyclerView.INVISIBLE);
        if(savedCities.size() != 0 || !savedCities.isEmpty()){
            rv_savedCitiesList.setVisibility(RecyclerView.VISIBLE);
            tv_noCitySaved.setVisibility(TextView.INVISIBLE);
        }

        if(isConnected()){
            btn_setCurrentCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callAlertDialogue();
                }
            });

            tv_CurrentCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callAlertDialogue();
                }
            });

            rv_savedCitiesList.setHasFixedSize(true);
            rv_layoutManager = new LinearLayoutManager(this);
            rv_savedCitiesList.setLayoutManager(rv_layoutManager);
            rv_adapter = new savedCityAdapter(savedCities, this);
            rv_savedCitiesList.setAdapter(rv_adapter);

            btn_searchCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchedCities.clear();
                    if(et_searchCity.getText()==null || et_searchCity.getText().equals("")){
                        Toast.makeText(MainActivity.this, "Enter valid City Details", Toast.LENGTH_SHORT).show();
                    }
                    else if(et_searchCountry.getText()==null || et_searchCountry.getText().equals("")){
                        Toast.makeText(MainActivity.this, "Enter valid Country Details", Toast.LENGTH_SHORT).show();
                    }else{
                        String url =  "http://dataservice.accuweather.com/locations/v1/cities/"+et_searchCountry.getText()+"/search?apikey="+getString(R.string.apiKey)+"&q="+et_searchCity.getText();
                        new getListOfCities().execute(url);
                    }
                }
            });

        }else{
            Toast.makeText(this, "Check Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        et_searchCity.setText("");
        et_searchCountry.setText("");

        if (requestCode == REQ_CODE) {
            if(resultCode==RESULT_OK && data != null){
                City resultSavedCity = (City)data.getExtras().get("RESULTCITY");
                rv_savedCitiesList.setVisibility(RecyclerView.VISIBLE);
                tv_noCitySaved.setVisibility(TextView.INVISIBLE);
                if(savedCities.contains(resultSavedCity)){
                    for(City c : savedCities ){
                        if(c.getCitykey().equals(resultSavedCity.getCitykey())){
                            c.setMetricValue(resultSavedCity.getMetricValue());
                            c.setMetricUnit(resultSavedCity.getMetricUnit());
                            c.setLocalObservationTime(resultSavedCity.getLocalObservationTime());
                            break;
                        }
                    }
                    rv_adapter.notifyDataSetChanged();
                    Toast.makeText(this, "City Updated", Toast.LENGTH_SHORT).show();
                }else{
                    savedCities.add(resultSavedCity);
                    Toast.makeText(this, "City Saved", Toast.LENGTH_SHORT).show();
                    rv_adapter.notifyDataSetChanged();
                }
            }
            if (resultCode==RESULT_CANCELED && data != null) {
                City resultCurrentCity = (City)data.getExtras().get("RESULTCITY");
                if(resultCurrentCity.getCitykey().equals(currentCity.getCitykey())){
                    tv_CurrentCityTemperature.setText("Temperature : "+resultCurrentCity.getMetricValue()+" "+resultCurrentCity.getMetricUnit());
                    PrettyTime p = new PrettyTime();
                    tv_CurrentCityUpdated.setText("Updated : "+String.valueOf(p.format(resultCurrentCity.getLocalObservationTime())));
                    currentCity.setMetricValue(resultCurrentCity.getMetricValue());
                    currentCity.setMetricUnit(resultCurrentCity.getMetricUnit());
                    currentCity.setLocalObservationTime(resultCurrentCity.getLocalObservationTime());
                    tv_currentCityNotSet.setVisibility(TextView.INVISIBLE);
                    btn_setCurrentCity.setVisibility(Button.INVISIBLE);
                    pb_currentCity.setVisibility(ProgressBar.INVISIBLE);
                    tv_CurrentCity.setVisibility(TextView.VISIBLE);
                    tv_CurrentCity.setEnabled(true);
                    tv_CurrentCity.setClickable(true);
                    tv_CurrentCityWeather.setVisibility(TextView.VISIBLE);
                    tv_CurrentCityTemperature.setVisibility(TextView.VISIBLE);
                    tv_CurrentCityUpdated.setVisibility(TextView.VISIBLE);
                    iv_CurrentCityWeatherIcon.setVisibility(ImageView.VISIBLE);
                    Toast.makeText(this, "Current City Updated", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "onActivityResult: "+resultCurrentCity.toString());
                    currentCity.setMetricValue(resultCurrentCity.getMetricValue());
                    currentCity.setMetricUnit(resultCurrentCity.getMetricUnit());
                    currentCity.setLocalObservationTime(resultCurrentCity.getLocalObservationTime());
                    currentCity.setAdministrativeArea(resultCurrentCity.getAdministrativeArea());
                    currentCity.setWeatherText(resultCurrentCity.getWeatherText());
                    currentCity.setWeatherIcon(resultCurrentCity.getWeatherIcon());
                    currentCity.setCountry(resultCurrentCity.getCountry());
                    currentCity.setCityName(resultCurrentCity.getCityName());
                    currentCity.setCitykey(resultCurrentCity.getCitykey());
                    tv_CurrentCity.setText(resultCurrentCity.getCityName()+", "+resultCurrentCity.getCountry());
                    tv_CurrentCityTemperature.setText("Temperature : "+resultCurrentCity.getMetricValue()+" "+resultCurrentCity.getMetricUnit());
                    tv_CurrentCityWeather.setText(resultCurrentCity.getWeatherText());
                    Picasso.get().load("http://developer.accuweather.com/sites/default/files/"+resultCurrentCity.getWeatherIcon()+"-s.png").into(iv_CurrentCityWeatherIcon);
                    PrettyTime p = new PrettyTime();
                    tv_CurrentCityUpdated.setText("Updated : "+String.valueOf(p.format(resultCurrentCity.getLocalObservationTime())));
                    tv_currentCityNotSet.setVisibility(TextView.INVISIBLE);
                    btn_setCurrentCity.setVisibility(Button.INVISIBLE);
                    pb_currentCity.setVisibility(ProgressBar.INVISIBLE);
                    tv_CurrentCity.setVisibility(TextView.VISIBLE);
                    tv_CurrentCity.setEnabled(true);
                    tv_CurrentCity.setClickable(true);
                    tv_CurrentCityWeather.setVisibility(TextView.VISIBLE);
                    tv_CurrentCityTemperature.setVisibility(TextView.VISIBLE);
                    tv_CurrentCityUpdated.setVisibility(TextView.VISIBLE);
                    iv_CurrentCityWeatherIcon.setVisibility(ImageView.VISIBLE);
                    Toast.makeText(this, "Current City Saved", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Void callAlertDialogue(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Enter City Details");
        LayoutInflater inflater = getLayoutInflater();
        View layOutView = inflater.inflate(R.layout.alertdialoguelayout,null);
        final EditText et_city = (EditText)layOutView.findViewById(R.id.et_currentCity);
        final EditText et_country = (EditText)layOutView.findViewById(R.id.et_currentCountry);
        alert.setView(layOutView);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(et_city.getText()==null || et_city.getText().equals("")){
                    Toast.makeText(MainActivity.this, "Enter valid City Details", Toast.LENGTH_SHORT).show();
                }
                else if(et_country.getText()==null || et_country.getText().equals("")){
                    Toast.makeText(MainActivity.this, "Enter valid Country Details", Toast.LENGTH_SHORT).show();
                }else{
                    String url =  "http://dataservice.accuweather.com/locations/v1/cities/"+et_country.getText()+"/search?apikey="+getString(R.string.apiKey)+"&q="+et_city.getText();
                    new getCurrentCity().execute(url);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
        return null;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    @Override
    public void selecteditem(int position) {
        savedCities.remove(position);
        if(savedCities.size() == 0 || savedCities.isEmpty()){
            rv_savedCitiesList.setVisibility(RecyclerView.INVISIBLE);
            tv_noCitySaved.setVisibility(TextView.VISIBLE);
        }
        rv_adapter.notifyDataSetChanged();
    }

    @Override
    public void setFavourate(int position) {
        if(!savedCities.get(position).isFavoutite()) savedCities.get(position).setFavoutite(true);
        else savedCities.get(position).setFavoutite(false);
        rv_adapter.notifyDataSetChanged();
    }

    class getCurrentCity extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            pb_currentCity.setVisibility(ProgressBar.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String currentCityConditionsurl="";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(strings[0]).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String json = response.body().string();
                JSONArray CurrentCity = new JSONArray(json);

                if(CurrentCity.length() == 0){
                    return null;
                }
                JSONObject cityJson = CurrentCity.getJSONObject(0);

                currentCity.setCityName(cityJson.getString("EnglishName"));
                currentCity.setCountry(cityJson.getJSONObject("Country").getString("ID"));
                currentCity.setCitykey(cityJson.getString("Key"));
                currentCity.setAdministrativeArea(cityJson.getJSONObject("AdministrativeArea").getString("ID"));
                currentCityConditionsurl = "http://dataservice.accuweather.com/currentconditions/v1/"+currentCity.getCitykey()+"?apikey="+getString(R.string.apiKey);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return currentCityConditionsurl;
        }

        @Override
        protected void onPostExecute(String url) {
            super.onPostExecute(url);
            new getCurrentCityConditions().execute(url);
        }
    }

    class getListOfCities extends AsyncTask<String,Void,ArrayList<City>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<City> doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(strings[0]).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String json = response.body().string();
                JSONArray citiesList = new JSONArray(json);

                if(citiesList.length() == 0){
                    return null;
                }
                for(int i=0;i<citiesList.length();i++){
                    JSONObject cityJson = citiesList.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityJson.getString("EnglishName"));
                    city.setCountry(cityJson.getJSONObject("Country").getString("ID"));
                    city.setCitykey(cityJson.getString("Key"));
                    city.setAdministrativeArea(cityJson.getJSONObject("AdministrativeArea").getString("ID"));

                    searchedCities.add(city);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return searchedCities;
        }

        @Override
        protected void onPostExecute(ArrayList<City> cities) {
            super.onPostExecute(cities);
            if(cities == null || cities.isEmpty() ){
                Toast.makeText(MainActivity.this, "No Cities matches your Search", Toast.LENGTH_SHORT).show();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose the City");
                ArrayList<String> list = new ArrayList<>();
                for(City city: cities){
                    list.add(city.getCityName()+", "+city.getAdministrativeArea());
                }

                builder.setItems(list.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentCityWeather = new Intent(MainActivity.this,CityWeather.class);
                        intentCityWeather.putExtra("SELECTEDCITY", searchedCities.get(i));
                        startActivityForResult(intentCityWeather, REQ_CODE);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }



    class getCurrentCityConditions extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(strings[0] == null){
                return null;
            }
            String imageURL ="";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(strings[0]).build();
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
                currentCity.setWeatherIcon(weatherIconID);
                currentCity.setMetricValue(cityJson.getJSONObject("Temperature").getJSONObject("Metric").getString("Value"));
                currentCity.setWeatherText(cityJson.getString("WeatherText"));
                String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = simpleDateFormat.parse(cityJson.getString("LocalObservationDateTime").substring(0,19));
                currentCity.setLocalObservationTime(date);
                currentCity.setMetricUnit(cityJson.getJSONObject("Temperature").getJSONObject("Metric").getString("Unit"));
                imageURL = "http://developer.accuweather.com/sites/default/files/"+currentCity.getWeatherIcon()+"-s.png";

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return imageURL;
        }

        @Override
        protected void onPostExecute(String url) {

            if(url == null){
                pb_currentCity.setVisibility(ProgressBar.INVISIBLE);
                Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_SHORT).show();
            }
            else{
                tv_currentCityNotSet.setVisibility(TextView.INVISIBLE);
                btn_setCurrentCity.setVisibility(Button.INVISIBLE);

                tv_CurrentCity.setText(currentCity.getCityName()+", "+currentCity.getCountry());
                tv_CurrentCityTemperature.setText("Temperature : "+currentCity.getMetricValue()+" "+currentCity.getMetricUnit());
                tv_CurrentCityWeather.setText(currentCity.getWeatherText());
                Picasso.get().load(url).into(iv_CurrentCityWeatherIcon);

                PrettyTime p = new PrettyTime();
                tv_CurrentCityUpdated.setText("Updated : "+String.valueOf(p.format(currentCity.getLocalObservationTime())));
                pb_currentCity.setVisibility(ProgressBar.INVISIBLE);
                tv_CurrentCity.setVisibility(TextView.VISIBLE);
                tv_CurrentCity.setEnabled(true);
                tv_CurrentCity.setClickable(true);
                tv_CurrentCityWeather.setVisibility(TextView.VISIBLE);
                tv_CurrentCityTemperature.setVisibility(TextView.VISIBLE);
                tv_CurrentCityUpdated.setVisibility(TextView.VISIBLE);
                iv_CurrentCityWeatherIcon.setVisibility(ImageView.VISIBLE);
                Toast.makeText(MainActivity.this, "Current City details saved", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
