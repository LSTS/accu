package pt.up.fe.dceg.accu;

import pt.up.fe.dceg.accu.panel.AccuAction;
import pt.up.fe.dceg.accu.panel.AccuBasePanel;
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
