package org.abc.singleton_scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.sql.exec.spi.StandardEntityInstanceResolver;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

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
        logger.info("Starting the bundle - singleton-scanner");
    }

    /**
     * <p>
     * Invoked when the osgi bundle stops.
     * </p>
     * @param context Refers the context of the bundle.
     */
    public void stop(final BundleContext context) {
        logger.info("Stopping the bundle - singleton-scanner");
    }
}