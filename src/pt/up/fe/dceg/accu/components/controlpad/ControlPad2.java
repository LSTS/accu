package pt.up.fe.dceg.accu.components.controlpad;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import pt.up.fe.dceg.accu.components.interfaces.JoystickPadChangeListener;
import pt.up.fe.dceg.accu.components.interfaces.PadButtonListener;
import pt.up.fe.dceg.accu.components.interfaces.PadEventListener;
import pt.up.fe.dceg.accu.components.interfaces.PadStateListener;
import pt.up.fe.dceg.accu.util.Container;
import android.content.Context;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class ControlPad2 extends Container
implements JoystickPadChangeListener, PadButtonListener
{
	public static final String TAG = "ControlPad2";
	Context context;
	View view;
	public ArrayList<View> viewList; // List of pad components
	LinkedHashMap<View,String> actions = new LinkedHashMap<View,String>();
	
	PadStateListener listener;
	PadEventListener listener2;
	
	ArrayList<PadTextField> textFields = new ArrayList<PadTextField>();
	
	int layoutID;
	
	public ControlPad2(Context context) {
		super(context);
		initialize(context);
	}
	public ControlPad2(Context context, AttributeSet attrs) {
		super(context, attrs);
		layoutID = attrs.getAttributeResourceValue(null, "pad_layout", 0);
		initialize(context);
	}

	void initialize(Context context)
	{
		// Do un-setup of previous pad, if available... // FIXME jqcorreia
		if(viewList != null)
		{
			for(View v : viewList)
			{
				if(v instanceof SensorPad)
				{
					((SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE)).unregisterListener((SensorPad)v);
				}
			}
		}
		removeAllViews(); // Remove previous graphical pad components
			
		this.context = context;
		LayoutInflater.from(context).inflate(layoutID,this,true);

		viewList = flattenLayout(this);

		setupPad();
	}
	
	/**
	 * Registers Layout components as pad components in the padState and the listeners too
	 */
	private void setupPad()
	{
		for(View v : viewList)
		{
			if(v instanceof JoystickPad)
			{
				((JoystickPad)v).setOnPadChangeListener(this);
			}
			else if(v instanceof PadButton)
			{
				((PadButton)v).setPadButtonListener(this);
			}
			else if( v instanceof PadToggleButton)
			{
				((PadToggleButton)v).setPadButtonListener(this);
			}
			else if(v instanceof PadTextField)
			{
				textFields.add((PadTextField)v);
			}
		}
	}
	public void setPadLayout(int layoutId)
	{
		this.layoutID = layoutId;
		initialize(context); // Re-run initialize for the new pad layout
	}
	
	/**
	 * Recursive function that flattens a root layout and returns all the child Views independent of
	 * layout level.
	 * @param viewgroup The root viewroup to be analized
	 * @return an ArrayList containing all the views below the original ViewGroup
	 */
	ArrayList<View> flattenLayout(ViewGroup viewgroup)
	{
		ArrayList<View> viewList = new ArrayList<View>();
		
		for(int i = 0; i < viewgroup.getChildCount();i++)
		{
			View v = viewgroup.getChildAt(i);
			if(v instanceof ViewGroup)
			{
				viewList.addAll(flattenLayout((ViewGroup)v));
			}
			else if(v instanceof View)
			{
				viewList.add((View)v);
			}
		}		
		return viewList;
	}
	
	public void setPadEventListener(PadEventListener l)
	{
		listener2 = l;
	}
	void notifyEventListener(PadEvent event)
	{
		if(listener2!=null)
			listener2.onPadEvent(event);
	}
	
	@Override
	public void onJoystickPadChange(View v, int axisX, int axisY) 
	{
		PadEvent padEvent = new PadEvent(v,(float)axisX,(float)axisY,((JoystickPad)v).getAxisXAction(),((JoystickPad)v).getAxisYAction(),0);
		notifyEventListener(padEvent);
	}
	
	@Override
	public void onPadButtonTouch(View v, MotionEvent event) {
		PadEvent padEvent=null;
		
		// For now the difference between ToggleButton and Button is hardcoded
		if(v instanceof PadToggleButton)
		{
			
			if(event.getAction()==MotionEvent.ACTION_DOWN)
			{
				padEvent = new PadEvent(v,1f,-1f,((PadToggleButton)v).getAction(),null,-1);
				notifyEventListener(padEvent);
			}
			if(event.getAction()==MotionEvent.ACTION_UP)
			{
				padEvent = new PadEvent(v,0f,-1f,((PadToggleButton)v).getAction(),null,-1);
				notifyEventListener(padEvent);
			}
		}
		else
		{
			if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE)
			{
				padEvent = new PadEvent(v,1f,-1f,((PadButton)v).getAction(),null,-1);
			}
			if(event.getAction()==MotionEvent.ACTION_UP)
			{
				padEvent = new PadEvent(v,0f,-1f,((PadButton)v).getAction(),null,-1);
			}
			notifyEventListener(padEvent);
		}
	}
	
	public ArrayList<PadTextField> getTextFields()
	{
		return textFields;
	}
}

