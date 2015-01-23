package pt.lsts.accu.components;

import java.util.ArrayList;
import java.util.Iterator;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.SystemList;
import pt.lsts.accu.state.SystemListChangeListener;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.util.AccuTimer;
import android.util.Log;

public class Heart implements SystemListChangeListener 
{
	public static final boolean DEBUG = false;
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
        Iterator<Sys> iterator = vehicleList.iterator();
		while(iterator.hasNext())
		{
			if(DEBUG)
                Log.v(TAG,"Beating...");
			imm.sendToSys(iterator.next(), "HeartBeat");
		}
	}
	public void updateVehicleList(ArrayList<Sys> list)
	{
		vehicleList.clear();
		for(Sys s: list)
		{
//			if(!s.getType().equalsIgnoreCase("CCU"))
				vehicleList.add(s);
		}
	}
	
	@Override
	public void onSystemListChange(ArrayList<Sys> list) {
		updateVehicleList(list);
	}
}
