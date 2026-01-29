package com.asafeorneles.gymstock.queryFilters;

import com.asafeorneles.gymstock.entities.Sale;
import com.asafeorneles.gymstock.enums.PaymentMethod;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.asafeorneles.gymstock.specifications.SaleSpec.*;

@Data
public class SaleQueryFilters {
    private BigDecimal totalPriceMax;
    private BigDecimal totalPriceMin;
    @DateTimeFormat (iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdMax;
    @DateTimeFormat (iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdMin;
    private PaymentMethod paymentMethod;


    public Specification<Sale> toSpecification() {
        LocalDateTime max = createdMax != null
                ? createdMax.atTime(23, 59, 59) : null;

        LocalDateTime min = createdMin != null
                ? createdMin.atStartOfDay() : null;

        return totalPriceGreaterThanOrEqualTo(totalPriceMin)
                .and(totalPriceLessThanOrEqualTo(totalPriceMax))
                .and(createdAtGreaterThanOrEqualTo(min))
                .and(createdAtLessThanOrEqualTo(max))
                .and(paymentMethodEqual(paymentMethod));
    }
}
