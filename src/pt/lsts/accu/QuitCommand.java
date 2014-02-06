package pt.lsts.accu;

import pt.lsts.accu.panel.AccuBaseCommand;
import pt.up.fe.dceg.accu.R;
import android.app.Activity;
import android.content.Context;

public class QuitCommand extends AccuBaseCommand {

	public QuitCommand(Context context) {
		super(context);
	}

	@Override
	public void command() {
		((Activity)getContext()).finish();
	}

	@Override
	public int getIcon() {
		// TODO Auto-generated method stub
		return R.drawable.exit;
	}

}
