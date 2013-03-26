package pt.up.fe.dceg.accu.types.android;

import java.util.LinkedHashMap;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class SystemOverlay extends ItemizedOverlay<CustomOverlayItem> {
	
	LinkedHashMap<Integer, CustomOverlayItem> itemList = new LinkedHashMap<Integer, CustomOverlayItem>();
	
	public SystemOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
		populate(); // call this to avoid a null pointer exception in case of empty list
	}
	
	public void putItem(int id, CustomOverlayItem item)
	{
		itemList.put(id,item);
		populate();
	}
	@Override
	protected CustomOverlayItem createItem(int i) {
		int c = 0;
		for(Integer k: itemList.keySet())
		{
			if(c==i)
				return itemList.get(k);
			else
				c++;
		}
		return null;
	}

	@Override
	public int size() {
		return itemList.size();
	}
	@Override
	public void draw(android.graphics.Canvas canvas,
			MapView mapView,
			boolean shadow) 
	{
			super.draw(canvas, mapView, false);
	}

}
