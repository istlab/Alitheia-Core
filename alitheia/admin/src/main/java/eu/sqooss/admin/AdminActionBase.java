package eu.sqooss.admin;

import java.util.HashMap;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;

public abstract class AdminActionBase implements AdminAction {

    protected Map<String, Object> result;
    protected Map<String, Object> error;
    protected Map<String, Object> args;
    protected AdminActionStatus status;
    protected Long id;

    protected Logger log;
    
    protected AdminActionBase() {
        status = AdminActionStatus.CREATED;
        if (AlitheiaCore.getInstance() != null)
            log = AlitheiaCore.getInstance().getLogManager().createLogger("sqooss.admin");
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
    public void execute() {
        status = AdminActionStatus.EXECUTING;
    }
    
    public final void finished() {
        status = AdminActionStatus.FINISHED;
    }
    
    public final void result (String key, Object value) {
        if (result == null)
            result = new HashMap<String, Object>();
        result.put(key, value);
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
    public final void setArgs(Map<String, Object> args) {
        this.args = args;
    }
    
    @Override
    public final AdminActionStatus getStatus() {
        return status;
    }
    
    protected final void error(String key, Object o) {
        if (error == null)
            error = new HashMap<String, Object>();
        error.put(key, o);
        changeStatus(AdminActionStatus.ERROR);
    }
    
    protected final void error(Exception e) {
        if (error == null)
            error = new HashMap<String, Object>();
        error.put("exception", e);
        
        changeStatus(AdminActionStatus.ERROR);
    }

    protected void info(String msg) {
        log.info(getMnemonic() + ":" + msg);
    }

    protected void warn(String msg) {
        log.warn(getMnemonic() + ":" + msg);
    }

    protected void err(String msg) {
        log.error(getMnemonic() + ":" + msg);
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
}
