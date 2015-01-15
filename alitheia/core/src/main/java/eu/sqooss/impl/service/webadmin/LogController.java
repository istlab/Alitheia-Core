package eu.sqooss.impl.service.webadmin;

import java.util.Collections;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.util.StringUtils;

public class LogController extends ActionController {

    /**
     * Instantiates a new log controller.
     */
    public LogController() {
        super("logs.html");
    }
    
    /**
     * Write logs to the VelocityContext
     *
     * @param requestParameters the request parameters
     * @param velocityContext the velocity context
     */
    @Action
    public void showLogs(Map<String, String> requestParameters, VelocityContext velocityContext) {
        AlitheiaCore core = AlitheiaCore.getInstance();
        if (null == core) {
            addDanger(Localization.getMsg("core_unavailable"));
            velocityContext.put("logs", Collections.emptyList());
            return;
        }
        LogManager logManager = core.getLogManager();
        if (null == logManager) {
            addWarning(Localization.getMsg("no_log_manager"));
            velocityContext.put("logs", Collections.emptyList());
            return;
        }
        String[] logs = logManager.getRecentEntries();
        
        if ((logs != null) && (logs.length > 0)) {
            String[] xhtmlSafeLogs = new String[logs.length];
            int i = 0;
            for (String s : logs) {
                xhtmlSafeLogs[i++] = StringUtils.makeXHTMLSafe(s);
            }

            velocityContext.put("logs", xhtmlSafeLogs);
        } else {
            velocityContext.put("logs", Collections.emptyList());
        }
    }
    
}
