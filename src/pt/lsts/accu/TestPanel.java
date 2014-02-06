package pt.lsts.accu;

import pt.lsts.accu.components.Finder;
import pt.lsts.accu.foo.AccuMapComponent;
import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.view.View;

@AccuAction(name = "Testing Panel", icon=R.drawable.icon)
public class TestPanel extends AccuBasePanel 
{
	Finder finder;
	
	public TestPanel(Context context) {
		super(context);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View buildLayout() 
	{
		return new AccuMapComponent(getContext());
	}

	@Override
	public int getIcon() {
		return R.drawable.icon;
	}
}
