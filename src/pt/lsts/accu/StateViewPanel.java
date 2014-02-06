package pt.lsts.accu;

import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.view.ViewGroup;

@AccuAction(name = "Vehicle State", icon=R.drawable.stateview_icon_1 )
public class StateViewPanel extends AccuBasePanel {
	
	public StateViewPanel(Context context) {
		super(context);
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public ViewGroup buildLayout() {
		return inflateFromResource(R.layout.stateview_panel_layout);
	}

	@Override
	public boolean requiresActiveSys() {
		return true;
	}
}
