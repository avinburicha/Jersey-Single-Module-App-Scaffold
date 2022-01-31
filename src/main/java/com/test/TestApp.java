package com.test;

import com.test.constants.Constants;
import com.test.injection.ServiceDependencyResolver;
import lombok.extern.log4j.Log4j2;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.TracingConfig;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

@Log4j2
@ApplicationPath("/test")
public class TestApp extends ResourceConfig {

	@Inject
	public TestApp (ServiceLocator serviceLocator) {
		System.out.println("Test Application Started");

		// Register Service Locator
		registerServices(serviceLocator);

		// Scan for Routes and Providers
		registerRoutesAndProviders();

		// Register Server Properties
		registerServerProperties();
	}


	private void registerServices (ServiceLocator serviceLocator) {
		try {
			final Class <?>[] ac = ServiceDependencyResolver.getClassesWithAnnotationFromAPackage(Service.class,
					Constants.DEFAULT_BASE_PACKAGE);
			for (final ActiveDescriptor <?> ad : ServiceLocatorUtilities.addClasses(serviceLocator, ac)) {
				logger.trace("Added {}", ad.toString());
			}
		} catch (final ClassNotFoundException e) {
			logger.error(e);
		}

		ServiceDependencyResolver.setServiceLocator(serviceLocator);
		this.register(serviceLocator);
	}


	private void registerRoutesAndProviders () {
		packages(true, Constants.DEFAULT_BASE_PACKAGE);
	}


	private void registerServerProperties () {
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		property(ServerProperties.APPLICATION_NAME, Constants.APP_NAME);
		property(ServerProperties.PROVIDER_SCANNING_RECURSIVE, true);
		property(ServerProperties.TRACING, TracingConfig.ALL.name());
		property(ServerProperties.TRACING_THRESHOLD, TracingLogger.Level.SUMMARY.name());
		property(ServerProperties.MONITORING_STATISTICS_ENABLED, true);
		register(LoggingFeature.class);
	}
}
