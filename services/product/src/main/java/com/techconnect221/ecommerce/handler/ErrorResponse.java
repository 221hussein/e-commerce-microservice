package com.techconnect221.ecommerce.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors

) {
}
