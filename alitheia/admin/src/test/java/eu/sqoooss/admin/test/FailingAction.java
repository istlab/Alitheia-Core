package eu.sqoooss.admin.test;

import eu.sqooss.admin.AdminActionBase;

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
    public void execute() {
        super.execute();
        error("1", "#fail");
        throw new RuntimeException();
    }
}