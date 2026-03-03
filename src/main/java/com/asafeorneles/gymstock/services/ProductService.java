package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.product.*;
import com.asafeorneles.gymstock.entities.Category;
import com.asafeorneles.gymstock.entities.Product;
import com.asafeorneles.gymstock.entities.ProductInventory;
import com.asafeorneles.gymstock.exceptions.ActivityStatusException;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.ProductMapper;
import com.asafeorneles.gymstock.repositories.CategoryRepository;
import com.asafeorneles.gymstock.repositories.ProductRepository;
import com.asafeorneles.gymstock.repositories.SaleItemRepository;
import com.asafeorneles.gymstock.services.factory.ProductInventoryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProductService {
    final ProductRepository productRepository;
    final CategoryRepository categoryRepository;
    final SaleItemRepository saleItemRepository;
    final ProductMapper productMapper;

    @Transactional
    public ProductDetailResponse createProduct(ProductCreateRequest productCreateRequest) {
        UUID categoryId = productCreateRequest.categoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("The category {" + categoryId + "} does not exist. Please insert a valid category."));

        if (!category.isActivity()) {
            throw new ActivityStatusException("This category is inactivity!");
        }

        if (productRepository.existsByNameAndBrand(productCreateRequest.name(), productCreateRequest.brand())) {
            throw new BusinessConflictException("Product already exists");
        }

        Product product = productMapper.toEntity(productCreateRequest);

        ProductInventory productInventory = ProductInventoryFactory
                .newProductInventory(product, productCreateRequest.quantity(), productCreateRequest.lowStockThreshold());

        product.setInventory(productInventory);
        product.setCategory(category);
        product.activity();

        productRepository.save(product);

        return productMapper.toResponseDetails(product);
    }

    public List<ProductResponse> getAllProducts(Specification<Product> specification) {
        return productRepository.findAll(specification)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public List<ProductDetailResponse> getAllProductsDetails(Specification<Product> specification) {
        return productRepository.findAll(specification)
                .stream()
                .map(productMapper::toResponseDetails)
                .toList();
    }

    public ProductResponse getProductById(UUID id) {
        return productRepository.findById(id)
                .filter(Product::isActivity)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));
    }

    public List<ProductDetailResponse> getAllProductsWithLowStock() {
        return productRepository.findProductWithLowStock()
                .stream()
                .map(productMapper::toResponseDetails)
                .toList();
    }

    @Transactional
    public ProductDetailResponse updateProduct(UUID id, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        checkProductIsActiveBeforeUpdate(product.isActivity(), "This product is inactive.. You can only update activity products.");

        UUID updateCategoryId = productUpdateRequest.categoryId();
        Category category = categoryRepository.findById(updateCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("The category {" + updateCategoryId + "} does not exist. Please insert a valid category to update the product."));

        productMapper.updateEntity(productUpdateRequest, product);
        product.setCategory(category);

        productRepository.save(product);

        return productMapper.toResponseDetails(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (saleItemRepository.existsByProduct_ProductId(id)) {
            throw new BusinessConflictException("This product has already been used in a sale. Please use the deactivate option.");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        productRepository.delete(product);
    }


    @Transactional
    public ProductDetailResponse deactivateProduct(UUID id, ProductDeactivateRequest productDeactivateRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        product.inactivity(productDeactivateRequest.reason());

        productRepository.save(product);

        return productMapper.toResponseDetails(product);
    }

    @Transactional
    public ProductDetailResponse activateProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        product.activity();

        productRepository.save(product);

        return productMapper.toResponseDetails(product);
    }

    public static void checkProductIsActiveBeforeUpdate(boolean isActivity, String error) {
        if (!isActivity) {
            throw new BusinessConflictException(error);
        }
    }
}
