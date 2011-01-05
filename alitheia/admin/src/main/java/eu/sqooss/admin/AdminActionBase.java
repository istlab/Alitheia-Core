package eu.sqooss.admin;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;

public abstract class AdminActionBase implements AdminAction {

    private AdminActionResult result;
    private AdminActionError error;
    private AdminActionStatus status;

    private Logger log;
    
    protected AdminActionBase() {
        status = AdminActionStatus.CREATED;
        log = AlitheiaCore.getInstance().getLogManager().createLogger("sqooss.admin");
    }

    @Override
    public final AdminActionResult getResult() {
        return result;
    }

    @Override
    public final AdminActionError getError() {
        return error;
    }
    
    @Override
    public final AdminActionStatus getStatus() {
        return status;
    }
    
    protected final void error(AdminActionError e) {
        this.error = e;
        changeStatus(AdminActionStatus.ERROR);
    }
    
    protected final void error(Exception e) {
        AdminActionError err = new AdminActionError();
        
        changeStatus(AdminActionStatus.ERROR);
    }

    protected final void success(AdminActionResult r) {
        this.result = r;
        changeStatus(AdminActionStatus.FINISHED);
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
