package eu.sqooss.metrics.abstractmetric;

public class ConversionNotSupportedException extends Exception {
    private static final long serialVersionUID = 1;
    public ConversionNotSupportedException(String msg) {
        super(msg);
    }
}
