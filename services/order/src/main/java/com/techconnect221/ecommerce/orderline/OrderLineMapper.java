package com.techconnect221.ecommerce.orderline;

import com.techconnect221.ecommerce.order.Order;
import com.techconnect221.ecommerce.order.OrderLineRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper {
    public OrderLine toOrderLine(OrderLineRequest orderLineRequest) {
        return OrderLine.builder()
                .id(orderLineRequest.id())
                .quantity(orderLineRequest.quantity())
                .order(
                        Order.builder().id(orderLineRequest.orderId()).build()
                )
                .productId(orderLineRequest.productId())
                .build();
    }
}
