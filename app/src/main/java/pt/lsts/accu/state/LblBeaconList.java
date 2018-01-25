package pt.lsts.accu.state;

import java.util.ArrayList;

import pt.lsts.accu.components.beaconconfig.Beacon;
import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.imc.IMCMessage;
import android.util.Log;

public class LblBeaconList implements IMCSubscriber
{
	public static final String[] SUBSCRIBED_MSGS = {"LblConfig"};
	public static final String TAG = "LblBeaconList";
	ArrayList<Beacon> list = new ArrayList<Beacon>(); 
	ArrayList<BeaconListChangeListener> listeners = new ArrayList<BeaconListChangeListener>();
	
	IMCManager imm;
	public LblBeaconList()
	{
		imm = Accu.getInstance().getIMCManager();
		imm.addSubscriber(this, SUBSCRIBED_MSGS);
	}
	@Override
	public void onReceive(IMCMessage msg) 
	{
		Log.i(TAG,"List Received");
		Log.i(TAG,msg.toString());
		
		list.clear();
		for(int i = 0; i < 6; i++) //FIXME For now hard-code max beacon number
		{
			IMCMessage m = msg.getMessage("beacon"+i);
			// do this because if beacon is not set it returns NULL(maybe 'continue' instead of 'break'?);
			if(m == null) break; 
			
			Log.i(TAG,m.toString());
			Beacon beacon = new Beacon(
					m.getString("beacon"),
					Math.toDegrees(m.getDouble("lat")),
					Math.toDegrees(m.getDouble("lon")),
					m.getDouble("depth")
			);
			
			beacon.setInterrogationChannel(m.getInteger("query_channel"));
			beacon.setReplyChannel(m.getInteger("reply_channel"));
			beacon.setTransponderDelay(m.getInteger("transponder_delay"));
			list.add(beacon);
		}
		notifyListeners();
	}
	
	public void notifyListeners()
	{
		for(BeaconListChangeListener l: listeners)
		{
			l.onBeaconListChange(list);
		}
	}
	
	public ArrayList<Beacon> getList()
	{
		return list;
	}
	public ArrayList<String> getNameList()
	{
		ArrayList<String> array = new ArrayList<String>();
		
		for(Beacon b : list)
		{
			array.add(b.getName());
		}
		return array;
	}
	public Beacon getBeaconByName(String name)
	{
		for(Beacon b : list)
		{
			if(b.getName().equalsIgnoreCase(name))
				return b;
		}
		return null;
	}
	public void addBeaconListChangeListener(BeaconListChangeListener l)
	{
		listeners.add(l);
	}
}

