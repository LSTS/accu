package pt.up.fe.dceg.accu;

import pt.up.fe.dceg.accu.panel.AccuBaseCommand;
import android.content.Context;
import android.location.LocationManager;

public class TestCommand extends AccuBaseCommand {

	LocationManager lm;

	public TestCommand(Context context) {
		super(context);
	}

	@Override
	public void command() 
	{

	}

	@Override
	public int getIcon() {
		return R.drawable.icon;
	}
}
