package pt.lsts.accu.types.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class MyBitmapDrawable extends BitmapDrawable {

	float degrees = 0.0f;
	
	public void setDegrees(float deg)
	{
		degrees = deg;
	}
	
	public MyBitmapDrawable(Bitmap bitmap)
	{
		super(bitmap);
	}
	public MyBitmapDrawable(Drawable d)
	{
		super(((BitmapDrawable)d).getBitmap());
	}
	@Override
	public void draw(Canvas canvas)
	{
		int save = canvas.save();
		Rect r = getBounds();
		float px=(r.right - r.left)/2;
		float py= (r.bottom - r.top)/2;
		canvas.rotate(degrees,px,py);
		super.draw(canvas);
		canvas.restoreToCount(save);
	}
}
