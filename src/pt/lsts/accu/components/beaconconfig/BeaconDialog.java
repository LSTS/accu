package pt.lsts.accu.components.beaconconfig;

import pt.lsts.accu.components.Finder;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.GPSManager;
import pt.lsts.accu.state.LocationChangeListener;
import pt.lsts.accu.util.CoordUtil;
import pt.lsts.accu.util.MUtil;
import pt.up.fe.dceg.accu.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class BeaconDialog 
extends Dialog 
implements android.view.View.OnClickListener, LocationChangeListener, OnEditorActionListener, Finder.OnFinderChangeListener 
{
	Button btnOk;
	Button btnSet;
	Beacon beacon;
	TextView lat,lon;
	EditText depth;
	private double currentLat;
	private double currentLon;
	
	Finder finder;
	
	private GPSManager gps = Accu.getInstance().getGpsManager();
	
	public BeaconDialog(Context context,Beacon beacon) {
		super(context);
		this.beacon = beacon;
		setContentView(R.layout.beacon_dialog);
		setTitle("Edit "+ beacon.getName()+" Beacon");
	
		btnOk = (Button) findViewById(R.id.beacon_dialog_btnOk);
		btnSet = (Button)findViewById(R.id.beacon_dialog_btnSet);
		
		lat = (TextView) findViewById(R.id.beacon_dialog_valLat);
		lon = (TextView) findViewById(R.id.beacon_dialog_valLon);
		depth = (EditText) findViewById(R.id.beacon_dialog_valDepth);
		
		finder = (Finder) findViewById(R.id.beacon_dialog_find);
		finder.onStart();
		finder.setTarget(beacon.getLat(), beacon.getLon());
		finder.setOnFinderChangeListener(this);
		
		btnOk.setOnClickListener(this);
		btnSet.setOnClickListener(this);
		depth.setOnEditorActionListener(this);
		gps.addListener(this);
		drawBeaconData();
	}

	protected void drawBeaconData()
	{
		lat.setText(beacon.getLatDMS()+"");
		lon.setText(beacon.getLonDMS()+"");
		depth.setText(beacon.getDepth()+"");
	}
	
	@Override
	public void onClick(View view) 
	{
		if(view == btnOk)
		{
			// Close Dialog
			dismiss();
			finder.onEnd();
			gps.removeListener(this);
		}
		if(view == btnSet)
		{
			beacon.setLat(currentLat);
			beacon.setLon(currentLon);
			drawBeaconData();
		}
	}
	@Override
	public void onLocationChange(Location location) 
	{
		currentLat = location.getLatitude();
		currentLon = location.getLongitude();
		updateCurrentLatLon();
	}
	
	public void updateCurrentLatLon()
	{
		TextView t = (TextView)findViewById(R.id.beacon_dialog_lblCurrentGpsLat);
		t.setTextColor(Color.BLUE);
		t.setText("(" + CoordUtil.degreesToDMS(currentLat,true)+")");
		
		t = (TextView)findViewById(R.id.beacon_dialog_lblCurrentGpsLon);
		t.setTextColor(Color.BLUE);
		t.setText("(" + CoordUtil.degreesToDMS(currentLon,false)+")");
	}

	@Override
	public boolean onEditorAction(TextView view, int arg1, KeyEvent arg2) {
		beacon.setDepth(Double.valueOf(view.getText()+""));
		return false;
	}

	@Override
	public void onFinderChange(double bearingDiff, double distance, double accuracy) 
	{
		TextView tv = (TextView) findViewById(R.id.beacon_dialog_lblDist);
		tv.setText("Dist: " + MUtil.roundn(distance*1000, 2)+"m"); // Convert to meters and round to 2 decimal places
	}
}
