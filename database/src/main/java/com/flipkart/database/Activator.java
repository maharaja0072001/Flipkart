package com.flipkart.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * Starts and stops the osgi bundle.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Activator implements BundleActivator {

    private static final Logger LOGGER = LogManager.getLogger(Activator.class);

    /**
     * <p>
     * Invoked when the osgi bundle starts.
     * </p>
     * @param context Refers the context of the bundle.
     */
    @Override
    public void start(final BundleContext context) {
        LOGGER.info("Starting the bundle - database");
    }

    /**
     * <p>
     * Invoked when the osgi bundle stops.
     * </p>
     * @param context Refers the context of the bundle.
     */
    @Override
    public void stop(final BundleContext context) {
        LOGGER.info("Stopping the bundle - database");
    }
}
