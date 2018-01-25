package pt.lsts.accu;

import pt.lsts.accu.components.beaconconfig.BeaconConfigurator;
import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.lsts.accu.state.Accu;
import pt.lsts.accu.util.LayoutUtil;
import pt.lsts.accu.R;
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
