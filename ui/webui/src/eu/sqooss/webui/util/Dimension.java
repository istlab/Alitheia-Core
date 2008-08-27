package eu.sqooss.webui.util;

public class Dimension {
    private Long height;
    private Long width;

    private String cssUnit = CSSUnitPx;
    public static String CSSUnitPercent = "%";
    public static String CSSUnitIn = "in";
    public static String CSSUnitCm = "cm";
    public static String CSSUnitMm = "mm";
    public static String CSSUnitEm = "em";
    public static String CSSUnitEx = "ex";
    public static String CSSUnitPt = "pt";
    public static String CSSUnitPc = "pc";
    public static String CSSUnitPx = "px";

    public Dimension() {
        super();
    }

    public Dimension(Long height, Long width) {
        super();
        this.height = height;
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public String getCssUnit() {
        return cssUnit;
    }

    public void setCssUnit(String cssUnit) {
        this.cssUnit = cssUnit;
    }

}
