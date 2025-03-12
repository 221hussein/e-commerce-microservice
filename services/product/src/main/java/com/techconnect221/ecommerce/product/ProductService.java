package com.techconnect221.ecommerce.product;

import com.techconnect221.ecommerce.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Integer createProduct(@Valid ProductRequest request) {
        var Product = mapper.toProduct(request);
        return repository.save(Product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> requests) {
        var productIds = requests.stream()
                .map(ProductPurchaseRequest:: productId)
                .toList();
        var storedProducts = repository.findAllByIdInOrderById(productIds);
        if (productIds.size() != storedProducts.size()) {
            throw new ProductPurchaseException("One or more products does not exits");
        }

        var storesRequest = requests
                .stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();
        for (int i = 0; i < storesRequest.size(); i++) {
            var product = storedProducts.get(i);
            var productRequest = storesRequest.get(i);
            if (product.getAvailableQuantity() < productRequest.quantity()) {
                throw new ProductPurchaseException("One or more products do not have enough quantity");
            }
            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            repository.save(product);
            purchasedProducts.add(mapper.toProductPurchaseRespone(product,productRequest.quantity()));
        }
        return purchasedProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
