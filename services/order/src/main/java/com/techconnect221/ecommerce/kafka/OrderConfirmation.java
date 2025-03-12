package com.techconnect221.ecommerce.kafka;

import com.techconnect221.ecommerce.customer.CustomerResponse;
import com.techconnect221.ecommerce.order.PaymentMethod;
import com.techconnect221.ecommerce.product.PurcharseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurcharseResponse> products
) {

}
