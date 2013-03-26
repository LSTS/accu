package pt.up.fe.dceg.accu.msg;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import pt.up.fe.dceg.accu.state.Accu;
import pt.up.fe.dceg.neptus.imc.IMCMessage;

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
		for(String s: getAnnounceService(msg,"imc+udp"))
		{
			try {
				if(InetAddress.getByName(s.split(":")[0]).isReachable(50))
				{
					String foo[] = s.split(":");
					String res[] = { foo[0], foo[1].substring(0, foo[1].length()-1)};
					return res;
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
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
