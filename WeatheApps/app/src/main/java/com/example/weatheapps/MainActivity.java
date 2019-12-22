package com.example.weatheapps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
  String city;
  String met = "metric";
  TextView weather;
  CheckBox c,f,k;
  EditText cityin;
  Button subuton,location;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    weather=(TextView)findViewById(R.id.text);
    cityin = (EditText)findViewById(R.id.City);
    c = (CheckBox)findViewById(R.id.Cels);
    f = (CheckBox)findViewById(R.id.Fahre);
    k = (CheckBox)findViewById(R.id.Kelvin);
    subuton = (Button)findViewById(R.id.button);
    location = (Button)findViewById(R.id.location);

    location.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Function.Location asyncTask =new Function.Location(new Function.AsyncResponse() {
          @Override
          public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String sun_rise) {

          }

          @Override
          public void getLocation(String city) {
            cityin.setText(city);
          }

        });
        asyncTask.execute();
      }
    });

    subuton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        city=cityin.getText().toString();

        if(c.isChecked()){
          met="metric";
        } else if(f.isChecked()){
          met="imperial";
        } else if (k.isChecked()){
          met="default";
        }
        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
         @Override
          public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String sun_rise) {
            weather.setText(weather_temperature);
          }

          @Override
          public void getLocation(String city) {

          }

        });
        asyncTask.execute(city,met);
      }
    });
  }
}
