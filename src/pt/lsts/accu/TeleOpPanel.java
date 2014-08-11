package pt.lsts.accu;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.accu.R;
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
			Toast.makeText(getContext(), "There is no active system yet! Tele-Operation unavailable", 7000).show();
			
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
		IMCMessage msg;
		try {
            IMCMessage teleoperationMsg = IMCDefinition.getInstance().create("Teleoperation");
            int reqId = Accu.getInstance().getNextRequestId();
            msg = IMCDefinition.getInstance().create(
                    "PlanControl", "type", "REQUEST", "op", "START", "request_id",
                    reqId, "plan_id", "teleoperation-mode", 
                    "flags", 0, "arg", teleoperationMsg);
            while(teleop==false){
                imm.sendToActiveSys(msg);
                wait(1);
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
		IMCMessage msg;
		try {
			msg = IMCDefinition.getInstance().create("TeleOperationDone");
			imm.send(activeS.getAddress(), activeS.getPort(), msg);
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
//	private void mapActions()
//	{
//		LinkedHashMap<String,String> foo = controlPad.getActions();
//		for(String s: actions.keySet())
//		{
//			actions.put(s,foo.get(s));
//		}
//	}
	@Override
	public void onReceive(IMCMessage msg) 
	{
		if(IMCUtils.isMsgFromActive(msg))
		{
			// Fill the PadTextField components on controlpad
			for(PadTextField ptf : textFields)
			{
				if(msg.getAbbrev().equals(ptf.getMessage()))
				{
					ptf.setText(msg.getString(ptf.getField())+" " + ptf.getUnits());
				}
			}
			
			if(msg.getAbbrev().equalsIgnoreCase("RemoteActionsRequest"))
			{
				actions = parseActions(msg.getString("actions"));
//				mapActions();

				for(String k: actions.keySet())
				{
					Log.i("Log",k+" "+actions.get(k));
				}
			}	
			
			// For now EstimatedState is hard-coded since it needs some processing
			if(msg.getAbbrev().equalsIgnoreCase("EstimatedState"))
			{
				double vx = msg.getDouble("vx");
				double vy = msg.getDouble("vy");
				double vz = msg.getDouble("vz");
				double speed = Math.sqrt(Math.pow(vx, 2)+Math.pow(vy, 2)+Math.pow(vz, 2))*CoordUtil.msToKnot;
				((TextView)getLayout().findViewWithTag("speed")).setText(MUtil.roundn(speed, 2)+" Knot");
			}
			
			if(msg.getAbbrev().equalsIgnoreCase("VehicleState"))
			{
				if(msg.getString("op_mode").equalsIgnoreCase("maneuver")
					&& msg.getInteger("maneuver_type")==teleopid)
				{
					teleop=true;
				}
				else
				{
					teleop=false;
				}
//				((TextView)getLayout().findViewWithTag("opmode")).setText("Tele-Operation "+(teleop?"ON":"OFF"));
				((PadToggleButton)getLayout().findViewWithTag("btn1")).setChecked(teleop);
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
