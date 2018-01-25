/**
 * 
 */
package pt.lsts.accu;

import pt.lsts.accu.panel.AccuBasePanel;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.GPSManager;
import pt.lsts.accu.state.LocationChangeListener;
import pt.lsts.accu.util.MUtil;
import pt.lsts.accu.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @author pdias
 *
 */
public class HeadingTestPanel extends AccuBasePanel 
{
    private GPSManager gpsManager;
    private SensorManager sensorManager;
    private Sensor sensorOrientation;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;
    
    private double myHeading = 0;
    private Location myLocation = null;
    
    private int screenOrient = Configuration.ORIENTATION_UNDEFINED;
    private int screenRotation = Surface.ROTATION_0;
    
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];

    private boolean mLastOrientationSet = false;
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    
    private float[] mOrientationOrientation = new float[3];
    // GUI
    private TextView text;

    private LocationChangeListener locListener = new LocationChangeListener() {
        @Override
        public void onLocationChange(Location location) 
        {
            myLocation = location;
            updateText();
        }
    };
    private SensorEventListener orientationListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) 
        {
            myHeading = event.values[0];
            myHeading = MUtil.nomalizeAngleDegrees180(myHeading);
            mOrientationOrientation[0] = (float) myHeading;
            mOrientationOrientation[1] = (float) MUtil.nomalizeAngleDegrees180(event.values[1]);
            mOrientationOrientation[2] = (float) MUtil.nomalizeAngleDegrees180(event.values[2]);
            mLastOrientationSet = true;
            updateText();
        }
        
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) 
        {
        }
    };
    private SensorEventListener accelerationAndMagneticListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) 
        {
            if (event.sensor == sensorAccelerometer) {
                System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
                mLastAccelerometerSet = true;
            } else if (event.sensor == sensorMagnetometer) {
                System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
                mLastMagnetometerSet = true;
            }
//            System.out.println("----- " + mLastAccelerometerSet + " " + mLastMagnetometerSet);
            if (mLastAccelerometerSet && mLastMagnetometerSet) {
                SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
//                SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, mR); //Overwriting rotate matrix with the rotated values
                SensorManager.getOrientation(mR, mOrientation);
                Log.i("OrientationTestActivity", String.format("Orientation: %f°, %f°, %f°",
                        Math.toDegrees(mOrientation[0]), Math.toDegrees(mOrientation[1]), Math.toDegrees(mOrientation[2])));
                
                updateText();
            }
        }
        
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) 
        {
        }
    };

    public HeadingTestPanel(Context context) 
    {
        super(context);
    }

    /* (non-Javadoc)
     * @see pt.lsts.accu.panel.AccuBasePanel#onStart()
     */
    @Override
    public void onStart() 
    {
        text = (TextView) getLayout().findViewWithTag("textHeadingTest");
        
        gpsManager = Accu.getInstance().getGpsManager();
        sensorManager = Accu.getInstance().getSensorManager();
        
        sensorOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        gpsManager.addListener(locListener);
        sensorManager.registerListener(orientationListener, sensorOrientation, SensorManager.SENSOR_DELAY_NORMAL); // SENSOR_DELAY_GAME
        sensorManager.registerListener(accelerationAndMagneticListener, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accelerationAndMagneticListener, sensorMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        
        screenOrient = getContext().getResources().getConfiguration().orientation;
        Display display = ((WindowManager) getContext().getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();
        screenRotation = display.getRotation();
        
        updateText();
    }

    /* (non-Javadoc)
     * @see pt.lsts.accu.panel.AccuBasePanel#onStop()
     */
    @Override
    public void onStop() 
    {
        gpsManager.removeListener(locListener);
        sensorManager.unregisterListener(orientationListener);
        sensorManager.unregisterListener(accelerationAndMagneticListener);
    }

    /* (non-Javadoc)
     * @see pt.lsts.accu.panel.AccuBasePanel#buildLayout()
     */
    @Override
    public View buildLayout() 
    {
        View v = inflateFromResource(R.layout.heading_test_layout);
        return v;
    }

    @Override
    public int getIcon() 
    {
        return R.drawable.compass1;
//        return R.drawable.icon;
    }
   
    private void updateText() {
        screenOrient = getContext().getResources().getConfiguration().orientation;
        Display display = ((WindowManager) getContext().getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();
        screenRotation = display.getRotation();

        String txtStr = "HEADING TEST";
        txtStr += "\n";
        txtStr += "Has Orientation " + mLastOrientationSet + " " + sensorOrientation;
        txtStr += "\n";
        txtStr += "Has Accelerometer " + mLastAccelerometerSet + " " + sensorAccelerometer;
        txtStr += "\n";
        txtStr += "Has Magnetometer " + mLastMagnetometerSet + " " + sensorMagnetometer;
        txtStr += "\n";
        txtStr += "Location: " + (myLocation != null ? myLocation.toString() : "");
        txtStr += "\n";
        txtStr += "My Heading from GPS: " + (myLocation != null && myLocation.hasBearing() ? MUtil.nomalizeAngleDegrees180(myLocation.getBearing()) + "°" : "");
        txtStr += "\n";
        txtStr += "My Heading from Orientation: " + (mLastOrientationSet ? String.format("A %f°, P %f°, R %f°",
                mOrientationOrientation[0], mOrientationOrientation[1], mOrientationOrientation[2]) : "");
        txtStr += "\n";
        txtStr += "My Heading from Acce/Magn: " + ((mLastAccelerometerSet && mLastMagnetometerSet) ? String.format("A %f°, P %f°, R %f°",
                Math.toDegrees(mOrientation[0]), Math.toDegrees(mOrientation[1]), Math.toDegrees(mOrientation[2])) : "");
        txtStr += "\n";
        txtStr += "Accelerometer: " + ((mLastAccelerometerSet) ? String.format("  %fm/s²,   %fm/s²,   %fm/s²",
                mLastAccelerometer[0], mLastAccelerometer[1], mLastAccelerometer[2]) : "");
        txtStr += "\n";
        txtStr += "Magnetometer: " + ((mLastMagnetometerSet) ? String.format("  %fμT,   %fμT,   %fμT",
                mLastMagnetometer[0], mLastMagnetometer[1], mLastMagnetometer[2]) : "");
        txtStr += "\n";
        
        switch (screenOrient) {
            case Configuration.ORIENTATION_LANDSCAPE:
                txtStr += "Screen Orient: " + "Landscape\n";
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                txtStr += "Screen Orient: " + "Portrait\n";
                break;
            case Configuration.ORIENTATION_SQUARE:
                txtStr += "Screen Orient: " + "Square\n";
                break;
            case Configuration.ORIENTATION_UNDEFINED:
                txtStr += "Screen Orient: " + "Undefined\n";
                break;
            default:
                txtStr += "Screen Orient: " + "Undefined (" + screenOrient + ")\n";
                break;
        }

        switch (screenRotation) {
            case Surface.ROTATION_0:
                txtStr += "Screen Rotation: " + "0°\n";
                break;
            case Surface.ROTATION_90:
                txtStr += "Screen Rotation: " + "90°\n";
                break;
            case Surface.ROTATION_180:
                txtStr += "Screen Rotation: " + "180°\n";
                break;
            case Surface.ROTATION_270:
                txtStr += "Screen Rotation: " + "270°\n";
                break;
            default:
                txtStr += "Screen Rotation: " + "Undefined (" + screenRotation + ")\n";
                break;
        }

        System.out.println(txtStr);
        text.setText(txtStr);
    }
}
