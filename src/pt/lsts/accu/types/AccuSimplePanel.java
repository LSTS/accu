package pt.lsts.accu.types;

import pt.lsts.accu.panel.AccuBasePanel;
import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.view.View;

public class AccuSimplePanel extends AccuBasePanel {

	private int id=-1;
	private View root=null;

	public AccuSimplePanel(Context context, int layoutId) {
		super(context);
		System.out.println("id");
		id = layoutId;
	}
	
	public AccuSimplePanel(Context context, View root) {
		super(context);
		System.out.println("view");
		this.root = root;
	}
	
	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public View buildLayout() {
		if(id!=-1)
		{
			return inflateFromResource(id);
		}
		else
			return root;
	}

	@Override
	public int getIcon() {
		return R.drawable.icon;
	}

}
