package pt.lsts.accu.components.map;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class FeatureOverlay extends Overlay 
{
	ArrayList<MapFeature> featureList; 
	
	public FeatureOverlay(ArrayList<MapFeature> features)
	{
		featureList = features;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapv, boolean shadow)
	{
		Projection proj = mapv.getProjection();
		
		for(MapFeature feature: featureList)
		{
			Paint paint = new Paint();
			Path path = new Path();
			
			
			//Setup Paint
			paint.setColor(feature.color);
			
			if(feature.type.equalsIgnoreCase("FILLEDPOLY")||
					feature.type.equalsIgnoreCase("CONTOUREDPOLY")||
					feature.type.equalsIgnoreCase("LINE"))
			{	
				paint.setStyle(Paint.Style.FILL);
				
				// generate Path oject 
				Point p = new Point();
				Log.i("overlay","-------------");
				for(GeoPoint gp: feature.pointList)
				{
					proj.toPixels(gp, p);
					Log.i("overlay",gp.getLatitudeE6() + " " + gp.getLongitudeE6());
					Log.i("overlay",p.x + " " + p.y);
					if(gp == feature.pointList.get(0)) // If is is the first point
						path.moveTo(p.x,p.y);
					else
						path.lineTo(p.x, p.y);
				}
				
				// Draw the path object with paint
				canvas.drawPath(path,paint);
			}
			else
			{
				paint.setStyle(Paint.Style.FILL);
				paint.setStrokeWidth(10);
				Point p = new Point();
				for(GeoPoint gp: feature.pointList)
				{
					proj.toPixels(gp, p);
					canvas.drawPoint(p.x, p.y, paint);
				}
				paint.setColor(Color.WHITE);
				canvas.drawText(feature.id, p.x-5, p.y-15, paint);
			}
		}
	}
}
