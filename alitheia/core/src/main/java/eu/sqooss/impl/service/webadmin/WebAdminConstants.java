package eu.sqooss.impl.service.webadmin;

public interface WebAdminConstants {
	///////////////
	/// PLUGINS ///
	///////////////
	// Request parameters
	public static final String REQ_PAR_ACTION = "action";
	public static final String REQ_PAR_HASHCODE = "pluginHashcode";
	public static final String REQ_PAR_PROP_NAME = "propertyName";
	public static final String REQ_PAR_PROP_DESC = "propertyDescription";
	public static final String REQ_PAR_PROP_TYPE = "propertyType";
	public static final String REQ_PAR_PROP_VALUE = "propertyValue";
	public static final String REQ_PAR_SHOW_PROP = "showProperties";
	public static final String REQ_PAR_SHOW_ACTV = "showActivators";
	// Recognized "action" parameter's values
	public static final String ACT_VAL_INSTALL = "installPlugin";
	public static final String ACT_VAL_UNINSTALL = "uninstallPlugin";
	public static final String ACT_VAL_SYNC = "syncPlugin";
	public static final String ACT_VAL_REQ_ADD_PROP = "createProperty";
	public static final String ACT_VAL_REQ_UPD_PROP = "updateProperty";
	public static final String ACT_VAL_CON_ADD_PROP = "confirmProperty";
	public static final String ACT_VAL_CON_REM_PROP = "removeProperty";
	
	////////////////
	/// PROJECTS ///
	////////////////
	// Action parameter's values
    public static final String ACT_REQ_ADD_PROJECT   = "reqAddProject";
    public static final String ACT_CON_ADD_PROJECT   = "conAddProject";
    public static final String ACT_REQ_REM_PROJECT   = "reqRemProject";
    public static final String ACT_CON_REM_PROJECT   = "conRemProject";
    public static final String ACT_REQ_SHOW_PROJECT  = "conShowProject";
    public static final String ACT_CON_UPD_ALL       = "conUpdateAll";
    public static final String ACT_CON_UPD           = "conUpdate";
    public static final String ACT_CON_UPD_ALL_NODE  = "conUpdateAllOnNode";

    // Servlet parameters
    public static final String REQ_PROJECT_PAR_ACTION= "reqAction";
    public static final String REQ_PAR_PROJECT_ID    = "projectId";
    public static final String REQ_PAR_PRJ_NAME      = "projectName";
    public static final String REQ_PAR_PRJ_WEB       = "projectHomepage";
    public static final String REQ_PAR_PRJ_CONT      = "projectContact";
    public static final String REQ_PAR_PRJ_BUG       = "projectBL";
    public static final String REQ_PAR_PRJ_MAIL      = "projectML";
    public static final String REQ_PAR_PRJ_CODE      = "projectSCM";
    public static final String REQ_PAR_SYNC_PLUGIN   = "reqParSyncPlugin";
    public static final String REQ_PAR_UPD           = "reqUpd";
}
