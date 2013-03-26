package pt.up.fe.dceg.accu.state;

import java.util.ArrayList;

import pt.up.fe.dceg.accu.components.beaconconfig.Beacon;

public interface BeaconListChangeListener {

	void onBeaconListChange(ArrayList<Beacon> newlist);
}
