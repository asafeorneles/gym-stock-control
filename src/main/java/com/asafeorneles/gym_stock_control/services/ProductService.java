package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.product.*;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;
import com.asafeorneles.gym_stock_control.exceptions.CategoryNotFoundException;
import com.asafeorneles.gym_stock_control.exceptions.ProductAlreadyExistsException;
import com.asafeorneles.gym_stock_control.exceptions.ProductNotFoundException;
import com.asafeorneles.gym_stock_control.exceptions.ProductSoldException;
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
                .orElseThrow(() -> new CategoryNotFoundException("The category {" + categoryId + "} does not exist. Please insert a valid category."));

        if (productRepository.existsByNameAndBrand(createProductDto.name(), createProductDto.brand())) {
            throw new ProductAlreadyExistsException("Product already exists");
        }

        Product product = ProductMapper.createProductToProduct(createProductDto, category);

        ProductInventory productInventory = ProductInventoryFactory
                .newProductInventory(product, createProductDto.quantity(), createProductDto.lowStockThreshold());

        product.setInventory(productInventory);
        product.activity();

        productRepository.save(product);

        return ProductMapper.productToResponseCreatedProduct(product);
    }

    public List<ResponseProductDto> findProducts(Specification<Product> specification) {
        return productRepository.findAll(specification).stream().map(ProductMapper::productToResponseProduct).toList();
    }

    public List<ResponseProductDetailDto> findProductsDetails(Specification<Product> specification) {
        return productRepository.findAll(specification).stream().map(ProductMapper::productToResponseCreatedProduct).toList();
    }

    public ResponseProductDto findProductById(UUID id) {
        return productRepository.findById(id)
                .filter(Product::isActivity)
                .map(ProductMapper::productToResponseProduct)
                .orElseThrow(() -> new ProductNotFoundException("Product not found by id: " + id));
    }

    public List<ResponseProductDetailDto> findProductsWithLowStock() {
        return productRepository.findProductWithLowStock()
                .stream()
                .map(ProductMapper::productToResponseCreatedProduct)
                .toList();
    }

    @Transactional
    public ResponseProductDetailDto updateProduct(UUID id, UpdateProductDto updateProductDto) {
        Product productFound = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found by id: " + id));

        UUID updateCategoryId = updateProductDto.categoryId();
        Category category = categoryRepository.findById(updateCategoryId)
                .orElseThrow(() -> new CategoryNotFoundException("The category {" + updateCategoryId + "} does not exist. Please insert a valid category to update the product."));

        ProductMapper.updateProductToProduct(updateProductDto, productFound, category);

        productRepository.save(productFound);

        return ProductMapper.productToResponseCreatedProduct(productFound);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product productFound = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found by id: " + id));

        if (saleItemRepository.existsByProduct_id(id)){
            throw new ProductSoldException("This product has already been used in a sale. Please use the deactivate option.");
        }

        productRepository.delete(productFound);
    }


    @Transactional
    public ResponseProductDetailDto deactivateProduct(UUID id, DeactivateProductDto deactivateProductDto) {
        Product productFound = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found by id: " + id));

        productFound.inactivity(deactivateProductDto.reason());

        productRepository.save(productFound);

        return ProductMapper.productToResponseCreatedProduct(productFound);
    }

    @Transactional
    public ResponseProductDetailDto activateProduct(UUID id) {
        Product productFound = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found by id: " + id));

        productFound.activity();

        productRepository.save(productFound);

        return ProductMapper.productToResponseCreatedProduct(productFound);
    }
}
