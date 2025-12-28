package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.product.*;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;
import com.asafeorneles.gym_stock_control.enums.ActivityStatus;
import com.asafeorneles.gym_stock_control.exceptions.*;
import com.asafeorneles.gym_stock_control.mapper.ProductMapper;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import com.asafeorneles.gym_stock_control.repositories.SaleItemRepository;
import com.asafeorneles.gym_stock_control.services.factory.ProductInventoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    SaleItemRepository saleItemRepository;

    @Transactional
    public ResponseProductDetailDto createProduct(CreateProductDto createProductDto) {
        UUID categoryId = createProductDto.categoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("The category {" + categoryId + "} does not exist. Please insert a valid category."));

        if (!category.isActivity()){
            throw new StatusActivityException("This category is inactivity!");
        }

        if (productRepository.existsByNameAndBrand(createProductDto.name(), createProductDto.brand())) {
            throw new BusinessConflictException("Product already exists");
        }

        Product product = ProductMapper.createProductToProduct(createProductDto, category);

        ProductInventory productInventory = ProductInventoryFactory
                .newProductInventory(product, createProductDto.quantity(), createProductDto.lowStockThreshold());

        product.setInventory(productInventory);
        product.activity();

        productRepository.save(product);

        return ProductMapper.productToResponseDetailsProduct(product);
    }

    public List<ResponseProductDto> findProducts(Specification<Product> specification) {
        return productRepository.findAll(specification).stream().map(ProductMapper::productToResponseProduct).toList();
    }

    public List<ResponseProductDetailDto> findProductsDetails(Specification<Product> specification) {
        return productRepository.findAll(specification).stream().map(ProductMapper::productToResponseDetailsProduct).toList();
    }

    public ResponseProductDto findProductById(UUID id) {
        return productRepository.findById(id)
                .filter(Product::isActivity)
                .map(ProductMapper::productToResponseProduct)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));
    }

    public List<ResponseProductDetailDto> findProductsWithLowStock() {
        return productRepository.findProductWithLowStock()
                .stream()
                .map(ProductMapper::productToResponseDetailsProduct)
                .toList();
    }

    @Transactional
    public ResponseProductDetailDto updateProduct(UUID id, UpdateProductDto updateProductDto) {
        Product productFound = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        UUID updateCategoryId = updateProductDto.categoryId();
        Category category = categoryRepository.findById(updateCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("The category {" + updateCategoryId + "} does not exist. Please insert a valid category to update the product."));

        ProductMapper.updateProductToProduct(updateProductDto, productFound, category);

        productRepository.save(productFound);

        return ProductMapper.productToResponseDetailsProduct(productFound);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (saleItemRepository.existsByProduct_ProductId(id)){
            throw new BusinessConflictException("This product has already been used in a sale. Please use the deactivate option.");
        }

        Product productFound = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        productRepository.delete(productFound);
    }


    @Transactional
    public ResponseProductDetailDto deactivateProduct(UUID id, DeactivateProductDto deactivateProductDto) {
        Product productFound = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        productFound.inactivity(deactivateProductDto.reason());

        productRepository.save(productFound);

        return ProductMapper.productToResponseDetailsProduct(productFound);
    }

    @Transactional
    public ResponseProductDetailDto activateProduct(UUID id) {
        Product productFound = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + id));

        productFound.activity();

        productRepository.save(productFound);

        return ProductMapper.productToResponseDetailsProduct(productFound);
    }
}
