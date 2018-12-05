package gr.uom.android.lesson_9;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

public class WeatherFragment extends Fragment {

    private static final String TAG = "WeatherFragment";
    private ImageView weatherIcon;
    private TextView txtLocation;
    private TextView txtWeather;

    private String weatherString = "Weather String";
    private String locationString = "location string";

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setWeatherIconFromBitmap(Bitmap bitmap) {
        weatherIcon.setImageBitmap(bitmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weather,
                container,
                false);

        txtWeather = rootView.findViewById(R.id.txtWeather);
        weatherIcon = rootView.findViewById(R.id.weatherIcon);
        txtLocation = rootView.findViewById(R.id.txtLocation);
        txtWeather.setText("My weather will be here");

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Create async task
        FetchWeatherTask task =
                new FetchWeatherTask(this);

        // start async task
        task.execute();

    }

    public String getWeatherString() {
        return weatherString;
    }

    public void setWeatherString(String weatherString) {
        this.weatherString = weatherString;
        txtWeather.setText(weatherString);
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
        txtLocation.setText(locationString);
    }
}
