package eu.sqooss.parsers.java;

public class CodeFragment {
    
    private String fullyQualifiedName;
    private int startLine;
    private int endLine;
    
    public CodeFragment(String fullyQualifiedName, int startLine, int endLine) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }
    
    @Override
    public String toString() {
        return fullyQualifiedName + "[" + startLine + "," + endLine + "]";
    }
}
