package com.flipkart.authentication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * Starts and stops the osgi bundle. Contains JAX-RS resources for configuring server.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Activator implements BundleActivator {

    private static final Logger LOGGER = LogManager.getLogger(Activator.class);

    /**
     * <p>
     * Invoked when the osgi bundle starts. Initializes and starts JAX-RS server.
     * </p>
     *
     * @param context Refers the context of the bundle.
     */
    @Override
    public void start(final BundleContext context) {
        LOGGER.info("Starting bundle - authentication");
    }

    /**
     * <p>
     * Invoked when the osgi bundle stops. Stops and destroys the JAX-RS server.
     * </p>
     *
     * @param context Refers the context of the bundle.
     */
    @Override
    public void stop(final BundleContext context) {
        LOGGER.info("Stopping the bundle - authentication");
    }
}
