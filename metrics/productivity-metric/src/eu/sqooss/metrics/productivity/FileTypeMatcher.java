package eu.sqooss.metrics.productivity;

public class FileTypeMatcher {

    public enum FileType {
        SRC, BIN, DOC, XML, TXT
    }

    private static String[] srcMimes = { ".c", ".java", ".h", ".py", "cpp", ".C",
            ".properties", ".po", ".sh", ".rb", ".el", ".m4", ".cs" };

    private static String[] docMimes = { ".txt", ".sgml", ".html", ".tex" };

    private static String[] xmlFormats = { ".xml", ".svn", ".argo", ".graffle", ".vcproj", ".csproj" };

    private static String[] binMimes = { ".pdf", ".png", ".jpg", ".tiff", ".dvi",
            ".gz", ".zip", ".properties", ".gif", ".exe", ".jar", ".doc",
            ".png", ".o", ".class", ".pyc" };
    
    public static FileType getFileType(String path) {
        for(String s: srcMimes)
            if(path.endsWith(s))
                return FileType.SRC;
        
        for(String s: docMimes)
            if(path.endsWith(s))
                return FileType.DOC;
        
        for(String s: xmlFormats)
            if(path.endsWith(s))
                return FileType.XML;
        
        for(String s: binMimes)
            if(path.endsWith(s))
                return FileType.BIN;
        
        return FileType.TXT;
        
    }
    
    public static FileType getFileTypeFromExt(String ext) {
        
        return null;
        
    }

}
