package pt.lsts.accu.components.controlpad;

import pt.lsts.accu.components.interfaces.JoystickPadChangeListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class JoystickPad extends View {

    public static final String TAG = "JoystickPad";

    Bitmap bitmap;
    Canvas dc;
    Paint p;

    float pointX;
    float pointY;
    int axisX;
    int axisY;
    String axisXAction = null;
    String axisYAction = null;
    float density;
    Context context;
    int padViewSize, padCenterX, padCenterY;
    float padRadius;

    float prevX = 0;
    float prevY = 0;

    public String getAxisXAction() {
	return axisXAction;
    }

    public String getAxisYAction() {
	return axisYAction;
    }

    JoystickPadChangeListener listener;

    // All PAD dimensions should de density independent pixels (assuming a
    // 240x320 screen)
    int PAD_VIEW_SIZE = 250;
    int PAD_CENTER_X = 125;
    int PAD_CENTER_Y = 125;
    float PAD_RADIUS = 125; // This must be float to avoid division by zero in
    // translateTo methods

    int SCALE = 254;

    public JoystickPad(Context context) {
	super(context);
	this.context = context;
	initialize();
    }

    public JoystickPad(Context context, AttributeSet attrs) {
	super(context, attrs);
	axisXAction = attrs.getAttributeValue(null, "axisXaction");
	axisYAction = attrs.getAttributeValue(null, "axisYaction");
	this.context = context;
	initialize();
    }

    @Override
    public void onMeasure(int w, int h) {
	setMeasuredDimension(padViewSize, padViewSize);
    }

    private void initialize() {
	// bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
	// dc = new Canvas(bitmap);
	// p = new Paint();
	//
	// p.setColor(Color.GREEN);
	// p.setStrokeWidth(5);
	p = new Paint();
	p.setFlags(Paint.ANTI_ALIAS_FLAG);
	axisX = 0;
	axisY = 0;

	DisplayMetrics metric = new DisplayMetrics();
	((Activity) context).getWindowManager().getDefaultDisplay()
	.getMetrics(metric);
	density = metric.density;

	padRadius = PAD_RADIUS * density;
	padViewSize = (int) (PAD_VIEW_SIZE * density);
	padCenterX = (int) (PAD_CENTER_X * density);
	padCenterY = (int) (PAD_CENTER_Y * density);

    }

    private float[] translateToAxis(float x, float y) {
	float res[] = new float[2];
	res[0] = (-(SCALE / 2)) + ((SCALE / (padRadius * 2)) * (x - getLeft()));
	res[1] = -((-(SCALE / 2)) + ((SCALE / (padRadius * 2)) * (y - getTop()))); // INVERT
	// Y
	// AXIS
	return res;
    }

    private float[] translateToView(float x, float y) {
	float res[] = new float[2];
	res[0] = (x + (SCALE / 2)) / (SCALE / (padRadius * 2));
	res[1] = (-y + (SCALE / 2)) / (SCALE / (padRadius * 2)); // INVERT Y
	// AXIS
	return res;
    }

    public void setOnPadChangeListener(JoystickPadChangeListener l) {
	listener = l;
    }

    @Override
    public void onDraw(Canvas canvas) {

	float x = 0;
	float y = 0;

	p.setColor(Color.parseColor("#777777"));
	p.setStrokeWidth(5);
	canvas.drawCircle(padCenterX, padCenterY, padRadius-2, p);

	float coord[] = translateToView(axisX, axisY);

	boolean inCircle = Math.pow(coord[0] - padCenterX, 2)
		+ Math.pow(coord[1] - padCenterY, 2) <= Math.pow(padRadius, 2);

	if (inCircle) {
	    x = coord[0];
	    y = coord[1];
	    prevX = x;
	    prevY = y;
	} else {
	    x = prevX;
	    y = prevY;
	}
	p.setColor(Color.parseColor("#FF0000"));
	canvas.drawLine(padCenterX, padCenterY, x, y, p);
	p.setColor(Color.parseColor("#FACC12"));
	canvas.drawCircle(x, y, 35 * density, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	// System.out.println("pad: "+event.getX()+ " "+event.getY());
	if (event.getAction() == MotionEvent.ACTION_DOWN) {

	}
	if (event.getAction() == MotionEvent.ACTION_MOVE) {
	    int pos[] = new int[2];
	    getLocationOnScreen(pos);
	    float res[] = translateToAxis(event.getX() - pos[0], event.getY()
		    - pos[1]);
	    axisX = (int) res[0];
	    axisY = (int) res[1];
	    //	    System.out.println("x : " + (event.getX() - getLeft()) + " y: "
	    //		    + (event.getY() - getTop()) + "top: " + getTop() + "left: "
	    //		    + getLeft());
	    //	    System.out.println("rx : " + event.getRawX() + " ry: "
	    //		    + event.getRawY());
	    Log.i(TAG, "x : " + (event.getX() - getLeft()) + " y: " + (event.getY() - getTop()) + "top: " + getTop() + "left: " + getLeft());
	}
	if (event.getAction() == MotionEvent.ACTION_UP) {
	    axisX = 0;
	    axisY = 0;
	}
	if (listener != null) {
	    listener.onJoystickPadChange(this, axisX, axisY);
	}
	invalidate();
	return true;
    }
}