package pt.up.fe.dceg.accu.components.controlpad;

import android.view.View;

public class PadEvent {
	public static final byte ACTION_ACTIVE = 0; // Someone is doing something with the pad
	public static final byte ACTION_UNACTIVE = 1; // No one is doing nothing with the pad
	public static final byte ACTION_CHANGE = 2; // Normal Event for a pad state change
	
	View component;
	float value1,value2;
	String action1,action2;
	int Type;
	public PadEvent(View component, float value1,float value2, String action1,String action2, int mode) {
		super();
		this.component = component;
		this.value1 = value1;
		this.value2 = value2;
		this.action1 = action1;
		this.action2 = action2;
		this.Type = mode;
	}
	public View getComponent() {
		return component;
	}
	public float getValue1() {
		return value1;
	}
	public float getValue2() {
		return value2;
	}
	public String getAction1() {
		return action1;
	}
	public String getAction2() {
		return action2;
	}
	public int getType() {
		return Type;
	}
	
	
}
