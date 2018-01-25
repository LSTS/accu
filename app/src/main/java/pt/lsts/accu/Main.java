package pt.lsts.accu;

import java.io.FileInputStream;

import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import pt.lsts.accu.console.ConsoleConfig;
import pt.lsts.accu.panel.AccuPanelContainer;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.util.AccuTimer;
import pt.lsts.imc.lsf.LsfMessageLogger;

/**
 * Main Activity for ACCU application
 * 
 * @author jqcorreia
 *
 */
public class Main extends Activity {
	private static final String TAG = "ACCU";

	/**
	 * The Panel container (and respective panel selector)
	 */
	AccuPanelContainer container;

	/**
	 * Console Configuration object
	 */
	ConsoleConfig config;
	FileInputStream configFile;
	private PowerManager.WakeLock wl;

	private static boolean haveConnectedWifi;
	//Set a timer to check if is connected to a Wifi Network every 30 sec 
	AccuTimer timer = new AccuTimer(new Runnable() {
		@Override
		public void run() {
			// Check if is connected to a Wifi Network, if not popups a informative toast
			if (!isConnectedToWifi(Main.this)) {
				toast("Not connected to a network.");
			}
		}

	}, 30000);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		while(!PermissionActivity.fa.statePermission());
		Log.i(TAG, Main.class.getSimpleName() + ": onCreate");
		String logDir = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();
		Log.i("ACCU", "Log dir is "+logDir);
		LsfMessageLogger.changeLogBaseDir(logDir+"/");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	     
		String s = Accu.getInstance().getPrefs().getString("colorMode", "1");
		if (s.equals("1"))
			setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		else
			setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);

		super.onCreate(savedInstanceState);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

		if (!Accu.getInstance().isStarted())
			Accu.getInstance().start();

		setContentView(R.layout.main2);

		container = (AccuPanelContainer) findViewById(R.id.container);

		// Load/Process configuration panel
		config = new ConsoleConfig(this, "config.xml", container);
		config.initPanels();

		container.getSelector().setOpenerVisible(false); // Hide the selector
															// opener button

		// Replace the active system state (if one was selected previously)
		if (savedInstanceState != null) {
			Log.i(TAG, Main.class.getSimpleName()
					+ "onCreate: saved instance != null");
			if (savedInstanceState.getString("sys") != null) {
				Log.i(TAG,
						Main.class.getSimpleName()
								+ "onCreate: saved instance: "
								+ savedInstanceState.getString("sys"));
				Accu.getInstance().setActiveSys(
						Accu.getInstance().mSysList
								.findSysByName(savedInstanceState
										.getString("sys")));
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, Main.class.getSimpleName() + ": onStart");
		if (!Accu.getInstance().isStarted())
			Accu.getInstance().start();
			timer.start();
		container.startPanelWithId(container.currentPanelId);
		wl.acquire();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, Main.class.getSimpleName() + ": onSaveInstance");
		if (Accu.getInstance().getActiveSys() != null) {
			outState.putString("sys", Accu.getInstance().getActiveSys()
					.getName());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, Main.class.getSimpleName() + "Destroying Main Activity");
	}

	@Override
	public void onPause() {
		super.onPause();
		// Panel must be stopped before Accu instance because of
		// pausing/stopping messages
		// like TeleOperationDone on TeleOp Panel
		Log.i(TAG, Main.class.getSimpleName() + ": onPause");
		container.stopCurrentPanel();
		Accu.getInstance().pause();
		timer.stop();
		if (wl.isHeld())
			wl.release();
	}

	/*@Override
	protected boolean isRouteDisplayed() {
		Log.i(TAG, Main.class.getSimpleName() + ": isRouteDisplayed = false");
		return false;
	}*/

	public ConsoleConfig getConsoleConfig() {
		Log.i(TAG, Main.class.getSimpleName() + ": getConsoleConfig");
		return config;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, Main.class.getSimpleName() + ": onKeyDown");
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				if (event.isLongPress())
					this.finish();
				else
					container.getSelector().toggle();
			}
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.i(TAG, Main.class.getSimpleName() + ": onPrepareOptionsMenu");
		container.getCurrentPanel().prepareMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, Main.class.getSimpleName() + ": onOptionsItemSelected");
		container.getCurrentPanel().menuHandler(item);
		return true;
	}

	private boolean isConnectedToWifi(Context context) {
		haveConnectedWifi = false;
		try {
			ConnectivityManager nConManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (nConManager != null) {
				NetworkInfo nNetworkinfo = nConManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (nNetworkinfo.isConnected()) {
					haveConnectedWifi = true;
					return haveConnectedWifi;
				}
			}
		} catch (Exception e) {
		}
		return haveConnectedWifi;
	}

	public void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

}
