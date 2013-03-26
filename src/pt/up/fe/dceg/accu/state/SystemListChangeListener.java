package pt.up.fe.dceg.accu.state;

import java.util.ArrayList;

import pt.up.fe.dceg.accu.types.Sys;

public interface SystemListChangeListener {
	
	public void onSystemListChange(ArrayList<Sys> list);
}
