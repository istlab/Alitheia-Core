package eu.sqooss.impl.service.logging.utils;

import eu.sqooss.impl.service.logging.LogManagerConstants;

public class LogUtils {

    /**
     * Determines the level of the logger's name in the logger hierarchy. 
     * @param name the logger name
     * @return
     * <ul>
     *  <li> -1 - if the name is not valid
     *  <li> 0 - for sqooss (for SQO-OSS system logger)
     *  <li> 1 - for sqooss.service_name (for the children of the SQO-OSS system logger) 
     *  <li> 2 - for sqooss.service.plugin_name (for the children of service system logger i.e. metric plug-ins loggers)
     * </ul>
     */
    public static int getNameLevel(String name) {
        //null, "", incorrect.service - false
        if ((name == null) || (name.trim().equals("") ||
                (!name.startsWith(LogManagerConstants.NAME_ROOT_LOGGER)))) {
            return -1;
        }

        //sqooss - true
        if (LogManagerConstants.NAME_ROOT_LOGGER.equals(name)) {
            return 0;
        }

        int firstDelimPos = name.indexOf(LogManagerConstants.NAME_DELIMITER);
        int secondDelimPos = name.indexOf(LogManagerConstants.NAME_DELIMITER, firstDelimPos+1);

        int rootNameLength = LogManagerConstants.NAME_ROOT_LOGGER.length();
        //checks the position of the first delimiter
        if (firstDelimPos != (rootNameLength)) {
            return -1;
        }

        //sqooss.
        String nameSecondPart = name.substring(firstDelimPos + 1);
        if (nameSecondPart.equals(LogManagerConstants.SIBLING_DATABASE) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_MESSAGING) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_SCHEDULING) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_SECURITY) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_SERVICE_SYSTEM) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_UPDATER) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_WEB_SERVICES) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_WEBUI) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_TDS) ||
            nameSecondPart.equals(LogManagerConstants.SIBLING_FDS)) {
            
            return 1;
        }

        //checks the position of the second delimiter 
        if ((secondDelimPos != (rootNameLength + LogManagerConstants.SIBLING_SERVICE_SYSTEM.length() + 1)) ||
                (!nameSecondPart.startsWith(LogManagerConstants.SIBLING_SERVICE_SYSTEM))){
            return -1;
        } else {
            return 2;
        }
    }

    /**
     * @param name the logger name
     * @return the name of the parent logger, or empty string otherwise
     */
    public static String getParentLoggerName(String name) {
        int lastDelimiterPosition = name.lastIndexOf(LogManagerConstants.NAME_DELIMITER);
        if (lastDelimiterPosition == -1) {
            return "";
        } else {
            return name.substring(0, lastDelimiterPosition);
        }
    }

}
