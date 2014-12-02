package pt.lsts.accu.msg;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import pt.lsts.accu.state.Accu;
import pt.lsts.imc.IMCMessage;

public class IMCUtils {
	/**
	 * This function return the full address on a service specified in the Announce message of a node
	 * @param msg the message to be inspected
	 * @param service The name of the service to be looked for
	 * @return Returns a string like &lt;address:port&gt;, null if msg not an announce or service doesnt exist.
	 */
	public static ArrayList<String> getAnnounceService(IMCMessage msg, String service)
	{
		String str = msg.getString("services");
		ArrayList<String> list = new ArrayList<String>();
		String services[] = str.split(";");
		for(String s: services)
		{
			String foo[] = s.split("://");
			if(foo[0].equals(service))
				list.add(foo[1]);
		}
		return list;
	}
	/**
	 * 
	 * @param msg IMCMessage containing the Announce to extract the address from.
	 * @return The node address for IMC communications.
	 */
	public static String[] getAnnounceIMCAddressPort(IMCMessage msg)
	{
		String res[]=null;
		for (String s : getAnnounceService(msg, "imc+udp")) {
			try {
				String foo[] = s.split(":");
				res=new String[2];
				res[0] = foo[0];
				res[1] = foo[1].substring(0, foo[1].length() - 1);
				res[1] = res[1].split("/")[0];//remove services after port
				if (InetAddress.getByName(s.split(":")[0]).isReachable(50)) {
					return res;//return first reachable
				}
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		return res;//none reachable, return last one
	}

	
	public static boolean isMsgFromActive(IMCMessage msg)
	{
		// If active system doesnt exist or isnt a message from active system
		if(Accu.getInstance().getActiveSys()==null)
			return false;
		if(Accu.getInstance().getActiveSys().getId() != (Integer)msg.getHeaderValue("src"))
			return false;
		return true;
	}
}
