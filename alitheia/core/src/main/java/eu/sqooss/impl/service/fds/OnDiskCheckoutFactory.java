package eu.sqooss.impl.service.fds;

import java.io.File;

import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.tds.SCMAccessor;

public interface OnDiskCheckoutFactory {
	OnDiskCheckout create(SCMAccessor accessor, String path,
            					ProjectVersion pv, File root);
}
