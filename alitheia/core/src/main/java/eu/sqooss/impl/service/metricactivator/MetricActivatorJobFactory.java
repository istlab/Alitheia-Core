package eu.sqooss.impl.service.metricactivator;

import com.google.inject.assistedinject.Assisted;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.logging.Logger;

public interface MetricActivatorJobFactory {
	MetricActivatorJob create(@Assisted AbstractMetric m,
								@Assisted("daoID") Long daoID,
								@Assisted Logger l,
								@Assisted Class<? extends DAObject> daoType,
								@Assisted("priority") long priority,
								@Assisted boolean fastSync);
}
