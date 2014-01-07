package eu.sqooss.impl.service.webadmin;

import javax.servlet.http.HttpServletRequest;

import eu.sqooss.service.tds.TDSService;

/*
 * FIXME Elwin temp solution public abstract view
 * Class made for testing purpose for
 * This is a temporary solution
 */
public abstract class PublicAbstractView {
	/**
     * FIXME Elwin visibility 
     */
    public static String debugRequest (HttpServletRequest request) {
    	return AbstractView.debugRequest(request);
    }
    
	/**
     * FIXME Elwin visibility 
     */
    public static String sp(long num) {
    	return AbstractView.sp(num);
    }
    
    public static String normalInputRow (
    	String title, String parName, String parValue, long in
    ) {
    		return AbstractView.normalInputRow(title, parName, parValue, in);
    }
    
    public static String normalInfoRow (
        String title, String value, long in
    ) {
    	return AbstractView.normalInfoRow(title, value, in);
    }
    
    public static String normalFieldset (
            String name,
            String css,
            StringBuilder content,
            long in
    ) {
    	return AbstractView.normalFieldset(name, css, content, in);
    }
    
    public static String errorFieldset(StringBuilder errors, long in) {
    	return AbstractView.errorFieldset(errors, in);
    }
    
    public static Long fromString (String value) {
    	return AbstractView.fromString(value);
    }
    
    public static boolean checkName (String text) {
    	return AbstractView.checkName(text);
    }
    
    public static boolean checkProjectName (String text) {
    	return AbstractView.checkProjectName(text);
    }
    
    public static boolean checkEmail (String text) {
    	return AbstractView.checkEmail(text);
    }
    
    public static boolean checkTDSUrl(String url) {
    	return AbstractView.checkTDSUrl(url);
    }
    
}
