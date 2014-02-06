package pt.lsts.accu.state;

import java.util.ArrayList;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.accu.msg.IMCUtils;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.util.AccuTimer;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import android.util.Log;

public class SystemList implements IMCSubscriber{

	public static final String TAG = "SystemList";
	public static final int CONNECTED_TIME_LIMIT = 5000;
	public static final boolean DEBUG = false; 
	
	ArrayList<Sys> sysList = new ArrayList<Sys>();
	ArrayList<SystemListChangeListener> listeners = new ArrayList<SystemListChangeListener>();
	
	AccuTimer timer;
	Runnable task = new Runnable()
	{
		public void run()
		{
			checkConnection();
		}
	};
	
	public SystemList(IMCManager imm)
	{
		imm.addSubscriberToAllMessages(this);
		timer = new AccuTimer(task, 1000);
	}
	
	// Timed action, in this case checking connection state trough Heartbeat
	//FIXME Heartbeat is not that good of a metric used simply like that
	private void checkConnection()
	{
		long currentTime = System.currentTimeMillis();

		if(DEBUG)Log.v("SystemList","Checking Connections");
		
		for(Sys s: sysList)
		{
			if(DEBUG)Log.i("Log",s.getName() + " - " + (currentTime-s.lastMessageReceived));
			if((currentTime-s.lastMessageReceived)>CONNECTED_TIME_LIMIT&&s.isConnected())
			{
				s.setConnected(false);
				changeList(sysList);
			}
			else if((currentTime-s.lastMessageReceived)<CONNECTED_TIME_LIMIT&&!s.isConnected())
			{
				s.setConnected(false);
				changeList(sysList);
			}
		}
	}
	
