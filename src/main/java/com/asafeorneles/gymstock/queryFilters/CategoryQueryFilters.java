package com.asafeorneles.gymstock.queryFilters;

import com.asafeorneles.gymstock.entities.Category;
import com.asafeorneles.gymstock.enums.ActivityStatus;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import static com.asafeorneles.gymstock.specifications.CategorySpec.activityStatusEquals;
import static com.asafeorneles.gymstock.specifications.CategorySpec.nameContains;

@Data
public class CategoryQueryFilters {
    private String name;
    private ActivityStatus activityStatus;

    public Specification<Category> toSpecification() {

        return nameContains(name).and(activityStatusEquals(activityStatus));
    }
}
