package pt.lsts.accu.types.android;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CustomOverlayItem extends OverlayItem
{
	public MyDrawable marker;
	
	public CustomOverlayItem(GeoPoint point, String title, String snippet, Drawable marker) {
		super(point, title, snippet);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        //boundCenter(marker);
		this.marker = new MyDrawable(marker);
	}
	
    @Override
    public Drawable getMarker(int stateBitset) {
        Drawable result=(marker);

        setState(result, stateBitset);

        return(result);
    }
}
