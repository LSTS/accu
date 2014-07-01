package pt.lsts.accu;

import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.lsts.accu.R;
import android.content.Context;
import android.view.View;

@AccuAction(name = "System List", icon=R.drawable.systemlist_icon_1 )
public class SystemListPanel extends AccuBasePanel
{

	public SystemListPanel(Context context) {
		super(context);
	}

	@Override
	public View buildLayout() {
		return inflateFromResource(R.layout.systemlist_layout);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
	}
	
}
