package eu.sqoooss.test.rest.api.utils;

import java.net.URISyntaxException;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;

public class TestUtils {
	
	public static MockHttpResponse fireMockHttpRequest(Class<?> c, String location)
			throws URISyntaxException {

		Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

		POJOResourceFactory noDefaults = new POJOResourceFactory(c);
		dispatcher.getRegistry().addResourceFactory(noDefaults);

		MockHttpRequest request = MockHttpRequest.get(location);
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		return response;
	}

}
