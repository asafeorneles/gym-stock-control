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
    public ResponseProductDetailDto createProduct(CreateProductDto createProductDto) {
        UUID categoryId = createProductDto.categoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("The category {" + categoryId + "} does not exist. Please insert a valid category."));

        if (!category.isActivity()) {
            throw new ActivityStatusException("This category is inactivity!");
        }

        if (productRepository.existsByNameAndBrand(createProductDto.name(), createProductDto.brand())) {
            throw new BusinessConflictException("Product already exists");
        }

        Product product = productMapper.toEntity(createProductDto);

        ProductInventory productInventory = ProductInventoryFactory
                .newProductInventory(product, createProductDto.quantity(), createProductDto.lowStockThreshold());

        product.setInventory(productInventory);
        product.setCategory(category);
        product.activity();

        productRepository.save(product);

        return productMapper.toResponseDetails(product);
    }

    public List<ResponseProductDto> getAllProducts(Specification<Product> specification) {
        return productRepository.findAll(specification)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public List<ResponseProductDetailDto> getAllProductsDetails(Specification<Product> specification) {
        return productRepository.findAll(specification)
                .stream()
                .map(productMapper::toResponseDetails)
                .toList();
    }

    public ResponseProductDto getProductById(UUID id) {
        return productRepository.findById(id)
                .filter(Product::isActivity)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));
    }

    public List<ResponseProductDetailDto> getAllProductsWithLowStock() {
        return productRepository.findProductWithLowStock()
                .stream()
                .map(productMapper::toResponseDetails)
                .toList();
    }

    @Transactional
    public ResponseProductDetailDto updateProduct(UUID id, UpdateProductDto updateProductDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        checkProductIsActiveBeforeUpdate(product.isActivity(), "This product is inactive.. You can only update activity products.");

        UUID updateCategoryId = updateProductDto.categoryId();
        Category category = categoryRepository.findById(updateCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("The category {" + updateCategoryId + "} does not exist. Please insert a valid category to update the product."));

        productMapper.updateEntity(updateProductDto, product);
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
    public ResponseProductDetailDto deactivateProduct(UUID id, DeactivateProductDto deactivateProductDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        product.inactivity(deactivateProductDto.reason());

        productRepository.save(product);

        return productMapper.toResponseDetails(product);
    }

    @Transactional
    public ResponseProductDetailDto activateProduct(UUID id) {
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
