package eu.sqoooss.test.rest.api.utils;

import java.net.URISyntaxException;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;

public class TestUtils {
	
	public static MockHttpResponse fireMockGETHttpRequest(Class<?> c, String location)
			throws URISyntaxException {

		Dispatcher dispatcher = setupRequest(c);

		MockHttpRequest request = MockHttpRequest.get(location);
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		return response;
	}
	
	public static MockHttpResponse fireMockPUTHttpRequest(Class<?> c, String location)
			throws URISyntaxException {

		Dispatcher dispatcher = setupRequest(c);

		MockHttpRequest request = MockHttpRequest.put(location);
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		return response;
	}
	
	public static MockHttpResponse fireMockDELETEHttpRequest(Class<?> c, String location)
			throws URISyntaxException {

		Dispatcher dispatcher = setupRequest(c);

		MockHttpRequest request = MockHttpRequest.delete(location);
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		return response;
	}
	
	public static MockHttpResponse fireMockPOSTHttpRequest(Class<?> c, String location)
			throws URISyntaxException {

		Dispatcher dispatcher = setupRequest(c);

		MockHttpRequest request = MockHttpRequest.post(location);
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		return response;
	}

	private static Dispatcher setupRequest(Class<?> c) {
		Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

		POJOResourceFactory noDefaults = new POJOResourceFactory(c);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		return dispatcher;
	}

}
