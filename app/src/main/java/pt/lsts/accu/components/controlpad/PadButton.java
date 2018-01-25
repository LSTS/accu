package pt.lsts.accu.components.controlpad;

import pt.lsts.accu.components.interfaces.PadButtonListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class PadButton extends Button 
{
	boolean toggle;
	String action="";
	
	PadButtonListener listener;
	public PadButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		action = attrs.getAttributeValue(null, "action");
		toggle = attrs.getAttributeBooleanValue("", "toggle", false);
		setTextSize(20);
		setWidth(200);
	}
	public boolean isToggle() {
		return toggle;
	}
	public String getAction() {
		return action;
	}
	public void setPadButtonListener(PadButtonListener listener)
	{
		this.listener = listener;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if(listener!=null)
			listener.onPadButtonTouch(this, event);
		
		setPressed(true);
		if(event.getAction()==MotionEvent.ACTION_DOWN)
			setPressed(true);
			
		if(event.getAction()==MotionEvent.ACTION_UP)
			setPressed(false);
		return true;
	}	
}
