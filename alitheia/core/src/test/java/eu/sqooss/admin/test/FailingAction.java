package eu.sqooss.admin.test;

import eu.sqooss.service.admin.AdminActionBase;

public class FailingAction extends AdminActionBase {

    @Override
    public String mnemonic() {
        return "fail";
    }

    @Override
    public String descr() {
        return "An action that enjoys to fail itself";
    }

    @Override
    public void execute() throws Exception {
        super.execute();
        error("1", "#fail");
        throw new RuntimeException();
    }
}