package pt.lsts.accu.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;
import pt.lsts.accu.R;
import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.accu.msg.IMCUtils;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.util.CoordUtil;
import pt.lsts.accu.util.MUtil;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.Rpm;
import pt.lsts.imc.VehicleState;
import pt.lsts.util.WGS84Utilities;

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

	private void on(EstimatedState msg) {
		double[] lld = WGS84Utilities.toLatLonDepth((EstimatedState)msg);
		
		TextView tv = (TextView) findViewWithTag("lat");
		tv.setText("" + CoordUtil.degreesToDMS(lld[0], true));

		tv = (TextView) findViewWithTag("lon");
		tv.setText("" + CoordUtil.degreesToDMS(lld[1], false));

		tv = (TextView) findViewWithTag("depth");
		tv.setText("" + MUtil.roundn(msg.getDepth(), 2));

		tv = (TextView) findViewWithTag("roll");
		tv.setText(""
				+ MUtil.roundn(Math.toDegrees(msg.getPhi()), 2));

		tv = (TextView) findViewWithTag("pitch");
		tv.setText(""
				+ MUtil.roundn(Math.toDegrees(msg.getTheta()), 2));

		tv = (TextView) findViewWithTag("yaw");
		tv.setText(""
				+ MUtil.roundn(Math.toDegrees(msg.getPsi()), 2));

		double vx = msg.getVx();
		double vy = msg.getVy();
		double vz = msg.getVz();
		double speed = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2)
				+ Math.pow(vz, 2))
				* CoordUtil.msToKnot;
		tv = (TextView) findViewWithTag("speed");
		tv.setText(MUtil.roundn(speed, 2) + " Knot");
	}
	
	private void on(VehicleState msg) {
		TextView tv = (TextView) findViewWithTag("error_count");
		tv.setText("" + msg.getErrorCount());

		tv = (TextView) findViewWithTag("last_error");
		tv.setText("" + msg.getLastError());			// FIXME add timestamp
														// @ msg field
														// "last_error_time"
		tv = (TextView) findViewWithTag("op_mode");
		tv.setText("" + msg.getOpModeStr());
	}
	
	@Override
	public void onReceive(IMCMessage msg) {
		final int ID_STATIC = msg.getMgid();
		// Check for Active System
		if (!IMCUtils.isMsgFromActive(msg))
			return;

		if (ID_STATIC == EstimatedState.ID_STATIC) {
			on((EstimatedState)msg);			
		}
		if (ID_STATIC == VehicleState.ID_STATIC) {
			on((VehicleState)msg);
			
		}
		if (ID_STATIC == Rpm.ID_STATIC) {
			int rpm = msg.getInteger("value");
			TextView tv = (TextView) findViewWithTag("rpm");
			tv.setText(rpm + " Rpm");
		}
	}
}
