package pt.lsts.accu.foo;

import pt.lsts.accu.panel.AccuComponent;
import pt.lsts.accu.util.CoordUtil;
import pt.lsts.accu.util.MUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class AccuMapComponent 
extends View
implements AccuComponent
{
	Context context;
	private Bitmap bitmap;
	private float prevX;
	private float prevY;
	private float currentX=0;
	private float currentY=0;
	private float zoom = 3;
	private int screenSizeW;
	private int screenSizeH;
	private double startLat = 41.19;
	private double startLon = -8.71;
	private double currentLat = startLat;
	private double currentLon = startLon;
	private float moveInitX;
	private float moveInitY;
	double bottomLat;
	double bottomLon;
    float scale = 1;
	
//	private Runnable task = new Runnable() 
//	{	
//		@Override
//		public void run() {
//			System.out.println("task running");
//			bitmap = MUtil.requestBitmap(currentLat, currentLon, 
//					bottomLat, bottomLon,
//					screenSizeW, screenSizeH);
//			
//		}
//	};
	
	public AccuMapComponent(Context context) {
		super(context);
		this.context = context;
		System.out.println("sdkj");
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenSizeW = dm.widthPixels;
		screenSizeH = dm.heightPixels;
	}
	
	public void onDraw(Canvas canvas)
	{
		Paint p = new Paint();
		p.setColor(Color.BLUE);
		
		if(bitmap!=null)
			canvas.drawBitmap(bitmap, currentX, currentY, p);
		
	}
	
	@Override 
	public void onStart()
	{
//		bitmap = MUtil.requestBitmap(startLat,startLon,startLat - (zoom*screenSizeH),startLon + (zoom*screenSizeW), screenSizeW, screenSizeH);
		if(bitmap == null)
			System.out.println("bitmap null");
		invalidate();
	} 	
	@Override
	public void onEnd() 
	{
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(screenSizeW, screenSizeH);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			prevX = event.getX();
			prevY = event.getY();
			moveInitX = prevX;
			moveInitY = prevY;
		}
		
		if(event.getAction()==MotionEvent.ACTION_MOVE)
		{
			currentX += (event.getX()-prevX);
			currentY += (event.getY()-prevY);

			prevX = event.getX();
			prevY = event.getY();
			invalidate();
		}
		if(event.getAction()==MotionEvent.ACTION_UP)
		{			
			double topCoords[] = CoordUtil.latLonAddNE2(currentLat, currentLon, (event.getY() - moveInitY) * zoom, -(event.getX() - moveInitX) * zoom);
			
			currentLat = topCoords[0];
			currentLon = topCoords[1];
			
			double bottomCoords[] = CoordUtil.latLonAddNE2(currentLat, currentLon, -(screenSizeH*zoom), screenSizeW*zoom);
			bottomLat = bottomCoords[0];
			bottomLon = bottomCoords[1];
			
			System.out.println(currentLat + " " + currentLon + " " + bottomCoords[0] + " " + bottomCoords[1]);
			bitmap = MUtil.requestBitmap(currentLat, currentLon, 
					bottomCoords[0], bottomCoords[1],
					screenSizeW, screenSizeH);
			
			currentX = 0;
			currentY = 0;
			invalidate();
		}
		if(event.getAction()==MotionEvent.ACTION_POINTER_DOWN)
		{
			scale = 2;
			invalidate();
		}
		return true;
	}
}
	
