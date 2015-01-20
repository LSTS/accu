package pt.lsts.accu;

import java.io.FileInputStream;

import pt.lsts.accu.console.ConsoleConfig;
import pt.lsts.accu.panel.AccuPanelContainer;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.MapActivity;

/**
 * Main Activity for ACCU application
 * 
 * @author jqcorreia
 *
 */
public class Main extends MapActivity {
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, Main.class.getSimpleName() + ": onCreate");

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

		// Check if is connected to a network, if not popups a informative toast
		if (!isConnectedToNetwork(this)) {
			toast("Not connected to a network.");
		}

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
		if (wl.isHeld())
			wl.release();
	}

	@Override
	protected boolean isRouteDisplayed() {
		Log.i(TAG, Main.class.getSimpleName() + ": isRouteDisplayed = false");
		return false;
	}

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

	public static boolean isConnectedToNetwork(Context context) {
		try {
			ConnectivityManager nConManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (nConManager != null) {
				NetworkInfo nNetworkinfo = nConManager.getActiveNetworkInfo();
				if (nNetworkinfo != null) {
					return nNetworkinfo.isConnected();
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

}
