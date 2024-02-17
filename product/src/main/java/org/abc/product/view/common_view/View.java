package org.abc.product.view.common_view;

import org.abc.authentication.view.AuthenticationView;
import org.abc.product.model.product.Product;
import org.abc.validation.Validator;
import org.abc.singleton_scanner.SingletonScanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * <p>
 * Provides common methods for all the view classes.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public abstract class View {

    private final Logger logger = LogManager.getLogger(AuthenticationView.class);
    private final Validator validator = Validator.getInstance();

    /**
     * <p>
     * Gets the choice from the user .
     * </p>
     * @return the choice
     */
    protected int getChoice() {
        final String choice = SingletonScanner.getScanner().nextLine().trim();

        if (validator.checkToGoBack(choice)) {
            return -1;
        }

        if (validator.isPositiveNumber(choice)) {
            return Integer.parseInt(choice);
        } else {
            logger.warn("Invalid choice");
            return getChoice();
        }
    }

    /**
     * <p>
     * Shows the items to the user .
     * </p>
     * @param products Refers the products to be shown.
     */
    protected void showItems(final List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            String quantityStatus = "";

            if (0 == products.get(i).getQuantity()) {
                quantityStatus = "(Out of Stock)";
            }
            logger.info(String.format("[%d : %s%s]", i + 1, products.get(i), quantityStatus));
        }
    }
}
