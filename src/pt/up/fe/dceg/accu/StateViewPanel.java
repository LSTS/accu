package pt.up.fe.dceg.accu;

import pt.up.fe.dceg.accu.panel.AccuAction;
import pt.up.fe.dceg.accu.panel.AccuBasePanel;
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
