package pt.lsts.accu;

import java.util.List;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.types.android.SystemOverlay;
import pt.lsts.imc.IMCDefinition;
import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

@AccuAction(name = "Map(Google Maps)" )
public class MapPanel extends AccuBasePanel
//implements IMCSubscriber 
{
	public static final String[] SUBSCRIBED_MSGS = {"EstimatedState","Map", "PiccoloWaypoint",
        "PiccoloWaypointDeleted", "PiccoloTrackingState", "PiccoloControlConfiguration"};
	
	public static final boolean DEBUG = true;
	public static final String TAG = "Map";
	MapView map;
	Drawable marker;
	List<Overlay> mapOverlays;
	SystemOverlay sysOverlay;
	IMCManager imm;
	
	public MapPanel(Context context) {
		super(context);
//        AccuMap map = (AccuMap) getLayout().findViewById(R.id.map);
//        map.setBuiltInZoomControls(true);
//        map.setSatellite(true);
//        
//        mapOverlays = map.getOverlays();
//        marker = getContext().getResources().getDrawable(R.drawable.marker1);
//        
//        sysOverlay = new SystemOverlay(marker);
//        mapOverlays.add(sysOverlay);
        imm = Accu.getInstance().getIMCManager();
	}

	@Override
	public View buildLayout() {
		return inflateFromResource(R.layout.map);
	}

	@Override
	public int getIcon() {
		return R.drawable.map_icon_1;
	}

	@Override
	public void onStart() 
	{
		Log.i(TAG,"Starting with "+componentList.size()+"components");
//		imm.addSubscriber(this, SUBSCRIBED_MSGS);
	}

	@Override
	public void onStop() {
//		 imm.removeSubscriberAll(this);
	}
//	public void onReceive(IMCMessage msg) 
//	{
//		if(msg.getAbbrevName().equalsIgnoreCase("EstimatedState"))
//		{
//			Log.i(TAG,"Receiving...");
//			Sys sys = ACCU.getInstance().getSystemList().findSysById((Integer)msg.getHeaderValue("src"));
//			
//			double res[] = CoordUtil.getAbsoluteLatLonDepthFromMsg(msg);
//			res[0]*=1000000.0;
//			res[1]*=1000000.0;
//			GeoPoint point = new GeoPoint((int)res[0],(int)res[1]);
//			CustomOverlayItem coi = new CustomOverlayItem(point,"cenas1","cenas2",getContext().getResources().getDrawable(R.drawable.marker1));
//			coi.marker.rotate((float)Math.toDegrees(msg.getDouble("psi")));
//					
//			sysOverlay.putItem(sys.getId(),coi);
//			map.invalidate();
//		}
//	}
	
	@Override
	public void prepareMenu(Menu menu)
	{
		menu.clear();
		menu.add("Download");
		menu.add("CallOuts On/Off");
	}
	
	@Override 
	public void menuHandler(MenuItem item)
	{
		if(item.getTitle().equals("Download"))
		{
			Log.e(TAG,"Requesting Piccolo Waypoint");
			imm.sendToActiveSys(IMCDefinition.getInstance().create("ListPiccoloWaypoints"));
		}
		if(item.getTitle().equals("CallOuts On/Off"))
		{
			Accu.getInstance().getCallOut().toggle();
		}
	}
}
