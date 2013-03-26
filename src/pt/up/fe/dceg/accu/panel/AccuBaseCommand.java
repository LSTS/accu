package pt.up.fe.dceg.accu.panel;

import android.content.Context;

public abstract class AccuBaseCommand extends AccuBaseAction {

	Context context;
	
	public AccuBaseCommand(Context context)
	{
		this.context = context;
	}
	public Context getContext()
	{
		return context;
	}
	@Override
	public int getType() {
		return TYPE_COMMAND;
	}
	
	public abstract void command();
}
