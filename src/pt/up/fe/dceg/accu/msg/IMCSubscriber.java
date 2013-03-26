package pt.up.fe.dceg.accu.msg;

import pt.up.fe.dceg.neptus.imc.IMCMessage;

/**
 * Interface that a class should implement to receive IMCMessages
 * @author jqcorreia
 *
 */
public interface IMCSubscriber {
	public void onReceive(IMCMessage msg);
}