package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.analytics.TopSellingProductsDto;
import com.asafeorneles.gymstock.repositories.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    final AnalyticsRepository analyticsRepository;

    public List<TopSellingProductsDto> getTopSellingProducts(Integer limit) {
        int finalLimit = limit == null ? 10 : limit;

        return analyticsRepository.findTopSellingProducts(finalLimit);
    }

    public List<TopSellingProductsDto> getTopSellingProductsByPeriod(Integer limit, LocalDate startDate, LocalDate endDate) {
        int finalLimit = limit == null ? 10 : limit;
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return analyticsRepository.findTopSellingProductsByPeriod(finalLimit, startDateTime, endDateTime);
    }
}
