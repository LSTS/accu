package pt.lsts.accu.components;

import java.util.ArrayList;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.SystemList;
import pt.lsts.accu.state.SystemListChangeListener;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.util.AccuTimer;
import android.util.Log;

import pt.lsts.imc.Heartbeat;

public class Heart implements SystemListChangeListener 
{
	public static final boolean DEBUG = true;
	public static final String TAG = "Heart";
	AccuTimer timer;
	ArrayList<Sys> vehicleList = new ArrayList<Sys>();
	SystemList sysList = Accu.getInstance().getSystemList();
	IMCManager imm = Accu.getInstance().getIMCManager();
	
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			sendHeartbeat();
		}
	};
	
	public Heart()
	{
		sysList.addSystemListChangeListener(this);
		timer = new AccuTimer(runnable,1000);
	}
	public void start()
	{
		timer.start();
	}
	public void stop()
	{
		timer.stop();
	}
	public void sendHeartbeat()
	{
		 ArrayList<Sys> arrayListSys = sysList.getList();
		 for (Sys sys : arrayListSys) {
			 if (DEBUG)
				 Log.v(TAG, "Beating... to sys:"+sys.getName());
			 try {
				 //imm.sendToSys(sys, "HeartBeat");//accu old version
				 Heartbeat heartbeat = new Heartbeat();
				 Accu.getInstance().getIMCManager().sendToSys(sys, heartbeat);
			 }catch(Exception e){
				 Log.e(TAG,"sendHeartBeat exception: "+e.getMessage(),e);
				 e.printStackTrace();
			 }
		 }
	}
	public void updateVehicleList(ArrayList<Sys> list)
	{
		vehicleList.clear();
		for(Sys s: list)
		{
				vehicleList.add(s);
		}
	}
	
	@Override
	public void onSystemListChange(ArrayList<Sys> list) {
		updateVehicleList(list);
	}
}
