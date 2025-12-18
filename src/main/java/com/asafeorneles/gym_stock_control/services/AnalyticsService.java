package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.analytics.TopSellingProductsDto;
import com.asafeorneles.gym_stock_control.repositories.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {

    @Autowired
    AnalyticsRepository analyticsRepository;

    public List<TopSellingProductsDto> getTopSellingProducts(Integer limit) {
        int finalLimit = (limit == null || limit < 0) ? 10 : Math.min(limit, 30);

        return analyticsRepository.findTopSellingProducts(finalLimit);
    }

    public List<TopSellingProductsDto> getTopSellingProductsByPeriod(Integer limit, LocalDate startDate, LocalDate endDate) {
        int finalLimit = (limit == null || limit < 0) ? 10 : Math.min(limit, 30);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return analyticsRepository.findTopSellingProductsByPeriod(finalLimit, startDateTime, endDateTime);
    }
}
