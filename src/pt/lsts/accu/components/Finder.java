package pt.lsts.accu.components;

import pt.lsts.accu.panel.AccuComponent;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.GPSManager;
import pt.lsts.accu.state.LocationChangeListener;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.types.android.MyBitmapDrawable;
import pt.lsts.accu.util.CoordUtil;
import pt.lsts.accu.R;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class Finder
extends ImageView
implements AccuComponent, SensorEventListener, LocationChangeListener 
{
	public static final String[] SUBSCRIBED_MSGS = {"EstimatedState","LblConfig","LblBeacon"};
	public static final boolean DEBUG = true;
	public static final String TAG = "Finder";
	
	SensorManager sensorManager;
	GPSManager gpsManager = Accu.getInstance().getGpsManager();
	
	Sensor sensor;
	Sys targetSys;
		
	double myLat, myLon;
	float myAzimuth;
	
	double targetLat, targetLon;
	
	MyBitmapDrawable headingArrow;
	
	private float accuracy;
	
	Context context;
	
	private OnFinderChangeListener listener;
	public Finder(Context context) 
	{
		super(context);
		this.context = context;
		initialize();
	}
	
	public Finder(Context context, AttributeSet attr)
	{
		super(context, attr);
		this.context = context;
		initialize();
	}
	void initialize()
	{
		myLat = 0.0;
		myLon = 0.0;
		
		targetLat = 0.0;
		targetLon = 0.0;
		
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		headingArrow = new MyBitmapDrawable(context.getResources().getDrawable(R.drawable.compass2));
				
		Log.i("Log","Creating compass!");
		setImageDrawable(headingArrow);
		setAdjustViewBounds(true);
		invalidate();
		
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}
	
	@Override
	public void onStart() 
	{
		setTarget(0.0f,0.0f);
	
    	gpsManager.addListener(this);
    	sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onLocationChange(Location location) {
		myLat = location.getLatitude();
		myLon = location.getLongitude();
		accuracy = location.getAccuracy();
		update();	
	}
	
	void update()
	{	
		double head = CoordUtil.bearing2LatLon(myLat, myLon, targetLat,targetLon);
		double dist = CoordUtil.dist2LatLon(myLat, myLon, targetLat, targetLon);
		if (DEBUG)
			Log.i("FinderAct", "lat: " + myLat + " lon:" + myLon + " azi: "
					+ myAzimuth + " acc: " + accuracy);

		// Change arrow orientation
		headingArrow.setDegrees((float) (-(myAzimuth - 270)) + (float) head);
		invalidate();
		
		// Publish updates
		if(listener != null)
			listener.onFinderChange(head, dist, accuracy);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		myAzimuth = arg0.values[0];
		update();
	}
	
	public void setTarget(double tLat, double tLon)
	{
		targetLat = tLat;
		targetLon = tLon;
	}
	@Override
	public void onEnd() 
	{
		Log.i(TAG,"Finder Component Stop");
    	sensorManager.unregisterListener(this);
    	gpsManager.removeListener(this); 	
	}
	
	public void setOnFinderChangeListener(OnFinderChangeListener listener) {
		this.listener = listener;
	}
	
	public interface OnFinderChangeListener
	{
		/**
		 * Called when sensor changes or GPS position is updated
		 * @param bearingDiff difference of bearing (in degrees)
		 * @param distance distance between the two points (in Kilometers)
		 * @param accuracy accuracy of reading (directly from Android LocationManager API)
		 */
		void onFinderChange(double bearingDiff, double distance, double accuracy);
	}
}
