package pt.lsts.accu;

import pt.lsts.accu.components.PlanStateView;
import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.lsts.accu.R;
import android.content.Context;
import android.view.View;

@AccuAction(name = "Plan State/Control" , icon=R.drawable.planstate_icon_1 )
public class PlanStatePanel extends AccuBasePanel {

	public PlanStatePanel(Context context) {
		super(context);
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}
	@Override
	public View buildLayout() {
		return new PlanStateView(getContext());
	}
	@Override
	public boolean requiresActiveSys() {
		return true;
	}
}
