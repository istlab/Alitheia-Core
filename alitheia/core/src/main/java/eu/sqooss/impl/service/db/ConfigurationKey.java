package eu.sqooss.impl.service.db;

import eu.sqooss.properties.PropertyKey;

public enum ConfigurationKey implements PropertyKey {
	
	DRIVER_CLASS("hibernate.connection.driver_class"),
	URL("hibernate.connection.url"),
	USERNAME("hibernate.connection.username"),
	PASSWORD("hibernate.connection.password"),
	DIALECT("hibernate.connection.dialect"),
	PROVIDER_CLASS("hibernate.connection.provider_class");
	
	private String key;
	
	ConfigurationKey(String key) {
		this.key = key;
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
}