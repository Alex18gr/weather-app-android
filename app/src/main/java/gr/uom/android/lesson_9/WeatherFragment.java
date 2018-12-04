package gr.uom.android.lesson_9;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WeatherFragment extends Fragment {


    private TextView txtWeather;

    private String weatherString = "Weather String";

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weather,
                container,
                false);

        txtWeather = rootView.findViewById(R.id.txtWeather);

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
}
