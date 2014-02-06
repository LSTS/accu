package pt.lsts.accu;

import pt.lsts.accu.panel.AccuBasePanel;
import pt.lsts.imc.IMCDefinition;
import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class AboutPanel extends AccuBasePanel 
{	
	public AboutPanel(Context context) {
		super(context);
	}

	@Override
	public void onStart() 
	{		
		String html = "<p><b>A</b>ndroid <b>C</b>ommand and <b>C</>ontrol <b>U</>nit </p><p>";
		TextView title = (TextView)getLayout().findViewWithTag("title");
		TextView version = (TextView)getLayout().findViewWithTag("version");
		TextView authors = (TextView)getLayout().findViewWithTag("authors");
		TextView lab = (TextView)getLayout().findViewWithTag("lab");
		
		title.setText(Html.fromHtml(html));
		version.setText("Version: 1.0 Date: 15 Nov 2011 IMC version: " + IMCDefinition.getInstance().getVersion());
		authors.setText("Authors: José Quadrado Correia");
		lab.setText("Laboratório Sistemas e Tecnologias Subaquáticas");
	}

	@Override
	public void onStop() {
		
	}

	@Override
	public View buildLayout() {
		View v = inflateFromResource(R.layout.about_layout);
		
		return v;
	}

	@Override
	public int getIcon() {
		return R.drawable.icon;
	}
	
}
