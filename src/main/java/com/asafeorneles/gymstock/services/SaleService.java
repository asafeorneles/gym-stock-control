package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.SaleItem.CreateSaleItemDto;
import com.asafeorneles.gymstock.dtos.sale.CreateSaleDto;
import com.asafeorneles.gymstock.dtos.sale.PatchPaymentMethodDto;
import com.asafeorneles.gymstock.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gymstock.entities.*;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.exceptions.ActivityStatusException;
import com.asafeorneles.gymstock.mapper.SaleMapper;
import com.asafeorneles.gymstock.repositories.CouponRepository;
import com.asafeorneles.gymstock.repositories.ProductRepository;
import com.asafeorneles.gymstock.repositories.SaleRepository;
import com.asafeorneles.gymstock.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SaleService {
    @Autowired
    SaleRepository saleRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductInventoryService productInventoryService;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CouponService couponService;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public ResponseSaleDto createSale(CreateSaleDto createSaleDto, JwtAuthenticationToken token) {

        User user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found to create the sale."));

        Sale sale = SaleMapper.createSaleToSale(createSaleDto, user);
        List<SaleItem> saleItems = newSaleItemList(createSaleDto.saleItems(), productRepository, sale, productInventoryService);

        sale.setSaleItems(saleItems);

        productInventoryService.updateQuantityAfterSale(saleItems);

        sale.calculateTotalPrice();

        if (createSaleDto.couponId() != null){
            UUID couponId = createSaleDto.couponId();
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon not found by id: " + couponId));

            couponService.validateCouponToCreateSale(coupon);
            sale.setCoupon(coupon);
            couponService.applyCoupon(sale);
        }

        saleRepository.save(sale);
        return SaleMapper.saleToResponseSale(sale);
    }

    public static List<SaleItem> newSaleItemList(List<CreateSaleItemDto> createSaleItemDtoList, ProductRepository productRepository, Sale sale, ProductInventoryService productInventoryService) {
        List<SaleItem> saleItems = new ArrayList<>();

        for (CreateSaleItemDto createSaleItem : createSaleItemDtoList) {
            UUID productId = createSaleItem.productId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found by id: " + productId));

            if (!product.isActivity()){
                throw new ActivityStatusException("This product is inactivity!");
            }

            productInventoryService.validateQuantity(product, createSaleItem.quantity());

            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(createSaleItem.quantity()));

            SaleItem saleItem = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(createSaleItem.quantity())
                    .costPrice(product.getCostPrice())
                    .unityPrice(product.getPrice())
                    .totalPrice(totalPrice)
                    .build();

            saleItems.add(saleItem);
        }
        return saleItems;
    }

    public Page<ResponseSaleDto> getAllSales(Specification<Sale> specification, Pageable pageable) {
        return saleRepository.findAll(specification, pageable).map(SaleMapper::saleToResponseSale);
    }

    public ResponseSaleDto getSaleById(UUID id) {
        return saleRepository.findById(id)
                .map(SaleMapper::saleToResponseSale)
                .orElseThrow(() -> new ResourceNotFoundException("No sales registered with id {" + id + "}"));
    }

    @Transactional
    public void deleteSale(UUID id) {
        Sale saleFound = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No sales registered with id {" + id + "}"));

        saleRepository.delete(saleFound);
    }

    @Transactional
    public ResponseSaleDto updatePaymentMethod(UUID id, PatchPaymentMethodDto patchPaymentMethod) {
        Sale saleFound = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No sales registered with id {" + id + "}"));

        saleFound.setPaymentMethod(patchPaymentMethod.paymentMethod());
        saleRepository.save(saleFound);

        return SaleMapper.saleToResponseSale(saleFound);
    }
}
