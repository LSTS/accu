package pt.lsts.accu.state;

import java.util.ArrayList;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Simple GPS manager that only deals with location changes and routes the info
 * to a series of listeners in a list.
 * If there is no listener it auto-shutdown the GPS locator
 * @author sharp
 *
 */
public class GPSManager {
	public static final String TAG = "GPSManager";
	LocationManager manager;
	private ArrayList<LocationChangeListener> listeners = new ArrayList<LocationChangeListener>();
	private Location currentLocation = new Location("gps");
	boolean running = false;
    Context mcontext;

	LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			currentLocation = location;
			updateLocation();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("GPSManager", provider + " out");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("GPSManager", provider + " on");

		}

		@Override
		public void onStatusChanged(String provider, int status,
									Bundle extras) {

		}
	};

	public GPSManager(Context context) {
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mcontext = context;
	}

	public void addListener(LocationChangeListener l) {
		// Verify if adding first listener to start GPS (battery expensive so doesnt need to be up alll the time)
		if (listeners.size() == 0) {
			start();
		}
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
		Log.i(TAG, "Now with " + listeners.size());
		updateLocation();
	}

	public void removeListener(LocationChangeListener l) {
		if (listeners.contains(l)) {
			listeners.remove(l);
		}
		// Verify if there is no listener left to shutdown GPS
		if (listeners.size() == 0) {
			stop();
		}
		Log.i(TAG, "Now with " + listeners.size());
	}

	// Advertise Location Change through listener system
	private void updateLocation() {
		for (LocationChangeListener l : listeners) {
			l.onLocationChange(currentLocation);
		}
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void start() {
		if (!running) {
			// Register for GPS and NETWORK updates
			if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
					ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
			// UNCOMMENT TO GET NETWORK UPDATES
			if(Accu.getInstance().getPrefs().getBoolean("networkFix", false))
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
			running = true;
		}
	}
	
	public void stop()
	{
		if(running)
		{
			manager.removeUpdates(listener);
			running = false;
		}
	}
}
