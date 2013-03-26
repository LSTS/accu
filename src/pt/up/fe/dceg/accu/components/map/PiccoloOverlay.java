package pt.up.fe.dceg.accu.components.map;

import java.util.LinkedHashMap;

import pt.up.fe.dceg.accu.msg.IMCSubscriber;
import pt.up.fe.dceg.accu.state.Accu;
import pt.up.fe.dceg.neptus.imc.IMCMessage;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PiccoloOverlay extends Overlay implements IMCSubscriber
{
	LinkedHashMap<Integer, PCCWaypoint> wptList = new LinkedHashMap<Integer, PCCWaypoint>();
	private static final float minWaypointRadius = 5;
	
	int fromIndex;
	int toIndex;
	
	public PiccoloOverlay()
	{
		Accu.getInstance().getIMCManager().addSubscriber(this, "PiccoloWaypoint");
		Accu.getInstance().getIMCManager().addSubscriber(this, "PiccoloTrackingState");
	}
	
	@Override
	public void onReceive(IMCMessage msg) 
	{
		if(msg.getAbbrev().equals("PiccoloWaypoint"))
		{
			double lat = msg.getDouble("lat");
			double lon = msg.getDouble("lon");
			wptList.put(msg.getInteger("index"), new PCCWaypoint(lat,lon,msg.getInteger("index"),msg.getInteger("next"),(float)msg.getFloat("lradius")));
		}
		else
		{
			// Piccolo Tracking State
			toIndex = msg.getInteger("to");
			fromIndex = msg.getInteger("from");
		}
//		for(PCCWaypoint wpt : wptList.values())
//		{
//			if(wpt.indexNext==100)
//				Log.e("PCC", "AFINAL HA 100");
//			Log.v("PCC", wpt.lat + " " + wpt.lon + " " + wpt.indexNext);
//		}
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapv, boolean shadow)
	{
		Projection proj = mapv.getProjection();
		float radius = proj.metersToEquatorPixels(10);
		if(radius < minWaypointRadius) radius = minWaypointRadius ;
		Paint paint = new Paint();

		for(PCCWaypoint wpt: wptList.values())
		{	
			Point p1 = new Point();
			Point p2 = new Point();
			PCCWaypoint next = wptList.get(wpt.indexNext);
			if(next == null) return;
			proj.toPixels(new GeoPoint((int)wpt.lat, (int)wpt.lon), p1);
			proj.toPixels(new GeoPoint((int)next.lat, (int)next.lon), p2);
			//Setup Paint
			
			paint.setColor(Color.CYAN);
		
			if(wpt.indexNext == toIndex && wpt.index == fromIndex)
				paint.setColor(Color.YELLOW);
			paint.setTextSize(16);
			canvas.drawText(wpt.index+"", p1.x,p1.y-8, paint);
			paint.setStyle(Paint.Style.STROKE);
			
			float[] intervals = {5,5};
			paint.setPathEffect(new DashPathEffect(intervals, 1));
			float lradius  = proj.metersToEquatorPixels(wpt.lradius);
			
			
			canvas.drawCircle(p1.x, p1.y, lradius, paint);
			
			paint.setPathEffect(new PathEffect());
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(3);
			
			canvas.drawCircle(p1.x, p1.y, radius, paint);
			canvas.drawCircle(p2.x, p2.y, radius, paint);
			canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
		}
	}
}
