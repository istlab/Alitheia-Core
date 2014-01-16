package eu.sqooss.impl.service.scheduler;

import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.WorkerThread;

public interface WorkerThreadFactory {
	WorkerThread create(Scheduler s, int n);
	WorkerThread create(Scheduler s, boolean oneshot);
}
