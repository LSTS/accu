package pt.lsts.accu.panel;

public abstract class AccuBaseAction {
	public static final int TYPE_COMMAND = 0;
	public static final int TYPE_PANEL = 1;

	/**
	 * Override this method to supply an icon different than the annotation
	 * @return id of the Panel icon
	 */
	public int getIcon()
	{
		return getClass().getAnnotation(AccuAction.class).icon();
	}
	public abstract int getType();
	
	/**
	 * Override this method to disable panel when no Main system is selected
	 * @return
	 */
	public boolean requiresActiveSys() {
		return false;
	}
}
