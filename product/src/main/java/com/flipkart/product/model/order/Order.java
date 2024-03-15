package com.flipkart.product.model.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.flipkart.product.model.OrderStatus;
import com.flipkart.product.model.PaymentMode;
import com.flipkart.authentication.model.Address;
import com.flipkart.authentication.validation.groups.OrderChecker;
import com.flipkart.product.validation.group.CancelOrderChecker;
import com.flipkart.product.validation.group.UserIdChecker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
    @Positive(message = "Order id should be positive", groups = CancelOrderChecker.class)
    private int id;
    @Column(name = "user_id")
    @Positive(message = "User id should be positive", groups = {UserIdChecker.class, OrderChecker.class, CancelOrderChecker.class})
    private int userId;
    @Column(name = "product_id")
    @Positive(message = "Product id should be positive", groups = {OrderChecker.class, CancelOrderChecker.class})
    private int productId;
    @Column(name = "quantity")
    @Positive(message = "Order quantity should be positive", groups = {OrderChecker.class, CancelOrderChecker.class})
    private int quantity;
    @Column(name = "total_amount")
    @Positive(message = "Total amount should be positive", groups = OrderChecker.class)
    private float totalAmount;
    @Valid
    private Address address;
    @Column(name = "payment_mode_id")
    @Positive(message = "Payment mode id can't be negative", groups = OrderChecker.class)
    private int paymentModeId;
    @Transient
    @NotNull(message = "Product name can't be null", groups = OrderChecker.class)
    private String productName;
    @Column(name = "order_status_id")
    @Positive(message = "Order status id can't be negative", groups = OrderChecker.class)
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

    public Address getAddress() {
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
        return String.format("Order id : %d\n%s\nproduct quantity : %d\ntotal amount : %.2f\nPayment mode : %s\nShipping address id : %s\nStatus : %s",
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

        private int id;
        private final int userId;
        private int productId;
        private int quantity;
        private float totalAmount;
        private Address address;
        private int paymentModeId;
        private String productName;
        private int orderStatusId;

        /**
         * Utilizes the builder pattern to construct the instance of {@link Order}.
         */
        @JsonCreator
        public OrderBuilder(@JsonProperty("userId")final int userId) {
           this.userId = userId;
        }

        @JsonProperty("address")
        public OrderBuilder setAddress(final Address address) {
            this.address = address;

            return this;
        }

        @JsonProperty("paymentModeId")
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

        @JsonProperty("orderStatusId")
        public OrderBuilder setOrderStatusId(final int orderStatusId) {
            this.orderStatusId = orderStatusId;

            return this;
        }

        public Order build() {
            return new Order(this) ;
        }
    }
}
