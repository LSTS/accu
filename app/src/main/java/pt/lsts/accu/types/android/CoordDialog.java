package pt.lsts.accu.types.android;

import pt.lsts.accu.FinderPanel;
import pt.lsts.accu.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CoordDialog extends Dialog implements android.view.View.OnClickListener 
{
	Button btnOk;
	private FinderPanel fp;
	
	public CoordDialog(Context context, FinderPanel fp) {
		super(context);
		initialize(context);
		this.fp = fp;
	}
	void initialize(Context context)
	{
		setTitle("Input GPS Coordinates");
		setContentView(R.layout.coord_dialog_layout);
		btnOk = (Button)findViewById(R.id.btnOk);
		
		btnOk.setOnClickListener(this);
	}
	
	public double getLat()
	{
		return Double.valueOf(((TextView)findViewById(R.id.valLat)).getText()+"");
	}
	public double getLon()
	{
		return Double.valueOf(((TextView)findViewById(R.id.valLon)).getText()+"");
	}
	@Override
	public void onClick(View view)
	{
		fp.setTarget(getLat(), getLon(), "GPS Point");
		fp.targetSys = null; // To prevent FinderPanel from overriding 
		dismiss();
	}
}
