package pt.lsts.accu.state;

import java.util.ArrayList;

import pt.lsts.accu.types.Sys;

public interface SystemListChangeListener {
	
	public void onSystemListChange(ArrayList<Sys> list);
}
