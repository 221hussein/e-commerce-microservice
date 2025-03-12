package com.techconnect221.ecommerce.order;

import com.techconnect221.ecommerce.customer.CustomerClient;
import com.techconnect221.ecommerce.exception.BusinessException;
import com.techconnect221.ecommerce.kafka.OrderConfirmation;
import com.techconnect221.ecommerce.kafka.OrderProducer;
import com.techconnect221.ecommerce.orderline.OrderLineService;
import com.techconnect221.ecommerce.product.ProductClient;
import com.techconnect221.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    public Integer createdOrder( OrderRequest request) {
//        check tht customer(open Feign
        var customer = this.customerClient.findCustomerById(request.customId())
                .orElseThrow(() -> new BusinessException("Cannot create order ::No Custoner exists with the provided ID::"+request.customId()));

//        purchase the products ---> product-ms (restTemplate)
        var purchaseProducts = this.productClient.purcharseProducts(request.products());


//        persist the order lines
        var order = this.repository.save(mapper.toOrder(request));
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
//      todo  start payment process


//        send order confirmation --> noctification-ms
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchaseProducts
                )
        );
        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream().map(mapper::fromOrder)
        .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Order with id %s not found", orderId)));
    }
}
