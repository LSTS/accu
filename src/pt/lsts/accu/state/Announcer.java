package pt.lsts.accu.state;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.util.AccuTimer;
import pt.lsts.accu.util.MUtil;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import android.location.Location;
import android.util.Log;

public class Announcer 
{
	public static final String TAG = "Announcer";
	public static final String ANNOUNCE_ADDR = "224.0.75.69"; //FIXME hardcoded value
	public static final int ANNOUNCE_PORT = 30100;
	public static final long ANNOUNCE_DELAY = 10000; // 10 seconds
	public static final boolean DEBUG = false;
	
	IMCMessage announce;
	IMCManager imm;
	String broadcastAddress;
	String multicastAddress;
	AccuTimer at;
	
	Runnable task = new Runnable()
	{

		@Override
		public void run() 
		{
			if(DEBUG)Log.i(TAG,announce.toString());
			if(imm.getListener()==null) return;
			for(int i = 0; i <5;i++)
			{
				imm.send(broadcastAddress,ANNOUNCE_PORT+i, announce);
				imm.send(multicastAddress,ANNOUNCE_PORT+i, announce);
			}
		}
	};
	
	public Announcer(IMCManager imm,String broadcast, String multicast)
	{
		this.imm = imm;
		broadcastAddress = broadcast; // Needed for broadcasting 
		multicastAddress = multicast;
		
		at = new AccuTimer(task,ANNOUNCE_DELAY);
		System.out.println("Broadcast address: " + broadcastAddress);
	}

	public void start()
	{
		generateAnnounce(); // Generate announce message only on start
		at.start();
	}
	public void stop()
	{
		at.stop();
	}
	
	public void setBroadcastAddress(String addr)
	{
		broadcastAddress = addr; 
	}
	/**
	 * This function can be called to (re)generate the Announce message sent by the console
	 */
	public void generateAnnounce()
	{
		String ipfull = MUtil.getLocalIpAddress();
		Location currentLocation = Accu.getInstance().getGpsManager().getCurrentLocation();
		
		String[] ip = new String[4];
		
		if(ipfull==null) // Means no connection
		{
			ip[0]=ip[1]=ip[2]=ip[3]="0";
		}
		else
		{
			ip = ipfull.split("\\.");
		}
		
		String sysName = "accu-"+ip[2]+ip[3]; 
		String sysType = "CCU"; 
		double lat = currentLocation.getLatitude();
		double lon = currentLocation.getLatitude();
		double height = currentLocation.getAltitude();
		
		int owner = 0xFFFF;
		String services = "imc+udp://"+MUtil.getLocalIpAddress()+":6001/;"; //FIXME
		try {
			announce = IMCDefinition.getInstance().create("Announce", 
					"sys_name",sysName,
					"sys_type",sysType,
					"owner",owner,
					"lat",lat,
					"lon",lon,
					"height",height,
					"services",services);
			announce.getHeader().setValue("src", imm.getLocalId());
			announce.getHeader().setValue("src_ent", 255);
			announce.getHeader().setValue("dst_ent", 255);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
