package pt.lsts.accu.state;

import java.io.IOException;
import java.util.ArrayList;

import pt.lsts.accu.components.Heart;
import pt.lsts.accu.components.HeartbeatVibrator;
import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.util.MUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Global Singleton that actually acts as the workhorse of ACCU. Contains all
 * the structures that need to be global and persistent across all
 * activities/panels
 * 
 * @author jqcorreia
 * 
 */
public class Accu {

    private static final String TAG = "ACCU";

    private static Context mContext;
    private static Accu instance;
    private static Sys activeSys;

    private static IMCManager imcManager;
    public SystemList mSysList;
    public static Announcer mAnnouncer;
    public static AccuSmsHandler mSmsHandler;
    public static GPSManager mGpsManager;
    public static HeartbeatVibrator mHBVibrator;
    public static Heart mHeart;
    public static LblBeaconList mBeaconList;
    public static CallOut callOut;
    public static SensorManager mSensorManager;

    private static ArrayList<MainSysChangeListener> mMainSysChangeListeners;
    public String broadcastAddress;
    public boolean started = false;
    public SharedPreferences mPrefs;

    private static Integer requestId = 0xFFFF; // Request ID for quick plan
    // sending

    private Accu(Context context) {
	Log.i(TAG, Accu.class.getSimpleName()
		+ ": Initializing Global ACCU Object");
	mContext = context;
	imcManager = new IMCManager();
	imcManager.startComms(); // Start comms here upfront

	mSysList = new SystemList(imcManager);

	try {
	    broadcastAddress = MUtil.getBroadcastAddress(mContext);
	} catch (IOException e) {
	    Log.e(TAG, Accu.class.getSimpleName()
		    + ": Couldn't get Brodcast address", e);
	}

	mGpsManager = new GPSManager(mContext);
	mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	mMainSysChangeListeners = new ArrayList<MainSysChangeListener>();
	mAnnouncer = new Announcer(imcManager, broadcastAddress, "224.0.75.69");
	mSmsHandler = new AccuSmsHandler(mContext, imcManager);
	mHBVibrator = new HeartbeatVibrator(mContext, imcManager);
	callOut = new CallOut(mContext);
    }

    public void load() {
	Log.i(TAG, Accu.class.getSimpleName() + ": load");
	mHeart = new Heart();
	mBeaconList = new LblBeaconList();
    }

    public void start() {
	Log.i(TAG, Accu.class.getSimpleName() + ": start");
	if (!started) {
	    imcManager.startComms();
	    mAnnouncer.start();
	    mSysList.start();
	    mHeart.start();
	    started = true;
	} else
	    Log.e(TAG, Accu.class.getSimpleName()
		    + ": ACCU ERROR: Already Started ACCU Global");
    }

    public void pause() {
	Log.i(TAG, Accu.class.getSimpleName() + ": pause");
	if (started) {
	    imcManager.killComms();
	    mAnnouncer.stop();
	    mSysList.stop();
	    mHeart.stop();
	    mSmsHandler.stop();
	    started = false;
	} else
	    Log.e(TAG, Accu.class.getSimpleName()
		    + ": ACCU ERROR: ACCU Global already stopped");
    }

    public static Accu getInstance(Context context) {
	Log.i(TAG, Accu.class.getSimpleName() + ": getInstance(context)");
	if (instance == null) {
	    instance = new Accu(context);
	}
	return instance;
    }

    public static Accu getInstance() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getInstance");
	return instance;
    }

    public Sys getActiveSys() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getActiveSys");
	return activeSys;
    }

    public void setActiveSys(Sys activeS) {
	Log.i(TAG, Accu.class.getSimpleName() + ": setActiveSys");
	activeSys = activeS;
	notifyMainSysChange();
    }

    public IMCManager getIMCManager() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getIMCManager");
	return imcManager;
    }

    public SystemList getSystemList() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getSystemList");
	return mSysList;
    }

    public GPSManager getGpsManager() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getGpsManager");
	return mGpsManager;
    }

    public SensorManager getSensorManager() {
		return mSensorManager;
	}
    
    public LblBeaconList getLblBeaconList() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getLblBeaconList");
	return mBeaconList;
    }

    public CallOut getCallOut() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getCallOut");
	return callOut;
    }

    // Main System listeners list related code
    public void addMainSysChangeListener(MainSysChangeListener listener) {
	Log.i(TAG, Accu.class.getSimpleName() + ": addMainSysChangeListener");
	mMainSysChangeListeners.add(listener);
    }

    public void removeMainSysChangeListener(MainSysChangeListener listener) {
	Log.i(TAG, Accu.class.getSimpleName() + ": removeMainSysChangeListener");
	mMainSysChangeListeners.remove(listener);
    }

    private static void notifyMainSysChange() {
	Log.i(TAG, Accu.class.getSimpleName() + ": notifyMainSysChange");
	for (MainSysChangeListener l : mMainSysChangeListeners) {
	    l.onMainSysChange(activeSys);
	}
    }

    public SharedPreferences getPrefs() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getPrefs");
	return mPrefs;
    }

    public boolean isStarted() {
	Log.i(TAG, Accu.class.getSimpleName() + ": isStarted");
	return started;
    }

    /**
     * @return the next requestId
     */
    public int getNextRequestId() {
	Log.i(TAG, Accu.class.getSimpleName() + ": getNextRequestId");
	synchronized (requestId) {
	    ++requestId;
	    if (requestId > 0xFFFF)
		requestId = 0;
	    if (requestId < 0)
		requestId = 0;
	    return requestId;
	}
    }
}
