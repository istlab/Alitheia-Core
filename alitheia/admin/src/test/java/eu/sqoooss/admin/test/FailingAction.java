package eu.sqoooss.admin.test;

import eu.sqooss.admin.AdminActionBase;

public class FailingAction extends AdminActionBase {

    @Override
    public String getMnemonic() {
        return "fail";
    }

    @Override
    public String getDescription() {
        return "An action that enjoys to fail itself";
    }

    @Override
    public void execute() {
        super.execute();
        err("#fail");
    }
}