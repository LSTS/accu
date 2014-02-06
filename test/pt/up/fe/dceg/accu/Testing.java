package pt.up.fe.dceg.accu;

import junit.framework.TestCase;
import pt.lsts.accu.msg.IMCUtils;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.state.SystemList;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

public class Testing extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testRunTest() 
	{	
		assertEquals("size <> 0",0,Accu.getInstance().getSystemList().getList().size());
	}
	public void testSize()
	{
		SystemList sl = Accu.getInstance().getSystemList();
		try {
			IMCMessage msg = IMCDefinition.getInstance().create("Announce");
			msg.getHeader().setValue("services", "imc+udp://asadasdasd");
			sl.onReceive(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("size <> 0",1,Accu.getInstance().getSystemList().getList().size());	
	}
	public void testAnnounceService()
	{
		try {
			IMCMessage msg = IMCDefinition.getInstance().create("Announce");
			msg.getHeader().setValue("services", "imc+udp://asadasdasd");
			
			assertNotNull(IMCUtils.getAnnounceService(msg, "imc+udp"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
