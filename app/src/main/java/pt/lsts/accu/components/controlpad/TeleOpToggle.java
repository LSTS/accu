package pt.lsts.accu.components.controlpad;

import android.content.Context;
import android.util.AttributeSet;

public class TeleOpToggle extends PadToggleButton{

	public TeleOpToggle(Context context, AttributeSet attrs) {
		super(context, attrs);
		actionOn = "teleop-stop";
		actionOff = "teleop-start";
		setTextOn("TeleOp On");
		setTextOff("TeleOp Off");	
		setChecked(false);
		setWidth(200);
	}

}
