package mmm.i7bachelor_smartsale.app.Webapi;

import android.location.Location;

public interface LocationCallback {
    void OnApiCallbackString(String location);
    void onApiCallbackLocation(Location location);
}