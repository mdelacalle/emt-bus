package com.brownietech.emtbus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import org.glob3.mobile.generated.AltitudeMode;
import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.G3MWidget;
import org.glob3.mobile.generated.Geodetic3D;
import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarksRenderer;
import org.glob3.mobile.generated.TimeInterval;
import org.glob3.mobile.generated.URL;

import java.util.List;


/**
 * @author mlmateo
 */

public class LocationUtil {


    public static final long TWO_MINUTES = 1000 * 60 * 2;


    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new
     *                            one
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        final long timeDelta = location.getTime() - currentBestLocation.getTime();
        final boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        final boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        final boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        final int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        final boolean isLessAccurate = accuracyDelta > 0;
        final boolean isMoreAccurate = accuracyDelta < 0;
        final boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        final boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }


    /** Checks whether two providers are the same */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    /**
     * @return whether or not any provider is enabled
     */
    public static boolean isLocationEnabled(final LocationManager locationManager) {
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }


    /**
     * @return the best last known location among enabled providers
     */
    public static Location getLastKnownLocation(final Context context,
                                                final LocationManager locationManager) {
        Location lastLocation = null;
        if (PermissionUtil.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            final List<String> matchingProviders = locationManager.getProviders(true);
            for (String provider : matchingProviders) {
                final Location location = locationManager.getLastKnownLocation(provider);
                if ((location != null) && isBetterLocation(location, lastLocation)) {
                    lastLocation = location;
                }
            }
        }

        return lastLocation;
    }


    public static Location getLocation(Activity activity) {
        LocationManager mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if(LocationUtil.getLastKnownLocation(activity, mLocationManager) == null ){
            return null;
        }else {
            return LocationUtil.getLastKnownLocation(activity, mLocationManager);
        }

        //FAKE LOCATION
//        Location targetLocation = new Location("Plaza del Encuentro");//provider name is unnecessary
//        targetLocation.setLatitude(40.405986d);//your coords of course
//        targetLocation.setLongitude(-3.6512d);
//        return targetLocation;
    }

    public static void setLocationFromGPS(String positionIcon, MarksRenderer markRenderer, Location location , G3MWidget g3mWidget, int cameraHeight) {
        markRenderer.removeAllMarks();



        if(cameraHeight==0){
            cameraHeight = 500;
        }

        Geodetic3D position = new Geodetic3D(Angle.fromDegrees(40.4146500),Angle.fromDegrees(-3.7004000d), cameraHeight);

        if(location != null ){
            position = new Geodetic3D(Angle.fromDegrees(location.getLatitude()), Angle.fromDegrees(location.getLongitude()), cameraHeight);
        }
        g3mWidget.setAnimatedCameraPosition(TimeInterval.fromSeconds(3),position);
        markRenderer.addMark(new Mark(new URL(positionIcon,false),
                new Geodetic3D(position.asGeodetic2D(),0),
                AltitudeMode.RELATIVE_TO_GROUND));
    }

}
