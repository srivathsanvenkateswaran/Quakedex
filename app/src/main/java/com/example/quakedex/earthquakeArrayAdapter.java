package com.example.quakedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.graphics.drawable.GradientDrawable;

public class earthquakeArrayAdapter extends ArrayAdapter<Earthquake> {
    public earthquakeArrayAdapter(@NonNull Context context, @NonNull ArrayList<Earthquake> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_layout, parent, false);
        }

        Earthquake currentEarthquake = getItem(position);

        //---------------------- Magnitude Handling ----------------------

        DecimalFormat decimalFormatter = new DecimalFormat("0.0");
        String magnitudeString = decimalFormatter.format(currentEarthquake.getMagnitude());

        TextView quakeMagnitude = (TextView) listItemView.findViewById(R.id.magnitude);
        quakeMagnitude.setText(magnitudeString);

        //---------------------- Magnitude Color Handling ----------------------

        GradientDrawable magnitudeCircle = (GradientDrawable) quakeMagnitude.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        //---------------------- Place Handling ----------------------

        String fullPlaceString = currentEarthquake.getPlace();
        int splitIndex = fullPlaceString.indexOf("of");
        splitIndex+=2;
        String offsetString, placeString;
        if(fullPlaceString.contains("of")){
            offsetString = fullPlaceString.substring(splitIndex+1);
            placeString = fullPlaceString.substring(0, splitIndex-1);
        }
        else{
            offsetString = "Near the";
            placeString = fullPlaceString;
        }

        TextView quakePlace = (TextView) listItemView.findViewById(R.id.primary_location);
        quakePlace.setText(placeString);

        TextView quakeOffSet = (TextView) listItemView.findViewById(R.id.location_offset);
        quakeOffSet.setText(offsetString);

        //---------------------- Date Handling ----------------------

        Date dateObject = new Date(currentEarthquake.getTime());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");

        TextView quakeDate = (TextView) listItemView.findViewById(R.id.date);
        quakeDate.setText(dateFormatter.format(dateObject));

        TextView quakeTime = (TextView) listItemView.findViewById(R.id.time);
        quakeTime.setText(timeFormatter.format(dateObject));

        return listItemView;
    }

    private int getMagnitudeColor(double magnitude) {int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
