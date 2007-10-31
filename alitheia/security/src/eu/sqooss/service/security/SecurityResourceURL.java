package eu.sqooss.service.security;

public interface SecurityResourceURL {

    /**
     * @return the id of the resource url
     */
    public long getId();

    /**
     * @return the resource url
     */
    public String getURL();

    /**
     * Sets a new resource url.
     * @param resourceURL the new resource url
     */
    public void setURL(String resourceURL);

    /**
     * Removes the resource url.
     */
    public void remove();

}
