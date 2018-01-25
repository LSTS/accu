package pt.lsts.accu.state;

import java.util.ArrayList;

import pt.lsts.accu.components.beaconconfig.Beacon;

public interface BeaconListChangeListener {

	void onBeaconListChange(ArrayList<Beacon> newlist);
}
