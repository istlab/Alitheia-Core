package eu.sqooss.impl.service.messaging.senders.smtp.connection;

public abstract class ConnectionException extends Exception {

	public static int TYPE_POP3 = 1;
	public static int TYPE_SMTP = 2;
	public static int TYPE_MIME = 4;
	public static int TYPE_MAIL = 8;
	public static int TYPE_CONTACTS = 16;
	public static int TYPE_ACCOUNT = 32;
	public static int TYPE_STORAGE = 64;

	protected int type = 0;

	
	private Throwable cause;
  protected int errorCode = 0;
  protected String errorMessage = null;
	
	public ConnectionException() {
		super();
	}

	public ConnectionException(String arg0) {
		super(arg0);
		errorMessage = arg0;
	}

	public ConnectionException(Throwable arg0) {
		this((arg0 != null)? arg0.getMessage(): null);
		this.cause = arg0;
	}

	public ConnectionException(String arg0, Throwable arg1) {
		this(arg0);
		this.cause = arg1;
	}

	public Throwable getCause() {
		return cause;
	}
	
	public int getType() {
		return type;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setType(int type) {
		this.type = type;
	}
}
