package pt.up.fe.dceg.accu.state;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
	
	LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			currentLocation = location;
			updateLocation();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("GPSManager", provider +" out");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("GPSManager", provider +" on");

		}
		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {

		}
	};
	
	public GPSManager(Context context)
	{
		manager =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public void addListener(LocationChangeListener l)
	{
		// Verify if adding first listener to start GPS (battery expensive so doesnt need to be up alll the time)
		if(listeners.size()==0)
		{
			start();
		}
		if(!listeners.contains(l))
		{
			listeners.add(l);
		}
		Log.i(TAG,"Now with "+listeners.size());
		updateLocation();
	}
	public void removeListener(LocationChangeListener l)
	{
		if(listeners.contains(l))
		{
			listeners.remove(l);
		}
		// Verify if there is no listener left to shutdown GPS
		if(listeners.size()==0)
		{
			stop();
		}
		Log.i(TAG,"Now with "+listeners.size());
	}
	
	// Advertise Location Change through listener system
	private void updateLocation()
	{
		for(LocationChangeListener l: listeners)
		{
			l.onLocationChange(currentLocation);
		}
	}
	public Location getCurrentLocation()
	{
		return currentLocation;
	}
	public void start()
	{
		if(!running)
		{
			// Register for GPS and NETWORK updates
			manager.requestLocationUpdates("gps", 2000, 0, listener);
			// UNCOMMENT TO GET NETWORK UPDATES
			if(Accu.getInstance().getPrefs().getBoolean("networkFix", false))
				manager.requestLocationUpdates("network", 2000, 0, listener);
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
