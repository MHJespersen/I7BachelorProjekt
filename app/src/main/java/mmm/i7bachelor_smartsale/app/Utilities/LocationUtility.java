package mmm.i7bachelor_smartsale.app.Utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtility {
    Context context;

    public LocationUtility(Context context) {
        this.context = context;
    }

    // https://stackoverflow.com/a/2296416/1448765
    public String getCityName(double lat, double lng) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String stringResult = null;

        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String postalCode = address.getPostalCode();
                String subLocality = address.getSubLocality();
                String locality = address.getLocality();

                if (subLocality == null) {
                    stringResult = postalCode + " " + locality;
                } else {
                    stringResult = subLocality + ", " + postalCode + " " + locality;
                }

            }
        }

        return stringResult;

    }

    // https://stackoverflow.com/questions/9698328/how-to-get-coordinates-of-an-address-in-android
    public Location getLocationFromString(String address) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        Location location = new Location("");

        try {
            addresses = gcd.getFromLocationName(address, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double latitude = 0;
        double longitude = 0;

        if(addresses != null) {
            if(addresses.size() > 0) {
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();

                location.setLatitude(latitude);
                location.setLongitude(longitude);
            }
        }

        return location;
    }
}

