package pt.up.fe.dceg.accu;

import java.util.ArrayList;

import pt.up.fe.dceg.accu.components.Finder;
import pt.up.fe.dceg.accu.components.Finder.OnFinderChangeListener;
import pt.up.fe.dceg.accu.components.beaconconfig.Beacon;
import pt.up.fe.dceg.accu.msg.IMCSubscriber;
import pt.up.fe.dceg.accu.panel.AccuAction;
import pt.up.fe.dceg.accu.panel.AccuBasePanel;
import pt.up.fe.dceg.accu.state.Accu;
import pt.up.fe.dceg.accu.types.Sys;
import pt.up.fe.dceg.accu.types.android.CoordDialog;
import pt.up.fe.dceg.accu.util.CoordUtil;
import pt.up.fe.dceg.accu.util.MUtil;
import pt.up.fe.dceg.neptus.imc.IMCMessage;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

@AccuAction(name = "Finder", icon=R.drawable.finder_icon_1)
public class FinderPanel 
extends AccuBasePanel
implements IMCSubscriber,OnFinderChangeListener 
{
	public static final String[] SUBSCRIBED_MSGS = {"EstimatedState","LblConfig","LblBeacon"};
	public static final boolean DEBUG = false;
	public static final String TAG = "Finder";
		
	public Sys targetSys;
	double myLat, myLon;	
	double targetLat, targetLon;
	protected String targetName = " ";
	
	TextView tvDist;
	TextView tvInfo;
	Finder finder;
	
	public FinderPanel(Context context) 
	{
		super(context);
	
		View layout = getLayout();
		
		targetLat = 0.0;
		targetLon = 0.0;
						
		tvDist=(TextView)layout.findViewById(R.id.lblDistance);
		tvInfo=(TextView)layout.findViewById(R.id.finder_info);
		
		finder = (Finder) layout.findViewById(R.id.ll_finder);
		finder.setOnFinderChangeListener(this);
		Log.i("Log","Creating compass!");		
	}

	@Override
	public void onStart() {
		Accu.getInstance().getIMCManager().addSubscriber(this, SUBSCRIBED_MSGS);
		targetSys = Accu.getInstance().getActiveSys();
		if(targetSys == null)
		{
			targetName = "No target";
		}
		else
		{
			setTarget(0.0f,0.0f,targetSys.getName());
		}
	}
	
	@Override
	public void onStop() {
		Accu.getInstance().getIMCManager().removeSubscriberToAll(this);
	}
	
	@Override
	public View buildLayout() {
		return inflateFromResource(R.layout.finder_panel_layout);
	}
	
	@Override
	public void onReceive(IMCMessage msg) 
	{
		
		if(targetSys != null)
		{
			if((Integer)msg.getHeaderValue("src")==targetSys.getId())
			{
				double lat = (Math.toDegrees(msg.getDouble("lat")));
				double lon = (Math.toDegrees(msg.getDouble("lon")));

				Log.i(TAG,"latitude: " + lat + " longitude: " + lon);
				double res[] = CoordUtil.getAbsoluteLatLonDepth(lat, lon, 0, msg.getDouble("x"), msg.getDouble("y"), 0);

				targetLat = res[0];
				targetLon = res[1];
				setTarget(targetLat, targetLon);
			}
		}
	}
	
	@Override
	public void prepareMenu(Menu menu)
	{
		menu.clear();
		menu.add("Select Target");
		menu.add("Set GPS Target");
	}
	
	@Override 
	public void menuHandler(MenuItem item)
	{
		if(item.getTitle().equals("Select Target"))
		{
			showSelectSystemDialog();
		}
		if(item.getTitle().equals("Set GPS Target"))
		{
			showSelectCoordDialog();
		}
	}
	
	void showSelectCoordDialog() 
	{
		final CoordDialog cd = new CoordDialog(getContext(),this);
		
		cd.show();
		cd.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
//				setTarget(cd.getLat(),cd.getLon(),"GPS Point");
				// Set targetsys to null to avoid received messages overriding lat/lon info
//				targetSys = null; 
			}
		});
	}

	void showSelectSystemDialog()
	{
		// Build System List
		// Get a CCU list and Beacon List
		final ArrayList<String> slist =  Accu.getInstance().getSystemList().getNameList();
		final ArrayList<String> blist =  Accu.getInstance().getLblBeaconList().getNameList();
		final ArrayList<String> list =   new ArrayList<String>();
		
		list.addAll(slist); // Add system list to overall list
		list.addAll(blist); // Add beacon list to overall list
		
		final String[] systems = new String[list.size()];
		list.toArray(systems);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); 
		
		builder.setTitle("Select target: ");
		builder.setItems(systems, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				String name = systems[which];
				if(slist.contains(name))
				{
					targetSys = Accu.getInstance().getSystemList().findSysByName(name);
					
					// Reset this for the case of selected system doesn't have this information
					//FIXME At least is zeroed, but...
					setTarget(0.0f,0.0f,targetSys.getName());
				}
				if(blist.contains(name))
				{
					Beacon b = Accu.getInstance().getLblBeaconList().getBeaconByName(name);
					setTarget(b.getLat(),b.getLon(),b.getName());
				}
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	public void setTarget(double tLat, double tLon, String name)
	{
		targetLat = tLat;
		targetLon = tLon;
		targetName = name;
		finder.setTarget(tLat, tLon);
	}
	public void setTarget(double tLat, double tLon)
	{
		targetLat = tLat;
		targetLon = tLon;
		finder.setTarget(tLat, tLon);
	}
	@Override
	public void onFinderChange(double bearingDiff, double distance,
			double accuracy) {
		tvInfo.setText("Target: " + targetName + " Lat: "
				+ CoordUtil.degreesToDMS(targetLat, true) + " Lon: "
				+ CoordUtil.degreesToDMS(targetLon, false));
		tvDist.setText("Distance: "
				+ MUtil.roundn(/* convert to meters */distance*1000,2)+ " meters Accu: "+accuracy);		
	}
}
