package eu.sqooss.webui;

public class InputValidator {
    public boolean isEmpty (String parameter) {
        if ((parameter != null) && (parameter.length() > 0)) {
            return false;
        }
        return true;
    }
}
