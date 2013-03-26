package pt.up.fe.dceg.accu.components.controlpad;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import pt.up.fe.dceg.accu.R;
import pt.up.fe.dceg.accu.components.interfaces.JoystickPadChangeListener;
import pt.up.fe.dceg.accu.components.interfaces.PadStateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ControlPad extends LinearLayout
implements JoystickPadChangeListener, OnTouchListener
{
	Context context;
	View view;
	LinkedHashMap<String,Integer> padState = new LinkedHashMap<String,Integer>();
	ArrayList<View> viewList = new ArrayList<View>(); // List of pad components
	LinkedHashMap<String,String> actions = new LinkedHashMap<String,String>();
	PadStateListener listener;
	
	public ControlPad(Context context) {
		super(context);
		initialize(context);
	}
	public ControlPad(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	void initialize(Context context)
	{
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.command_pad_layout,this,true);

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
				padState.put(v.getTag()+"axisX", 0);
				if(((JoystickPad)v).getAxisXAction()!=null) // To safeguard the components with no action defined
				{
					actions.put(((JoystickPad)v).getAxisXAction(),v.getTag()+"axisX");
				}
				
				padState.put(v.getTag()+"axisY", 0);
				if(((JoystickPad)v).getAxisYAction()!=null) // To safeguard the components with no action defined
				{
					actions.put(((JoystickPad)v).getAxisYAction(),v.getTag()+"axisY");
				}	
	
				((JoystickPad)v).setOnPadChangeListener(this);
			}
			else if(v instanceof PadButton)
			{
				padState.put(v.getTag()+"", 0);
				if(((PadButton)v).getAction()!=null) // To safeguard the components with no action defined
				{
					actions.put(((PadButton)v).getAction(),v.getTag()+"");
				}
				((PadButton)v).setOnTouchListener(this);
			}
		}
	}

	public LinkedHashMap<String,String> getActions()
	{
		return actions;
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
	
	public void setPadStateListener(PadStateListener l)
	{
		listener = l;
	}
	void notifyListener(String[] updated)
	{
		if(listener!=null)
			listener.onPadStateChange(padState,updated);
	}
	@Override
	public void onJoystickPadChange(View v, int axisX, int axisY) 
	{
		padState.put(v.getTag()+"axisX", axisX);
		padState.put(v.getTag()+"axisY", axisY);
		String[] upd  = {v.getTag()+"axisX",v.getTag()+"axisY"};
		notifyListener(upd);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		String[] upd = { v.getTag()+"" };
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			padState.put(v.getTag()+"", 1);
			
		}
		if(event.getAction()==MotionEvent.ACTION_UP)
		{
			padState.put(v.getTag()+"", 0);
		}
		notifyListener(upd);
		return false;
	}

}

