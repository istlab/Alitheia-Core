package eu.sqooss.service.util;

import java.net.URI;

public class URIUtills {

	public static URI toURI(String url) {
		try {
			url = url.replace("\\", "/").replace(" ", "%20");
			return URI.create(url);
		} catch (IllegalArgumentException iae) {
			return null;
		}
	}
}
