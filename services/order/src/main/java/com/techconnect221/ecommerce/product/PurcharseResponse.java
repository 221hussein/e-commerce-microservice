package com.techconnect221.ecommerce.product;

import java.math.BigDecimal;

public record PurcharseResponse(
        Integer productId,
        String name,
        String description,
        BigDecimal price,
        double quantity
) {
}
