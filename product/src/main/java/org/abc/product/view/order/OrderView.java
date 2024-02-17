package org.abc.product.view.order;

import org.abc.product.OrderStatus;
import org.abc.product.PaymentMode;
import org.abc.product.controller.order.OrderController;
import org.abc.product.model.order.Order;
import org.abc.product.model.product.Product;
import org.abc.product.view.common_view.View;
import org.abc.product.view.homepage.HomepageView;
import org.abc.singleton_scanner.SingletonScanner;
import org.abc.authentication.model.User;
import org.abc.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * <p>
 * Views the orders placed by the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class OrderView extends View {

    private static OrderView orderView;
    private final OrderController orderController;
    private final Scanner scanner;
    private final HomepageView homepageView;
    private final Logger logger;

    /**
     * <p>
     * Default constructor of OrderView class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private OrderView() {
        orderController = OrderController.getInstance();
        scanner = SingletonScanner.getScanner();
        homepageView = HomepageView.getInstance();
        logger = LogManager.getLogger(OrderView.class);
    }

    /**
     * <p>
     * Creates a single object of OrderView class and returns it.
     * </p>
     *
     * @return the single instance of OrderView class.
     */
    public static OrderView getInstance() {
        return Objects.isNull(orderView) ? orderView = new OrderView() : orderView;
    }

    /**
     * <p>
     * Shows the order details of the user.
     * </p>
     *
     * @param user Refers {@link User} whose orders will be shown.
     */
    public void viewAndCancelOrder(final User user) {
        final List<Order> orders = orderController.getOrders(user.getId());

        if (Objects.isNull(orders) || orders.isEmpty()) {
            logger.info(String.format("User id :%d - No orders found", user.getId()));
            homepageView.showHomePage(user);
        } else {
            for (int i = 0; i < orders.size(); i++) {
                logger.info(String.format("%d :[%s]\n", i + 1, orders.get(i)));
            }
            logger.info("Enter the order no to cancel the order.[Press '$' to go back]");
            final int index = getChoice();

            toGoBack(index, user);

            if (orders.size() >= index) {
                final Order order = orders.get(index - 1) ;

                orderController.cancelOrder(order);
                logger.info(String.format("User id :%d Order id :%d - Your order has been cancelled",
                        user.getId(), order.getId()));

            } else {
                logger.warn("Enter a valid number");
                viewAndCancelOrder(user);
            }
        }
    }

    /**
     * <p>
     * Places the order for the user.
     * </p>
     *
     * @param product Refers the product to be placed.
     * @param user Refers the current {@link User}
     */
    public void placeOrder(final Product product, final User user){
        final int productQuantity = getQuantity(user, product);
        final String address = getAddress(orderController.getAllAddresses(user.getId()), user);
        final PaymentMode paymentMode = getPaymentMode(user);

        orderController.addOrder(user.getId(), new Order.OrderBuilder(user.getId())
                .setAddress(address).setQuantity(productQuantity).setTotalAmount((float) (productQuantity * product.getPrice()))
                .setProductName(product.toString()).setOrderStatusId(OrderStatus.PLACED.getId()).setPaymentModeId(paymentMode.getId())
                .setProductId(product.getId()).build());
        product.setQuantity(product.getQuantity() - productQuantity);
        logger.info(String.format("User Id :%d Product Id :%d - Order placed successfully", user.getId(), product.getId()));
    }

    /**
     * <p>
     * Gets the payment mode from the user.
     * </p>
     *
     * @param user Refers the current {@link User}
     */
    private PaymentMode getPaymentMode(final User user) {
        logger.info("Choose your payment mode : \n1.Cash On Delivery\n2.Credit or Debit card\n3.Net Banking\n4.UPI");
        final int choice = getChoice();

        toGoBack(choice, user);

        switch (choice) {
            case 1 -> {
                return PaymentMode.CASH_ON_DELIVERY;
            }
            case 2 -> {
                return PaymentMode.CREDIT_OR_DEBIT_CARD;
            }
            case 3 -> {
                return PaymentMode.NET_BANKING;
            }
            case 4 -> {
                return PaymentMode.UPI;
            }
            default -> logger.warn("Enter a valid choice");
        }

        return getPaymentMode(user);
    }

    /**
     * <p>
     * Gets the quantity of product needed to place the order.
     * </p>
     *
     * @param product Refers the product to be placed.
     * @param user Refers the current {@link User}
     * @return the quantity of product need to place the order.
     */
    private int getQuantity(final User user, final Product product) {
        logger.info("Enter the quantity : [Press '$' to go back]");
        final String quantity = scanner.nextLine().trim();

        toGoBack(quantity, user);

        if (Validator.getInstance().isPositiveNumber(quantity) && Integer.parseInt(quantity) <= product.getQuantity()) {
            return Integer.parseInt(quantity);
        } else {
            logger.warn(String.format("User id :%d Product id:%d - Enter a valid quantity. Available quantity :%d",
                    product.getId(), user.getId(), product.getQuantity()));

            return getQuantity(user, product);
        }
    }

    /**
     * <p>
     * Gets a address of the user.
     * </p>
     *
     * @param addresses Refers the addresses of the user.
     * @param user Refers the current {@link User}
     * @return the address of the user.
     */
    private String getAddress(final List<String> addresses, final User user) {
        if (Objects.isNull(addresses) || addresses.isEmpty()) {
            logger.info("Enter a address : [Press '$' to go back]");
            final String newAddress = scanner.nextLine().trim();

            toGoBack(newAddress, user);
            orderController.addAddress(user.getId(), newAddress);

            return newAddress;
        }
        showAllAddress(addresses);
        logger.info("Press '1' to select the address or '2' to add new address");
        final int choice = getChoice();

        toGoBack(choice, user);

        switch (choice) {
            case 1 -> {
                logger.info("Enter the index:");
                final String index = scanner.nextLine().trim();
                toGoBack(index, user);

                return orderController.getAllAddresses(user.getId()).get(Integer.parseInt(index));
            }
            case 2 -> {
                logger.info("Enter a new address:");
                final String newAddress = scanner.nextLine().trim();
                toGoBack(newAddress, user);
                orderController.addAddress(user.getId(), newAddress);

                return newAddress;
            }
            default -> logger.warn("Enter a valid choice");
        }

        return getAddress(addresses, user);
    }

    /**
     * <p>
     * Shows all the addresses of the user.
     * </p>
     *
     * @param addresses Refers the addresses of the user.
     */
    private void showAllAddress(final List<String> addresses) {
        for (int i = 0; i < addresses.size(); i++) {
            logger.info(String.format("%d :[%s]\n", i + 1, addresses.get(i)));
        }
    }

    /**
     * <p>
     * Goes back to the previous page.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param input Refers the input given by the user
     */
    private void toGoBack(final int input, final User user) {
        if (-1 == input) {
            homepageView.showHomePage(user);
        }
    }

    /**
     * <p>
     * Goes back to the previous page.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @param input Refers the input given by the user
     */
    private void toGoBack(final String input, final User user) {
        if (Validator.getInstance().checkToGoBack(input)) {
            homepageView.showHomePage(user);
        }
    }
}
