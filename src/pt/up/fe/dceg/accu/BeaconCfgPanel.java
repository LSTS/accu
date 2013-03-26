package pt.up.fe.dceg.accu;

import pt.up.fe.dceg.accu.components.beaconconfig.BeaconConfigurator;
import pt.up.fe.dceg.accu.panel.AccuAction;
import pt.up.fe.dceg.accu.panel.AccuBasePanel;
import pt.up.fe.dceg.accu.state.Accu;
import pt.up.fe.dceg.accu.util.LayoutUtil;
import android.content.Context;
import android.util.Log;
import android.view.View;

@AccuAction (name = "Beacon Configurator", icon = R.drawable.lbl_icon_1)
public class BeaconCfgPanel extends AccuBasePanel {
	
	BeaconConfigurator configurator;
	
	public BeaconCfgPanel(Context context) {
		super(context);
	}

	@Override
	public void onStart() {
		Log.i("Beacon Panel",componentList.size()+"");
		configurator.setBeaconList(Accu.getInstance().getLblBeaconList().getList());
	}

	@Override
	public void onStop() 
	{

	}

	@Override
	public View buildLayout() {
		configurator = new BeaconConfigurator(getContext());
		
		Log.d("beacon",LayoutUtil.flattenLayout(configurator).size()+"");
		return configurator;
	}

	@Override
	public int getIcon() {
		return R.drawable.lbl_icon_1;
	}
}
