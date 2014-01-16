package eu.sqooss.impl.service.fds;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.Timeline;

public interface TimelineFactory {
	Timeline create(StoredProject project);
}
