package pt.lsts.accu;

import pt.lsts.accu.panel.AccuBasePanel;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.accu.R;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class AboutPanel extends AccuBasePanel {
	public AboutPanel(Context context) {
		super(context);
	}

	@Override
	public void onStart() {
		String html = "<p><b>A</b>ndroid <b>C</b>ommand and <b>C</>ontrol <b>U</>nit </p><p>";
		TextView title = (TextView) getLayout().findViewWithTag("title");
		TextView version = (TextView) getLayout().findViewWithTag("version");
		TextView authors = (TextView) getLayout().findViewWithTag("authors");
		TextView contributors = (TextView) getLayout().findViewWithTag(
				"contributors");
		TextView lab = (TextView) getLayout().findViewWithTag("lab");

		title.setText(Html.fromHtml(html));
		String versionString = "1.3.5-39-gbc6d1b4 (feature-estimatedState)";
		String dateString = "23-01-2015";
		version.setText("Version: " + versionString + "\n" + "Date: "
				+ dateString + "\n" + "IMC version: "
				+ IMCDefinition.getInstance().getVersion());
		authors.setText("Author: José Quadrado Correia");
		contributors
				.setText("Contributors: José Pinto, Hugo Queirós, Paulo Dias, José Loureiro");
		lab.setText("© Laboratório Sistemas e Tecnologias Subaquáticas");
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
