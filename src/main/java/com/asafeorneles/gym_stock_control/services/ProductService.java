package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.product.CreateProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.mapper.ProductMapper;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import com.asafeorneles.gym_stock_control.services.factory.ProductInventoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseProductDto createProduct(CreateProductDto createProductDto) {
        Category category = categoryRepository.findById(createProductDto.category_id())
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

        // Product -> ResponseProductDto
        return ProductMapper.productToResponseProduct(product);
    }

    public List<ResponseProductDto> findProducts() {
        List<Product> productsFound = productRepository.findAll();
        if (productsFound.isEmpty()) {
            throw new ErrorResponseException(HttpStatus.NOT_FOUND); // Create an Exception Handler for when Pet is not found
        }
        return productsFound.stream().map(ProductMapper::productToResponseProduct).toList();
    }

    public ResponseProductDto findProductById(UUID id) {
        Product productFound = productRepository.findById(id).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND)); // Create an Exception Handler for when Pet is not found
        return ProductMapper.productToResponseProduct(productFound);
    }

    public List<ResponseProductDto> findProductsWithLowStock() {
        List<ResponseProductDto> productsWithLowStock = productRepository.findAll()
                .stream()
                .filter(product -> product.getInventory().getQuantity() <= product.getInventory().getLowStockThreshold())
                .map(ProductMapper::productToResponseProduct)
                .toList();
        if (productsWithLowStock.isEmpty()) {
            throw new ErrorResponseException(HttpStatus.NOT_FOUND); // Create an Exception Handler for when Pet is not found
        }
        return productsWithLowStock;
    }

}
