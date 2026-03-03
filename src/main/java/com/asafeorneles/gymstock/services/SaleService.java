package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.SaleItem.SaleItemCreateRequest;
import com.asafeorneles.gymstock.dtos.sale.SaleCreateRequest;
import com.asafeorneles.gymstock.dtos.sale.SalePaymentMethodRequest;
import com.asafeorneles.gymstock.dtos.sale.SaleResponse;
import com.asafeorneles.gymstock.entities.*;
import com.asafeorneles.gymstock.exceptions.ActivityStatusException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.SaleMapper;
import com.asafeorneles.gymstock.repositories.CouponRepository;
import com.asafeorneles.gymstock.repositories.ProductRepository;
import com.asafeorneles.gymstock.repositories.SaleRepository;
import com.asafeorneles.gymstock.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SaleService {
    final SaleRepository saleRepository;
    final ProductRepository productRepository;
    final ProductInventoryService productInventoryService;
    final CouponRepository couponRepository;
    final CouponService couponService;
    final UserRepository userRepository;
    final SaleMapper saleMapper;

    @Transactional
    public SaleResponse createSale(SaleCreateRequest saleCreateRequest, JwtAuthenticationToken token) {

        User user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found to create the sale."));

        Sale sale = new Sale();
        List<SaleItem> saleItems = newSaleItemList(saleCreateRequest.saleItems(), productRepository, productInventoryService, sale);

        sale.setUser(user);
        sale.setSaleItems(saleItems);
        sale.setPaymentMethod(saleCreateRequest.paymentMethod());

        sale.calculateTotalPrice();

        productInventoryService.updateQuantityAfterSale(saleItems);

        if (saleCreateRequest.couponId() != null){
            UUID couponId = saleCreateRequest.couponId();
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon not found by id: " + couponId));

            couponService.validateCouponToCreateSale(coupon);
            sale.setCoupon(coupon);
            couponService.applyCoupon(sale);
        }

        saleRepository.save(sale);
        return saleMapper.toResponse(sale);
    }

    public static List<SaleItem> newSaleItemList(List<SaleItemCreateRequest> saleItemCreateRequestList, ProductRepository productRepository, ProductInventoryService productInventoryService, Sale sale) {
        List<SaleItem> saleItems = new ArrayList<>();

        for (SaleItemCreateRequest createSaleItem : saleItemCreateRequestList) {
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

    public Page<SaleResponse> getAllSales(Specification<Sale> specification, Pageable pageable) {
        return saleRepository.findAll(specification, pageable)
                .map(saleMapper::toResponse);
    }

    public SaleResponse getSaleById(UUID id) {
        return saleRepository.findById(id)
                .map(saleMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No sales registered with id {" + id + "}"));
    }

    @Transactional
    public void deleteSale(UUID id) {
        Sale saleFound = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No sales registered with id {" + id + "}"));

        saleRepository.delete(saleFound);
    }

    @Transactional
    public SaleResponse updatePaymentMethod(UUID id, SalePaymentMethodRequest patchPaymentMethod) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No sales registered with id {" + id + "}"));

        sale.setPaymentMethod(patchPaymentMethod.paymentMethod());
        saleRepository.save(sale);

        return saleMapper.toResponse(sale);
    }
}
