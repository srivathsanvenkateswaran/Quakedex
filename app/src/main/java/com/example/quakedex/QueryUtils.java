package com.example.quakedex;

import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class QueryUtils {
    private static final String LOG_TAG = "";

    private QueryUtils() {
    }

    private static URL createURL(String urlString){
        URL urlObject = null;
        try {
            urlObject = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return urlObject;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if(inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while(line != null){
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }

        return stringBuilder.toString();
    }

    private static String makeHttpRequest(URL urlObject) throws IOException {
        String JSON_Response = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode()== HttpURLConnection.HTTP_OK){
                inputStream = urlConnection.getInputStream();
                JSON_Response = readFromInputStream(inputStream);
            }
            else {
                Log.e(LOG_TAG,"Http Response code is " + urlConnection.getResponseCode());
            }
        }
        finally {
            if(inputStream!=null){
                inputStream.close();
            }
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
        return JSON_Response;
    }

    public static ArrayList<Earthquake> getEarthquakeData(String requestURL){
        URL urlObject = createURL(requestURL);

        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(urlObject);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Earthquake> earthquakes = extractEarthquakesFromJSON(jsonResponse);

        return  earthquakes;
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<Earthquake> extractEarthquakesFromJSON(String jsonResponse) {

        if(TextUtils.isEmpty(jsonResponse)){
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        
        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject rootObject = new JSONObject(jsonResponse);

            JSONArray featuresObject = rootObject.getJSONArray("features");
            for(int i=0;i<featuresObject.length();i++)
            {
                JSONObject earthquakeJSONObject = featuresObject.getJSONObject(i);
                JSONObject propertiesObject = earthquakeJSONObject.getJSONObject("properties");
                double magnitude = propertiesObject.getDouble("mag");
                String place = propertiesObject.getString("place");
                long time = propertiesObject.getLong("time");
                String url = propertiesObject.getString("url");
                Earthquake earthquakeJavaObject = new Earthquake(magnitude, place, time, url);
                earthquakes.add(earthquakeJavaObject);
            }
            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}