package pt.up.fe.dceg.accu.components.controlpad;

import pt.up.fe.dceg.accu.components.interfaces.PadButtonListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ToggleButton;

public class PadToggleButton extends ToggleButton {
	String actionOn = "";
	String actionOff = "";
	PadButtonListener listener;
	
	public PadToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		actionOn = attrs.getAttributeValue(null, "actionOn");
		actionOff = attrs.getAttributeValue(null, "actionOff");
	}
	public String getAction()
	{
		return (isChecked() ? actionOn : actionOff);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		
		setPressed(true);
		
		// Send action just on 'DOWN' event
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			if(listener!=null)
				listener.onPadButtonTouch(this, event);
		
			setPressed(true);
		}	
		
		if(event.getAction()==MotionEvent.ACTION_UP)
			setPressed(false);
		return true;
	}
	public void setPadButtonListener(PadButtonListener listener)
	{
		this.listener = listener;
	}
}
