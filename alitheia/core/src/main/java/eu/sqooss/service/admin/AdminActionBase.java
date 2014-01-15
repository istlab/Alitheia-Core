package eu.sqooss.service.admin;

import java.util.HashMap;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LoggerName;

public abstract class AdminActionBase implements AdminAction {

    protected Map<String, Object> result;
    protected Map<String, Object> error;
    protected Map<String, Object> args;
    protected Map<String, Object> warnings;
    protected AdminActionStatus status;
    protected Long id;

    protected Logger log;
    
    protected AdminActionBase() {
        status = AdminActionStatus.CREATED;
        if (AlitheiaCore.getInstance() != null)
            log = AlitheiaCore.getInstance().getLogManager().createLogger(LoggerName.ADMIN);
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public void execute() throws Exception {
        status = AdminActionStatus.EXECUTING;
    }
    
    public final void finished(String results) {
        status = AdminActionStatus.FINISHED;
        result("result", results);
    }
    
    public final void result (String key, Object value) {
        if (result == null)
            result = new HashMap<>();
        result.put(key, value);
    }
    
    public final void warn (String key, Object value) {
        if (warnings == null)
            warnings = new HashMap<>();
        warnings.put(key, value);
    }
    
    @Override
    public final Map<String, Object> errors() {
        return error;
    }
    
    @Override
    public final Map<String, Object> results() {
        return result;
    }
    
    @Override
    public final Map<String, Object> args() {
        return args;
    }
    
    @Override
    public final Map<String, Object> warnings() {
        return args;
    }
    
    @Override
    public final void addArg(String key, Object value) {
    	if (args == null)
    		args = new HashMap<>();
    	args.put(key, value);
    }
    
    @Override
    public final void setArgs(Map<String, Object> args) {
        this.args = args;
    }
    
    @Override
    public final AdminActionStatus status() {
        return status;
    }
    
    @Override
    public boolean hasErrors()  {
    	if (status == AdminActionStatus.ERROR)
    		return true;
    	return false;	
    }
    
    protected final void error(String key, Object o) throws Exception {
        if (error == null)
            error = new HashMap<>();
        error.put(key, o);
        changeStatus(AdminActionStatus.ERROR);
        throw new Exception(o.toString());
    }

    protected final void error(Exception e) throws Exception {
        if (error == null)
            error = new HashMap<>();
        error.put("exception", e);
        
        changeStatus(AdminActionStatus.ERROR);
        throw e;
    }

    protected void log(String msg) {
        if (log != null)
            log.info(mnemonic() + ":" + msg);
    }
    
    protected void debug(String msg) {
        if (log != null)
            log.debug(mnemonic() + ":" + msg);
    }


    private synchronized void changeStatus(AdminActionStatus st) {
        switch (status) {
        case CREATED:
            if (st == AdminActionStatus.EXECUTING) {
                status = st;
            } else {
                status = AdminActionStatus.UNKNOWN;   
            }
            break;
        case EXECUTING:
            if (st == AdminActionStatus.FINISHED || st == AdminActionStatus.ERROR) {
                status = st;
            } else {
                status = AdminActionStatus.UNKNOWN;
            }
            break;
        case FINISHED:
            status = AdminActionStatus.UNKNOWN;
            break;
        case ERROR:
        case UNKNOWN:
        }
    }
    
    @Override
    public String toString() {
        String msg = "";
        msg += "args: ";
        msg += args;
        msg += ", status:";
        msg += status();
        return msg;
    }
}
