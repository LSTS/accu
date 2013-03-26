package pt.up.fe.dceg.accu.types;

public class EntityStateType {
	public static final int ENTITY_COL = 0, STATE_COL = 1,
			DONT_CARE_FLAG_COL = 2, DESCRIPTION_COL = 3, TIME_COL = 4;

	String entity;
	String state = null;
	boolean dontCare = false;
	String description;
	long timeDelta = 0;

	public EntityStateType(String entity, String state, String description, long timeDelta) {
		setEntity(entity);
		setState(state);
		setDescription(description);
		setTimeDelta(timeDelta);
	}

	public void update(String entity, String state, String description, long timeDelta) {
		setEntity(entity);
		setState(state);
		setDescription(description);
		setTimeDelta(timeDelta);
	}

	/**
	 * @return the device
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * @param entity the device to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
		
	}

	/**
	 * @return the event
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the event to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	
	
	/**
	 * @return the dontCare
	 */
	public boolean isDontCare() {
		return dontCare;
	}

	/**
	 * @param dontCare the dontCare to set
	 */
	public void setDontCare(boolean dontCare) {
		this.dontCare = dontCare;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the timeDelta
	 */
	public long getTimeDelta() {
		return timeDelta;
	}

	/**
	 * @param timeDelta the timeDelta to set
	 */
	public void setTimeDelta(long timeDelta) {
		this.timeDelta = timeDelta;
	}

	public void setElement(int columnIndex, Object value) {
		switch (columnIndex) {
		case ENTITY_COL:
			entity = (String)value;
			break;
		case STATE_COL:
			state = (String)value;
			break;
		case DONT_CARE_FLAG_COL:
			dontCare = (Boolean)value;
			break;
		case DESCRIPTION_COL:
			description = (String)value;
			break;
		case TIME_COL:
			timeDelta = (Long)value;
			break;
		default:
			break;
		}
	}
	public Object getElement(int columnIndex) {
		switch (columnIndex) {
		case ENTITY_COL:
			return entity;
		case STATE_COL:
			return state;
		case DONT_CARE_FLAG_COL:
			return dontCare;
		case DESCRIPTION_COL:
			return description;
		case TIME_COL:
			return timeDelta;
		default:
			return null;
		}
	}
}