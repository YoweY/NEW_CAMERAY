package com.jamieyao.cameray.LocationManager;

import android.content.Context;
import android.location.Location;
import android.util.Log;

/**
 * Created by jamie.yao_cp on 2017/1/17.
 */

public class LocationManager {
    private static final  String TAG = "LocationManager";

    LocationProvider mLocationProvider;
    private boolean mRecordLocation;

    public LocationManager(Context context) {
        Log.d(TAG, "Using legacy location provider.");
        LegacyLocationProvider llp = new LegacyLocationProvider(context);
        mLocationProvider = llp;
    }

    /**
     * Start/stop location recording.
     */
    public void recordLocation(boolean recordLocation) {
        mRecordLocation = recordLocation;
        mLocationProvider.recordLocation(mRecordLocation);
    }

    /**
     * Returns the current location from the location provider or null, if
     * location could not be determined or is switched off.
     */
    public Location getCurrentLocation() {
        return mLocationProvider.getCurrentLocation();
    }

    /*
     * Disconnects the location provider.
     */
    public void disconnect() {
        mLocationProvider.disconnect();
    }
}

