package eu.sqooss.vcs;

public class InvalidRepositoryException extends Exception{

    private static final long serialVersionUID = 1L;
    
    private String message;
    
    public InvalidRepositoryException(String s) {
	this.message = s;
    }
    
    public String getMessage(){
	return message;
    }

}