	@Override
	public void onReceive(IMCMessage msg) 
	{
		// Process Heartbeat
		// Update lastHeartbeat received on systemList
		if(msg.getAbbrev().equalsIgnoreCase("heartbeat"))
		{

			return;
		}
		
		// Process Estimated State
		// Store Position Information
		// COMMENTED FOR NOW, WE DONT NEED THIS INFO TO BE GLOBAL
		
//		if(msg.getAbbrevName().equalsIgnoreCase("EstimatedState"))
//		{
//			Sys sys = findSysById((Integer)msg.getHeaderValue("src"));
//			
//			if(sys!=null) // Safeguard some rogue message of a system that doesnt exist
//			{
//				String ref = msg.getString("ref");
//				sys.setRefMode(ref);
//				double[] rpy = {msg.getDouble("phi"),msg.getDouble("theta"),msg.getDouble("psi")};
//				sys.setRPY(rpy);
//				if(ref.equalsIgnoreCase("LLD_ONLY"))
//				{
//					double[] ned = {0.0,0.0,0.0};
//					double[] lld = {msg.getDouble("lat"),msg.getDouble("lon"),msg.getDouble("depth")};
//					sys.setLLD(lld);
//					sys.setNED(ned);
//				}
//				if(ref.equalsIgnoreCase("NED_ONLY"))
//				{
//					double[] ned = {msg.getDouble("x"),msg.getDouble("y"),msg.getDouble("z")};
//					double[] lld = {0.0,0.0,0.0};
//					sys.setLLD(lld);
//					sys.setNED(ned);
//				}
//				if(ref.equalsIgnoreCase("NED_LLD"))
//				{
//					double[] ned = {msg.getDouble("x"),msg.getDouble("y"),msg.getDouble("z")};
//					double[] lld = {msg.getDouble("lat"),msg.getDouble("lon"),msg.getDouble("depth")};
//					sys.setLLD(lld);
//					sys.setNED(ned);
//				}
//				if(DEBUG)Log.i("Log","Name : " + sys.getName() + " lat " + sys.getLLD()[1] + " x " + sys.getNED()[1]); // Simple debug message
//			}
//		}
		
		// Process Announce routine
		if(msg.getAbbrev().equalsIgnoreCase("Announce"))
		{
			// If System already exists in host list
			if(containsSysName(msg.getString("sys_name")))
			{
				Sys s = findSysByName(msg.getString("sys_name"));

				if(DEBUG)Log.i("Log","Repeated announce from: " + msg.getString("sys_name"));

				if(!s.isConnected())
				{
					findSysByName(msg.getString("sys_name")).lastMessageReceived = System.currentTimeMillis();
					findSysByName(msg.getString("sys_name")).setConnected(true);
					changeList(sysList);
					// Send an Heartbeat to resume communications in case of system prior crash
					try {
						Accu.getInstance().getIMCManager().send(s.getAddress(), s.getPort(), IMCDefinition.getInstance().create("Heartbeat"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}		
				}

				return;
			}
			// If Service IMC+UDP doesnt exist or isnt reachable, return...
			if(IMCUtils.getAnnounceService(msg, "imc+udp")==null)  
			{
				Log.e(TAG,msg.getString("sys_name")+" node doesn't have IMC protocol or isn't reachable");
				Log.e(TAG,msg.toString());
				return;
			}
			String[] addrAndPort = IMCUtils.getAnnounceIMCAddressPort(msg);
			if(addrAndPort==null)
			{
				Log.e(TAG,"Unreachable System - " + msg.getString("sys_name"));
				return;
			}
			// If Not include it
			Log.i("Log","Adding new System");
			Sys s = new Sys(
					addrAndPort[0], 
					Integer.parseInt(addrAndPort[1]), msg.getString("sys_name"),
					(Integer)msg.getHeaderValue("src"),msg.getString("sys_type"),true, false);
			
			sysList.add(s);

			// Update the list of available Vehicles
			changeList(sysList);

			// Send an Heartbeat to register as a node in the vehicle (maybe EntityList?)
			try {
				IMCMessage m = IMCDefinition.getInstance().create("Heartbeat");
				m.getHeader().setValue("src",0x4100);
				Accu.getInstance().getIMCManager().send(s.getAddress(),s.getPort(), m);
				Accu.getInstance().getIMCManager().getComm().sendMessage(s.getAddress(),s.getPort(), m);
			} catch (Exception e1) {
				e1.printStackTrace();
			}		
		}
		// Process VehicleState to get error count
		else if(msg.getAbbrev().equalsIgnoreCase("VehicleState"))
		{
			if(DEBUG)
				Log.i("Log","Received VehicleState"+msg.toString());
			Sys s = findSysById((Integer)msg.getHeaderValue("src"));
			int errors = msg.getInteger("error_count");
			if(s!=null) // Meaning it exists on the list
			{
				if(DEBUG)
					Log.i("Log",""+errors);
				s.setError(errors>0);
				changeList(sysList); // Update the list
			}
		}
		// Update last messageReceived
		else
		{
			Sys sys = findSysById((Integer)msg.getHeaderValue("src"));
			
			// Returning from a ACCU crash this will prevent from listening to messages with nothing on the list
			if(sys==null)
				return;
			sys.lastMessageReceived = System.currentTimeMillis();
		}
	}
	
	public void addSystemListChangeListener(SystemListChangeListener l)
	{
		listeners.add(l);
	}
	public void changeList(ArrayList<Sys> list)
	{
		// Pass the new list to the listeners
		for(SystemListChangeListener l:listeners)
			l.onSystemListChange(list);
	}
	public boolean containsSysName(String name)
	{
		for(int c = 0; c < sysList.size();c++)
		{
			if(sysList.get(c).getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	public boolean containsSysId(int id)
	{
		for(int c = 0; c < sysList.size();c++)
		{
			if(sysList.get(c).getId()==id)
				return true;
		}
		return false;
	}
	public Sys findSysByName(String name)
	{
		for(int c = 0; c < sysList.size();c++)
		{
			if(sysList.get(c).getName().equalsIgnoreCase(name))
				return sysList.get(c);
		}
		return null;
	}
	public Sys findSysById(int id)
	{
		for(int c = 0; c < sysList.size();c++)
		{
			if(sysList.get(c).getId()==id)
				return sysList.get(c);
		}
		return null;
	}
	public ArrayList<Sys> getList()
	{
		return sysList;
	}
	public ArrayList<String> getNameList()
	{
		ArrayList<String> list = new ArrayList<String>();
		for(Sys s: sysList)
		{
			list.add(s.getName());
		}
		return list;
	}
	public ArrayList<String> getNameListByType(String type)
	{
		ArrayList<String> list = new ArrayList<String>();
		for(Sys s: sysList)
		{
			if(s.getType().equals(type))
				list.add(s.getName());
		}
		return list;
	}
	
	public void start()
	{
		timer.start();
	}
	public void stop()
	{
		timer.stop();
	}
	
}
