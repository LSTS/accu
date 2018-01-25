package pt.lsts.accu.components.map;

import java.util.ArrayList;

import pt.lsts.imc.IMCMessage;
import android.graphics.Color;

import com.google.android.maps.GeoPoint;

public class MapFeature 
{
	public int alpha = 50;
	public String id;
	public String type;
	public ArrayList<GeoPoint> pointList = new ArrayList<GeoPoint>();
	public int color;
	public MapFeature(IMCMessage feature)
	{
		id = feature.getString("id");
		type = feature.getString("feature_type");

		color = Color.argb(alpha,feature.getInteger("rgb_red"), feature.getInteger("rgb_green"), feature.getInteger("rgb_blue"));
//		color = Color.rgb(feature.getInteger("rgb_red"), feature.getInteger("rgb_green"), feature.getInteger("rgb_blue"));
		
		
		if(type.equalsIgnoreCase("POI")||type.equalsIgnoreCase("TRANSPONDER")||
				type.equalsIgnoreCase("STARTLOC")||type.equalsIgnoreCase("HOMEREF"))
		{
			int lat = (int)(Math.toDegrees(feature.getMessage("feature").getFloat("lat"))*1000000);
			int lon = (int)(Math.toDegrees(feature.getMessage("feature").getFloat("lon"))*1000000);
			pointList.add(new GeoPoint(lat,lon));
		}
		else
		{
			for (IMCMessage pointer = feature.getMessage("feature"); pointer != null; pointer = pointer
					.getMessage("next")) {
				IMCMessage m = pointer.getMessage("msg");
				int lat = (int)( Math.toDegrees(m.getFloat("lat")) * 1000000);
				int lon = (int)( Math.toDegrees(m.getFloat("lon")) * 1000000);
				pointList.add(new GeoPoint(lat, lon));
			}
		}
	}
}
