package pt.lsts.accu.types.android;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Classes that wraps around Drawable that implements a rotation feature that is possible to call from code, 
 * not needing XML declaration like native RotateDrawable.
 * @author jqcorreia
 *
 */

public class MyDrawable extends Drawable
{
	Drawable mDrawable;
	private float mCurrentDegrees = 0.0f; 
	
	public MyDrawable(Drawable drawable)
	{
		mDrawable = drawable;
		mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
	}
	@Override
	public void draw(Canvas canvas) 
	{
		int savecount = canvas.save();
		Rect bound = mDrawable.getBounds();
		float px = (bound.right - bound.left) * 0.5f;
		float py = (bound.bottom - bound.top) * 0.5f;
		canvas.rotate(mCurrentDegrees,px,py);
		mDrawable.draw(canvas);
		canvas.restoreToCount(savecount);
	}

	public void rotate(float degrees)
	{
		mCurrentDegrees = degrees;
	}
	@Override
	public int getOpacity() {
		return mDrawable.getOpacity();
	}

	@Override
	public void setAlpha(int arg0) {
		mDrawable.setAlpha(arg0);
	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
		mDrawable.setColorFilter(arg0);
	}
}
