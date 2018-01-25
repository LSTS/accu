package pt.lsts.accu;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import pt.lsts.accu.components.controlpad.ControlPad2;
import pt.lsts.accu.components.controlpad.PadEvent;
import pt.lsts.accu.components.controlpad.PadTextField;
import pt.lsts.accu.components.controlpad.PadToggleButton;
import pt.lsts.accu.components.controlpad.SensorPad;
import pt.lsts.accu.components.interfaces.PadEventListener;
import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.accu.msg.IMCUtils;
import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.util.AccuTimer;
import pt.lsts.accu.util.CoordUtil;
import pt.lsts.accu.util.MUtil;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.TYPE;
import pt.lsts.imc.RemoteActionsRequest;
import pt.lsts.imc.Teleoperation;
import pt.lsts.imc.TeleoperationDone;
import pt.lsts.imc.VehicleState;
import pt.lsts.imc.VehicleState.OP_MODE;

@AccuAction(name = "Tele-Operation", icon=R.drawable.teleop_icon_1)
public class TeleOpPanel extends AccuBasePanel 
implements IMCSubscriber, PadEventListener
{
	public static final String[] SUBSCRIBED_MSGS = { "RemoteActionsRequest","Rpm","EstimatedState","VehicleState" };
	Sys activeS;
	TextView tvRpm;
	IMCManager imm = Accu.getInstance().getIMCManager();
	boolean remoteOn;
	ControlPad2 controlPad;
	int teleopid;
	boolean teleop = false;
	

	LinkedHashMap<String, String> actions;      // Available actions for the Active
												// Vehicle with corresponding
											    // mapping
	LinkedHashMap<String, Float> persistency = new LinkedHashMap<String,Float>();
	
	private LinkedHashMap<String, Integer> layouts = new LinkedHashMap<String, Integer>(); // Available Layouts
	private Dialog layoutDialog;
	private LinkedHashMap<String, Object> actionsForMsg = new LinkedHashMap<String,Object>();
	private ArrayList<PadTextField> textFields;
	
	AccuTimer timer;
	Runnable task = new Runnable(){

		@Override
		public void run() {
			try {
				String remoteAction = IMCMessage.encodeTupleList(actionsForMsg).replace(',', ';');
				System.out.println("--> " + remoteAction);
					IMCMessage msg = IMCDefinition.getInstance().create("RemoteActions", "actions", IMCMessage.encodeTupleList(actionsForMsg).replace(',', ';'));
					imm.sendToActiveSys(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	public TeleOpPanel(Context context) {
		super(context);
		timer = new AccuTimer(task, 250);
		controlPad = (ControlPad2) getLayout().findViewById(R.id.pad);
		controlPad.setPadEventListener(this);
		textFields = controlPad.getTextFields();
		try {
			teleopid = IMCDefinition.getInstance().create("Teleoperation").getMgid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Build layout choosing dialog
		layouts.put("Basic Pad", R.layout.command_pad_layout);
		layouts.put("Double Pad", R.layout.command_pad_double);
		layouts.put("Sensor Pad", R.layout.command_pad_sensor);
		
		final String[] list = new String[layouts.size()];
		layouts.keySet().toArray(list);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		builder.setTitle("Choose Layout:");
		builder.setItems(list, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				controlPad.setPadLayout(layouts.get(list[which]));
				actionsForMsg.clear();
			}
		});
		layoutDialog = builder.create();
	}

	@Override
	public View buildLayout() {
		return inflateFromResource(R.layout.teleoperation_layout_2);
	}

	@Override
	public int getIcon() {
		return R.drawable.teleop_icon_1;
	}

	@Override
	public void onStart() {
		imm.addSubscriberToAllMessages(this); // Subscribe all messages in order to properly fill the PadTextFields
		activeS = Accu.getInstance().getActiveSys();
		
		//Check if there is an active system
		if(activeS == null)
		{
			Toast.makeText(getContext(), "There is no active system yet! Tele-Operation unavailable", Toast.LENGTH_LONG).show();
			
		}
		else
		{
			// Request RemoteActions
			Log.i("TeleOp Log","Requesting Remote Actions");
			IMCMessage msg;
			try {
				msg = IMCDefinition.getInstance().create("RemoteActionsRequest","op",1);
				imm.sendToActiveSys(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void startTeleOp()
	{	
		Log.i("Log","Initializing TeleOp...");
		timer.start();
		try {
			int reqId = Accu.getInstance().getNextRequestId();
			Teleoperation teleoperationMsg = new Teleoperation();
			teleoperationMsg.setCustom("src="+imm.getLocalId());
			PlanControl msg = new PlanControl();
			msg.setType(PlanControl.TYPE.REQUEST);
			msg.setOp(PlanControl.OP.START);
			msg.setFlags(0);
			msg.setRequestId(reqId);
            msg.setPlanId("teleoperation-mode");
            msg.setArg(teleoperationMsg);
            
            while(teleop==false){
                imm.sendToActiveSys(msg);
                wait(700);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopTeleOp()
	{
		Log.i("Log","TeleOp onStop ");
		remoteOn=false;
		timer.stop();
		try {
			while(teleop==true){
				imm.sendToActiveSys(new TeleoperationDone());				
				wait(700);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onStop() {
		stopTeleOp();
		imm.removeSubscriberToAll(this);
		
		// Unregister sensorpad components
		for(View v : controlPad.viewList)
		{
			if(v instanceof SensorPad)
			{
				((SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE)).unregisterListener((SensorPad)v);
			}
		}
	}
	
	private LinkedHashMap<String,String> parseActions(String actions)
	{
		LinkedHashMap<String,String> actionList = new LinkedHashMap<String,String>();
		
		String[] foo = actions.split(";");
		
		for(int i = 0; i < foo.length; i++)
		{
			String[] bar=foo[i].split("=");
			actionList.put(bar[0], bar[1]);
		}
		return actionList;
	}

	public void on(RemoteActionsRequest msg) {
		actions = parseActions(msg.getString("actions"));

		for(String k: actions.keySet())
			Log.i("Log",k+" "+actions.get(k));		
	}
	
	public void on(EstimatedState msg) {
		double speedMs = Math.sqrt(Math.pow(msg.getVx(), 2) + Math.pow(msg.getVy(), 2) + Math.pow(msg.getVz(), 2));
		double speed = speedMs * CoordUtil.msToKnot;
		((TextView)getLayout().findViewWithTag("speed")).setText(MUtil.roundn(speed, 2)+" Knot\n"+MUtil.roundn(speedMs, 2)+" m/s");
	}
	
	public void on(VehicleState msg) {
		teleop = msg.getOpMode() == OP_MODE.MANEUVER && msg.getManeuverType() == teleopid;
		((PadToggleButton)getLayout().findViewWithTag("btn1")).setChecked(teleop);
	}
	
	
	@Override
	public void onReceive(IMCMessage msg) 
	{
		if(IMCUtils.isMsgFromActive(msg))
		{
			final int ID_MSG = msg.getMgid();
			for(PadTextField ptf : textFields)
			{
				if(msg.getAbbrev().equals(ptf.getMessage()))
				{
					ptf.setText(msg.getString(ptf.getField())+" " + ptf.getUnits());
				}
			}
			
			switch (ID_MSG) {
			case RemoteActionsRequest.ID_STATIC:
				on((RemoteActionsRequest)msg);
				break;
			case EstimatedState.ID_STATIC:
				on((EstimatedState)msg);
				break;
			case VehicleState.ID_STATIC:
				on((VehicleState)msg);
				break;
			default:
				break;
			}			
		}
	}

	@Override
	public void onPadEvent(PadEvent event)
	{
		Log.v("TeleOpPanel",
				event.getAction1() + " " + event.getAction2()+" " 
				+ event.getValue1() + " " + event.getValue2());
		
		// Check for "special" actions (Remote Start/Stop and such)
		if(event.getAction1() != null) 
		{
			if(event.getAction1().equals("teleop-start"))
			{
				startTeleOp();
				return;
			}	
			if(event.getAction1().equals("teleop-stop"))
			{
				stopTeleOp();
				return;
			}
		}
		//Now check generic actions
		if(event.getAction1()!=null)
			actionsForMsg.put(event.getAction1(), event.getValue1());
		if(event.getAction2()!=null)
			actionsForMsg.put(event.getAction2(), event.getValue2());
	}
	
	@Override
	public boolean requiresActiveSys() {
		return true;
	}

	
	@Override
	public void prepareMenu(Menu menu) {
		super.prepareMenu(menu);
		menu.add("Change Layout");
	}

	@Override
	public void menuHandler(MenuItem item) {
		if(item.getTitle().equals("Change Layout"))
		{
			layoutDialog.show();
		}
	}
}
