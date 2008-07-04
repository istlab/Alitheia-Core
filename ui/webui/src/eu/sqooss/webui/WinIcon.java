package eu.sqooss.webui;

public class WinIcon {
    private String path;
    private String image;
    private String parameter;
    private String value;
    private String alt;
    
    public static String enable    = "true";
    public static String disable   = "false";

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getParameter() {
        return parameter;
    }
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getAlt() {
        return alt;
    }
    public void setAlt(String alt) {
        this.alt = alt;
    }

    public static WinIcon maximize (String path, String parameter) {
        WinIcon icon = new WinIcon();
        icon.setPath(path);
        icon.setParameter(parameter);
        icon.setValue(enable);
        icon.setImage("/img/icons/16x16/list-add.png");
        icon.setAlt("Show");
        return icon;
    }

    public static WinIcon minimize (String path, String parameter) {
        WinIcon icon = new WinIcon();
        icon.setPath(path);
        icon.setParameter(parameter);
        icon.setValue(disable);
        icon.setImage("/img/icons/16x16/list-remove.png");
        icon.setAlt("Hide");
        return icon;
    }
}
