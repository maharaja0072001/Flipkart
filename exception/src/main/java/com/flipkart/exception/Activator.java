package com.flipkart.exception;

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
    public void start(BundleContext context) {
        LOGGER.info("Starting the bundle - exception");
    }

    /**
     * <p>
     * Invoked when the osgi bundle stops.
     * </p>
     * @param context Refers the context of the bundle.
     */
    public void stop(BundleContext context) {
        LOGGER.info("Stopping the bundle - exception");
    }
}