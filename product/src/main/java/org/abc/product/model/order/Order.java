package org.abc.product.model.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import jakarta.validation.constraints.Size;
import org.abc.product.OrderStatus;
import org.abc.product.PaymentMode;
import org.abc.product.validation.group.AddressChecker;
import org.abc.product.validation.group.OrderChecker;
import org.abc.product.validation.group.UserIdChecker;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * <p>
 * Represents a order placed by the user and contains all the order related information.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Entity(name = "orders")
@JsonDeserialize(builder = Order.OrderBuilder.class)
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "product_id")
    private int productId;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "total_amount")
    private float totalAmount;
    @Column(length = 100)
    private String address;
    @Column(name = "payment_mode_id")
    private int paymentModeId;
    @Transient
    private String productName;
    @Column(name = "order_status_id")
    private int orderStatusId;

    public Order() {}
    private Order(final OrderBuilder orderBuilder) {
        this.productId = orderBuilder.productId;
        this.userId = orderBuilder.userId;
        this.address = orderBuilder.address;
        this.quantity = orderBuilder.quantity;
        this.productName = orderBuilder.productName;
        this.totalAmount = orderBuilder.totalAmount;
        this.paymentModeId = orderBuilder.paymentModeId;
        this.id = orderBuilder.id;
        this.orderStatusId = orderBuilder.orderStatusId;
    }

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public int getUserId() {
        return userId;
    }

    public String getAddress() {
        return address;
    }

    public int getPaymentModeId() {
        return paymentModeId;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(final int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    @Override
    public String toString() {
        return String.format("Order id : %d\n%s\nproduct quantity : %d\ntotal amount : %.2f\nPayment mode : %s\nShipping address : %s\nStatus : %s",
                id, productName, quantity, totalAmount, PaymentMode.valueOf(paymentModeId), address, OrderStatus.valueOf(orderStatusId));
    }

    /**
     * <p>
     * Represents a static OrderBuilder class implement using builder design pattern which creates order instance.
     * </p>
     *
     * @author Maharaja S
     * @version 1.0
     */
    public static class OrderBuilder {

        @Positive(message = "Order id should be positive", groups = OrderChecker.class)
        private int id;
        @Positive(message = "User id should be positive", groups = UserIdChecker.class)
        private final int userId;
        @Positive(message = "Product id should be positive", groups = OrderChecker.class)
        private int productId;
        @Positive(message = "Order quantity should be positive", groups = OrderChecker.class)
        private int quantity;
        @Positive(message = "Total amount should be positive", groups = OrderChecker.class)
        private float totalAmount;
        @NotNull(message = "Address can't be null", groups = AddressChecker.class)
        @Size(min=10 , max = 100, groups = AddressChecker.class)
        private String address;
        @Positive(message = "Payment mode id can't be negative", groups = OrderChecker.class)
        private int paymentModeId;
        @NotNull(message = "Product name can't be null", groups = OrderChecker.class)
        private String productName;
        @Positive(message = "Order status id can't be negative", groups = OrderChecker.class)
        private int orderStatusId;

        /**
         * Utilizes the builder pattern to construct the instance of {@link Order}.
         */
        @JsonCreator
        public OrderBuilder(@JsonProperty("userId")final int userId) {
           this.userId = userId;
        }

        @JsonProperty("address")
        public OrderBuilder setAddress(final String address) {
            this.address = address;

            return this;
        }

        @JsonProperty("paymentMode")
        public OrderBuilder setPaymentModeId(final int paymentModeId) {
            this.paymentModeId = paymentModeId;

            return this;
        }

        @JsonProperty("productId")
        public OrderBuilder setProductId(final int productId) {
            this.productId = productId;

            return this;
        }

        @JsonProperty("id")
        public OrderBuilder setId(final int id) {
            this.id = id;

            return this;
        }

        @JsonProperty("totalAmount")
        public OrderBuilder setTotalAmount(final float totalAmount) {
            this.totalAmount = totalAmount;

            return this;
        }

        @JsonProperty("quantity")
        public OrderBuilder setQuantity(final int quantity) {
            this.quantity = quantity;

            return this;
        }

        @JsonProperty("productName")
        public OrderBuilder setProductName(final String productName) {
            this.productName = productName;

            return this;
        }

        @JsonProperty("orderStatus")
        public OrderBuilder setOrderStatusId(final int orderStatusId) {
            this.orderStatusId = orderStatusId;

            return this;
        }

        public Order build() {
            return new Order(this) ;
        }
    }
}
