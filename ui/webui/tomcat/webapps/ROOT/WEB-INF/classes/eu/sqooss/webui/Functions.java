package eu.sqooss.webui;

public class Functions {
    
    public static final String NOT_YET_EVALUATED =
        "This project has not yet been evaluated!";

    public static String dude() {
        return "<h1>Duuuuuuude!</h1>";
    }

    public static String error(String msg) {
        return "<strong><font color=\"red\">" + msg + "</font></strong>";
    }

    public static String debug(String msg) {
        return "<strong><font color=\"orange\">" + msg + "</font></strong>";
    }

}
