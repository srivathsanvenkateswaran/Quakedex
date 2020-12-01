package com.example.quakedex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private earthquakeArrayAdapter adapter;
    TextView emptyStateTextView;
    ProgressBar dataFetchingProgressBar;

    public static final String URL_STRING = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            ConnectivityManager cm =
                    (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();


            emptyStateTextView = (TextView) findViewById(R.id.emptyStateTextView);

            dataFetchingProgressBar = (ProgressBar) findViewById(R.id.dataFetchingProgressBar);

            if(isConnected){
                EarthquakeAsyncTask task = new EarthquakeAsyncTask();
                task.execute(URL_STRING);
            }
            else {
                emptyStateTextView.setText(R.string.no_internet_text);
                dataFetchingProgressBar.setVisibility(View.GONE);
            }

            // Find a reference to the {@link ListView} in the layout
            ListView earthquakeListView = (ListView) findViewById(R.id.list);

            // Create a new {@link ArrayAdapter} of earthquakes
            adapter = new earthquakeArrayAdapter(this, new ArrayList<Earthquake>());

            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(adapter);
            earthquakeListView.setEmptyView(emptyStateTextView);

            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // Find the current earthquake that was clicked on
                    Earthquake currentEarthquake = adapter.getItem(position);

                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                    // Create a new intent to view the earthquake URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            });
        }

        class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

            @Override
            protected List<Earthquake> doInBackground(String... args) {
                if(args.length<1 || args[0]==null){
                    return null;
                }
                List<Earthquake> earthquakes = QueryUtils.getEarthquakeData(URL_STRING);
                return earthquakes;
            }

            @Override
            protected void onPostExecute(List<Earthquake> earthquakes) {
                adapter.clear();

                dataFetchingProgressBar.setVisibility(View.GONE);

                if(earthquakes != null && !earthquakes.isEmpty()){
                    adapter.addAll(earthquakes);
                }

                emptyStateTextView.setText(R.string.empty_state_text);
            }
        }
    }