package pt.lsts.accu.components.controlpad;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * Simple Scriptable TextField which declares a message and a field to be displayed with the possibility of showing units
 * @author jqcorreia
 *
 */
public class PadTextField extends TextView {

	private String message;
	private String field;
	private String units;
	
	public PadTextField(Context context, AttributeSet attrs) {
		super(context, attrs);
		message = attrs.getAttributeValue(null, "message");
		field = attrs.getAttributeValue(null,"field");
		units = attrs.getAttributeValue(null,"units");
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}	
}
