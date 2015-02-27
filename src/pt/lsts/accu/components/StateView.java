package pt.lsts.accu.components;

import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.accu.msg.IMCUtils;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.util.CoordUtil;
import pt.lsts.accu.util.MUtil;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.Rpm;
import pt.lsts.imc.VehicleState;
import pt.lsts.accu.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

public class StateView extends TableLayout implements IMCSubscriber {
	public static final String[] SUBSCRIBED_MSGS = { "EstimatedState", "Rpm",
			"VehicleState" };

	private Context context;

	public StateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initialize();
	}

	private void initialize() {
		LayoutInflater.from(context).inflate(R.layout.stateview_layout, this,
				true);

		Accu.getInstance().getIMCManager().addSubscriber(this, SUBSCRIBED_MSGS);
	}

	@Override
	public void onReceive(IMCMessage msg) {
		final int ID_STATIC = msg.getMgid();
		// Check for Active System
		if (!IMCUtils.isMsgFromActive(msg))
			return;

		if (ID_STATIC == EstimatedState.ID_STATIC) {
			// Process EstimatedState
			double res[] = CoordUtil.getAbsoluteLatLonDepthFromMsg(msg);

			TextView tv = (TextView) findViewWithTag("lat");
			tv.setText("" + CoordUtil.degreesToDMS(res[0], true));

			tv = (TextView) findViewWithTag("lon");
			tv.setText("" + CoordUtil.degreesToDMS(res[1], false));

			tv = (TextView) findViewWithTag("depth");
			tv.setText("" + MUtil.roundn(msg.getDouble("depth"), 2));

			tv = (TextView) findViewWithTag("roll");
			tv.setText(""
					+ MUtil.roundn(Math.toDegrees(msg.getFloat("phi")), 2));

			tv = (TextView) findViewWithTag("pitch");
			tv.setText(""
					+ MUtil.roundn(Math.toDegrees(msg.getFloat("theta")), 2));

			tv = (TextView) findViewWithTag("yaw");
			tv.setText(""
					+ MUtil.roundn(Math.toDegrees(msg.getFloat("psi")), 2));

			double vx = msg.getDouble("vx");
			double vy = msg.getDouble("vy");
			double vz = msg.getDouble("vz");
			double speed = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2)
					+ Math.pow(vz, 2))
					* CoordUtil.msToKnot;
			tv = (TextView) findViewWithTag("speed");
			tv.setText(MUtil.roundn(speed, 2) + " Knot");
		}
		if (ID_STATIC == VehicleState.ID_STATIC) {
			TextView tv = (TextView) findViewWithTag("error_count");
			tv.setText("" + msg.getInteger("error_count"));

			tv = (TextView) findViewWithTag("last_error");
			tv.setText("" + msg.getString("last_error")); // FIXME add timestamp
															// @ msg field
															// "last_error_time"

			tv = (TextView) findViewWithTag("op_mode");
			tv.setText("" + msg.getString("op_mode"));
		}
		if (ID_STATIC == Rpm.ID_STATIC) {
			int rpm = msg.getInteger("value");
			TextView tv = (TextView) findViewWithTag("rpm");
			tv.setText(rpm + " Rpm");
		}
	}
}
