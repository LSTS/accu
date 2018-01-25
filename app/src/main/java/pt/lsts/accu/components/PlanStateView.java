package pt.lsts.accu.components;

import java.text.SimpleDateFormat;
import java.util.Date;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.accu.msg.IMCUtils;
import pt.lsts.accu.panel.AccuComponent;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.util.LayoutUtil;
import pt.lsts.imc.IMCMessage;
import pt.lsts.accu.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * Very simple Plan State View & Command
 * @author jqcorreia
 *
 */
public class PlanStateView extends LinearLayout 
implements IMCSubscriber, AccuComponent, OnClickListener
{
	public static final String[] SUBSCRIBED_MSGS = {"PlanControlState"};
	@SuppressWarnings("unused")
	private static final String TAG = "PlanStateView";
	private Context context;
	private IMCManager imm = Accu.getInstance().getIMCManager();
	private String currentPlanID;
	
	public PlanStateView(Context context) {
		super(context);
		this.context = context;
		initialize();
	
	}
	public PlanStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initialize();
	
	}

	public void initialize()
	{
		LayoutInflater.from(context).inflate(R.layout.planstate_layout, this,true);
		
		Button btn = (Button)findViewWithTag("btnStart");
		btn.setOnClickListener(this);
		btn = (Button) findViewWithTag("btnStop");
		btn.setOnClickListener(this);
		btn = (Button) findViewWithTag("btnAbort");
		btn.setOnClickListener(this);
	}

	@Override
	public void onReceive(IMCMessage msg) 
	{
		if(!IMCUtils.isMsgFromActive(msg))
			return;
		TextView tv = (TextView) findViewWithTag("state");
		tv.setText(msg.getString("state"));
		
		LayoutUtil.fillLayoutField(this, "plan_id", msg.getString("plan_id"));
		currentPlanID = msg.getString("plan_id");
//		
		LayoutUtil.fillLayoutField(this, "maneuver_id", msg.getString("node_id"));
		
		long lastEventTimeMillis = (long)((msg.getDouble("last_event_time")+3600)*1000);
		
		LayoutUtil.fillLayoutField(this, "lastevent", "("+new SimpleDateFormat("HH'h'mm'm'ss's'").format(new Date(lastEventTimeMillis))+") "+msg.getString("last_event"));
	}

	@Override
	public void onStart() {
		Accu.getInstance().getIMCManager().addSubscriber(this,SUBSCRIBED_MSGS);
	}

	@Override
	public void onEnd() {
		Accu.getInstance().getIMCManager().removeSubscriberToAll(this);
	}
	@Override
	public void onClick(View view) {
		if(view.getTag().equals("btnStart"))
		{
			imm.sendToActiveSys("PlanControl","type",0,"op","START","plan_id",currentPlanID);
		}
		if(view.getTag().equals("btnStop"))
		{
			imm.sendToActiveSys("PlanControl","type",0,"op","STOP");
		}
		if(view.getTag().equals("btnAbort"))
		{
			imm.sendToActiveSys("Abort");
		}
	}
}
