package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.product.CreateProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDetailDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.UpdateProductDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.mapper.ProductMapper;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import com.asafeorneles.gym_stock_control.services.factory.ProductInventoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.UUID;


@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;

    public ResponseProductDetailDto createProduct(CreateProductDto createProductDto) {
        Category category = categoryRepository.findById(createProductDto.categoryId())
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND)); // Create an Exception Handler for when Category does not exist

        if (productRepository.existsByNameAndBrand(createProductDto.name(), createProductDto.brand())) {
            throw new IllegalArgumentException("product already exists");
        }

        // CreateProductDto -> Product
        var product = ProductMapper.createProductToProduct(createProductDto, category);

        var productInventory = ProductInventoryFactory
                .newProductInventory(product, createProductDto.quantity(), createProductDto.lowStockThreshold());

        product.setInventory(productInventory);

        productRepository.save(product);

        // Product -> ResponseProductDetailDto
        return ProductMapper.productToResponseCreatedProduct(product);
    }

    public List<ResponseProductDto> findProducts(Specification<Product> specification) {
        List<Product> productsFound = productRepository.findAll(specification);
        if (productsFound.isEmpty()) {
            throw new ErrorResponseException(HttpStatus.NOT_FOUND); // Create an Exception Handler for when Pet is not found
        }
        return productsFound.stream().map(ProductMapper::productToResponseProduct).toList();
    }

    public ResponseProductDto findProductById(UUID id) {
        Product productFound = productRepository.findById(id).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND)); // Create an Exception Handler for when Pet is not found
        return ProductMapper.productToResponseProduct(productFound);
    }

    public List<ResponseProductDetailDto> findProductsWithLowStock() { // Improve later
        List<ResponseProductDetailDto> productsWithLowStock = productRepository.findProductWithLowStock()
                .stream()
                .map(ProductMapper::productToResponseCreatedProduct)
                .toList();

        if (productsWithLowStock.isEmpty()) {
            throw new ErrorResponseException(HttpStatus.NOT_FOUND); // Create an Exception Handler for when Pet is not found
        }
        return productsWithLowStock;
    }


    public ResponseProductDetailDto updateProduct(UUID id, UpdateProductDto updateProductDto) {
        Product productFound = productRepository.findById(id).orElseThrow(()-> new ErrorResponseException(HttpStatus.NOT_FOUND)); // Create an Exception Handler for when Pet is not found

        Category category = categoryRepository.findById(updateProductDto.categoryId())
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND)); // Create an Exception Handler for when Category does not exist

        ProductMapper.updateProductToProduct(updateProductDto, productFound, category);

        productRepository.save(productFound);

        return ProductMapper.productToResponseCreatedProduct(productFound);
    }

    public void deleteProduct(UUID id) {
        Product productFound = productRepository.findById(id).orElseThrow(()-> new ErrorResponseException(HttpStatus.NOT_FOUND)); // Create an Exception Handler for when Pet is not found
        productRepository.delete(productFound);
    }
}
