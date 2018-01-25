package pt.lsts.accu.state;

import java.util.ArrayList;

import pt.lsts.accu.msg.IMCManager;
import pt.lsts.accu.msg.IMCSubscriber;
import pt.lsts.accu.msg.IMCUtils;
import pt.lsts.accu.types.Sys;
import pt.lsts.accu.util.AccuTimer;
import pt.lsts.imc.Announce;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.VehicleState;
import android.util.Log;

public class SystemList implements IMCSubscriber {

	public static final String TAG = "SystemList";
	public static final int CONNECTED_TIME_LIMIT = 5000;
	public static final boolean DEBUG = false;

	ArrayList<Sys> sysList = new ArrayList<Sys>();
	ArrayList<SystemListChangeListener> listeners = new ArrayList<SystemListChangeListener>();

	AccuTimer timer;
	Runnable task = new Runnable() {
		public void run() {
			checkConnection();
		}
	};

	public SystemList(IMCManager imm) {
		imm.addSubscriberToAllMessages(this);
		timer = new AccuTimer(task, 1000);
	}

	// Timed action, in this case checking connection state trough Heartbeat
	// FIXME Heartbeat is not that good of a metric used simply like that
	private void checkConnection() {
		long currentTime = System.currentTimeMillis();

		if (DEBUG)
			Log.v("SystemList", "Checking Connections");

		for (Sys s : sysList) {
			if (DEBUG)
				Log.i("Log", s.getName() + " - "
						+ (currentTime - s.lastMessageReceived));
			if ((currentTime - s.lastMessageReceived) > CONNECTED_TIME_LIMIT
					&& s.isConnected()) {
				s.setConnected(false);
				changeList(sysList);
			} else if ((currentTime - s.lastMessageReceived) < CONNECTED_TIME_LIMIT
					&& !s.isConnected()) {
				s.setConnected(false);
				changeList(sysList);
			}
		}
	}
	
	private void on(Announce announce) {
		// If System already exists in host list
		String sysName = announce.getSysName();
		if (containsSysName(sysName)) {
			Sys s = findSysByName(sysName);

			if (DEBUG)
				Log.i("Log", "Repeated announce from: " + sysName);

			if (!s.isConnected()) {
				findSysByName(sysName).lastMessageReceived = System
						.currentTimeMillis();
				findSysByName(sysName).setConnected(true);
				changeList(sysList);
				// Send an Heartbeat to resume communications in case of
				// system prior crash
				try {
					Accu.getInstance()
							.getIMCManager()
							.send(s.getAddress(),
									s.getPort(),
									IMCDefinition.getInstance().create(
											"Heartbeat"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			return;
		}
		// If Service IMC+UDP doesnt exist or isnt reachable, return...
		if (IMCUtils.getAnnounceService(announce, "imc+udp") == null) {
			Log.e(TAG, sysName
					+ " node doesn't have IMC protocol or isn't reachable");
			Log.e(TAG, announce.toString());
			return;
		}
		String[] addrAndPort = IMCUtils.getAnnounceIMCAddressPort(announce);
		if (addrAndPort == null) {
			Log.e(TAG, "No Announce Services - " + sysName);
			return;
		}
		// If Not include it
		Log.i("Log", "Adding new System");
		Sys s = new Sys(addrAndPort[0], Integer.parseInt(addrAndPort[1]),
				sysName, announce.getSrc(), announce
						.getSysType().name(), true, "");

		sysList.add(s);

		// Update the list of available Vehicles
		changeList(sysList);

		// Send an Heartbeat to register as a node in the vehicle (maybe
		// EntityList?)
		try {
			Heartbeat heartbeat = new Heartbeat();
			heartbeat.setSrc(0x4100);
			Accu.getInstance().getIMCManager()
					.send(s.getAddress(), s.getPort(), heartbeat);
			Accu.getInstance().getIMCManager().getComm()
					.sendMessage(s.getAddress(), s.getPort(), heartbeat);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void on(VehicleState msg) {
		if (DEBUG)
			Log.i("Log", "Received VehicleState" + msg.toString());
		Sys s = findSysById((Integer) msg.getSrc());
		String errors = msg.getErrorEnts();
		if (s != null) // Meaning it exists on the list
		{
			if (DEBUG)
				Log.i("Log", "Errors: " + errors);
			s.setErrors(errors);
			changeList(sysList); // Update the list
		}
		
		switch (msg.getOpMode()) {
		case SERVICE:
			s.setMode("Idle");
			break;
		case BOOT:
			s.setMode("Boot");
			break;
		case CALIBRATION:
			s.setMode("Calibration");
			break;
		case EXTERNAL:
			s.setMode("External");
			break;
		case MANEUVER:
			s.setMode(IMCDefinition.getInstance().getMessageName(msg.getManeuverType()));
			break;
		default:
			s.setMode(msg.getOpModeStr());
			break;
		}
	}

	@Override
	public void onReceive(IMCMessage msg) {
		final int ID_MSG = msg.getMgid();

		if (ID_MSG == Announce.ID_STATIC)
			on((Announce) msg);			
		else if (ID_MSG == VehicleState.ID_STATIC)
			on((VehicleState)msg);
		else {
			Sys sys = findSysById(msg.getSrc());
			if (sys == null)
				return;
			sys.lastMessageReceived = System.currentTimeMillis();
		}
	}

	public void addSystemListChangeListener(SystemListChangeListener l) {
		listeners.add(l);
	}

	public void changeList(ArrayList<Sys> list) {
		// Pass the new list to the listeners
		for (SystemListChangeListener l : listeners)
			l.onSystemListChange(list);
	}

	public boolean containsSysName(String name) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public boolean containsSysId(int id) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getId() == id)
				return true;
		}
		return false;
	}

	public Sys findSysByName(String name) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getName().equalsIgnoreCase(name))
				return sysList.get(c);
		}
		return null;
	}

	public Sys findSysById(int id) {
		for (int c = 0; c < sysList.size(); c++) {
			if (sysList.get(c).getId() == id)
				return sysList.get(c);
		}
		return null;
	}

	public ArrayList<Sys> getList() {
		return sysList;
	}

	public ArrayList<String> getNameList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Sys s : sysList) {
			list.add(s.getName());
		}
		return list;
	}

	public ArrayList<String> getNameListByType(String type) {
		ArrayList<String> list = new ArrayList<String>();
		for (Sys s : sysList) {
			if (s.getType().equals(type))
				list.add(s.getName());
		}
		return list;
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

}
