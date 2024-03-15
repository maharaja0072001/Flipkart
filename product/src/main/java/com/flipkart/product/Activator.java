package com.flipkart.product;

import com.flipkart.authentication.controller.UserController;
import com.flipkart.authentication.json.JsonFactory;
import com.flipkart.product.controller.cart.CartController;
import com.flipkart.product.controller.inventory.InventoryController;
import com.flipkart.product.controller.order.OrderController;
import com.flipkart.product.controller.wishlist.WishlistController;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Starts and stops the osgi bundle. Contains JAX-RS resources for configuring server.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Activator implements BundleActivator {

    private Server server;
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
        LOGGER.info("Starting the bundle - product");
        final List<Object> serviceBeans = Arrays.asList(UserController.getInstance(), CartController.getInstance(),
                 WishlistController.getInstance(), InventoryController.getInstance(), OrderController.getInstance());
        final JAXRSServerFactoryBean serverFactory = new JAXRSServerFactoryBean();

        serverFactory.setAddress("/");
        serverFactory.setServiceBeans(serviceBeans);
        serverFactory.setProvider(JsonFactory.getProvider());
        server = serverFactory.create();
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
        LOGGER.info("Stopping the bundle - product");

        if (Objects.nonNull(server)) {
            server.destroy();
        }
    }
}
