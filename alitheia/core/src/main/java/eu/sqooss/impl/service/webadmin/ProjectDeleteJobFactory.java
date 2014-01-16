package eu.sqooss.impl.service.webadmin;

import eu.sqooss.service.db.StoredProject;

public interface ProjectDeleteJobFactory {
	ProjectDeleteJob create(StoredProject sp);
}
