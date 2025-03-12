package com.techconnect221.ecommerce.product;

import com.techconnect221.ecommerce.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClient {

    @Value("${application.config.product-url}")
    private String productUrl;
    private final RestTemplate restTemplate;

    public List<PurcharseResponse> purcharseProducts(List<PurchaseRequest> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<List<PurchaseRequest>> requestEntity =
                new HttpEntity<>(requestBody, headers);
        ParameterizedTypeReference<List<PurcharseResponse>> responseType =
                new ParameterizedTypeReference<>() {};
        ResponseEntity<List<PurcharseResponse>> responseEntity
                = restTemplate.exchange(
                        productUrl + "/purchase",
                HttpMethod.POST,
                requestEntity,
                responseType
        );
        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("An error occurred while trying to retrieve purcharse responses" + responseEntity.getStatusCode());
        }
        return  responseEntity.getBody();
    }

}
