package pt.up.fe.dceg.accu;

import pt.up.fe.dceg.accu.components.Finder;
import pt.up.fe.dceg.accu.foo.AccuMapComponent;
import pt.up.fe.dceg.accu.panel.AccuAction;
import pt.up.fe.dceg.accu.panel.AccuBasePanel;
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
