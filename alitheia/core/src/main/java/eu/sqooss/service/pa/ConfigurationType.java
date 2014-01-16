package eu.sqooss.service.pa;

/**
 * This enumeration includes all permitted types of configuration values,
 * that a metrics can support. The various configuration parameters and
 * their values are used mostly from internal metric processes, like
 * results rendering and validation.
 */
public enum ConfigurationType {
    INTEGER,
    STRING,
    BOOLEAN,
    DOUBLE;
    
    public void checkValue(String value) throws InvalidValueForTypeException{
    	if( value == null ){
    		throw new InvalidValueForTypeException("null","is not a valid value for any type");
    	} else if( this.equals(INTEGER) ){
    		try{
    			Integer.valueOf(value);
    		} catch( Exception e ){
    			throw new InvalidValueForTypeException(value,"is not a valid integer value");
    		}
    	} else if( this.equals(BOOLEAN) ){
    		if( !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
    			throw new InvalidValueForTypeException(value,"is not a valid boolean value");
    		}
    	} else if( this.equals(DOUBLE) ){
    		try{
    			Double.valueOf(value);
    		} catch( Exception e ){
    			throw new InvalidValueForTypeException(value,"is not a valid double value");
    		}
    	}
    }
    
    public static class InvalidValueForTypeException extends Exception{
		public InvalidValueForTypeException(String value, String error) {
			super("'" + value + "' " + error + "!");
		}
    }
}