package com.example.weatheapps;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Function {



  public interface AsyncResponse {
    void processFinish(String output1, String output2, String output3, String output4, String output5, String output6, String output8);
    void getLocation(String city);
  }

  public static class Location extends AsyncTask<String,Void,JSONObject>{
    public AsyncResponse del = null;
    public Location(AsyncResponse as){
      del=as;
    }
    @Override
    protected JSONObject doInBackground(String... params) {
      JSONObject jsonLoc = null;
      try {
        jsonLoc = getLocation();
      } catch (Exception e) {
        Log.d("Error", "Cannot process JSON results", e);
      }
      return jsonLoc;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
      try {
        if(json != null){
          String city = json.getString("city");
          del.getLocation(city);
        }
      } catch (JSONException e) {

      }
    }


    public static JSONObject getLocation(){

      URL obj = null;
      try {
        obj = new URL("http://api.ipstack.com/check?access_key=fc3e2a13f4fe42814bff600fac16ac9b");
      } catch (MalformedURLException e3) {
        e3.printStackTrace();
      }
      HttpURLConnection connection = null;
      try {
        connection = (HttpURLConnection) obj.openConnection();
      } catch (IOException e3) {
        e3.printStackTrace();
      }

      BufferedReader in = null;
      try {
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } catch (IOException e2) {
        e2.printStackTrace();
      }
      String inputLine;
      StringBuffer response = new StringBuffer();

      try {
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      try {
        in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      JSONObject data = null;
      try {
        data = new JSONObject(response.toString());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return data;

    }


  }


  public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

    public AsyncResponse delegate = null;

    public placeIdTask(AsyncResponse asyncResponse) {
      delegate = asyncResponse;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
      JSONObject jsonWeather = null;
      try {
        jsonWeather = getWeatherJSON(params[0],params[1]);
      } catch (Exception e) {
        Log.d("Error", "Cannot process JSON results", e);
      }
      return jsonWeather;
    }


    @Override
    protected void onPostExecute(JSONObject json) {
      try {
        if(json != null){
          JSONObject details = json.getJSONArray("weather").getJSONObject(0);
          JSONObject main = json.getJSONObject("main");
          DateFormat df = DateFormat.getDateTimeInstance();

          String city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
          String description = details.getString("description").toUpperCase(Locale.US);
          String temperature = String.format("%.2f", main.getDouble("temp"))+ "°";
          String humidity = main.getString("humidity") + "%";
          String pressure = main.getString("pressure") + " hPa";
          String updatedOn = df.format(new Date(json.getLong("dt")*1000));

          delegate.processFinish(city, description, temperature, humidity, pressure, updatedOn, ""+ (json.getJSONObject("sys").getLong("sunrise") * 1000));
        }
      } catch (JSONException e) {

      }
    }
  }

  public static JSONObject getWeatherJSON(String s,String met){
    try {

      URL url = new URL(String.format("http://api.openweathermap.org/data/2.5/weather?q="+s+"&mode=json&APPID=2b1cdf491ccf9192213ece92d3da79c1&units="+met));
      HttpURLConnection connection =
        (HttpURLConnection)url.openConnection();

      BufferedReader reader = new BufferedReader(
        new InputStreamReader(connection.getInputStream()));

      StringBuffer json = new StringBuffer(1024);
      String tmp="";
      while((tmp=reader.readLine())!=null)
        json.append(tmp).append("\n");
      reader.close();

      JSONObject data = new JSONObject(json.toString());

      if(data.getInt("cod") != 200){
        return null;
      }
      return data;
    }catch(Exception e){
      return null;
    }
  }
}
