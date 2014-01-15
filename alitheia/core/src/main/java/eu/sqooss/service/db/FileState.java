package eu.sqooss.service.db;

public enum FileState {
	ADDED(0x1),
	MODIFIED(0x2),
	DELETED(0x4),
	REPLACED(0x8);

	private int state;

	FileState(int state) {
		this.state = state;
	}

	public final int getState() {
		return this.state;
	}

	public static FileState fromInt(int i) {
		switch (i) {
			case 0x1:
				return ADDED;
			case 0x2:
				return MODIFIED;
			case 0x4:
				return DELETED;
			case 0x8:
				return REPLACED;
			default:
				throw new IllegalArgumentException();
		}
	}
}
