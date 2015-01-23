package pt.lsts.accu;

import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBaseCommand;
import pt.lsts.accu.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

@AccuAction(name = "Preferences", icon=R.drawable.preferences_icon_1)
public class PreferenceAction extends AccuBaseCommand{

	public PreferenceAction(Context context) {
		super(context);
	}

	@Override
	public void command() {
		Intent i = new Intent();
		i.setClass(getContext(), Preferences.class);
		((Activity)getContext()).startActivityForResult(i, 1);
	}

	@Override
	public int getIcon() {
		return R.drawable.preferences_icon_1;
	}
}
