package pt.up.fe.dceg.accu.types;

import android.content.Context;
import android.view.View;
import pt.up.fe.dceg.accu.R;
import pt.up.fe.dceg.accu.panel.AccuBasePanel;

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
