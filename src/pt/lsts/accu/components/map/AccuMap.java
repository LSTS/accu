package pt.lsts.accu.components.map;

import java.util.List;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.accu.panel.AccuComponent;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.types.android.CustomOverlayItem;
import pt.lsts.accu.types.android.SystemOverlay;
import pt.lsts.accu.util.CoordUtil;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class AccuMap
extends MapView
implements IMCSubscriber, AccuComponent
{

	public static final String[] SUBSCRIBED_MSGS = {"Map","EstimatedState", "PiccoloWaypoint"};
	public static final String TAG = "Map";
	
	List<Overlay> mapOverlays;
	IMCManager imm;
	Drawable marker;

	SystemOverlay sysOverlay;
	FeatureOverlay featureOverlay;
	PiccoloOverlay piccoloOverlay;
	
	MapParser parser;
	float longX;
	float longY;
	
	Projection proj;
	GestureDetector gd;
	
	public AccuMap(Context context, AttributeSet att) 
	{
		super(context, att);
		
		imm = Accu.getInstance().getIMCManager();
		proj = getProjection();
		gd = new GestureDetector(getGestureListener());
		setBuiltInZoomControls(true);
		setSatellite(true);
		
		mapOverlays = getOverlays();
		marker = getContext().getResources().getDrawable(R.drawable.marker1);

//		mapOverlays.add(new S57Overlay());
		
		sysOverlay = new SystemOverlay(marker);
		piccoloOverlay = new PiccoloOverlay();
		
		mapOverlays.add(sysOverlay);
		mapOverlays.add(piccoloOverlay);
	}

	
	@Override
	public void onReceive(IMCMessage msg) {
		if(msg.getAbbrev().equalsIgnoreCase("EstimatedState"))
		{
			Sys sys = Accu.getInstance().getSystemList().findSysById((Integer)msg.getHeaderValue("src"));
			
			double res[] = CoordUtil.getAbsoluteLatLonDepthFromMsg(msg);
			res[0]*=1000000.0;
			res[1]*=1000000.0;
			GeoPoint point = new GeoPoint((int)res[0],(int)res[1]);
			CustomOverlayItem coi = new CustomOverlayItem(point,"cenas1","cenas2",getContext().getResources().getDrawable(R.drawable.marker1));
			coi.marker.rotate((float)Math.toDegrees(msg.getDouble("psi")));
					
			sysOverlay.putItem(sys.getId(),coi);
			invalidate();
		}
		else if(msg.getAbbrev().equalsIgnoreCase("Map"))
		{
			parser = new MapParser(msg);
			
			if(featureOverlay!=null)
				mapOverlays.remove(featureOverlay);
			
			featureOverlay = new FeatureOverlay(parser.getFeatureList());
			mapOverlays.add(featureOverlay);
			invalidate();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		gd.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}


	@Override
	public void onStart() {
		Log.i(TAG +"on start","starting");
		imm.addSubscriber(this, SUBSCRIBED_MSGS);
	}
	
	@Override
	public void onEnd() {
		imm.removeSubscriberToAll(this);
		imm.removeSubscriberToAll(piccoloOverlay);
	}

	OnGestureListener getGestureListener()
	{
		return new SimpleOnGestureListener() 
		{
			@Override
			public void onLongPress(MotionEvent e) {
				GeoPoint gp = proj.fromPixels((int)e.getX(), (int)e.getY());
				
				IMCMessage gotoMsg = IMCDefinition.getInstance().create("Goto", 
						"lat",Math.toRadians(gp.getLatitudeE6()/1000000f),
						"lon",Math.toRadians(gp.getLongitudeE6()/1000000f),
						"depth",0,
						"speed",1000,"speed_units","RPM");
				
				
				IMCMessage maneuverMsg = IMCDefinition.getInstance().create("ManeuverSpecification")
					.setValue("maneuver_id","goto")
					.setValue("data",gotoMsg)
					.setValue("num_transitions",1);
				
				IMCMessage planSpec = IMCDefinition.getInstance().create("PlanSpecification", "plan_id","simple_goto","description","simple goto plan","start_man_id","goto","num_maneuvers",1,"maneuvers",maneuverMsg);
				
				Accu.getInstance().getIMCManager().sendToActiveSys("PlanControl","type",0,"op","LOAD","plan_id","simple_goto","arg",planSpec);
				Accu.getInstance().getIMCManager().sendToActiveSys("PlanControl","type",0,"op","START","plan_id","simple_goto");
			}
		};
	}
	
}
