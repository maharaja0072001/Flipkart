package org.abc.authentication;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.abc.authentication.controller.UserControllerREST;

import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.Objects;

/**
 * <p>
 * Starts and stops the osgi bundle. Contains JAX-RS resources for configuring server.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Component
public class Activator implements BundleActivator {

    private Server server;
    private final Logger logger;

    /**
     * <p>
     * Constructs the Activator instance.
     * </p>
     */
    public Activator() {
        logger = LogManager.getLogger(Activator.class);
    }

    /**
     * <p>
     * Invoked when the osgi bundle starts. Initializes and starts JAX-RS server.
     * </p>
     * @param context Refers the context of the bundle.
     */
    @Activate
    @Override
    public void start(final BundleContext context) {
        logger.info("Starting bundle - authentication");
        final JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();

        bean.setAddress("/user");
        bean.setBus(BusFactory.getDefaultBus());
        bean.setProvider(new JacksonJsonProvider());
        bean.setServiceBean(UserControllerREST.getInstance());

        server = bean.create();
    }

    /**
     * <p>
     * Invoked when the osgi bundle stops. Stops and destroys the JAX-RS server.
     * </p>
     * @param context Refers the context of the bundle.
     */
    @Deactivate
    @Override
    public void stop(final BundleContext context) {
        logger.info("Stopping the bundle - authentication");

        if (Objects.nonNull(server)) {
            server.destroy();
        }
    }
}
