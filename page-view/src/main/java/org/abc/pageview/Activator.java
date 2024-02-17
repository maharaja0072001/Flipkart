package org.abc.pageview;

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
     * Invoked when the osgi bundle starts.
     * </p>
     * @param context Refers the context of the bundle.
     */
    public void start(final BundleContext context) {
        logger.info("Starting the bundle - page-view");
    }

    /**
     * <p>
     * Invoked when the osgi bundle stops.
     * </p>
     * @param context Refers the context of the bundle.
     */
    public void stop(BundleContext context) {
        logger.info("Stopping the bundle - page-view");
    }
}