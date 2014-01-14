package eu.sqooss.impl.service.cluster;

import com.google.inject.AbstractModule;

import eu.sqooss.service.cluster.ClusterNodeService;

public class ClusterNodeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ClusterNodeService.class).to(ClusterNodeServiceImpl.class);
    }

}
