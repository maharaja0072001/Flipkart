package org.abc.product;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.abc.product.controller.cart.CartControllerREST;
import org.abc.product.controller.inventory.InventoryControllerREST;
import org.abc.product.controller.order.OrderControllerREST;
import org.abc.product.controller.wishlist.WishlistControllerREST;

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

    private final JacksonJsonProvider jacksonJsonProvider;
    private final Logger logger;
    private Server serverForCart;
    private Server serverForInventory;
    private Server serverForOrder;
    private Server serverForWishlist;

    /**
     * <p>
     * Constructs the Activator instance.
     * </p>
     */
    public Activator() {
        logger = LogManager.getLogger(Activator.class);
        jacksonJsonProvider = new JacksonJsonProvider();
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
        logger.info("Starting the bundle - product");
        serverForCart = createServerFactoryBean("/cart", CartControllerREST.getInstance()).create();
        serverForInventory = createServerFactoryBean("/inventory", InventoryControllerREST.getInstance()).create();
        serverForOrder = createServerFactoryBean("/order", OrderControllerREST.getInstance()).create();
        serverForWishlist = createServerFactoryBean("/wishlist", WishlistControllerREST.getInstance()).create();
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
        logger.info("Stopping the bundle - product");
        destroy(serverForCart);
        destroy(serverForInventory);
        destroy(serverForOrder);
        destroy(serverForWishlist);
    }

    /**
     * <p>
     * Destroys the server when bundle stops.
     * </p>
     * @param server Refers the server to be destroyed.
     */
    private void destroy(final Server server) {
        if (Objects.nonNull(server)) {
            server.destroy();
        }
    }

    /**
     * <p>
     * Creates a JAX-RS server factory bean with the specified address and service bean.
     * </p>
     *
     * @param address Refers the base address for the JAX-RS server.
     * @param serviceBean Refers the service bean instance.
     *
     * @return the created JAX-RS server factory bean.
     */
    private JAXRSServerFactoryBean createServerFactoryBean(final String address, final Object serviceBean) {
        final JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();

        bean.setAddress(address);
        bean.setBus(BusFactory.getDefaultBus());
        bean.setProvider(jacksonJsonProvider);
        bean.setServiceBean(serviceBean);

        return bean;
    }
}
