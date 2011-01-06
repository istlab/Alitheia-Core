package eu.sqoooss.admin.test;

import eu.sqooss.admin.AdminActionBase;

public class SucceedingAction extends AdminActionBase {

    @Override
    public String getMnemonic() {
        return "win";
    }

    @Override
    public String getDescription() {
        return "An action that enjoys success";
    }

    @Override
    public void execute() {
        super.execute();
        result.put("1", "#win");
        finished();
    }
}