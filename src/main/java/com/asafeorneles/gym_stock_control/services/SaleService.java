package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.SaleItem.CreateSaleItemDto;
import com.asafeorneles.gym_stock_control.dtos.sale.CreateSaleDto;
import com.asafeorneles.gym_stock_control.dtos.sale.PatchPaymentMethodDto;
import com.asafeorneles.gym_stock_control.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.Sale;
import com.asafeorneles.gym_stock_control.entities.SaleItem;
import com.asafeorneles.gym_stock_control.mapper.SaleMapper;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import com.asafeorneles.gym_stock_control.repositories.SaleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;

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

    @Transactional
    public ResponseSaleDto createSale(@Valid CreateSaleDto createSaleDto) {
        Sale sale = SaleMapper.createSaleToSale(createSaleDto);
        List<SaleItem> saleItems = newSaleItemList(createSaleDto.saleItems(), productRepository, sale);

        sale.setSaleItems(saleItems);
        sale.calculateTotalPrice();

        productInventoryService.updateQuantityAfterSale(saleItems);

        saleRepository.save(sale);
        return SaleMapper.saleToResponseSale(sale);
    }

    public static List<SaleItem> newSaleItemList(List<CreateSaleItemDto> createSaleItemDtoList, ProductRepository productRepository, Sale sale) {
        List<SaleItem> saleItems = new ArrayList<>();

        for (CreateSaleItemDto createSaleItem : createSaleItemDtoList) {
            Product product = productRepository.findById(createSaleItem.productId())
                    .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND)); // Create an Exception Handler for when Category does not exist

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

    public List<ResponseSaleDto> findSales(Specification<Sale> specification) {
        List<Sale> salesFound = saleRepository.findAll(specification);

        if (salesFound.isEmpty()){
            throw new ErrorResponseException(HttpStatus.NOT_FOUND); // Create an Exception Handler for when Sale does not exist
        }

        return salesFound.stream().map(SaleMapper::saleToResponseSale).toList();
    }

    public ResponseSaleDto findSaleById(UUID id) {
        Sale saleFound = saleRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));

        return SaleMapper.saleToResponseSale(saleFound);
    }

    public void deleteSale(UUID id) {
        Sale saleFound = saleRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));

        saleRepository.delete(saleFound);
    }

    public ResponseSaleDto updatePaymentMethod(UUID id, PatchPaymentMethodDto patchPaymentMethod) {
        Sale saleFound = saleRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));

        saleFound.setPaymentMethod(patchPaymentMethod.paymentMethod());
        saleRepository.save(saleFound);

        return SaleMapper.saleToResponseSale(saleFound);
    }
}
