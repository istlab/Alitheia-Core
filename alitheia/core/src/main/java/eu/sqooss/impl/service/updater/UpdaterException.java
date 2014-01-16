package eu.sqooss.impl.service.updater;

public class UpdaterException extends Exception {
	String error;
	public UpdaterException(String s) {
		error = s;
	}
	public String toString() {
		return error;
	}
}
