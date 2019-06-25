package fort.guide.fort;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by dj on 22/3/19.
 */

public class GPSManager {

    private Context context;

    MLocationListener mLocationListener;
    LocationManager locationManager;

    public GPSManager(Context context) {
        this.context = context;
    }

    public void getLocation(final MLocationListener mLocationListener) {

        this.mLocationListener = mLocationListener;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            mLocationListener.onLocation(location);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {


        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    public void stop() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }


    public interface MLocationListener {
        void onLocation(Location location);
    }


}

