package pt.lsts.accu;

import pt.lsts.accu.panel.AccuBaseCommand;
import pt.up.fe.dceg.accu.R;
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
