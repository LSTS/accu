package pt.lsts.accu.components.controlpad;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

public class SensorPad extends JoystickPad implements SensorEventListener {
	int index;
	boolean positive; 

	public SensorPad(Context context, AttributeSet attrs) {
		super(context, attrs);
		String axis = attrs.getAttributeValue(null, "axis");
		positive = attrs.getAttributeBooleanValue(null,"positive",true);
		if(axis.equalsIgnoreCase("x")) index = 0;
		if(axis.equalsIgnoreCase("y")) index = 1;
		if(axis.equalsIgnoreCase("z")) index = 2;
		setVisibility(View.GONE); // Use the axis facilites of JoystickPad by inheritance but dont be visible;
		SensorManager sm = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(listener!=null)
		{
			float tilt = event.values[index];
			int currentTilt;
//			System.out.println(e.values[0]+" "+e.values[1]+" "+e.values[2]);
			if(tilt < -90) tilt =  -90;
			if(tilt > 90) tilt =  90;
			currentTilt = (int) ((int)tilt*(127/90f));
//			System.out.println((positive ? currentTilt : -currentTilt));
			System.out.println(positive);
			listener.onJoystickPadChange(this, (positive ? currentTilt : -currentTilt) ,0);
		}
	}
}
