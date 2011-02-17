package eu.sqooss.service.db;

/**
 * Single point of reference for all programming languages supported by Alitheia
 * Core.
 * 
 * <b>Warning:</b> Do not change the order or name of of the enumeration values
 * unless you are prepared to do manual changes to the database data!
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
public enum Language {
    
    /*DO NOT CHANGE NAMES OR ORDER (extensions are OK to add/remove)*/
    C (new String[] {".c", ".h"}),
    CPP (new String[] {".C", ".cpp", ".hpp", ".H", ".hxx"}),
    CSHARP (new String[] {".cs"}),
    JAVA (new String[] {".java"}),
    PYTHON (new String[] {".py"}),
    RUBY (new String[] {".rb"}),
    PERL (new String[] {".pl", ".cgi", ".perl", ".pad", ".pm"}),
    SCALA (new String[] {".scala"}),
    FORTRAN (new String[] {".f", ".F"}),
    SHELL (new String[] {".sh", ".bash", ".zsh"}),
    PHP (new String[] {".php", ".php3", ".php4", ".php5", ".phpt"}),
    SQL (new String[] {".sql"}),
    JAVASCRIPT (new String[] {".js"});
    /*Add new languages here*/
    
    private final String[] extensions;
    
    private Language(String[] extensions) {
        this.extensions = extensions;
    }
    
    public String[] extensions() {
        return extensions;
    }
}
