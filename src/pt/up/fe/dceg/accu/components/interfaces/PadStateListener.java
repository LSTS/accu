package pt.up.fe.dceg.accu.components.interfaces;

import java.util.LinkedHashMap;

public interface PadStateListener {
	void onPadStateChange(LinkedHashMap<String,Integer> state, String[] updated);
}
