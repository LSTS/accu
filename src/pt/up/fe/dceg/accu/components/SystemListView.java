package pt.up.fe.dceg.accu.components;

import java.util.ArrayList;

import pt.up.fe.dceg.accu.adapter.SystemListAdapter;
import pt.up.fe.dceg.accu.state.Accu;
import pt.up.fe.dceg.accu.state.SystemListChangeListener;
import pt.up.fe.dceg.accu.types.Sys;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SystemListView extends ListView implements 
android.widget.AdapterView.OnItemLongClickListener, 
SystemListChangeListener, android.widget.AdapterView.OnItemClickListener
{	
	Context context;
	ArrayList<Sys> sysList;
	SystemListAdapter adapter;
	
	public SystemListView(Context context) {
		super(context);
		initialize(context);
	}
	public SystemListView(Context context, AttributeSet attribSet) {
		super(context, attribSet);
		initialize(context);
	}
	
	private void initialize(Context ct)
	{
		context = ct;
		sysList = Accu.getInstance().getSystemList().getList();
		Accu.getInstance().mSysList.addSystemListChangeListener(this);
		adapter = new SystemListAdapter(context,sysList);
		setAdapter(adapter);
		setChoiceMode(CHOICE_MODE_SINGLE);

		setOnItemClickListener(this);
		setOnItemLongClickListener(this);
	}
	
	
	public void setMainSys(Sys sys)
	{
		Accu.getInstance().setActiveSys(sys);
		Toast.makeText(context, "Main System change to " + sys.getName(), 4000).show();
	
		((SystemListAdapter)getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos,
			long arg3) {
		
		Sys sys = ((SystemListAdapter)parent.getAdapter()).getItem(pos);
		
		// If trying to reselect main vehicle do nothing
		if(sys==Accu.getInstance().getActiveSys())
			return false;
		
		setMainSys(sys);
		return true;
	}
	
	@Override
	public void onSystemListChange(ArrayList<Sys> list) {
		sysList = list;
		
		if(Accu.getInstance().getPrefs().getBoolean("vehicleOnly", false))
		{
			ArrayList<Sys> clone = new ArrayList<Sys>();
			for(Sys s: sysList)
				if(!s.getType().equals("CCU"))
					clone.add(s);
			adapter.setList(clone);
		}
		else
			adapter.setList(sysList);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(context, "Long-press to select active System", 3000).show();
	}
}
