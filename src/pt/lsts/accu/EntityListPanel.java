package pt.lsts.accu;
import pt.lsts.accu.components.EntityStateList;
import pt.lsts.accu.panel.AccuAction;
import pt.lsts.accu.panel.AccuBasePanel;
import pt.up.fe.dceg.accu.R;
import android.content.Context;
import android.view.View;

@AccuAction(name = "Entity List/State", icon=R.drawable.entitylist_icon_1 )
public class EntityListPanel extends AccuBasePanel {

	public EntityListPanel(Context context) {
		super(context);
	}

	@Override
	public int getIcon() {
		return R.drawable.entitylist_icon_1;
	}

	@Override
	public void onStart() {
		
	}

	@Override
	public void onStop() {
		
	}

	@Override
	public View buildLayout() {
		return new EntityStateList(this.getContext());
	}
	@Override
	public boolean requiresActiveSys() {
		return true;
	}
}
