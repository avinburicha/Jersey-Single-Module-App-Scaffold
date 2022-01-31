package com.test.injection;

import com.test.constants.Constants;
import eu.infomas.annotation.AnnotationDetector;
import gov.va.oia.HK2Utilities.AnnotatedClasses;
import gov.va.oia.HK2Utilities.AnnotationReporter;
import gov.va.oia.HK2Utilities.HK2RuntimeInitializer;
import lombok.extern.log4j.Log4j2;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

@Log4j2
public class ServiceDependencyResolver implements ContainerLifecycleListener {

	private static ServiceLocator serviceLocator;


	public synchronized static <T> T getDependency (Class <T> clazz) {
		if (serviceLocator != null) {
			return serviceLocator.getService(clazz);
		}

		serviceLocator = createDefaultServiceLocator(Constants.APP_NAME);
		return serviceLocator.getService(clazz);
	}


	public synchronized static ServiceLocator createDefaultServiceLocator (String name) {
		final String[] packageNames = {Constants.DEFAULT_BASE_PACKAGE};
		try {
			return HK2RuntimeInitializer.init(name, false, packageNames);
		} catch (ClassNotFoundException | IOException e) {
			logger.error("Error Occurred while creating Default Service Locator => ", e);
		}
		return ServiceLocatorUtilities.createAndPopulateServiceLocator(name);
	}


	public synchronized static <T> T createAndInitialize (Class <T> serviceClazz) {
		if (serviceLocator != null) {
			return serviceLocator.createAndInitialize(serviceClazz);
		}
		serviceLocator = createDefaultServiceLocator(Constants.APP_NAME);
		return serviceLocator.createAndInitialize(serviceClazz);
	}


	public synchronized static <T> T getNamedDependency (Class <T> clazz, String name) {
		if (serviceLocator != null) {
			return serviceLocator.getService(clazz, name);
		}

		serviceLocator = createDefaultServiceLocator(Constants.APP_NAME);
		return serviceLocator.getService(clazz, name);
	}


	public synchronized static <T> T getAnnotatedDependency (Class <T> clazz, Annotation annotation, String name) {
		if (serviceLocator != null) {
			return serviceLocator.getService(clazz, name, annotation);
		}

		serviceLocator = createDefaultServiceLocator(Constants.APP_NAME);
		return serviceLocator.getService(clazz, name, annotation);
	}


	public static ServiceLocator getServiceLocator () {
		return serviceLocator;
	}


	public static void setServiceLocator (ServiceLocator serviceLocator) {
		ServiceDependencyResolver.serviceLocator = serviceLocator;
	}


	public synchronized static Class <?>[] getClassesWithAnnotationFromAPackage (Class <? extends Annotation> annotation, String... packageNames) throws ClassNotFoundException {
		final AnnotatedClasses ac = new AnnotatedClasses();

		try {
			@SuppressWarnings("unchecked")
			final AnnotationDetector cf = new AnnotationDetector(new AnnotationReporter(ac, new Class[]{annotation}));
			if (packageNames == null || packageNames.length == 0) {
				cf.detect();
			} else {
				cf.detect(packageNames);
			}
		} catch (final IOException e) {
			logger.error("Error While Getting Classes with Annotation @Service => ", e);
		}
		return ac.getAnnotatedClasses();
	}


	public synchronized static <T> Set <Class <? extends T>> getClassesExtendingInterfaceFromAPackage (Class <T> interfaceClazz, String packageName) {
		Reflections reflections = new Reflections(packageName);
		return reflections.getSubTypesOf(interfaceClazz);
	}


	@Override
	public void onStartup (Container container) {
		this.init(getServiceLocatorFromContainer(container));
	}


	private void init (ServiceLocator sl) {
		serviceLocator = sl;
	}


	private ServiceLocator getServiceLocatorFromContainer (Container container) {
		InjectionManager im = container.getApplicationHandler().getInjectionManager();
		return im.getInstance(ServiceLocator.class);
	}


	@Override
	public void onReload (Container container) {
		this.init(getServiceLocatorFromContainer(container));
	}


	@Override
	public void onShutdown (Container container) {
	}
}

