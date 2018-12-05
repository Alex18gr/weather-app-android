package gr.uom.android.lesson_9;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FetchWeatherTask extends AsyncTask<String, Void, List<String>> {

    private static final String TAG = "FetchWeatherTask";

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private WeatherFragment weatherFragment;

    public FetchWeatherTask(WeatherFragment fragment) {
        this.weatherFragment = fragment;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        return roundedHigh + "/" + roundedLow;
    }

    private List<List<String>> getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";
        final String OWM_ICON = "icon";
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        List<List<String>> resultList = new ArrayList<>();
        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;
            String icon;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);
            icon = weatherObject.getString(OWM_ICON);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            // get city data
            // JSONObject weatherCity = dayForecast.getJSONObject(OWM_CITY);
            //String cityName = dayForecast.getString(OWM_CITY_NAME);
            //Log.d(TAG, "getWeatherDataFromJson: " + cityName);

            highAndLow = formatHighLows(high, low);
            List<String> data = new ArrayList<>();
            data.add(day);
            data.add(description);
            data.add(high + "");
            data.add(low + "");
            data.add("http://openweathermap.org/img/w/" + icon + ".png");
            //data.add(cityName);
            resultList.add(data);
            //resultStrings.add(day + " - " + description + " - " + highAndLow);


        }

        for (List<String> s : resultList) {
            String resultString = s.get(0) + " - " + s.get(1) + " - " + s.get(2) + "/" + s.get(3);
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }
        return resultList;

    }

    public Bitmap getBitmap(String bitmapUrl)
    {
        try
        {
            URL url = new URL(bitmapUrl);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }
        catch(Exception ex) {
            Log.e(TAG, "getBitmap: ", ex);
            return null;}
    }

    private String getIconLink(String description) {
        return "link";
    }

    @Override
    protected List<String> doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        String weatherFormat = "json";
        int numDays = 1;
        String units = "metric";

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            //MODIFIED FOR CITY OF THESSALONIKI, GREECE
            final String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            //"id=734077&mode=json&units=metric&cnt=7";
            final String queryParam = "id";
            final String formatParam = "mode";
            final String unitsParam = "units";
            final String daysParam = "cnt";
            final String apiKeyParam = "APPID";

            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    //.appendQueryParameter(queryParam, "734077") //code for thessaloniki
                    .appendQueryParameter(queryParam, "2643743") //code for london

                    .appendQueryParameter(formatParam, weatherFormat)
                    .appendQueryParameter(unitsParam, units)
                    .appendQueryParameter(daysParam, Integer.toString(numDays))
                    .appendQueryParameter(apiKeyParam, "27949ea6b6dffa1dad1deb925c9b024b")
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {

            List<String> data = getWeatherDataFromJson(forecastJsonStr, numDays).get(0);

            return getWeatherDataFromJson(forecastJsonStr, numDays).get(0);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        Log.d(LOG_TAG, "onPostExecute: " + result);

        //fragment.setWeatherText(result);
        String resultString = result.get(0) + " - " + result.get(1) + " - " + result.get(2) + "/" + result.get(3);
        weatherFragment.setWeatherString(resultString);
        //weatherFragment.setLocationString(result.get(5));
        new DownloadImageTask().execute(result.get(4));

    }

    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("dd MM YYYY");
        return shortenedDateFormat.format(time);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "image download error");
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            weatherFragment.setWeatherIconFromBitmap(result);
        }
    }


}