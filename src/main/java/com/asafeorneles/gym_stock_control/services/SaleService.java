package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.SaleItem.CreateSaleItemDto;
import com.asafeorneles.gym_stock_control.dtos.sale.CreateSaleDto;
import com.asafeorneles.gym_stock_control.dtos.sale.PatchPaymentMethodDto;
import com.asafeorneles.gym_stock_control.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gym_stock_control.entities.Coupon;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.Sale;
import com.asafeorneles.gym_stock_control.entities.SaleItem;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.exceptions.ActivityStatusException;
import com.asafeorneles.gym_stock_control.mapper.SaleMapper;
import com.asafeorneles.gym_stock_control.repositories.CouponRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import com.asafeorneles.gym_stock_control.repositories.SaleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Transactional
    public ResponseSaleDto createSale(@Valid CreateSaleDto createSaleDto) {
        Sale sale = SaleMapper.createSaleToSale(createSaleDto);
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
